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
	private ObjectInputStream input;
	private ObjectOutputStream output;

	public TimeClient() {

		try {

			minD = null;
			
			for (int i = 0; i < 10; i++) {
				System.out.println("Sending new request...");
				//establish socket connection to server
				socket = new Socket(InetAddress.getByName(hostUrl), PORT);
							
				//write to socket using ObjectOutputStream
				output = new ObjectOutputStream(socket.getOutputStream());
				
				NTPRequest request = new NTPRequest();
				request.setT1(new Date().getTime());
				sendNTPRequest(request);
				
				//read the server response message using ObjectInputStream
				input = new ObjectInputStream(socket.getInputStream());
				//convert ObjectInputStream object to NTPRequest			
				request = (NTPRequest) input.readObject();
				request.setT3(new Date().getTime() + getRandomDelay());
				sendNTPRequest(request);
				
				//convert ObjectInputStream object to NTPRequest
				request = (NTPRequest) input.readObject();
				
				//calculate Offset and Delay
				request.calculateOandD();
//				System.out.println("T values: " + request.toString());
				System.out.println("Oi: " + request.o + ", Di: " + request.d);
				
				//find the smallest delay and its corresponding NTPRequest
				if(minD == null || request.d < minD) {
					minD = request.d;
					minNTPrequest = request;
				}
				
				socket.close();
				
				//wait 350ms between two measurements
				threadSleep(350);
			}
			
			//print time difference and the corresponding accuracy
			System.out.println("Time difference (delay): " + minNTPrequest.d + ", Accuracy (offset): " + minNTPrequest.o);
			
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
		try {
			output.writeObject(request);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//generate random integer between 10 to 100 as delay
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
