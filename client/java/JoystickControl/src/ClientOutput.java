
/**
 * Opens a socket to the desired ip and port and sends the inputted string through it.
 * @author Team Pegasus (dimi)
 */
import java.io.PrintWriter;
import java.net.Socket;


public class ClientOutput {
	Socket socket;
	PrintWriter out;
	public ClientOutput(){
		try { //192.168.1.100
			socket = new Socket("127.0.0.1", 8787);
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception ex) {

		}
	}
	
	public void send2pi (String GUIinput){
		out.println(GUIinput);
	}
	
	public void send2pi (int[] GUIinput){
		out.println(GUIinput);
	}
	
	public void close(){
		out.close();
	}
}
