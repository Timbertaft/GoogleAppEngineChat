package client;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.representation.Representation;
import compute.RegistrationInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatClient implements Runnable{

	/**
	 * The below class handles all webclient.client side interaction once within the Chat Client of ComputePi.
	 */
	//private static final long serialVersionUID = 7925812905228927260L;
	//private static PresenceService server;
	private static RegistrationInfo ClientName;
    private static String baseUrl = "https://ultra-resolver-223804.appspot.com//v1";
	private static boolean chkExit = true;
	private boolean chkLog = true;
	private static boolean chkExit2 = true;



	ChatClient(RegistrationInfo clientinfo)
	{
		//ChatClient.server = chatinterface;
		ChatClient.ClientName = clientinfo;
		chkLog = ChatClient.register(clientinfo);
	}


	static boolean GetChk() {
		return chkExit;
	}

	@Override
	public void run() {
		if(chkLog)
		//Starts by validating that the user is successfully registered with the Presence Service.
		{

			Scanner scanner = new Scanner(System.in);
			String message;
			System.out.println("Successfully Connected to the Chat Server!");
			try {
				InetAddress ip = InetAddress.getLocalHost();
				System.out.println("ChatClient bound:\nport: " + ClientName.getPort() + "\nIP: " + ip);

				// Below sets default initial state of user to Active to be able to accept chat messages.

				ClientName.setStatus(true);
				//server.setStatus(ClientName.getUserName(),true);

				// Below instantiates a Server Socket on the webclient.client on a different thread to listen for
				// webclient.client socket requests. (receive messages)

				SocketServer socket = new SocketServer(ChatClient.ClientName);

				// Below insures proper reset of ChkExit value for re-entry into the ChatMenu.
				chkExit = true;
				new Thread(socket).start();

			}
			catch(Exception e) {
				System.err.println("ChatServer exception: " +
						e.getMessage());
				e.printStackTrace();
			}



			while(chkExit)
			{
                try {
                    ChatMenu(scanner);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Hit Enter to return to Menu or type EXIT to leave the Chat Server.");

				//Below listens to the scanner to receive user entry and processes based on
				//chosen menu option.

				message = scanner.nextLine();

				// Below processes exit reqeuest.  Unregisters webclient.client and sets booleans to exit chat webclient.client section
				// of ComputePi.  Re-enters the ComputePi original menu upon completion.

				if(message.contains("EXIT") || !chkExit2)
				{
					chkExit = false;
					chkExit2 = true;
					try {
                        String widgetsResourceURL = baseUrl + "/users/" + ClientName.getUserName();
                        Request request = new Request(Method.DELETE, widgetsResourceURL);
                        request.getClientInfo().getAcceptedMediaTypes().add(new Preference<>(MediaType.APPLICATION_JSON));

                        // Now we do the HTTP GET
                        System.out.println("Sending an HTTP GET to " + widgetsResourceURL + ".");
                        Response resp = new Client(Protocol.HTTP).handle(request);
                        if (Status.SUCCESS_OK.equals(resp.getStatus())) {
                            System.out.println("\nSuccessfully Logged Out From The Chat Server!\n");
                        }
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					ComputePi.Loop();
				}
			}
		}

		// Below is the response if registration fails.  Will always be triggered
		// by finding duplicate names registered.

		else if(!chkLog){
			System.out.println("Sorry, this username is already taken.");
			ComputePi.Loop();
		}
	}

	// Below enters EchoClient method.  Creates a new socket using webclient.client's information to send
	// scanned message from webclient.client to serversocket thread on destination webclient.client system.

	private static void EchoClient(RegistrationInfo client, RegistrationInfo clienttarget, String m) {
		try {
			String line;
			BufferedReader is, server;
			PrintStream os;


			Socket clientSocket = new Socket("localhost", clienttarget.getPort());
			m = client.getUserName() + " on " + "[" + client.getHost() + ":"  + client.getPort() + "]" + " says: " + m;
			InputStream messagestream = new ByteArrayInputStream(m.getBytes());
			is = new BufferedReader(new InputStreamReader(messagestream) );
			os = new PrintStream(clientSocket.getOutputStream());
			server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			while(true) {
				line = is.readLine();

				//Below checks if input has been completely processed or if inputted scanner value was
				// EXIT.  This is included in order to safeguard proper socket termination.

				if(line == null) {
					clientSocket.close();
					break;
				} else if(line.equals("EXIT")) {
					clientSocket.close();
					break;
				}
				os.println(line);
				line = server.readLine();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Below is the main Chat dialogue tree.  Provides instructions to the user on what each command does,
	// An invalid command will prompt if user wants to exit.  All other commands are handled within.

	private static void ChatMenu(Scanner s) throws IOException {
        System.out.println("Awaiting input.\n Please enter one of the following commands:\n FRIENDS -"
                + " shows a list of all registered users.\n TALK <username> <message> - "
                + " Starts a pirvate message conversation with inputted username if they are available.\n"
                + " BROADCAST <message> - " + " sends a message to every available user.\n BUSY -"
                + " Sets users current status to show as unavailable.\n AVAILABLE -"
                + " Sets users current status to show as available.\n EXIT -"
                + " Unregisters username and terminates chat application.");

        String message = s.nextLine();
        if (message.contains("FRIENDS")) {
            String widgetsResourceURL = baseUrl + "/users";
            Request request = new Request(Method.GET, widgetsResourceURL);
            request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));

            // Now we do the HTTP GET
            Response resp = new Client(Protocol.HTTP).handle(request);
            Representation responseData = resp.getEntity();

            String jsonString = responseData.getText();

            ArrayList<RegistrationInfo> list = RegistrationInfo.jsonConvertList(jsonString);
            try {
                for (RegistrationInfo e : list) {
                    if (!e.getUserName().equals(ClientName.getUserName()))
                        System.out.println(e.getUserName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Below triggers handling of message submission attempt using TALK command. If message and user
        // are valid, begins creation of ClietnSocket to communicate to peer's socket server.

        if (message.contains("TALK")) {
            message = message.replace("TALK" + " ", "");
            try {
                //RegistrationInfo e = server.lookup(message);
                String widgetsResourceURL = baseUrl + "/users";
                Request request = new Request(Method.GET, widgetsResourceURL);
                request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));

                // Now we do the HTTP GET
                Response resp = new Client(Protocol.HTTP).handle(request);
                Representation  responseData = resp.getEntity();

                String jsonString = responseData.getText();
                //RegistrationInfo e = RegistrationInfo.Deserialize(jsonString);

                ArrayList<RegistrationInfo> list = RegistrationInfo.jsonConvertList(jsonString);

                for(RegistrationInfo e: list) {
                    if (e == null) {
                        System.out.println("Specified user does not presently exist in the system.");
                    }
                    else if(message.contains(e.getUserName()) && e.getStatus()) {
                            message = message.replace(e.getUserName() + " ", "");
                            EchoClient(ClientName, e, message);
                    }
                    else if (!e.getStatus()) {
                        System.out.println("User " + e.getUserName() + " is currently unavailable.");
                    }
                    else if (message.contains(ClientName.getUserName())) {
                    System.out.println("Why would you want to talk to yourself?");
                    }
                }
                }
                catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //Below is similar to TALK but submits the message with the exclusion of the current user
        // by looping through the Vector of registered users on the PresenceServer.

        if (message.contains("BROADCAST")) {
            message = message.replace("BROADCAST" + " ", "");
            String broad = "@everyone: ";
            message = broad + message;

            String widgetsResourceURL = baseUrl + "/users";
            Request request = new Request(Method.GET, widgetsResourceURL);
            request.getClientInfo().getAcceptedMediaTypes().add(new Preference(MediaType.APPLICATION_JSON));

            // Now we do the HTTP GET
            Response resp = new Client(Protocol.HTTP).handle(request);
            Representation responseData = resp.getEntity();

            String jsonString = responseData.getText();

            ArrayList<RegistrationInfo> list = RegistrationInfo.jsonConvertList(jsonString);

            try {
                for (RegistrationInfo e : list) {
                    if (e.getStatus() && !(e.getUserName().equals(ClientName.getUserName()))) {
                        EchoClient(ClientName, e, message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Changes users status to Available or changes nothing if user is already available.

        if (message.contains("AVAILABLE")) {
            try {
                if (!ClientName.getStatus()) {
                    ClientName.setStatus(true);
                    Form form = new Form();
                    form.add("userName",ClientName.getUserName());
                    form.add("port", String.valueOf(ClientName.getPort()));
                    form.add("host", ClientName.getHost());
                    form.add("status",String.valueOf(ClientName.getStatus()));

                    // construct request to create a new widget resource
                    String widgetsResourceURL = baseUrl + "/users/" + ClientName.getUserName();
                    Request request = new Request(Method.PUT, widgetsResourceURL);
                    request.setEntity(form.getWebRepresentation());
                    Response resp = new Client(Protocol.HTTP).handle(request);
                    //server.setStatus(ClientName.getUserName(), ClientName.getStatus());
                    System.out.println("Successfully set self to available!  Server Response: " + Status.SUCCESS_OK.equals(resp.getStatus()));
                } else {
                    System.out.println("You are already in available status!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Changes user's status to Busy preventing chat requests or does nothing if user is already available.

        if (message.contains("BUSY")) {
            try {
                if (ClientName.getStatus()) {
                    ClientName.setStatus(false);
                    //server.setStatus(ClientName.getUserName(), ClientName.getStatus());
                    Form form = new Form();
                    form.add("userName",ClientName.getUserName());
                    form.add("port", String.valueOf(ClientName.getPort()));
                    form.add("host", ClientName.getHost());
                    form.add("status",String.valueOf(ClientName.getStatus()));

                    // construct request to create a new widget resource
                    String widgetsResourceURL = baseUrl + "/users/" + ClientName.getUserName();
                    Request request = new Request(Method.PUT, widgetsResourceURL);
                    request.setEntity(form.getWebRepresentation());
                    Response resp = new Client(Protocol.HTTP).handle(request);
                    System.out.println("Successfully set self to unavailable.");
                } else {
                    System.out.println("You are already in unavailable status!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Unregisters the webclient.client and flags boolean for final exit call back to Computepi.

        if (message.contains("EXIT")) {
                    chkExit2 = false;
                }
            }

	private static boolean register(RegistrationInfo registrationInfo) {

		Form form = new Form();
		form.add("userName",registrationInfo.getUserName());
		form.add("port", String.valueOf(registrationInfo.getPort()));
		form.add("host",registrationInfo.getHost());
		form.add("status",String.valueOf(true));
		// construct request to create a new widget resource
		String widgetsResourceURL = baseUrl + "/users";
		Request request = new Request(Method.POST, widgetsResourceURL);
		request.setEntity(form.getWebRepresentation());
		Response resp = new Client(Protocol.HTTP).handle(request);
		System.out.println(resp.getStatus().toString());

		return Status.SUCCESS_OK.equals(resp.getStatus());
	}
}