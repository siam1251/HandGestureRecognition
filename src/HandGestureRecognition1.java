import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HandGestureRecognition1 {
	public Dimension screen;
	public Point red;	
	public int previousPoints=10;
	public Point redPoints[];
	public Point greenPoints[];
	public Point green;
	public Robot robot;
	private Point backForwardPoint=new Point();
	public Point previousGreen;
	public Point previousRed;
	public double previous_Distance;
	public Dimension imSize;
	private Point clickPoint=new Point();
	public Double multiplyX;
	public Double multiplyY;
	private boolean clickState=false;
	private double screenWidth;
	private int lowerAdjustment=150;
	private int leftAdjustment=100;
	private int clickStateTTL=0;
	private int backForwardTTL=0;
	private int backForwardTTLseconds=2;
	private long backForwardStartTime=0;
	private int upOrDownMove=6;
	private int leftOrRight=10;
	private int backOrForward=10;
	private int disp=2;
	private boolean backState=false;
	private boolean forwardState=false;
	private boolean backOrForwardState=false;
	public  HandGestureRecognition1(){
		redPoints=new Point[previousPoints];
		greenPoints=new Point[previousPoints];
		try {
			robot=new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		screen=toolkit.getScreenSize();
		System.out.println("width="+screen.getWidth()+"height="+screen.getHeight());
		red=new Point();
		for(int i=0;i<previousPoints;i++){
			redPoints[i]=new Point();
			greenPoints[i]=new Point();
		}
		green=new Point();
		previousGreen = new Point();
		previousRed = new Point();
		imSize=new Dimension(550,380);
		multiplyX= screen.getWidth()/imSize.getWidth();
		multiplyY=screen.getHeight()/imSize.getHeight();
		screenWidth=screen.getWidth();
	}
	
	public void setPresentCursor(Point r,Point g){
		//clickState=false;
		red.setX(r.getX());
		red.setY(r.getY());
		green.setX(g.getX());
		green.setY(g.getY());
		
		//-----------cursor will not move short displacement 
		if(Math.abs(red.getX()-redPoints[0].getX())<=disp){
			red.setX(redPoints[0].getX());
		}
		if(Math.abs(red.getY()-redPoints[0].getY())<=disp){
			red.setY(redPoints[0].getY());
		}
		//----------Back Forward Action--------------------------------------------
		
		if(green.getX()!=10&&red.getX()==10&&backOrForwardState){
			System.out.println("sayemfd");
				if(green.getX()-backForwardPoint.getX()>backOrForward)goBack();
				else if(green.getX()-backForwardPoint.getX()<-backOrForward)goForward();
				else System.out.println("robin");
		}
		else if(green.getX()!=10&&red.getX()==10&&backForwardTTL==0){
			isBackOrForwardState();
		}
		else if(backForwardTTL>0)backForwardTTL--;
		//--------------------click state ------------------------------------------------------------
		if(clickState==true&&red.getX()!=10&&green.getX()==10)clickAction();
		//else if(clickState==true&&rightClick==true)rightClickAction();
		//---------see if red and green presents------------------
		//if(red.getX()==10){isBackForwardState();}
		else if(red.getX()!=10&&green.getX()==10&&clickStateTTL==0){
			isClickState();
		}
		else if(clickStateTTL>0)clickStateTTL--;
		//--------zoom or cursor move-----------------
		if(green.getX()!=10&&red.getX()!=10)zoom();
		else cursorManipulation();
		
		previousGreen.setX(green.getX());
		previousGreen.setY(green.getY());
		previousRed.setX(red.getX());
		previousRed.setY(red.getY());
		previous_Distance = Math.sqrt((previousGreen.getX()-previousRed.getX())*(previousGreen.getX()-previousRed.getX()) + (previousGreen.getY()-previousRed.getY())*(previousGreen.getY()-previousRed.getY()));
        //---updating points-------
		this.updatePoints();
	}
	

	

	

	private void clickAction() {
		// TODO Auto-generated method stub
		System.out.println("left click");
		//--------------single click---------------------------------
		if(red.getY()-clickPoint.getY()<-upOrDownMove){
			System.out.println("right Clicked");
			//------moving cursor to clicking point-------------------
			int x=(int)(screenWidth-(clickPoint.getX()*multiplyX))+leftAdjustment;
			int y=(int)(clickPoint.getY()*multiplyY)-lowerAdjustment;
			robot.mouseMove(x,y);
			//---------------------------------------------------------
			robot.mousePress(InputEvent.BUTTON1_MASK);
			//robot.delay(1000); // Click one second
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			Toolkit.getDefaultToolkit().beep();
			clickState = false;
		}
		//--------double  click---------------------------------
		else if(red.getY()-clickPoint.getY()>upOrDownMove){
			System.out.println("double click");
			//------moving cursor to clicking point-------------------
			int x=(int)(screenWidth-(clickPoint.getX()*multiplyX))+leftAdjustment;
			int y=(int)(clickPoint.getY()*multiplyY)-lowerAdjustment;
			robot.mouseMove(x,y);
			//---------------------------------------------------------
			robot.mousePress(InputEvent.BUTTON1_MASK);
			//robot.delay(1000); // Click one second
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			robot.mousePress(InputEvent.BUTTON1_MASK);
			//robot.delay(1000); // Click one second
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			Toolkit.getDefaultToolkit().beep();
			clickState = false;
		}
		else if(Math.abs(red.getX()-clickPoint.getX())>leftOrRight){
			//------moving cursor to clicking point-------------------
			int x=(int)(screenWidth-(clickPoint.getX()*multiplyX))+leftAdjustment;
			int y=(int)(clickPoint.getY()*multiplyY)-lowerAdjustment;
			robot.mouseMove(x,y);
			//---------------------------------------------------------
			robot.mousePress(InputEvent.BUTTON3_MASK);
			//robot.delay(1000); // Click one second
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
			Toolkit.getDefaultToolkit().beep();
			clickState = false;
		}
		
	}

	private void isClickState() {
		System.out.println("isClickState");
		clickState=false;
		int count=1;
		float distance=0;
		for(int i=0;i<previousPoints;i++){
			if(redPoints[i].getX()!=10&&greenPoints[i].getX()==10){
				distance+=getDistance(redPoints[i], red);
				count++;
			}
		}
		if(count>previousPoints-3&&(distance/count)<6){
			System.out.println("clickState");
			clickState=true;
			clickStateTTL=6;
			clickPoint.setX(red.getX());
			clickPoint.setY(red.getY());
		}
		
	}

	public void cursorManipulation(){
		int x=0,y=0;
		if(red.getX()==10){
			/*x=(int)(screenWidth-(redPoints[0].getX()*multiplyX));
		    y=(int)(redPoints[0].getY()*multiplyY)-lowerAdjustment;
			robot.mouseMove(x,y);*/
		}
		else{
			 x=(int)(screenWidth-(red.getX()*multiplyX))+leftAdjustment;
			 y=(int)(red.getY()*multiplyY)-lowerAdjustment;
			robot.mouseMove(x,y);
		}
	}
	
	public void zoom(){
		
		double distance = Math.sqrt((red.getX()-green.getX())*(red.getX()-green.getX())+(red.getY()-green.getY())*(red.getY()-green.getY()));
		
		if(distance>previous_Distance && Math.abs(distance-previous_Distance)>13){
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			System.out.println(distance+" "+previous_Distance);
		}
		
		else if(distance<previous_Distance && Math.abs(distance-previous_Distance)>13){
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
			System.out.println(distance+" "+previous_Distance);
		}
	
	}
	public void updatePoints(){
		for(int i=previousPoints-1;i>=1;i--){
			redPoints[i].setX(redPoints[i-1].getX());
			redPoints[i].setY(redPoints[i-1].getY());
			greenPoints[i].setX(greenPoints[i-1].getX());
			greenPoints[i].setY(greenPoints[i-1].getY());
		}
		redPoints[0].setX(red.getX());
		redPoints[0].setY(red.getY());
		greenPoints[0].setX(green.getX());
		greenPoints[0].setY(green.getY());
	}
	public double getDistance(Point p1, Point p2) {
		double dist = 0;
		dist = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
				+ (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		return dist;
	}
	private void goBack(){
		//---for mozila browser---
		System.out.println("go back");
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_ALT);
		//---for power point------
		robot.keyPress(KeyEvent.VK_LEFT);
		robot.keyRelease(KeyEvent.VK_LEFT);
		backOrForwardState=false;
		
	}
	private void goForward(){
		//---for mozila browser---
		System.out.println("go forward");
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_ALT);
		//---for power point------
		robot.keyPress(KeyEvent.VK_RIGHT);
		robot.keyRelease(KeyEvent.VK_RIGHT);
		backOrForwardState=false;

	}
	private void isBackOrForwardState() {
		// TODO Auto-generated method stub
		System.out.println("isbackforward");
		backOrForwardState=false;
		int count=1,distance=0;
		for(int i=0;i<previousPoints;i++){
			if(greenPoints[i].getX()!=10&&redPoints[i].getX()==10){
				distance+=getDistance(greenPoints[i], green);
				count++;
			}
		}
		if(count>previousPoints-3&&(distance/count)<30){
			System.out.println("backfd");
			backOrForwardState=true;
			backForwardTTL=6;
			backForwardPoint.setX(green.getX());
			backForwardPoint.setY(green.getY());
			
		}
	}
}
