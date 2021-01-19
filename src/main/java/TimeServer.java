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

	public TimeServer() {
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Server started on port: " + PORT);
			//
			
			while(true) {
				Socket socket = serverSocket.accept();
				new NTPRequestHandler(socket).run();
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
				ObjectInputStream input;
				input = new ObjectInputStream(client.getInputStream());
			
				ObjectOutputStream output;
				output = new ObjectOutputStream(client.getOutputStream());
				
				// ...
				
				NTPRequest request = (NTPRequest) input.readObject();
				request.setT2(new Date().getTime() + getRandomDelay() + OFFSET);
				sendNTPAnswer(request);
				
				output.writeObject(request);
				

				request = (NTPRequest) input.readObject();
				request.setT4(new Date().getTime() + getRandomDelay() + OFFSET);
				sendNTPAnswer(request);
				
				output.writeObject(request);
				
				client.close();
				
			} catch(IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
		}
		
		private int getRandomDelay() {
			return ((int)(Math.random() * ((100 - 10) + 1)) + 10);
		}

		private void sendNTPAnswer(NTPRequest request) {
		}

	}

}
