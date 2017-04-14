package api;

import java.io.IOException;

import org.json.JSONException;
import org.restlet.data.Header;
import org.restlet.data.Tag;
import org.restlet.ext.crypto.DigestUtils;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import api.StarbucksAPI.OrderStatus;

public class OrderResource extends ServerResource {
	MongoDBJDBC mongoDBJDBC = new MongoDBJDBC();

	@Get
	public Representation get_action() throws JSONException {

		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		Series<Header> headers = (Series<Header>) getRequest().getAttributes().get("org.restlet.http.headers");
		if (headers != null) {
			String etag = headers.getFirstValue("If-None-Match");
			System.out.println("HEADERS: " + headers.getNames());
			System.out.println("ETAG: " + etag);
		}

		String order_id = getAttribute("order_id");
		System.out.println("order_id: " + order_id);
		// Order order = StarbucksAPI.getOrder(order_id);

		if (order_id == null || order_id.equals("")) {

			setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Order not found.";

			return new JacksonRepresentation<api.Status>(api);
		} else {
			// Order existing_order = StarbucksAPI.getOrder(order_id);
			System.out.println("---existing_order--:");
			if (order_id == null || order_id.equals("")) {
				System.out.println("---order_id--:" + order_id);
				setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
				api.Status api = new api.Status();
				api.status = "error";
				api.message = "Order not found.";
				return new JacksonRepresentation<api.Status>(api);
			} else {

				try {

					Representation orderReps = mongoDBJDBC.retrieveOrder(order_id);
					System.out.println("--get request-----");
					JacksonRepresentation<Order> resultRep = new JacksonRepresentation<Order>(orderReps, Order.class);

					Order orders = resultRep.getObject();
					System.out.println("--get request-----" + orders.id);
					Representation result = new JacksonRepresentation<Order>(orders);

					System.out.println("Get Text: " + result.getText());
					String hash = DigestUtils.toMd5(result.getText());
					System.out.println("Get Hash: " + hash);
					result.setTag(new Tag(hash));
					return result;
				} catch (Exception e) {
					setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
					api.Status api = new api.Status();
					api.status = "error";
					api.message = "Server Error, Try Again Later.";
					return new JacksonRepresentation<api.Status>(api);
				}
			}
		}
	}

	@Post
	public Representation post_action(Representation rep) throws IOException {

		JacksonRepresentation<Order> orderRep = new JacksonRepresentation<Order>(rep, Order.class);
		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		Order order = orderRep.getObject();

		StarbucksAPI.setOrderStatus(order, getReference().toString(), StarbucksAPI.OrderStatus.PLACED);
		// StarbucksAPI.placeOrder(order.id, order);

		Representation result = new JacksonRepresentation<Order>(order);
		try {
			System.out.println("Text: " + result.getText());

			String hash = DigestUtils.toMd5(result.getText());

			result.setTag(new Tag(hash));
			mongoDBJDBC.insertOrder(result);
			StarbucksAPI.orderQueue.put(order.id);

			return result;
		} catch (IOException | InterruptedException e) {
			setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Server Error, Try Again Later.";
			return new JacksonRepresentation<api.Status>(api);
		}
	}

	@Put
	public Representation put_action(Representation rep) throws IOException {
		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		JacksonRepresentation<Order> orderRep = new JacksonRepresentation<Order>(rep, Order.class);
		Order order = orderRep.getObject();

		String order_id = getAttribute("order_id");
		order.id = order_id;

		System.out.println("----order_id------:" + order_id);
		// Order existing_order = StarbucksAPI.getOrder(order_id);

		if (order_id == null || order_id.equals("")) {

			setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Order not found.";

			return new JacksonRepresentation<api.Status>(api);

		} else {

			StarbucksAPI.setOrderStatus(order, getReference().toString(), StarbucksAPI.OrderStatus.PLACED);
			// order.id = existing_order.id;
			// StarbucksAPI.updateOrder(order.id, order);
			Representation result = new JacksonRepresentation<Order>(order);
			System.out.println("----before update--- :" + result.getText());

			Representation orderReps = mongoDBJDBC.updateOrder(result, order_id);
			JacksonRepresentation<Order> resultRep = new JacksonRepresentation<Order>(orderReps, Order.class);
			Order orders = resultRep.getObject();
			result = new JacksonRepresentation<Order>(orders);

			try {
				System.out.println("Text: " + result.getText());
				String hash = DigestUtils.toMd5(result.getText());
				result.setTag(new Tag(hash));
				return result;
			} catch (IOException e) {
				setStatus(org.restlet.data.Status.SERVER_ERROR_INTERNAL);
				api.Status api = new api.Status();
				api.status = "error";
				api.message = "Server Error, Try Again Later.";
				return new JacksonRepresentation<api.Status>(api);
			}
		}
	}

	@Delete
	public Representation delete_action(Representation rep) throws IOException {
		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		String order_id = getAttribute("order_id");
		// Order existing_order = StarbucksAPI.getOrder(order_id);

		if (order_id == null || order_id.equals("")) {

			setStatus(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Order not found.";

			return new JacksonRepresentation<api.Status>(api);

		} else {

			StarbucksAPI.removeOrder(order_id);
			System.out.println("deleteing the order with id :" + order_id);

			mongoDBJDBC.deleteOrder(order_id);

			setStatus(org.restlet.data.Status.SUCCESS_NO_CONTENT);
			api.Status api = new api.Status();
			api.status = "Success";
			api.message = "order deleted successfully";

			StarbucksAPI.orderQueue.remove(order_id);

			return new JacksonRepresentation<api.Status>(api);

		}

	}

}