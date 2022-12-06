package lab.servlet.concert.services;

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
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lab.servlet.concert.common.Config;
import lab.servlet.concert.domain.Concert;

/**
 * Class to implement a simple REST Web service for managing Concerts.
 * <p>
 * ConcertResource implements a WEB service with the following interface:
 * - GET    <base-uri>/concerts/{id}
 * Retrieves a Concert based on its unique id. The HTTP response
 * message has a status code of either 200 or 404, depending on
 * whether the specified Concert is found.
 * <p>
 * - GET    <base-uri>/concerts?start&size
 * Retrieves a collection of Concerts, where the "start" query
 * parameter identifies an index position, and "size" represents the
 * max number of successive Concerts to return. The HTTP response
 * message returns 200.
 * <p>
 * - POST   <base-uri>/concerts
 * Creates a new Concert. The HTTP post message contains a
 * representation of the Concert to be created. The HTTP Response
 * message returns a Location header with the URI of the new Concert
 * and a status code of 201.
 * <p>
 * - DELETE <base-uri>/concerts
 * Deletes all Concerts, returning a status code of 204.
 * <p>
 * Where a HTTP request message doesn't contain a cookie named clientId
 * (Config.CLIENT_COOKIE), the Web service generates a new cookie, whose value
 * is a randomly generated UUID. The Web service returns the new cookie as part
 * of the HTTP response message.
 */
@Path("/concerts")
public class ConcertResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    private Map<Long, Concert> concertDB = new ConcurrentHashMap<>();
    private AtomicLong idCounter = new AtomicLong();


    @GET
    @Path("{id}")
    @Produces("application/java-serialization")
    public Response retrieveConcert(@PathParam("id") long id, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
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

    @GET
    @Produces ("application/java-serialization")
    public Response retrieveConcerts(@QueryParam("start") long start, @QueryParam("size") int size, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
       
    	List<Concert> concerts = new ArrayList<>();
    	
    	for (long i = start; i <= size; i++) {
    		Concert concert = concertDB.get(i);
    		if (concert != null) {
    			concerts.add(concert);
    		}
    	}
    	
    	GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
    	ResponseBuilder builder = Response.ok(entity);
    	
    	NewCookie cookie = makeCookie(clientId);

         if (cookie != null) {
             builder.cookie(cookie);
         }

         return builder.build();
    	
    	// The Response object should store an ArrayList<Concert> entity. The
        // ArrayList can be empty depending on the start and size arguments,
        // and Concerts stored.
        //
        // Because of type erasure with Java Generics, any generically typed
        // entity needs to be wrapped by a javax.ws.rs.core.GenericEntity that
        // stores the generic type information. Hence to add an ArrayList as a
        // Response object's entity, you should use the following code:
        //
        // List<Concert> concerts = new ArrayList<Concert>();
        // GenericEntity<List<Concert>> entity = new GenericEntity<List<Concert>>(concerts) {};
        // ResponseBuilder builder = Response.ok(entity);
    }

    @POST
    @Consumes ("application/java-serialization")
    public Response createConcert(Concert concert, @CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {

        // Reminder: You won't need to annotate the "concert" argument above - arguments without annotations are
        // assumed by JAX-RS to come from the HTTP request body.
    	long id = idCounter.incrementAndGet();
    	Concert newconcert = new Concert (id, concert.getTitle(), concert.getDate());
    	concertDB.put(newconcert.getId(), newconcert);
    	
    	ResponseBuilder builder = Response.created(URI.create("/concerts/" + newconcert.getId()));
    	
    	NewCookie cookie = makeCookie(clientId);
    	
    	if (cookie != null) {
    		builder.cookie(cookie);
    	}
        
    	return builder.build();
    }

    @DELETE
    public Response deleteAllConcerts(@CookieParam(Config.CLIENT_COOKIE) Cookie clientId) {
    	concertDB.clear();
    	idCounter = new AtomicLong();
    	ResponseBuilder builder = Response.status(204);
    	
    	NewCookie cookie = makeCookie(clientId);
    	
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