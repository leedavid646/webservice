package lab.concert.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

//TODO: add application path
@ApplicationPath("/services")
public class ConcertApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> classes = new HashSet<>();

    public ConcertApplication() {
        // TODO: add registration to resource class and
        // SerializationMessageBodyReaderAndWriter+
        singletons.add(new ConcertResource());
        classes.add(SerializationMessageBodyReaderAndWriter.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
