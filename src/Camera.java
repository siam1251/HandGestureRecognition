import java.awt.image.BufferedImage;

import com.googlecode.javacv.VideoInputFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;


public class Camera {
  public VideoInputFrameGrabber grabber ;
  public Camera(){
	  grabber=new VideoInputFrameGrabber(0);
	  try {
		grabber.start();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  
  }
  public BufferedImage captureImage(){
	  IplImage ipl = null;
	  try {
		 ipl=grabber.grab();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  BufferedImage bi=ipl.getBufferedImage();
	  return bi;
  }
  public void stop(){
	  try {
		grabber.stop();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.out.println("exception camara");
		e.printStackTrace();
	}
  }
}