import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class TrackerCorrelation2 {
	public float[][][] mask;
	public int maskH;
	public int maskW;
	public int imageH;
	public int imageW;
	public float maskMean;
	public double maskSD;
	public double imageSD;
	public float range;
	BufferedImage image;
	public float count;
	public float[][][] imageArray;
	public static float sakelValue;
	public double A;
	public double temprow, tempcollum;

	public TrackerCorrelation2() {
		System.out.println("constru");
		int x, y;
		range = 20;
		maskSD = 0;
		BufferedImage maskIm = null;
		try {
			maskIm = ImageIO.read(new File("E:\\save\\marker3.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		maskW = maskIm.getWidth(null);
		maskH = maskIm.getHeight(null);

		A = maskW * maskH;
		System.out.println("W" + maskW + "h" + maskH);
		mask = new float[maskH][maskW][2];
		float sum = 0;
		count = 0;
		for (y = 0; y < maskH; y++) {
			for (x = 0; x < maskW; x++) {
				int rgb = maskIm.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				// int b = (rgb) & 0xff;
				mask[y][x][0] = r;
				// mask[y][x][1] = g;
				sum += r;
				count++;
				// System.out.print(r + " ");
			}
		}
		maskMean = sum / count;
		for (y = 0; y < maskH; y++) {
			for (x = 0; x < maskW; x++) {
				//int rgb = maskIm.getRGB(x, y);
				//int r = (rgb >> 16) & 0xff;
				 //int g = (rgb >> 8) & 0xff;
				// int b = (rgb) & 0xff;
				mask[y][x][0] = mask[y][x][0] - maskMean;
				maskSD += (mask[y][x][0]) * (mask[y][x][0]);
				// mask[y][x][1] = g;
				// System.out.print(r + " ");
			}
		}
		maskSD = (float) Math.sqrt(maskSD);

	}

	public void setImage(BufferedImage im) {
		// TODO Auto-generated method stub
		image = im;
		imageH = image.getHeight(null);
		imageW = image.getWidth(null);
		imageArray = new float[imageH][imageW][2];
		int x, y;
		for (y = 0; y < imageH; y++) {
			for (x = 0; x < imageW; x++) {
				int rgb = im.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				// int b = (rgb) & 0xff;
				imageArray[y][x][0] = r;
				// imageArray[y][x][1] = g;
			}
		}

	}

	public Point searchCorrelation() {
		Point p = new Point();
		FileWriter f = null;
		try {
			f = new FileWriter("cor.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(f);
		p.setX(10);
		int i, j = 0, k, l;
		double peak = 0, value = 0;

		for (i = maskH / 2; i < imageH - maskH / 2; i++) {
			for (j = maskW / 2; j < imageW - maskW / 2; j++) {

				value = correlation(i, j);
				pw.print(value + " ");
				if (value >= peak) {
					peak = value;
					p.setX(j);
					p.setY(i);
				}

			}
			pw.println();
			// sayem

		}
		System.out.println("peak="+peak);
		pw.close();
		return p;
	}

	public double correlation(int i, int j) {

		int k, l, y, x;
		double imgMean = 0, imgSum = 0, cor = 0, value3 = 0, value4 = 0, value5 = 0, value6 = 0, cor1 = 0, cor2 = 0, sdSubImage = 0, sdTemplate = 0;

		for (k = -maskH / 2, y = 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				imgSum += imageArray[i + k][j + l][0];

			}

		}
		imgMean = imgSum / count;
		imageSD = 0;
		for (k = -maskH / 2, y = 0; k < maskH / 2; k++, y++) {
			for (l = -maskW / 2, x = 0; l < maskW / 2; l++, x++) {
				cor += (imageArray[i + k][j + l][0] - imgMean)
						* mask[k + maskH / 2][l + maskW / 2][0];
				imageSD += (imageArray[i + k][j + l][0] - imgMean)
						* (imageArray[i + k][j + l][0] - imgMean);

			}

		}
		imageSD= Math.sqrt(imageSD);

		return ((cor) / (imageSD * maskSD));
	}

}
