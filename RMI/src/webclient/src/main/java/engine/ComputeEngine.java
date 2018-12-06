package engine;

//import main..java.compute.Compute;
import compute.Task;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class ComputeEngine extends UnicastRemoteObject
                           //implements Compute
{


	/**
	 * This class handles the binding of the shared Compute interface to the ComputeEngine server object.
	 */
	private static final long serialVersionUID = -6020339579736078592L;

	public ComputeEngine() throws RemoteException {
        super();
    }

    public Object executeTask(Task t) {
        return t.execute();
    }

    public static void main(String[] args) {


        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }


        // the original example had "//host/Compute" here, which
        // has to be replaced with a real host name. 

        String name = "//localhost/Compute";
        try {
            //Compute engine = new ComputeEngine();

        // Below creates an accessible registry to bind the remote objects to.  MANDATORY to work correctly.

            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            //Naming.rebind(name, engine);

       // Below pulls IP information to help with troubleshooting as part of the binding process.

            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("ComputeEngine bound:\nport: " + Registry.REGISTRY_PORT + "\nIP: " + ip);
            Object o = new Object();

            synchronized (o) {
                o.wait();
            }
        } catch (Exception e) {
            System.err.println("ComputeEngine exception: " +
			       e.getMessage());
            e.printStackTrace();
        }


    }
}