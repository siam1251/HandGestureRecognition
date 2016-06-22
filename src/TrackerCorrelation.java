import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;


public class TrackerCorrelation {
	public int[][][] mask;
	public int maskH;
	public int maskW;
	public int imageH;
	public int imageW;
	public int maskMean;
	public int range;
	BufferedImage image;
	public int[][][] imageArray;
	public static int sakelValue;
	public double A; 
	public double temprow, tempcollum; 
	public TrackerCorrelation() {
		System.out.println("constru");
		int x, y;
		range = 20;
		BufferedImage maskIm = null;
		try {
			maskIm = ImageIO.read(new File("E:\\save\\marker1.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		maskW = maskIm.getWidth(null);
		maskH = maskIm.getHeight(null);
		
		 A = maskW * maskH;
		System.out.println("W" + maskW + "h" + maskH);
		mask = new int[maskH][maskW][2];
		int sum = 0, count = 0;
		for (y = 0; y < maskH; y++) {
			for (x = 0; x < maskW; x++) {
				int rgb = maskIm.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				//int g = (rgb >> 8) & 0xff;
				//int b = (rgb) & 0xff;
				mask[y][x][0] = r;
				//mask[y][x][1] = g;
				// sum += mask[y][x][0];
				// count++;
				// System.out.print(r + " ");
			}
		}
		// System.out.println();

		/*
		 * int mean = sum / (count); maskMean = mean; int sqrSum = 0;
		 * 
		 * 
		 * for (y = 0; y < maskH; y++) { for (x = 0; x < maskW; x++) {
		 * mask[y][x] = mask[y][x] - mean; sqrSum+=(mask[y][x])*(mask[y][x]);
		 * System.out.print((mask[y][x]+mean)+" "); } System.out.println();
		 * 
		 * }
		 */

		// System.out.println("sqrSum" + sqrSum);

	}

	public void setImage(BufferedImage im) {
		// TODO Auto-generated method stub
		image = im;
		imageH = image.getHeight(null);
		imageW = image.getWidth(null);
		imageArray = new int[imageH][imageW][2];
		int x, y;
		for (y = 0; y < imageH; y++) {
			for (x = 0; x < imageW; x++) {
				int rgb = im.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				//int g = (rgb >> 8) & 0xff;
				//int b = (rgb) & 0xff;
				imageArray[y][x][0] = r;
				//imageArray[y][x][1] = g;
			}
		}

	}
	
	public Point searchCorrelation(){
		Point p = new Point();
		FileWriter f=null;
		try {
			 f=new FileWriter("cor.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw=new PrintWriter(f);
		p.setX(10);
		int i, j = 0, k, l;
		double peak = 0, value = 0;
		
		
		for (i = maskH / 2; i < imageH - maskH / 2; i++) {
			for (j = maskW / 2; j < imageW - maskW / 2; j++) {
				
				value = correlation(i,j); 
				if(value >= peak) {peak = value; p.setX(j); p.setY(i);}
				pw.print(value+" ");
				
			}
			pw.println();
			//sayem
			
			}
				
	    pw.close();
		return p;
	}
	
	public double correlation ( int i , int j){
		
		int k,l,y,x;
		double value = 0,value1 = 0, value2 = 0, value3= 0, value4 = 0, value5=0,value6=0,cor1=0,cor2=0,sdSubImage = 0,sdTemplate = 0;
		
		for (k = -maskH / 2, y= 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				
				value += (imageArray[i + k][ j + l][0] * mask[y][x][0]);
				value1 += (imageArray[i + k][ j + l][0]);
				value2 += (mask[y][x][0]);
				value3 += (imageArray[i + k][ j + l][0]*imageArray[i + k][ j + l][0]);
				//value4 += (imageArray[i + k][ j + l][0]);
				value5 += (mask[y][x][0]*mask[y][x][0]);
				//value6 += (mask[y][x][0]);
			}
			
			
		}
		cor1 = value;
		cor2 = value1 * value2;
		sdSubImage = Math.sqrt(A*value3 - value1*value1);
		sdTemplate = Math.sqrt(A*value5 - value2*value2);
		
		return ((A*cor1)-cor2)/(sdSubImage*sdTemplate);
	}
	
	/*
	public double cor2 ( int i , int j){
		
		int k,l,y,x;
		double value1 = 0, value2 = 0;
		
		for (k = -maskH / 2, y= 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				
				value1 += (imageArray[i + k][ j + l][0]);
				value2 += (mask[y][x][0]);
			}
		}
		
		return value1 * value2;
	}
	
	public double SDsubImage( int i , int j){
		int k,l,y,x;
		double value1 = 0, value2 = 0;
		
		for (k = -maskH / 2, y= 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				
				value1 += (imageArray[i + k][ j + l][0]*imageArray[i + k][ j + l][0]);
				value2 += (imageArray[i + k][ j + l][0]);
			}
		}
		
		return Math.sqrt(A*value1 - value2*value2);
	}
	
	public double SDtemplate( int i , int j){
		int k,l,y,x;
		double value1 = 0, value2 = 0;
		
		for (k = -maskH / 2, y= 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				
				value1 += (mask[y][x][0]*mask[y][x][0]);
				value2 += (mask[y][x][0]);
			}
		}
		
		return Math.sqrt(A*value1 - value2*value2);
	}*/
	
	}

