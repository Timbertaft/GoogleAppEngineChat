package client;

import compute.ProcessIncomingRequest;
import compute.RegistrationInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer implements Runnable {

	private static RegistrationInfo connection;
	private static boolean running;

	SocketServer(RegistrationInfo userconnect) {

		SocketServer.connection = userconnect;
	}

	@Override
	public void run() {
		// This class instantiates a listening server socket on the calling webclient.client to receive incoming
		// messages that get passed.

		try {
			ServerSocket chatServer = new ServerSocket(SocketServer.connection.getPort());
			running = true;

			while(running) {
				try {
					Socket clientSocket = chatServer.accept();
					Thread thread  = new Thread(new ProcessIncomingRequest(clientSocket));
					thread.start();

					//The below block is to correct a memory leak and properly close the socket on exit.
					if(!ChatClient.GetChk()) {
						running = false;


						try {
							if (chatServer != null) {
								chatServer.close();
							}
						} catch (IOException ignored) {

						}
					}
					// The above block is to correct a memory leak and properly close the socket on exit.
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

}