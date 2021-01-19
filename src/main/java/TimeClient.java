import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;

public class TimeClient {
	private static String hostUrl = "127.0.0.1";
	private static int PORT = 27780;
	private Double minD;
	private NTPRequest minNTPrequest;
	private Socket socket;

	public TimeClient() {

		try {

			minD = null;
			
			for (int i = 0; i < 10; i++) {
				System.out.println("Sending new request...");
				socket = new Socket(InetAddress.getByName(hostUrl), PORT);
							
				ObjectOutputStream output;
				output = new ObjectOutputStream(socket.getOutputStream());
				
				NTPRequest request = new NTPRequest();
				request.setT1(new Date().getTime());
				output.writeObject(request);
				
				ObjectInputStream input;
				input = new ObjectInputStream(socket.getInputStream());
								
				request = (NTPRequest) input.readObject();
				request.setT3(new Date().getTime() + getRandomDelay());
				
				output.writeObject(request);
				
				request = (NTPRequest) input.readObject();
				
				request.calculateOandD();
				System.out.println("T values: " + request.toString());
				System.out.println("Oi: " + request.o + ", Di: " + request.d);
				
				if(minD == null || request.d < minD) {
					minD = request.d;
					minNTPrequest = request;
				}
				
				socket.close();
				
				threadSleep(350);
			}
			
			System.out.println("Time difference: " + minNTPrequest.o + ", Accuracy: ");
			
			socket.close();

		} catch (UnknownHostException e) {
			System.out.println("Unknownhost Error");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
			e.printStackTrace();
		}
	}

	private void sendNTPRequest(NTPRequest request) {
	}
	
	private int getRandomDelay() {
		return ((int)(Math.random() * ((100 - 10) + 1)) + 10);
	}

	private void threadSleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TimeClient();
	}

}
