package piSockets;

/**
 * This class gets incomming strings from the socket connection and process them accordingly.
 * Finally puts them on an outgoing list to be sent to the motor driver (arduino).
 * @author Team Pegasus (dimi)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class SocketReceive implements Runnable{
	BufferedReader brInput = null;
//	PrintWriter out = null;
	int direction = 0;
	int degrees = 0; //middle
	int speed = 0;//standard speed
	boolean backwards = false;
	public static ArrayList<byte[]> outputList = new ArrayList<byte[]>();
	Socket piSocket;
	
	public SocketReceive(Socket socket) {
		this.piSocket = socket;


	}

	private void socketProcess(String input) {
		if (!input.contains("#") && !input.contains("$") && !input.contains("!") && !input.contains(":")) {
			/* early version communication protocol */
			boolean validCommand = true;
			if (input.contains("forward")) {
				direction = 1;
				speed = 100;
				backwards = false;
			} else if (input.contains("backward")) {
				direction = -1;
				speed = 100;
				backwards = true;
			} else if (input.contains("stop")) {
				speed = 0;
			} else if (input.contains("left")) {
				if (degrees - 1 >= -89)
					degrees -= 2;
			} else if (input.contains("right")) {
				if (degrees + 1 <= 89)
					degrees += 2;
			} else if (input.contains("middle")) {
				degrees = 0;
			} else {
				System.out.println("Invalid Command");
				validCommand = false;
			}
			if (validCommand) {
				byte[] command = new byte[2];
				if (backwards && speed > 0) {
					speed *= direction;
				}
				command[0] = (byte) speed;
				command[1] = (byte) degrees;
				outputList.add(command);
			}
		}else if(input.contains("!") && input.contains(":")){
			/* tank STABLE version */
			String left = "", right = "";
			boolean leftMode = false, rightMode = false;
			for (int i=0;i<input.length();i++){
				
				if (leftMode && input.charAt(i) != ':'){
					left += input.charAt(i);
				}
				
				if (rightMode && input.charAt(i) != '!'){
					right += input.charAt(i);
				}
				
				if (input.charAt(i) == ':'){
					leftMode = false;
					rightMode = true;
				}
				
				if (input.charAt(i) == '!'){
					leftMode = true;
					rightMode = false;
				}


			}
			byte[] command = new byte[2];
			if (!input.contains("*")) {
				//when input comes from the java joystick
				int leftValue = Integer.parseInt(left);
				int rightValue = Integer.parseInt(right);
				command[0] = (byte) leftValue;
				command[1] = (byte) rightValue;
			}else{
				//this happens when input is UNSCALED, from the android app
				right = right.replace("*", "");
				int[] temp = rawJoystickInput(Integer.parseInt(left),Integer.parseInt(right));
		//		System.out.println("left: " + temp[0] + " right: " + temp[1]);
				command[0] = (byte) temp[0];
				command[1] = (byte) temp[1];
			}
			outputList.add(command);
		}else if(input.contains("#") && input.contains("$")){
			/* joystick version WITH STEERING WHEEL */
			String velocity = "", angle = "";
			boolean speedMode = false, angleMode = false;
			for (int i=0;i<input.length();i++){
				
				if (angleMode && input.charAt(i) != '$'){
					angle += input.charAt(i);
				}
				
				if (speedMode && input.charAt(i) != '#'){
					velocity += input.charAt(i);
				}
				
				if (input.charAt(i) == '#'){
					angleMode = true;
					speedMode = false;
				}
				if (input.charAt(i) == '$'){
					speedMode = true;
					angleMode = false;
				}

			}
			int speedValue = Integer.parseInt(velocity);
			int angleValue = Integer.parseInt(angle);
			byte[] command = new byte[2];
			command[0] = (byte) speedValue;
			command[1] = (byte) angleValue;
			outputList.add(command);
//			System.out.println("Speed: " + speedValue + " Angle: " + angleValue);
					
		}
	}
	/* a FIFO structure to handle the outbound commands */
	public byte[] SocketIO(){
		byte [] serialOut = outputList.get(0);
		outputList.remove(0);
		return serialOut;
	}

	@Override
	public void run() {
		System.out.println("listening to input");

		try {
			//you can use a printWriter to send stuff while receiving them, maybe to create
			//a handshake protocol?
			
	//		out = new PrintWriter(piSocket.getOutputStream(), true);
			InputStreamReader InputReader = new InputStreamReader((piSocket.getInputStream()));
			brInput = new BufferedReader(InputReader);
			String input;
			while ((input = brInput.readLine()) != null) {
//				System.out.println("input" + input);
				socketProcess(input);		
			}		
			InputReader.close();
			piSocket.close();			
			System.out.println("Socket closed");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/* this happens when the input is unscaled and not ready to be sent to the motors in "tank" mode */
	private int[] rawJoystickInput(int angle, int speed) {
	//	System.out.println("angle: " + angle + " speed: " + speed);
		double scale = 8.0; // how sensitive it is. depends on your power source
		speed = (int) Math.round((speed * scale) / 100.0);
		float rel = (float) ((angle * scale) / 90.0) ;
		rel = Math.round(rel);
		rel = (int) scale-Math.abs(rel);
		float sideSpeed = Math.round((float) ((rel/scale) * speed));
		int[] motorsOut = new int[2];
			if (angle>0){
				motorsOut[0] = speed;	
				motorsOut[1] = (int) sideSpeed;
			}else{
				motorsOut[0] = (int) sideSpeed;
				motorsOut[1] = speed;
			}
		if (motorsOut[0] == speed && motorsOut[1] == 0) motorsOut[1] = -speed;
		if (motorsOut[1] == speed && motorsOut[0] == 0) motorsOut[0] = -speed;
		
	//	System.out.println("left speed: " + motorsOut[0] + " right speed: " + motorsOut[1]);

		return motorsOut;
	}

}
