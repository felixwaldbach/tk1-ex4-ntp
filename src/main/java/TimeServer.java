import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Random;

public class TimeServer {
	private static int PORT = 27780;
	private static int OFFSET = 1100;
	private ServerSocket serverSocket;
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public TimeServer() {
		try {
			//create the socket server object
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started on port: " + PORT);
			
			//keep listens indefinitely until program terminates
			while(true) {
				//create socket
				Socket socket = serverSocket.accept();
				//handle the NTP Request message
				new NTPRequestHandler(socket).run();
				threadSleep(350);
			}
			

		} catch (IOException e) {
			e.printStackTrace();
			try {
				serverSocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeServer();
	}

	private class NTPRequestHandler implements Runnable {
		private Socket client;

		public NTPRequestHandler(Socket client) {
			System.out.println("New Socket connection established");
			this.client = client;
		}

		@Override
		public void run() {
			try {
				//read from socket to ObjectInputStream object
				input = new ObjectInputStream(client.getInputStream());
				output = new ObjectOutputStream(client.getOutputStream());
				
				//convert ObjectInputStream object to NTPRequest
				NTPRequest request = (NTPRequest) input.readObject();
				request.setT2(new Date().getTime() + getRandomDelay() + OFFSET);
				sendNTPAnswer(request);
				
				//convert ObjectInputStream object to NTPRequest
				request = (NTPRequest) input.readObject();
				request.setT4(new Date().getTime() + getRandomDelay() + OFFSET);
				sendNTPAnswer(request);
				
				client.close();
				
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
		//generate random integer between 10 to 100 as delay
		private int getRandomDelay() {
			return ((int)(Math.random() * ((100 - 10) + 1)) + 10);
		}

		private void sendNTPAnswer(NTPRequest request) {
			try {
				//write to socket using ObjectOutputStream
				output.writeObject(request);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

	}

}
