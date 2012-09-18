package controller.net;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class OutgoingConnection {

	private Socket connection;
	private BufferedOutputStream writer;
	
	// Use this instead of BufferedReader to handle servers that do not send a line end
	private InputStreamReader inputStreamReader;
	
	public OutgoingConnection(String hostname, int port) throws UnknownHostException{
		this(java.net.InetAddress.getByName(hostname), port);
	}
	
	public OutgoingConnection(InetAddress address, int port){
		try {
			connection = new Socket(address, port);
			writer = new BufferedOutputStream(connection.getOutputStream());
			inputStreamReader = new InputStreamReader(connection.getInputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendData(String data){
		try {
			writer.write(data.getBytes());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String waitForAnswer(){
		int readChar = 0;;
		String message = "";
		
		try {
			while((readChar = inputStreamReader.read()) != -1){
				
				message += (char) readChar;
				
				if(readChar == 10){
					break;
				}
			}
			
			return message;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	public void close() throws IOException{
			writer.close();
			inputStreamReader.close();
			connection.close();
	}
}
