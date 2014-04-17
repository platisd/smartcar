import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
/**
 * The JFrame containing the joystick. More components can be added through here.
 * @author Team Pegasus (dimi)
 *
 */
@SuppressWarnings("serial")
public class JoystickGUI extends JFrame {
	public JoystickGUI(ClientOutput userOutput){
		
		setTitle("SmartCar Joystick");
		setSize(480,500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		JPanel totalWrapper = new JPanel();
		getContentPane().add(totalWrapper, BorderLayout.CENTER);
		totalWrapper.setLayout(new BorderLayout(0, 0));

		JPanel centralPanel = new JPanel();
		totalWrapper.add(centralPanel, BorderLayout.CENTER);
		centralPanel.setLayout(new BorderLayout(0, 0));
		
		DrawingCanvas mainCanvas = new DrawingCanvas(getContentPane().getSize(), userOutput);
		totalWrapper.add(mainCanvas);
		mainCanvas.setLayout(new BorderLayout(0, 0));
		validate(); //otherwise the window sometimes doesn't appear
	}
}
