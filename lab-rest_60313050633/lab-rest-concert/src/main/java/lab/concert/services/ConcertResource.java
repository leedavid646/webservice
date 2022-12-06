package lab.concert.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.annotations.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.common.Config;
import lab.concert.domain.Concert;

// TODO: add resource path and produce/consume
@Path("/concerts")
@Produces({MediaType.APPLICATION_JSON, SerializationMessageBodyReaderAndWriter.APPLICATION_JAVA_SERIALIZED_OBJECT})
@Consumes({MediaType.APPLICATION_JSON, SerializationMessageBodyReaderAndWriter.APPLICATION_JAVA_SERIALIZED_OBJECT})
public class ConcertResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    private Map<Long, Concert> concertDB = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong();


   //TODO: add verb & path
   @GET
   @Path("{id}")
    public Response retrieveConcert(@PathParam("id") long id,@CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
        Concert concert = concertDB.get(id);
        if (concert == null) {
            // Return a HTTP 404 response if the specified Concert isn't found.
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        ResponseBuilder builder = Response.ok(concert);
        NewCookie cookie = makeCookie(clientId);

        if (cookie != null) {
            builder.cookie(cookie);
        }

        return builder.build();
    }


    //TODO: add verb and add QueryParam at the parameter below
    @GET
    public Response retrieveConcerts(@QueryParam("start") long start,@QueryParam("size") int size, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {

        List<Concert> concerts = new ArrayList<>();

        for (long i = start; i <= size; i++) {
            Concert concert = concertDB.get(i);
            if (concert != null) {
                concerts.add(concert);
            }
        }

        GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {
        };
        ResponseBuilder builder = Response.ok(entity);

        NewCookie cookie = makeCookie(clientId);

        if (cookie != null) {
            builder.cookie(cookie);
        }
        return builder.build();
    }


    //TODO: add verb
    @POST
    public Response createConcert(Concert concert, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
    	LOGGER.info("createConcert is called");
    	Long id = idCounter.incrementAndGet();
        Concert newConcert = new Concert(id, concert.getTitle(), concert.getDate());

        concertDB.put(id, newConcert);

        NewCookie cookie = makeCookie(clientId);

        ResponseBuilder builder = Response.created(URI.create("/concerts/" + id));
        if (cookie != null) {
            builder.cookie(cookie);
        }
        return builder.build();
    }


    //TODO: add verb
    @DELETE
    public Response deleteAllConcerts(@CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
        concertDB.clear();
        idCounter = new AtomicLong();

        NewCookie cookie = makeCookie(clientId);

        ResponseBuilder builder = Response.status(204);

        if (cookie != null) {
            builder.cookie(cookie);
        }

        return builder.build();
    }

    /**
     * Helper method that can be called from every service method to generate a
     * NewCookie instance, if necessary, based on the clientId parameter.
     *
     * @param clientId the Cookie whose name is Config.CLIENT_COOKIE, extracted
     *                 from a HTTP request message. This can be null if there was no cookie
     *                 named Config.CLIENT_COOKIE present in the HTTP request message.
     * @return a NewCookie object, with a generated UUID value, if the clientId
     * parameter is null. If the clientId parameter is non-null (i.e. the HTTP
     * request message contained a cookie named Config.CLIENT_COOKIE), this
     * method returns null as there's no need to return a NewCookie in the HTTP
     * response message.
     */
    private NewCookie makeCookie(Cookie clientId) {
        NewCookie newCookie = null;

        if (clientId == null) {
            newCookie = new NewCookie(Config.CLIENT_COOKIE, UUID.randomUUID().toString());
            LOGGER.info("Generated cookie: " + newCookie.getValue());
        }
        return newCookie;
    }
}