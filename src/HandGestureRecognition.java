import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class HandGestureRecognition {
	public Point presentCursorGreen;
	public Point PrevCursorGreen;
	public Point presentCursorRed;
	public double prevDistance;
	public double presentDistance;
	public Robot robot;
	public boolean zoomState = false;
	public boolean clickState = false;
	public java.awt.Point prevCursor = null;
	public Point PrevCursorRed;
	public Point prevBeforeRed;
	public Point[] pastP;
	public int pastLength;
	public int presentDistX = 0;
	public int prevDistX = 0;

	public HandGestureRecognition() {
		pastLength = 4;

		pastP = new Point[pastLength];
		for (int i = 0; i < pastLength; i++)
			pastP[i] = new Point();
		// pastP[0].setX(10);
		prevBeforeRed = new Point();
		presentCursorRed = new Point();
		presentCursorGreen = new Point();
		PrevCursorGreen = new Point();
		PrevCursorRed = new Point();
		prevCursor = new java.awt.Point();
		prevCursor.x = 683;
		prevCursor.y = 384;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setPresentCursor(Point red, Point green) {
		/*
		 * presentCursorRed = red; presentCursorGreen = green;
		 */
		//if(prevBeforeRed==)

		presentCursorGreen.setX(green.getX());
		presentCursorGreen.setY(green.getY());
		presentCursorRed.setX(red.getX());
		presentCursorRed.setY(red.getY());

		cursorManipulation();
		presentDistX = presentCursorGreen.getX() - presentCursorRed.getX();
		/*if (presentDistX < -5 && clickState == false) {
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			Toolkit.getDefaultToolkit().beep();
			clickState = true;
		} else if (presentDistX > 0 && clickState == true) {
			clickState = false;
			return;
		}*/
		// presentDistance = distance(presentCursorGreen, presentCursorRed);

		/*if (zoomState == true)
			zoomManipulation();
		else if (presentDistX > 0 && presentDistX < 200) {
			zoomState = true;
		}*/
		prevDistX = presentDistX;
		prevBeforeRed.setX(PrevCursorRed.getX());
		prevBeforeRed.setY(PrevCursorRed.getY());
		for (int i = pastLength - 1; i >= 1; i--) {
			if (pastP[i - 1] == null)
				continue;
			pastP[i].setX(pastP[i - 1].getX());
			pastP[i].setY(pastP[i - 1].getY());
		}

		pastP[0].setX(presentCursorRed.getX());
		pastP[0].setY(presentCursorRed.getY());

	}

	public double distance(Point p1, Point p2) {
		double dist = 0;
		dist = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
				+ (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		return dist;
	}

	public void zoomManipulation() {

		if (prevDistX < presentDistX) {
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_CONTROL);

		} else {
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
		}

	}

	public void cursorManipulation() {
		double x;
		double y;
		int multX, multY;
		multX = multY = 5;
		int falseMove = 5;
		//if(distance(PrevCursorRed, presentCursorRed)>300)return;
		if (Math.abs(pastP[2].getX() - presentCursorRed.getX()) < falseMove
				&& Math.abs(pastP[2].getY() - presentCursorRed.getY()) < falseMove) {
			return;
		}

		// multY=4;
		System.out.println("cursor x=" + prevCursor.getX() + "y="
				+ prevCursor.getY());
		System.out.println("prevRe" + PrevCursorRed.getX() + "pfds"
				+ presentCursorRed.getX());
		x = (PrevCursorRed.getX() - presentCursorRed.getX()) * multX
				+ prevCursor.getX();
		y = -(PrevCursorRed.getY() - presentCursorRed.getY()) * multY
				+ prevCursor.getY();
		System.out.println(x + " " + y);
		robot.mouseMove((int) x, (int) (y));
		// robot.mouseMove(presentCursorRed.getX(), presentCursorRed.getY());
		prevCursor = MouseInfo.getPointerInfo().getLocation();
		PrevCursorRed.setX(presentCursorRed.getX());
		PrevCursorRed.setY(presentCursorRed.getY());

	}

}
