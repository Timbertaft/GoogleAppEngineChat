package client;
import org.zeromq.ZMQ;
import compute.RegistrationInfo;

/**
 * Hello world!
 *
 */
public class SocketServerZMQ implements Runnable
{
	//    public static void main( String[] args )
//    {
//        Syste m.out.println( "Hello World!" );
//    }
	private static RegistrationInfo connection;

	SocketServerZMQ(RegistrationInfo userconnect) {

		SocketServerZMQ.connection = userconnect;
	}
	public void run() {
		ZMQ.Context context = ZMQ.context(1);

		//  Socket to talk to clients
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		responder.bind("tcp://*:" + SocketServerZMQ.connection.getPort());

		while (!Thread.currentThread().isInterrupted()) {
			// Wait for next request from the webclient.client
			byte[] request = responder.recv(0);
			System.out.println("Received " + new String (request));

			// Do some 'work'
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Send reply back to webclient.client
			String reply = "Message Sent!";
			responder.send(reply.getBytes(), 0);
		}
		responder.close();
		context.term();
	}
}
