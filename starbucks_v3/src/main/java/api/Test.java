package api;

public class Test {

	public static void main(String args[]) {

		try {

			MongoDBJDBC mongo = new MongoDBJDBC();
			mongo.deleteOrder("b1209487-2cd1-4e35-9520-7ab1224a16e4");

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

}
