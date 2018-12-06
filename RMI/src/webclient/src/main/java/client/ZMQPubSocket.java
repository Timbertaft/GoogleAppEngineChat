package client;
import org.zeromq.ZMQ;
import compute.RegistrationInfo;

public class ZMQPubSocket implements Runnable {

    private static RegistrationInfo connection;

    ZMQPubSocket(RegistrationInfo userconnect) {

        ZMQPubSocket.connection = userconnect;
    }

    @Override
    public void run() {
            ZMQ.Context context = ZMQ.context(1);

            //  Socket to talk to server
            ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            subscriber.subscribe(ZMQ.SUBSCRIPTION_ALL);
            subscriber.connect("tcp://localhost:5556");
        while (!Thread.currentThread().isInterrupted()) {


            subscriber.unsubscribe(ZMQPubSocket.connection.getUserName().getBytes());
            String string = subscriber.recvStr(0).trim();
                if(!string.isEmpty() && !string.contains(ZMQPubSocket.connection.getUserName()) &&
                        ZMQPubSocket.connection.getStatus()) {
                    System.out.println(string);
                }



        }
        //System.out.println("exited the loop");
        //subscriber.close();
        //context.term();
    }
}
