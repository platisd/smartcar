/**
 * This class handles the joystick appearance, its behaviour and the physics involved. Also generates the
 * string to be sent.
 * @author Team Pegasus (dimi)
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import javax.swing.Timer;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener, ActionListener{
	Graphics2D pointerCircle;
	Ellipse2D.Double pointer;
	int totalWidth, totalHeight, bigCircleDiameter,bigCircleRad, centerX,centerY;
	double pointerCenterX, pointerCenterY, pointerX, pointerY, sphereX, sphereY,prevPointerX, prevPointerY;
	int pDiameter = 40;
	int outerCircleX = 8;
	int outerCircleY = 8;
	boolean atCenter = true;
	boolean outOfBounds = false;
	Timer goBack = new Timer(1,this);
	Timer getCoordinates = new Timer (150,this);
	ClientOutput out;
	public DrawingCanvas(Dimension dimension, ClientOutput userOutput) {
		totalWidth = (int) dimension.getWidth();
		totalHeight = (int) dimension.getHeight();
		sphereX = (totalHeight-outerCircleX-6)/2;
		sphereY = sphereX;
		pointerCenterX = sphereX- (pDiameter/2)+10; // 10 is probably the stroke
		pointerCenterY = sphereY - (pDiameter/2)+10;
		prevPointerX = pointerCenterX;
		prevPointerY = pointerCenterY;
		bigCircleDiameter = totalHeight-10;
		bigCircleRad = bigCircleDiameter/2;
		centerX = (totalHeight-outerCircleX+14)/2;
		centerY = (totalHeight-outerCircleX+12)/2;
		addMouseListener(this);
		addMouseMotionListener(this);
		out = userOutput;
		revalidate(); //not sure if needed
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		/* the outer bigger circle */
		bigOuterCircle(g);
		/* the line in the middle of the outer bigger circle */
		centralHorizontalLine(g);
		/* the sphere in the center */ 
		centralSphere(g);
		/* our pointer */
		joyStickPointer(g);
	}

	private void joyStickPointer(Graphics g) {
		/* our pointer */
		pointerCircle = (Graphics2D)g;
		if (atCenter){
			pointer = new Ellipse2D.Double(pointerCenterX, pointerCenterY,pDiameter,pDiameter);
			pointerX = pointerCenterX;
			pointerY = pointerCenterY;
			prevPointerX = pointerCenterX;
			prevPointerY = pointerCenterY;
		}else{
			pointer = new Ellipse2D.Double(pointerX, pointerY,pDiameter,pDiameter);
		}
		pointerCircle.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		pointerCircle.setStroke(new BasicStroke(10));
		pointerCircle.setColor(new Color(255,0,0));
		pointerCircle.draw(pointer);		
	}

	private void centralSphere(Graphics g) {
		/* the sphere in the center */ 
		Graphics2D g2d = (Graphics2D)g;
		Ellipse2D.Double center = new Ellipse2D.Double(sphereX,sphereY,20,20);
		g2d.fill(center);		
	}

	private void centralHorizontalLine(Graphics g) {
		/* the line in the middle of the outer bigger circle */
		Graphics2D centralLine = (Graphics2D)g;
		centralLine.setStroke(new BasicStroke(4));
		centralLine.drawLine(outerCircleX,(totalWidth+outerCircleY-4)/2, totalWidth-1,(totalWidth+outerCircleY-4)/2);		
	}

	private void bigOuterCircle(Graphics g){
		/* the outer bigger circle */
		Graphics2D g2d = (Graphics2D)g;
		Ellipse2D.Double bigCircle = new Ellipse2D.Double(outerCircleX,outerCircleY,bigCircleDiameter,bigCircleDiameter);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(0, 0, 0));
		g2d.setStroke(new BasicStroke(8));
		g2d.draw(bigCircle);
	}

	private boolean inBounds(int x, int y){
		/* calculating whether that point is within the circle */
		double axisX = Math.pow((centerX - x), 2);
		double axisY = Math.pow((centerY - y), 2);
		double distance = Math.sqrt((axisX + axisY));
		return (int) distance < bigCircleRad;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (inBounds(e.getX(), e.getY())) {
			if (goBack.isRunning()) {
				goBack.stop();
			}

			//update the pointer position
			pointerX = e.getX() - (pDiameter / 2);
			pointerY = e.getY() - (pDiameter / 2);
			atCenter = false;
			repaint();
			if (!getCoordinates.isRunning()) getCoordinates.start(); //start sending
		}


	}

	@Override
	public void mouseReleased(MouseEvent e) {
		goBack.start();
		if (getCoordinates.isRunning()){
			getCoordinates.stop();
			//	out.send2pi("#0$0"); //stop the motor and the wheel when releasing mouse
			out.send2pi("!0:0"); // tank mode
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (inBounds(e.getX(), e.getY())) {
			if (!goBack.isRunning()) {
				/* move the pointer along with mouse */
				pointerX = e.getX() - (pDiameter / 2);
				pointerY = e.getY() - (pDiameter / 2);
				atCenter = false;
				repaint();
				if (!getCoordinates.isRunning()) getCoordinates.start();
			}
		}		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == goBack){
			if (!atCenter){
				/* move back to the center */
				if (pointerCenterX > pointerX) pointerX++;
				if (pointerCenterX < pointerX) pointerX--;
				if (pointerCenterY >pointerY) pointerY++;
				if (pointerCenterY < pointerY) pointerY--;
				if (pointerCenterY == pointerY && pointerCenterX == pointerX){
					atCenter = true;
					goBack.stop();
				}
				repaint();
			}
		}

		if (e.getSource() == getCoordinates){
			if (prevPointerX != pointerX || prevPointerY != pointerY){
				prevPointerX = pointerX;
				prevPointerY = pointerY;
				int scale = 8;
				double cursorX = pointerX + (pDiameter/2);
				double cursorY = pointerY + (pDiameter/2);
				double distanceX = cursorX - centerY;
				double scaledBy4distanceX = (distanceX * scale) / (bigCircleRad);
				scaledBy4distanceX = Math.round(scaledBy4distanceX);
				distanceX = (distanceX * 100) / (bigCircleRad);

				double distanceY = centerY - cursorY;
				double scaledBy4distanceY = (distanceY * scale) / (bigCircleRad);
				scaledBy4distanceY = Math.round(scaledBy4distanceY);
				distanceY = (distanceY * 100) / (bigCircleRad);
				/* passing the signed value of distance so to get optimum results */
				int angle = getAngle(distanceX,Math.abs(distanceY)); //not accurate when distance scaled by 4!!!
				int speed = getSpeed(cursorX,cursorY); //speed scaled to [-4,4]
				//		String dataOut = "!" + angle + ":" + speed ;
				int[] motors = getMotors(angle, speed);
				String dataOut = "!" + motors[0] + ":" + motors[1];
				out.send2pi(dataOut);
			}
		}
	}

	private int[] getMotors(int angle, int speed){
		double scale = 8.0;
		float rel = (float) ((angle * scale) / 90.0) ;
		rel = Math.round(rel);
		rel = (int) scale - Math.abs(rel);
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

		System.out.println("left speed: " + motorsOut[0] + " right speed: " + motorsOut[1]);

		return motorsOut;
	}

	private int getSpeed(double x, double y){
		/* get speed as distance from center and direction
		 * of speed from whether it's over or lower than the line */
		int scale = 8;
		double axisX = Math.pow((centerX - x), 2);
		double axisY = Math.pow((centerY - y), 2);
		int prefix = (int) ((centerY-y) / Math.abs(centerY-y));
		double distance = Math.sqrt((axisX + axisY));
		return  (int) (prefix*(distance*scale)/(bigCircleRad-20)); //scale of 4
	}
	private int getAngle(double x, double y){
		/* get angle as the corner of the triangle between the cursor position
		 * the diameter and the center */
		double tan = Math.atan2(x, y); // x first because we want to count vertically the degrees
		double degrees = Math.round(Math.toDegrees(tan));
		return (int) degrees;
	}
}