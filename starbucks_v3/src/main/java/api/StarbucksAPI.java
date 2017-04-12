package api ;

import java.util.Collection ;
import java.util.concurrent.BlockingQueue ;
import java.util.concurrent.ConcurrentHashMap ;
import java.util.concurrent.LinkedBlockingQueue ;

public class StarbucksAPI {

    public enum OrderStatus { PLACED, PAID, PREPARING, SERVED, COLLECTED  }

    private static BlockingQueue<String> orderQueue = new LinkedBlockingQueue<String>();
    private static ConcurrentHashMap<String,Order> orders = new ConcurrentHashMap<String,Order>();

    public static void placeOrder(String order_id, Order order) {
        try { 
        	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

            StarbucksAPI.orderQueue.put( order_id ) ; 
        } catch (Exception e) {}
        StarbucksAPI.orders.put( order_id, order ) ;
        System.out.println(order);
        System.out.println( "Order Placed: " + order_id ) ;
    }

    public static void startOrderProcessor() {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        StarbucksBarista barista = new StarbucksBarista( orderQueue ) ;
        new Thread(barista).start();
    }

    public static void updateOrder(String key, Order order) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        StarbucksAPI.orders.replace( key, order ) ;
    }

    public static Order getOrder(String key) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        return StarbucksAPI.orders.get( key ) ;
    }

    public static void removeOrder(String key) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        StarbucksAPI.orders.remove( key ) ;
        StarbucksAPI.orderQueue.remove( key ) ;
    }

    public static void setOrderStatus( Order order, String URI, OrderStatus status ) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        switch ( status ) {
            case PLACED:
                order.status = OrderStatus.PLACED ;
                order.message = "Order has been placed." ;
                order.links.put ("order", URI+"/"+order.id ) ;
                order.links.put ("payment",URI+"/"+order.id+"/pay" ) ;
            break;
                    
            case PAID:
                order.status = StarbucksAPI.OrderStatus.PAID ;
                order.message = "Payment Accepted." ;
                order.links.remove ( "payment" ) ;
            break;                        
        }
    }

    public static void setOrderStatus( Order order, OrderStatus status ) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        switch ( status ) {
            case PREPARING: 
                order.status = StarbucksAPI.OrderStatus.PREPARING ;
                order.message = "Order preparations in progress." ;
                break;

            case SERVED: 
                order.status = StarbucksAPI.OrderStatus.SERVED ;
                order.message = "Order served, wating for Customer pickup." ;                   
                break;

            case COLLECTED: 
                order.status = StarbucksAPI.OrderStatus.COLLECTED ;
                order.message = "Order retrived by Customer." ;     
                break;   
        }
    }


    public static Collection<Order> getOrders() {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

        return orders.values() ;
    }

}


