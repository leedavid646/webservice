package lab.servlet.parolee.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lab.servlet.parolee.domain.Gender;
import lab.servlet.parolee.domain.Parolee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to implement a simple REST Web service for managing parolees.
 * <p>
 * ParoleeResource implements a WEB service with the following interface:
 * <p>
 * - GET    <base-uri>/parolees/{id}
 * Retrieves a parolee based on their unique id. The format of the
 * returned data is JSON.
 * <p>
 * - POST   <base-uri>/parolees
 * Creates a new Parolee. The HTTP post message contains an JSON
 * representation of the parolee to be created.
 * <p>
 * - PUT    <base-uri>/parolees/{id}
 * Updates a parolee, identified by their id.The HTTP PUT message
 * contains an JSON document describing the new state of the parolee.
 * <p>
 * - DELETE <base-uri>/parolees/{id}
 * Deletes a parolee, identified by their unique id.
 * <p>
 * - DELETE <base-uri>/parolees
 * Deletes all parolees.
 */
@Path("/parolees")
public class ParoleeResource {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final Logger LOGGER = LoggerFactory.getLogger(ParoleeResource.class);
    private static final String PAROLEE_JSON_TEMPLATE =
            "{ \"id\": %d, \"firstName\": \"%s\", \"lastName\": \"%s\", \"gender\": \"%s\", \"dateOfBirth\": \"%s\" }";

    private Map<Long, Parolee> paroleeDB = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong();

    /**
     * Attempts to retrieve a particular Parolee based on their unique id. If
     * the required Parolee is found, this method returns a 200 response along
     * with an JSON representation of the Parolee. In other cases, this method
     * returns a 404 response.
     *
     * @param id the unique id of the Parolee to be returned.
     * @return a StreamingOutput object that writes out the Parolee state in
     * JSON form.
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public StreamingOutput retrieveParolee(@PathParam("id") long id) {
        LOGGER.info("Retrieving parolee with id: " + id);
        // Lookup the Parolee within the in-memory data structure.
        final Parolee parolee = paroleeDB.get(id);
        if (parolee == null) {
            // Return a HTTP 404 response if the specified Parolee isn't found.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Return a StreamingOuput instance that the JAX-RS implementation will
        // use to set the body of the HTTP response message.
        return new StreamingOutput() {
            public void write(OutputStream outputStream) throws IOException,
                    WebApplicationException {
                outputParolee(outputStream, parolee);
            }
        };
    }

    /**
     * Creates a new Parolee.
     *
     * @param is the Inputstream that contains an JSON representation of the
     *           Parolee to be created.
     * @return a Response object that includes the HTTP "Location" header,
     * whose value is the URI of the newly created resource. The HTTP
     * response code is 201. The JAX-RS run-time processes the Response
     * object when preparing the HTTP response message.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createParolee(InputStream is) {
        // Read an JSON representation of a new Parolee. Note that with JAX-RS,
        // any non-annotated parameter in a Resource method is assumed to hold
        // the HTTP request's message body.
        Parolee parolee = readParolee(is);

        // Generate an ID for the new Parolee, and store it in memory.
        parolee.setId(idCounter.incrementAndGet());
        paroleeDB.put(parolee.getId(), parolee);

        LOGGER.debug("Created parolee with id: " + parolee.getId());

        return Response
                .created(URI.create("/parolees/" + parolee.getId()))
                .build();
    }

    /**
     * Attempts to update an existing Parolee. If the specified Parolee is
     * found it is updated, resulting in a HTTP 204 response being returned to
     * the consumer. In other cases, a 404 response is returned.
     *
     * @param id the unique id of the Parolee to update.
     * @param is the InputStream used to store an JSON representation of the
     *           new state for the Parolee.
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateParolee(@PathParam("id") long id, InputStream is) {
        Parolee update = readParolee(is);
        Parolee current = paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Update the details of the Parolee to be updated.
        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setGender(update.getGender());
        current.setDateOfBirth(update.getDateOfBirth());

        // Methods with no return type (i.e. void) will return HTTP 204 to the client.
    }

    /**
     * Attempts to delete an existing Parolee. If the specified Parolee isn't
     * found, a 404 response is returned to the consumer. In other cases, a 204
     * response is returned after deleting the Parolee.
     *
     * @param id the unique id of the Parolee to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteParolee(@PathParam("id") long id) {
        Parolee current = paroleeDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        // Remove the Parolee.
        paroleeDB.remove(id);
        LOGGER.info("Deleted parolee with ID: " + id);

        // Methods with no return type (i.e. void) will return HTTP 204 to the client.
    }

    /**
     * Deletes all Parolees. A 204 response is returned to the consumer.
     */
    @DELETE
    public void deleteAllParolees() {
        paroleeDB.clear();
        idCounter = new AtomicLong();
    }


    /**
     * Helper method to generate an JSON representation of a particular Parolee.
     *
     * @param os      the OutputStream used to write out the JSON.
     * @param parolee the Parolee for which to generate an JSON representation.
     * @throws IOException if an error is encountered in writing the JSON to the
     *                     OutputStream.
     */
    protected void outputParolee(OutputStream os, Parolee parolee)
            throws IOException {
        String dateOfBirth = parolee.getDateOfBirth().format(DATE_FORMATTER);

        String paroleeJson = String.format(
                PAROLEE_JSON_TEMPLATE,
                parolee.getId(),
                parolee.getFirstName(),
                parolee.getLastName(),
                parolee.getGender(),
                dateOfBirth);

        PrintStream writer = new PrintStream(os);
        writer.println(paroleeJson);
    }

    /**
     * Helper method to read an JSON representation of a Parolee, and return a
     * corresponding Parolee object.
     *
     * @param is the InputStream containing an JSON representation of the
     *           Parolee to create.
     * @return a new Parolee object.
     */
    protected Parolee readParolee(InputStream is) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode paroleeNode = mapper.readTree(is);

            Parolee parolee = new Parolee();
            if (paroleeNode.has("id")) {
                parolee.setId(paroleeNode.get("id").asLong());
            }
            parolee.setFirstName(paroleeNode.get("firstName").asText());
            parolee.setLastName(paroleeNode.get("lastName").asText());
            parolee.setGender(Gender.valueOf(paroleeNode.get("gender").asText()));
            parolee.setDateOfBirth(LocalDate.parse(paroleeNode.get("dateOfBirth").asText(), DATE_FORMATTER));

            return parolee;

        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }
    }

}
