package api ;

import org.json.* ;
import org.restlet.representation.* ;
import org.restlet.ext.json.* ;
import org.restlet.resource.* ;
import org.restlet.ext.jackson.* ;

import java.io.IOException ;

public class PingResource extends ServerResource {

    @Get
    public Representation get_action (Representation rep) throws IOException {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        api.Status api = new api.Status() ;
        api.status = "OK" ;
        api.message = "Starbucks API Service: Version 3" ;
        return new JacksonRepresentation<api.Status>(api) ;

    }


}


