package api;

import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBJDBC {

	public static MongoClient mongoClient = null;
	public static DB db = null;
	public static DBCollection mongoCollection = null;

	public MongoDBJDBC() {

		// To connect to mongodb server
		mongoClient = new MongoClient("localhost", 27017);

		// Now connect to your databases
		db = mongoClient.getDB("test");
		System.out.println("Connect to database successfully");

		mongoCollection = db.getCollection("starbucks");
		System.out.println("Collection starbucks selected successfully");

	}

	public void insertOrder(Representation order) {

		try {

			BasicDBObject basicDBO = new BasicDBObject("order", com.mongodb.util.JSON.parse(order.getText()));

			mongoCollection.insert(basicDBO);
			System.out.println("Document inserted successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public Representation retrieveOrder(String orderId) {

		String currentOrderStirng = "";
		Representation result = null;

		try {

			BasicDBObject mongoQuery = new BasicDBObject();
			mongoQuery.put("order.id", orderId);

			DBCursor cursor = mongoCollection.find(mongoQuery);

			System.out.println("Document retrieved successfully cursor:" + cursor);

			while (cursor.hasNext()) {
				System.out.println("while loop:");
				DBObject eventDBO = cursor.next();
				String currentOrder = eventDBO.get("order").toString();
				// Order o = (Order)eventDBO.get("order");

				currentOrderStirng = currentOrderStirng + currentOrder + ",";

				System.out.println("currentOrderStirng = " + currentOrderStirng);
			}
			result = new StringRepresentation(currentOrderStirng);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Server Error, Try Again Later.";
			return new JacksonRepresentation<api.Status>(api);
		}
		return result;
	}

	public void deleteOrder(String orderId) {

		BasicDBObject mongoQuery = new BasicDBObject();
		mongoQuery.put("order.id", orderId);

		System.out.println("Delete ------>:" + mongoCollection.remove(mongoQuery));

	}

	public Representation retrieveOrders() {
		String currentOrderStirng = "";
		Representation result = null;
		int i =1;

		try {

			BasicDBObject mongoQuery = new BasicDBObject();

			DBCursor cursor = mongoCollection.find(mongoQuery);

			System.out.println("All Document retrieved successfully cursor:" + cursor);

			while (cursor.hasNext()) {
				System.out.println("while loop: count :"+i);
				DBObject eventDBO = cursor.next();
				String currentOrder = eventDBO.get("order").toString();
				// Order o = (Order)eventDBO.get("order");

				currentOrderStirng = currentOrderStirng + currentOrder + ",";

				System.out.println("currentOrderStirng = " + currentOrderStirng);
				i++;
			}
			result = new StringRepresentation(currentOrderStirng);

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());

			api.Status api = new api.Status();
			api.status = "error";
			api.message = "Server Error, Try Again Later.";
			return new JacksonRepresentation<api.Status>(api);
		}
		return result;

	}

}
