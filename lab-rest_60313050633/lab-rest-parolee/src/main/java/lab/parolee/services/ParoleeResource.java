package lab.parolee.services;

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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.parolee.domain.Parolee;

/**
 * Class to implement a simple REST Web service for managing parolees.
 * <p>
 * ParoleeResource implements a WEB service with the following interface:
 * <p>
 * - GET <base-uri>/parolees/{id} Retrieves a parolee based on their unique id.
 * The format of the returned data is JSON.
 * <p>
 * - POST <base-uri>/parolees Creates a new Parolee. The HTTP post message
 * contains an JSON representation of the parolee to be created.
 * <p>
 * - PUT <base-uri>/parolees/{id} Updates a parolee, identified by their id.The
 * HTTP PUT message contains an JSON document describing the new state of the
 * parolee.
 * <p>
 * - DELETE <base-uri>/parolees/{id} Deletes a parolee, identified by their
 * unique id.
 * <p>
 * - DELETE <base-uri>/parolees Deletes all parolees.
 */
// TODO: add path and consume/produce
@Path("/parolees")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class ParoleeResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ParoleeResource.class);

	private Map<Long, Parolee> paroleeDB = new ConcurrentHashMap<>();
	private AtomicLong idCounter = new AtomicLong();

	// TODO: add verb & path
	@GET
	@Path("{id}")
	public Response retrieveParolee(@PathParam("id") long id) {
		LOGGER.info("Retrieving parolee with id: " + id);
		// Lookup the Parolee within the in-memory data structure.
		final Parolee parolee = paroleeDB.get(id);
		if (parolee == null) {
			// Return a HTTP 404 response if the specified Parolee isn't found.
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		}
		// TODO: remove return null below and add proper return
		ResponseBuilder builder = Response.ok(parolee);
		return builder.build();
	}

	// TODO: add verb
	@POST
	public Response createParolee(Parolee parolee) {

		// Generate an ID for the new Parolee, and store it in memory.
		parolee.setId(idCounter.incrementAndGet());
		paroleeDB.put(parolee.getId(), parolee);

		LOGGER.debug("Created parolee with id: " + parolee.getId());

		// TODO: remove return null below and add proper return
		ResponseBuilder builder = Response.created(URI.create("/parolees/" + parolee.getId()));
		return builder.build();
	}

	/**
	 * Attempts to update an existing Parolee. If the specified Parolee is found it
	 * is updated, resulting in a HTTP 204 response being returned to the consumer.
	 * In other cases, a 404 response is returned.
	 *
	 * @param id the unique id of the Parolee to update.
	 * @param is the InputStream used to store an JSON representation of the new
	 *           state for the Parolee.
	 */
	// TODO: add verb & path
	@PUT
	@Path("{id}")
	public Response updateParolee(@PathParam("id") long id, Parolee parolee) {
		Parolee current = paroleeDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Update the details of the Parolee to be updated.
		current.setFirstName(parolee.getFirstName());
		current.setLastName(parolee.getLastName());
		current.setGender(parolee.getGender());
		current.setDateOfBirth(parolee.getDateOfBirth());

		// TODO: remove return null below and add proper return
		ResponseBuilder builder = Response.status(204);
		return builder.build();
	}

	// TODO: add verb & path
	@DELETE
	@Path("{id}")
	public Response deleteParolee(@PathParam("id")long id) {
		Parolee current = paroleeDB.get(id);
		if (current == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		// Remove the Parolee.
		paroleeDB.remove(id);
		LOGGER.info("Deleted parolee with ID: " + id);

		ResponseBuilder builder = Response.status(204);
		return builder.build();
	}

	/**
	 * Deletes all Parolees. A 204 response is returned to the consumer.
	 */
	// TODO: add verb
	@DELETE
	public Response deleteAllParolees() {
		paroleeDB.clear();
		idCounter = new AtomicLong();

		// TODO: remove return null below and add proper return
		ResponseBuilder builder = Response.status(204);
		return builder.build();
	}

}
