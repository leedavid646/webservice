package lab.servlet.concert.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
	public class ConcertApplication extends Application {
	    private Set<Object> singletons = new HashSet<>();
		private Set<Class<?>> classes = new HashSet<>();
		
	    public ConcertApplication() {
	        singletons.add(new ConcertResource());
	        classes.add(SerializationMessageBodyReaderAndWriter.class);
	    }

		@Override
		public Set<Class<?>> getClasses(){
			return classes;
			//TestTest
		}
		
	    @Override
	    public Set<Object> getSingletons() {
	        // Return a Set containing an instance of ParoleeResource that will be
	        // used to process all incoming requests on Parolee resources.
	        return singletons;
	    }
	    
	    
}
