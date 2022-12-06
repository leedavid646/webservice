package lab.end2end.concert.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
import lab.end2end.concert.domain.Concert;
import lab.end2end.concert.domain.Genre;
import lab.end2end.concert.domain.Performer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: add proper path and produce/consumes for JSON
@Path("/concerts")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class ConcertResource {

  private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

  //TODO: add @GET and @Path
  @GET
  @Path("{id}")
  public Response retrieveConcert(@PathParam("id") long id) { //TODO: add @PathParam for id
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
      // Start a transaction for persisting the audit data.
      em.getTransaction().begin();

      //TODO: remove =null below and find concert by ID suing em.find(...
      Concert concert = em.find(Concert.class, id);
      em.getTransaction().commit();

      if (concert == null) {
        // Return a HTTP 404 response if the specified Concert isn't found.
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }

      //TODO: remove return null below, return concert using Response.ok
      ResponseBuilder builder = Response.ok(concert);
      return builder.build();
    } finally {
      em.close();
    }
  }

  //TODO: add proper annotation Post verb
  @POST
  public Response createConcert(Concert concert) {
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
      em.getTransaction().begin();
      //TODO save concert to database using em.persist(..
      em.persist(concert);
      em.getTransaction().commit();

      //TODO: remove return null below, return Response.created(..
      ResponseBuilder builder = Response.created(
        URI.create("/concerts/" + concert.getId())
      );
      return builder.build();
    } finally {
      em.close();
    }
  }

  //TODO: add proper annotation Put verb
  @PUT
  public Response updateConcert(Concert concert) {
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
      em.getTransaction().begin();
      //TODO update concert using em.merge(..
      em.merge(concert);

      em.getTransaction().commit();

      //TODO: remove return null below, return Response.status(.. for NO_CONTENT
      ResponseBuilder builder = Response.status(204);
      return builder.build();
    } finally {
      em.close();
    }
  }

  //TODO: add annotation for Delete verb and and @Path for id
  @DELETE
  @Path("{id}")
  public Response delete(@PathParam("id") long id) { //TODO: add @PathParam for id
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
      em.getTransaction().begin();
      //TODO: remove =null below, find concert by given id using em.find
      Concert concert = em.find(Concert.class, id);

      if (concert == null) {
        // Return a HTTP 404 response if the specified Concert isn't found.
        throw new WebApplicationException(Response.Status.NOT_FOUND);
      }

      //TODO: delete concert using em.remove
      em.remove(concert);
      em.getTransaction().commit();

      //TODO: remove return null below, return Response.status(.. for NO_CONTENT
      ResponseBuilder builder = Response.status(204);
      return builder.build();
    } finally {
      em.close();
    }
  }

  //TODO: add annotation for Delete verb
  @DELETE
  public Response deleteAllConcerts() {
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
      em.getTransaction().begin();

      //TODO: query to get all concerts into a list using guideline in the reference
    
    TypedQuery<Concert> concertQuery = em.createQuery("select c from Concert c", Concert.class);
    List<Concert> concerts = concertQuery.getResultList();

      //TODO: make for loop through to remove all concert using em.remove
      for(int i = 0 ; i<concerts.size();i++  ){
        em.remove(concerts.get(i));
      }

      em.getTransaction().commit();

      //TODO: remove return null below, return Response.status(.. for NO_CONTENT
      ResponseBuilder builder = Response.status(204);
      return builder.build();
    } finally {
      em.close();
    }
  }
}
