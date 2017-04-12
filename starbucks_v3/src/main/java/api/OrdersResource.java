package api ;

import java.io.IOException ;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class OrdersResource extends ServerResource {
	public MongoDBJDBC mongoDBJDBC = new MongoDBJDBC();
    @Get
    public Representation get_action (Representation rep) throws IOException {
        //Collection<Order> orders = StarbucksAPI.getOrders() ;
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;
    	
    	
    	Representation orders = mongoDBJDBC.retrieveOrders();
    	Representation result = new JacksonRepresentation<Order>(orders, Order.class);
        return result ;           
    }


}


