package client;

import compute.RegistrationInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class ComputePi implements Runnable {
	
	private static Thread start = new Thread(new ComputePi());
	private static int x = 0;
    private static int iterator = 0;
    private final static Scanner scanner = new Scanner(System.in);
    private static boolean validOption = false;
    private static Random rand = new Random();
	private static int port = rand.nextInt(9999) + 1000;

	// Begins by spinning self on a new thread.  This allows multiple clients to be generatable.
	public static void main(String args[]) {
		start.start();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
    
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        Loop();
    }
	
	// Loop method is the primary body of the class.  Recalled from ChatClient when ChatClient exits.
	
	static void Loop() {
		
		//Ports are generated with a random value to prevent clients from conflicting with one another
		// when attempting socketing.  There is a check in the boolean method "available" to validate
		// that the random port assignments are not overlapping.

    	port = rand.nextInt(9999) + 1000;
		while(x != 4) {
			
	        //Start of Primary Try-Catch Block for Pi function.  Each selection binds a different 
			// task class to a Computation interface using RMI allowing each to instantiate
			// new threads as needed.
			
	        try {
	        	if (x == 0) {
	        	
	        	x = MainMenu(validOption, x, scanner);
	        	}
	        	
	        	if(x == 1) {
	        	x = DecimalPick(validOption, x);
	      	
	        	String y = Integer.toString(x);
	            //String name = "//" + "localhost" + "/Compute";
	            //Compute comp = (Compute) Naming.lookup(name);
	            Pi task = new Pi(Integer.parseInt(y));
	            BigDecimal pi = (BigDecimal) task.execute();
	            //BigDecimal pi = (BigDecimal) (comp.executeTask(task));
	            System.out.println("\nPi calculated to " + y + " decimals.\n" + pi + "\n");
	            x = 0;
	        	}
	        	
	        	if(x == 2) {
	        		iterator += 1;
	        		ArrayList<Integer> y = PrimePick(validOption, x, iterator);
	        		//String name = "//" + "localhost" + "/Compute";
	        		//Compute comp = (Compute) Naming.lookup(name);
	        		Primes task = new Primes(y);
					//Object[] primeresult = (Object[]) (comp.executeTask(task));
                    Object[] primeresult = (Object[]) (task.execute());
					if(Arrays.toString(primeresult).contains("Invalid entries.  Please try again.")) {
						System.out.println(Arrays.toString(primeresult));
						x = 2;
					}
					else {
					System.out.println("All prime numbers between " + y.toArray()[1] + " and " + y.toArray()[0] + 
							":\n" + Arrays.toString(primeresult) + "\n");
	        		x = 0;
	        		iterator = 0;
	        		
					}
	        	}
	        	
	        	if (x == 3) {
	        		String y = Username(validOption, x);
	        		//String name = "//" + "localhost" + "/ChatServer";
	        		boolean loginstatus = false;
	        		while(!available(port)) {
	        			port += 1;
	        		}
	        		//TODO: Handle login details differently.
	        		//PresenceService serve = (PresenceService) Naming.lookup(name);
	        		String hostname = InetAddress.getLocalHost().getHostName();
	        		RegistrationInfo registered = new RegistrationInfo(y, hostname, port, loginstatus);
	        		ChatClient client = new ChatClient(registered);
	        		new Thread(client).start();
	        		x = 0;
	        		return;

	        	}
	        	
	        	if (x == 4) {
	        		scanner.close();
	        		System.out.println("Terminating Application...\nPlease do not panic...\nApplication Successfully Terminated!\n");
	        		System.exit(port);
	        	}
	        } catch (Exception e) {
	            System.err.println("ComputePi exception: " + 
	                               e.getMessage());
	            e.printStackTrace();
	        }
	    }
	}
    
    
    private static int MainMenu(boolean a, int i, Scanner s) {
    	
   	 //Beginning of user prompt responses to menu of questions.
    	
      	while(!a) {
      	System.out.println("Which task would you like performed?"
      			+ "\nPlease enter a corresponding number:"
      			+ "\n1: Compute Pi"
      			+ "\n2: Compute Primes"
      			+ "\n3: Chat Client"
      			+ "\n4: Exit");
      	
      	// Try-Catch Block for catching invalid user entries for main menu.
      	
      	try {
			i = s.nextInt();
      	if(i <= 4) {
      		a = true;
      	}
      	
      	// Above returns true if range of values are acceptable.  Terminates loop. 
      	//Below catches error if value above option 3.
      	
      	else {
      		System.out.println("Invalid option.  Please try again.\n");
      		i = 0;
      		s.nextLine();
      	}
      	
      	// Below catches error if value is not an integer.
      	
      	}
      	catch(java.util.InputMismatchException e) {
      		System.out.println("Invalid option.  Please try again.\n");
      		a = false;
      		i = 0;
      		s.nextLine();
      	}
      	}
		return i;
      }
    
    private static int DecimalPick(boolean a, int i) {
    	a = false;
    	ComputePi.scanner.nextLine();
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	while(!a) {    		
        	System.out.println("COMPUTE PI:\n To how many decimal places do you want Pi computed?");
        	try {
				i = ComputePi.scanner.nextInt();
            	a = true;
            	
            	// Below catches error if value is not an integer.
            	
        	}
            	catch(java.util.InputMismatchException e) {
            		System.out.println("Invalid option.  Please try again.\n");
            		a = false;
            		i = 0;
            		ComputePi.scanner.nextLine();
            	}
    	}
    	return i;
    }
    
    private static ArrayList<Integer> PrimePick(boolean a, int i, int rep) {
    	a = false;
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	ArrayList<Integer> minmax = new ArrayList<Integer>();
    	
    	//iterator value "rep" used to assess if previous entry was invalid.  Prevents hanging from lack of user input.
    	
    	if(rep <= 1) {
    	ComputePi.scanner.nextLine();
    	}
    	while(!a) {    		
    		if(minmax.isEmpty()) {
        	System.out.println("COMPUTE PRIMES:\n What are the lower and upper bounds within "
        			+ "which you would like to search for prime numbers?");
        	System.out.println("\nChoose the upper bound.");
    		}
    		else {
    			System.out.println("\nChoose the lower bound.");
    		}
        	try {
				i = ComputePi.scanner.nextInt();
            	minmax.add(i);
            	if(minmax.size() >= 2) {
            		a = true;
            		ComputePi.scanner.nextLine();
            	}
            	
            	// Below catches error if value is not an integer.
            	
            	}
            	catch(java.util.InputMismatchException e) {
            		System.out.println("Invalid option.  Please try again.\n");
            		a = false;
            		i = 0;
            		ComputePi.scanner.nextLine();
            	}
    	}
    	return minmax;
    }
    
    private static String Username(boolean a, int i) {
    	a = false;
    	System.out.println("\nYou have chosen option " + i + "\n");
    	i = 0;
    	ComputePi.scanner.nextLine();
    	String username = null;
    	while(!a) {
    		System.out.println("CHAT CLIENT:\n Welcome to the AppEngiine CHAT!"
    				+ "  Please enter your preferred username.");
    		try {
    			username = ComputePi.scanner.nextLine();
    			a = true;
    		}
    		catch (java.util.InputMismatchException e) {
    			System.out.println("Something was wrong with this Username choice.  Please try again.\n");
    			a = false;
    			i = 0;
    			ComputePi.scanner.nextLine();
    		}
    	}
    	return username;
    	
    }
    
    private static boolean available(int port) {
        System.out.println("--------------Testing port " + port);
        Socket s = null;
        try {
            s = new Socket(InetAddress.getLocalHost().getHostName(), port);

            // If the code makes it this far without an exception it means
            // something is using the port and has responded.
            
            System.out.println("--------------Port " + port + " is not available");
            return false;
        } catch (IOException e) {
            System.out.println("--------------Port " + port + " is available");
            return true;
        } finally {
            if( s != null){
                try {
                    s.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unexpected socket error." , e);
                }
            }
        }
    }
}