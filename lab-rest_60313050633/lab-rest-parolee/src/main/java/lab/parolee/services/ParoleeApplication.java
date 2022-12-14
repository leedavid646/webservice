package lab.parolee.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application subclass for the Parolee Web service. This class is
 * discovered by the JAX-RS run-time and is used to obtain a reference to the
 * ParoleeResource object that will process Web service requests.
 * <p>
 * The base URI for the Parolee Web service is:
 * <p>
 * http://<host-name>:<port>/services.
 */
// TODO: add application path
@ApplicationPath("/services")
public class ParoleeApplication extends Application {
    private Set<Object> singletons = new HashSet<>();

    public ParoleeApplication() {
        // TODO: register resource here
        singletons.add(new ParoleeResource());

    }

    @Override
    public Set<Object> getSingletons() {
        // Return a Set containing an instance of ParoleeResource that will be
        // used to process all incoming requests on Parolee resources.
        return singletons;
    }
}
