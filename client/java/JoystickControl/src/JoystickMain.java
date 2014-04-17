/**
 * JAVA graphical joystick for the smartcar project
 * @author Team Pegasus (dimi)
 *
 */

public class JoystickMain {

	public static void main(String[] args) {
		ClientOutput userOutput = new ClientOutput();
		new JoystickGUI(userOutput);
	}

}
