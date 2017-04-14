
package api;

import java.util.concurrent.BlockingQueue;

import org.bson.Document;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class StarbucksBarista implements Runnable {

	protected BlockingQueue<String> blockingQueue;
	public MongoDBJDBC mongoDBJDBC = new MongoDBJDBC();

	public StarbucksBarista(BlockingQueue<String> queue) {
		System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
				+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

		this.blockingQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
			System.out.println("#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName()
					+ " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName());

			try {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}
				String order_id = blockingQueue.take();
				System.out.println("------order_id------> :"+ order_id);

				// Order order = StarbucksAPI.getOrder( order_id ) ;
				Representation orderReps = mongoDBJDBC.retrieveOrder(order_id);
				JacksonRepresentation<Order> orderRep = new JacksonRepresentation<Order>(orderReps, Order.class);
				Order order = orderRep.getObject();

				System.out.println("#### class Name ### : order:" + order.status);
				if (order != null && order.status == StarbucksAPI.OrderStatus.PAID) {
					// System.out.println(Thread.currentThread().getName() + "
					// Processed Order: " + order_id );
					// StarbucksAPI.setOrderStatus( order,
					// StarbucksAPI.OrderStatus.PREPARING ) ;
					// try { Thread.sleep(20000); } catch ( Exception e ) {}
					// StarbucksAPI.setOrderStatus( order,
					// StarbucksAPI.OrderStatus.SERVED ) ;
					// try { Thread.sleep(10000); } catch ( Exception e ) {}
					// StarbucksAPI.setOrderStatus( order,
					// StarbucksAPI.OrderStatus.COLLECTED ) ;

					MongoClient mongoClient = new MongoClient("localhost", 27017);

					MongoDatabase db = mongoClient.getDatabase("test");
					System.out.println("Connect to database successfully");

					Document doc1 = db.runCommand(new Document("$eval", "update_Order_Status(bad03ca9-abed-4c0f-8f65-1ead6698a870)"));
					System.out.println(doc1);
					mongoClient.close();

				} else {
					blockingQueue.put(order_id);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}