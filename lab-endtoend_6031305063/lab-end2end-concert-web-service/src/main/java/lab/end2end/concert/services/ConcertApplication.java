package lab.end2end.concert.services;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//TODO: add application path
@ApplicationPath("/services")
public class ConcertApplication extends Application {

  private Set<Object> singletons = new HashSet<>();
  private Set<Class<?>> classes = new HashSet<>();

  public ConcertApplication() {
    //TODO: registration of persistence manager and resource class
    singletons.add(PersistenceManager.instance());
    classes.add(ConcertResource.class);
  }

  @Override
  public Set<Object> getSingletons() {
    return singletons;
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
