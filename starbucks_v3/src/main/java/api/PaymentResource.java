package api;

import java.io.IOException;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class PaymentResource extends ServerResource {
	public MongoDBJDBC mongoDBJDBC = new MongoDBJDBC();
	public Representation orderReps = null;
	public JacksonRepresentation<Order> resultRep = null;

	@Post
	public Representation post_action(Representation rep) throws IOException {
		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		String order_id = getAttribute("order_id");
		// Order order = StarbucksAPI.getOrder( order_id ) ;
		orderReps = mongoDBJDBC.retrieveOrder(order_id);

		resultRep = new JacksonRepresentation<Order>(orderReps, Order.class);
		Order order = resultRep.getObject();

		if (order == null) {
			setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Order Not Found";
			return new JacksonRepresentation<api.Status>(api);
		}
		if (order != null && order.status != StarbucksAPI.OrderStatus.PLACED) {
			setStatus(org.restlet.data.Status.CLIENT_ERROR_PRECONDITION_FAILED);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Order Payment Rejected";
			return new JacksonRepresentation<api.Status>(api);
		} else {

			order.status = StarbucksAPI.OrderStatus.PAID;
			order.id = order_id;

			Representation result = new JacksonRepresentation<Order>(order);
			StarbucksAPI.setOrderStatus(order, getReference().toString(), StarbucksAPI.OrderStatus.PAID);

			orderReps = mongoDBJDBC.updateOrder(result, order_id);
			resultRep = new JacksonRepresentation<Order>(orderReps, Order.class);
			Order orders = resultRep.getObject();
			result = new JacksonRepresentation<Order>(orders);
			return result;
			
		}

	}

}
