import static java.lang.Math.pow;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.media.Player;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import Jama.Matrix;

import com.sun.media.ui.ButtonComp;

public class MainClass extends Panel {
	public KalmanFilter redKF;
	public KalmanFilter greenKF;
	public  ImagePanel imgpanel = null;
	public static ImagePanel imgpanel_2=null;
	public static  Camera camera;
	public static boolean buttonPressed = true;
	public static boolean resetPressed = true;
	public  Point previousRed = null;
	public  Point previousGreen = null;
	public BufferedImage im=null;
	public BufferedImage bi=null;
	public HandGestureRecognition1 recognition;
	//---Threads
	Thread greenThread=new Thread();
	Thread redThread=new Thread();
	Thread showDetectedMarker=new Thread();
	Thread handRecog=new Thread();
	Thread imCapture=new Thread();
	Thread writing=new Thread();
	//-----------------------------
	public  Point tempRed=null;
	public  Point tempGreen=null;
	public  TrackerHSB1 redTracker;
	public  TrackerHSB1 greenTracker;
	public JButton buttonCapture;
	public JButton resetButton;
	//---time calc---
	int i=0;
	long StartTime=0,endTime=0;
	//-------------
	public static boolean isRunning=true;
	private boolean takeFrame=false;
	private boolean detectRed=false;
	 static PrintWriter pw;

	public static void main(String[] args) {
		Frame f = new Frame("capture");
		MainClass panel = new MainClass();
		f.add(panel);
		System.out.println("tra");
		f.addWindowListener(new WindowAdapter() {
			

			@Override
			public void windowClosing(WindowEvent e) {
				pw.close();
				isRunning=false;
				buttonPressed=false;
				System.exit(0);
			}
		});
		f.setSize(600, 600);
		f.setVisible(true);
		panel.start();
	}

	

	private void start() {
		// TODO Auto-generated method stub

		while (buttonPressed) {

		}
		trackedPointManupulation();
	}



	public void trackedImage(BufferedImage im, Point p,int rgb) {
		int x, y,range=10;
		if (p.getX() <= 630 && p.getY() <= 470) {
			for (x = p.getX() - range; x < p.getX() + range; x++)
				for (y = p.getY() - range; y < p.getY() + range; y++) {
					if (y >= 0 && x >= 0 && y <= 480 && x <= 640) {
						im.setRGB(x, y, rgb);
					}
				}
		}
		
	}

	public MainClass() {
		recognition= new HandGestureRecognition1();
		//setting the trackers
		tempGreen=new Point();
		tempGreen.setX(10);
		tempGreen.setY(10);
		
		previousRed = new Point();
		previousGreen=new Point();
		
		previousGreen.x=-1;
		previousRed.x = -1;
		
		tempRed=new Point();
		tempRed.setX(10);
		tempRed.setY(10);
		
		redTracker = new TrackerHSB1();
		//---------------red tracker attribute-------------------------------------------------
		redTracker.setPeakThresholdPercent(20);
		redTracker.setCentroidThresholdPercent(40);
		redTracker.setLimit(700);
		redTracker.setHue(0);
		//redTracker.setHueRange(8);
		redTracker.setHueGreater(345);
		//--------day---------------
		//redTracker.setSaturation(97);
		//-------night--------------
		redTracker.setSaturation(80);
		
		
		
		//------green tracker attribute------------------------------------------------------
		
		greenTracker=new TrackerHSB1();
		greenTracker.setPeakThresholdPercent(20);
		greenTracker.setCentroidThresholdPercent(40);
		greenTracker.setLimit(2000);
		greenTracker.setHue(190);
		greenTracker.setHueGreater(360);
		//-----------day--------------
		greenTracker.setSaturation(80);
		//-----------night------------
		//greenTracker.setSaturation(75);
		
		
		setPreferredSize(new Dimension(600, 500));
		setLayout(new BorderLayout());
		camera = new Camera();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imgpanel_2=new ImagePanel();
		imgpanel_2.setPreferredSize(new Dimension(640, 480));
		add(imgpanel_2, BorderLayout.LINE_START);
		Thread showImage=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(isRunning){
							bi=camera.captureImage();
							imgpanel_2.setImage(bi);
						}
						
			}
		}) ;
		showImage.start();
		imgpanel = new ImagePanel();

		imgpanel.setPreferredSize(new Dimension(640, 480));
		imgpanel.setBorder(BorderFactory.createBevelBorder(SOMEBITS));
		// imgpanel.set
		add(imgpanel, BorderLayout.CENTER);
		// buttonCapture.setSize(100, 50);
		buttonCapture = new JButton("capture");
		resetButton = new JButton("reset");

		// buttonCapture.setPreferredSize(new Dimension(100,25));
		// resetButton.setPreferredSize(new Dimension(100,25));
		add(resetButton, BorderLayout.PAGE_START);
		add(buttonCapture, BorderLayout.PAGE_END);

		buttonCapture.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				buttonPressed = false;

			}
		});
		
	}

	public class ImagePanel extends JPanel {
		public Image myimg = null;

		public ImagePanel() {
			setLayout(null);
			setSize(300, 300);
		}

		public void setImage(Image img) {
			this.myimg = img;
			repaint();

		}

		public void update(Graphics g){
			paint(g);
		}

		@Override
		public void paint(Graphics g) {

			// super.repaint();
			if (myimg != null) {
				g.drawImage(myimg, 0, 0, this);
			}
			// update(g);
		}

	}
	private   void trackedPointManupulation() {
		
		// TODO Auto-generated method stub
	    
		
		//process parameters
        double dt = 50d;
        double processNoiseStdev = 4;
        double measurementNoiseStdev = 5000;
        double m = 0;
        double  mouseX=15,mouseY=.5;
        //init filter
        redKF = KalmanFilter.buildKF(0, 0, dt, pow(processNoiseStdev, 2) / 2, pow(measurementNoiseStdev, 2));
        redKF.setX(new Matrix(new double[][]{{mouseX}, {0}, {mouseY}, {0}}));
        greenKF = KalmanFilter.buildKF(0, 0, dt, pow(processNoiseStdev, 2) / 2, pow(measurementNoiseStdev, 2));
        greenKF.setX(new Matrix(new double[][]{{mouseX}, {0}, {mouseY}, {0}}));
        //------writing to the file--------------
        
		  FileWriter f = null; 
		  try { f = new FileWriter("output.txt"); } catch
		 (IOException e) { // TODO Auto-generated catch block
		  e.printStackTrace(); }
		 

		 pw = new PrintWriter(f);
         pw.println("time Ttime Dtime Real X  Y  Detected X Y    velocity");
        //-------for stopwatch---------------
        
        long systemStart=System.currentTimeMillis(),te=0;
        Graphics g;String str;
		float elapsed=0,totalTime=0,RealPrestenT=0,RealPrevT=0;
         int c=0;
         //im = camera.captureImage();
        // takeFrame=true;
		while (isRunning) {

			te=System.currentTimeMillis();
			/*if(!imCapture.isAlive()){
				 imCapture=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(takeFrame){
						im = camera.captureImage();
						detectRed=true;
						takeFrame=false;
						}
					}
				});
				imCapture.start();
			}*/
			//if(takeFrame)
			im = camera.captureImage();
			System.out.println("elapd:"+(System.currentTimeMillis()-te)/1000F);
			if(!greenThread.isAlive()){
				greenThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method st
	//------------------------ detecting Green --------------------------------------------
						detectMarker(greenTracker,greenKF,previousGreen,tempGreen);
						
					}
				});
				//greenThread.setPriority(Thread.NORM_PRIORITY+4);
				greenThread.start();
			}
// ----------------------detecting red ------------------------------------------------------
			StartTime=System.currentTimeMillis();
			detectMarker(redTracker,redKF,previousRed,tempRed);
			endTime=System.currentTimeMillis();
			/*if(!redThread.isAlive()){
				redThread=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						StartTime=System.currentTimeMillis();
						if(detectRed){
							detectMarker(redTracker,redKF,previousRed,tempRed);
							takeFrame=true;
							detectRed=false;
						}
						endTime=System.currentTimeMillis();
						
					}
				});
				redThread.start();
			}
			*/
			
		//--------------for showing purpose-----------------------------------
		if(!showDetectedMarker.isAlive()){
			 showDetectedMarker=new Thread(new Runnable(){
				public void run(){
					 //--------Coloring of detected Point------------------------------------------------
					trackedImage(im, tempRed,0);
					trackedImage(im, tempGreen,503939);
                   //--------Showing the image---------------------------------------------------------
					imgpanel.setImage(im);
				}
			});
			showDetectedMarker.start();
		}
		//--------------------------------------------------------------------------
		//--------------for hand recognition-----------------------------------------
		if(!handRecog.isAlive()){
			handRecog=new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
                   //------set the present cursor-------------
					recognition.setPresentCursor(tempRed, tempGreen);
					
					System.out.println("set");
					
				}
			});
			//handRecog.setPriority(Thread.NORM_PRIORITY+2);
			handRecog.start();
		}
			//------stop watch-------------------------------------------
			c++;
			if(c==20)StartTime=System.currentTimeMillis();
			if(c==30){
				c=0;
				System.out.println("time elapsed:"+(System.currentTimeMillis()-StartTime)/1000F);
			}
			
			
			
		//----------------------writing text on image----------------------------------------
			elapsed=(endTime-StartTime)/1000F;
			totalTime+=elapsed;
			str="frame"+i+"   "+"toalTime:"+totalTime+"    elapsed:"+elapsed;
			g = im.getGraphics();
		    g.setFont(g.getFont().deriveFont(20f));
		    g.drawString(str, 100, 50);
		    RealPrestenT=(System.currentTimeMillis()-systemStart)/1000F;
		    str="real time elapsed="+(System.currentTimeMillis()-systemStart)/1000F;
		    g.drawString(str, 100, 100);
		    g.dispose();
		    /*if((RealPrestenT-RealPrevT)!=0){
		    pw.println(""+i+"		"+totalTime+"		"+elapsed+ "		"+previousRed.getX()+" "+previousRed.getY()
		    		+"		"+tempRed.getX()+" "+tempRed.getY()+"		"+getDistance(previousRed,tempRed)/(RealPrestenT-RealPrevT));
		    RealPrevT=RealPrestenT;}
		    else pw.println("zero");*/
		    pw.println(i+"  "+previousRed.getX()+" "+previousRed.getY()+"		"+(int)redKF.getX().get(0,0)+" "+(int)redKF.getX().get(1,0)+";");
		//-----------------------saving every images---------------------------------------------------------
		    i++;
		    if(!writing.isAlive()){
			    writing=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							ImageIO.write((RenderedImage) im, "jpg", new File("E:" + "\\save"
									+ "\\" +"img"+i+ ".jpg"));
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							System.out.println("failed to write");
							e1.printStackTrace();
						}
						
					}
				});
				writing.start();
		    }
			
		}
	   //--------------Stopping the Camera----------------------------------------------------------------------
		
		System.out.println("closeing---");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		camera.stop();
		
	}
	public void detectMarker(TrackerHSB1 tracker,KalmanFilter kf,Point previous,Point temp){
		tracker.setImage(im);
	    //tracker.test();
		Point cur = null;
		System.out.println("prev.x "+previous.getX());
		if(previous.getX()==-1){
			kf.predict();
			cur=tracker.trackMarker();
			previous.setX(cur.getX());previous.setY(cur.getY());
			//kf.correct(new Matrix(new double[][]{{previous.getX()}, {previous.getY()}}));
		}
		else{
			kf.predict();
			/*previous.setX((int)kf.getX().get(0,0));
			previous.setY((int)kf.getX().get(1,0));*/
			cur=tracker.sayemSearch(previous);
			//cur=tracker.trackMarker();
			previous.setX(cur.getX());previous.setY(cur.getY());
			
		}
		if(previous.getX()!=-1){
			kf.correct(new Matrix(new double[][]{{previous.getX()}, {previous.getY()}}));
			temp.setX((int)kf.getX().get(0,0));
			temp.setY((int)kf.getX().get(1,0));
		}
		else{
			/*kf.correct(new Matrix(new double[][]{{10}, {10}}));
			temp.setX((int)kf.getX().get(0,0));
			temp.setY((int)kf.getX().get(1,0));*/
			temp.setX(10);
			temp.setY(10);
		}
		
	}
	public double getDistance(Point p1, Point p2) {
		double dist = 0;
		dist = Math.sqrt((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())
				+ (p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		return dist;
	}
	
	

}
