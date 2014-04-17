package piSockets;


/** This class opens a socket connection at port 8787, waits untill you get a user connection and when you do starts
 * a serial connection. When socket is closed, keeps waiting for a new user to connect.
 * 
 * @author Team Pegasus (dimi)
 */
import java.net.ServerSocket;
import java.net.Socket;

public class SocketsMain {

	public static void main(String[] args) {
		final int port = 8787;
		try {
			@SuppressWarnings("resource")
			ServerSocket piServer = new ServerSocket(port);
			System.out.println("opened socket and waiting");
			while(true){
				Socket piSocket = piServer.accept();
				System.out.println("Got a user connection!");
				SocketReceive receive = new SocketReceive(piSocket);
				Thread t = new Thread(receive);
				t.start();

				SerialOutput serial2Microchip = new SerialOutput();

				serial2Microchip.initialize();

				byte[] initializing = {0,0};
				serial2Microchip.send2Serial(initializing);

				while (t.isAlive()){

					if (!SocketReceive.outputList.isEmpty()){
						for (int i = 0; i < SocketReceive.outputList.size(); i++) {
							serial2Microchip.send2Serial(receive.SocketIO());
						}
					}
					Thread.sleep(5); //sampling rate

				}
				serial2Microchip.send2Serial(new byte[] {0,0}); //finalizing
				serial2Microchip.close();
//				piServer.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}