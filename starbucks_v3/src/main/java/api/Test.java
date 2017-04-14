package api;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Test {

	public static void main(String args[]) {

		try {

			MongoDBJDBC mongo = new MongoDBJDBC();
			//mongo.deleteOrder("b1209487-2cd1-4e35-9520-7ab1224a16e4");
			//mongo.retrieveOrder("e30e9a32-9443-45c6-ac03-35b85c47889e");
			
			
			MongoClient mongoClient = new MongoClient("localhost", 27017);

			DB db = mongoClient.getDB("test");
			System.out.println("Connect to database successfully");

//			Document doc1 = db.runCommand(new Document("$eval", "update_Order_Status(b9e8641d-0852-4523-ba68-b9526d52d85a)"));
//			System.out.println(doc1);
			String order_id = "55c6f590-6bcd-4d07-8b01-72129605d096";
			String orderId = "update_Order_Status('"+order_id+"')";
			//update_Order_Status('883ecc14-7f8f-4bfb-a171-03d6954c9c38')
			
			
			db.doEval(orderId).toString();
			mongoClient.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
