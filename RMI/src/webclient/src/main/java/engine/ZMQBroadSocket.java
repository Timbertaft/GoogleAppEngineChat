package engine;

import org.zeromq.ZMQ;

//
//  Weather update server in Java
//  Binds PUB socket to tcp://*:5556
//  Publishes random weather updates
//
public class ZMQBroadSocket {

    private static String message;


    void SetMessage(String msg) {
        ZMQBroadSocket.message = msg;
    }

    ZMQBroadSocket() {

    }

    void initialize() throws InterruptedException {
        //  Prepare our context and publisher
        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket publisher = context.socket(ZMQ.PUB);
        publisher.bind("tcp://*:5556");
        publisher.bind("ipc://broadcast");
        boolean truth = true;

            while (truth) {
                if(ZMQBroadSocket.message != null)
                    if(!ZMQBroadSocket.message.isEmpty()) {

                        publisher.send(ZMQBroadSocket.message, 0);
                        ZMQBroadSocket.message = "";
                    }
                    Thread.sleep(1000);
                }
        publisher.close ();
        context.term ();
            }

    }
