
package api ;

import java.util.concurrent.BlockingQueue;

public class StarbucksBarista implements Runnable {

	protected BlockingQueue<String> blockingQueue ;

	public StarbucksBarista(BlockingQueue<String> queue) {
    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

		this.blockingQueue = queue;
	}

	@Override
	public void run() {
		while (true) {
	    	System.out.println( "#### class Name ### :" + Thread.currentThread().getStackTrace()[1].getClassName() + " #### Method Name ###: " + Thread.currentThread().getStackTrace()[1].getMethodName() ) ;

			try {
				try { Thread.sleep(5000); } catch ( Exception e ) {}  
				String order_id = blockingQueue.take();
				Order order = StarbucksAPI.getOrder( order_id ) ;
				System.out.println( "#### class Name ### : order:" +order.status);
        		if ( order != null && order.status == StarbucksAPI.OrderStatus.PAID ) {
					System.out.println(Thread.currentThread().getName() + " Processed Order: " + order_id );
					StarbucksAPI.setOrderStatus( order, StarbucksAPI.OrderStatus.PREPARING ) ;
					try { Thread.sleep(20000); } catch ( Exception e ) {}  
					StarbucksAPI.setOrderStatus( order, StarbucksAPI.OrderStatus.SERVED ) ;					
					try { Thread.sleep(10000); } catch ( Exception e ) {}  
					StarbucksAPI.setOrderStatus( order, StarbucksAPI.OrderStatus.COLLECTED ) ;				
				}
				else {
					blockingQueue.put( order_id ) ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}