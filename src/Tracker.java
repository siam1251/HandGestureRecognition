import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class Tracker {
	public int[][][] mask;
	public int maskH;
	public int maskW;
	public int imageH;
	public int imageW;
	public int maskMean;
	public int range;
	public int peakThreshold;
	BufferedImage image;
	public int[][][] imageArray;
	public static int sakelValue;

	public Tracker() {
		System.out.println("constru");
		int x, y;
		range = 20;
		peakThreshold = 100;
		/*
		 * BufferedImage maskIm = null; try { maskIm = ImageIO.read(new
		 * File("E:\\save\\marker2.jpg")); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		maskW = 40;
		maskH = 40;
		/*
		 * System.out.println("W" + maskW + "h" + maskH); mask = new
		 * int[maskH][maskW][2]; int sum = 0, count = 0; for (y = 0; y < maskH;
		 * y++) { for (x = 0; x < maskW; x++) { int rgb = maskIm.getRGB(x, y);
		 * int r = (rgb >> 16) & 0xff; int g = (rgb >> 8) & 0xff; //int b =
		 * (rgb) & 0xff; mask[y][x][0] = r; mask[y][x][1] = g; // sum +=
		 * mask[y][x][0]; // count++; // System.out.print(r + " "); } } //
		 * System.out.println();
		 * 
		 * 
		 * int mean = sum / (count); maskMean = mean; int sqrSum = 0;
		 * 
		 * 
		 * for (y = 0; y < maskH; y++) { for (x = 0; x < maskW; x++) {
		 * mask[y][x] = mask[y][x] - mean; sqrSum+=(mask[y][x])*(mask[y][x]);
		 * System.out.print((mask[y][x]+mean)+" "); } System.out.println();
		 * 
		 * }
		 * 
		 * 
		 * // System.out.println("sqrSum" + sqrSum);
		 */
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
				int g = (rgb >> 8) & 0xff;
				// int b = (rgb) & 0xff;
				imageArray[y][x][0] = r;
				imageArray[y][x][1] = g;
			}
		}

	}

	public Point trackMarker() {
		Point p = new Point();
		int i, j = 0, k, l;
		int peak = 0, value = 0;
		for (i = maskH / 2; i < imageH - maskH / 2; i++) {
			for (j = maskW / 2; j < imageW - maskW / 2; j++) {
				int imgSum = 0, count = 0, mean = 0;

				for (k = -maskH / 2; k < maskH / 2; k++) {
					for (l = -maskW / 2; l < maskW / 2; l++) {

						int r = imageArray[i + k][j + l][0];
						int g = imageArray[i + k][j + l][1];

						if (Math.abs(mask[maskH / 2 + k][maskW / 2 + l][0] - r) <= range
								&& Math.abs(mask[maskH / 2 + k][maskW / 2 + l][1]
										- g) <= range) {
							value++;
						}
						// System.out.println(value);
					}
				}
				if (peak < value) {
					peak = value;
					p.setY(i);
					p.setX(j);
				}

				value = 0;
			}

		}
		sakelValue = peak;
		System.out
				.print("matched=" + peak + " x= " + p.x + " y= " + p.y + "  ");

		return p;
	}

	public Point sayemSearch(Point previous) {
		/*
		 * FileWriter file=null; try { file=new FileWriter("output1.txt"); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } PrintWriter pw=new PrintWriter(file);
		 */
		int i = 0, neighbour = 2, j, k, l, y, x;

		int peak = 0, value = 0, leftX, leftY, rightX, rightY;
		Point p = new Point();
		// setting the search area
		for (neighbour = 0;; neighbour += 2) {
			leftX = previous.x - neighbour;
			leftY = previous.y - neighbour;
			rightX = previous.x + neighbour;
			rightY = previous.y + neighbour;
			// System.out.println("leftX="+leftX+"leftY="+leftY);
			// System.out.println("rightX="+rightX+"rightY="+rightY);
			// System.out.println("up="+(imageH-maskH/2));
			if (leftX < maskW / 2 && leftY < maskH / 2
					&& rightY > (imageH - maskH / 2)
					&& rightX > (imageW - maskW / 2)) {
				// System.out.println("break");
				// System.out.println("y"+(imageH-maskH/2));
				// System.out.println("x"+(imageW-maskW/2));
				break;
			}

			if (leftX < maskW / 2)
				leftX = maskW / 2;
			if (rightX > imageW - maskW / 2)
				rightX = imageW - maskW / 2;
			if (leftY < maskH / 2)
				leftY = maskH / 2;
			if (rightY > imageH - maskH / 2)
				rightY = imageH - maskH / 2;
			// upper row start scanning
			k = previous.y - neighbour;
			// j is x axis
			// k is y axis
			if (k > maskH / 2) {
				// pw.println("upper");
				for (j = leftX; j <= rightX; j++) {
					// pw.print(j+" ");
					// points comparing with mask
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {
							int r = imageArray[leftY + k][j + l][0];
							int g = imageArray[leftY + k][j + l][1];
							if (Math.abs(mask[maskH / 2 + k][maskW / 2 + l][0]
									- r) <= range
									&& Math.abs(mask[maskH / 2 + k][maskW / 2
											+ l][1]
											- g) <= range) {
								value++;
							}
							// System.out.println(value);
						}
					}
					/*
					 * if (peak < value) { peak = value; p.setY(leftY);
					 * p.setX(j); }
					 */
					if (Math.abs(sakelValue - value) <= peakThreshold) {
						p.setY(leftY);
						p.setX(j);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;

				}
			}
			// upper row scan ended

			// lower row start scanning
			// j is x axis and k is y axis
			k = previous.y + neighbour;
			if (k < imageH - maskH / 2) {
				// pw.println("lower");
				for (j = leftX; j <= rightX; j++) {
					// pw.print(j+" ");
					// points comparing with mask
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {
							int r = imageArray[rightY + k][j + l][0];
							int g = imageArray[rightY + k][j + l][1];
							if (Math.abs(mask[maskH / 2 + k][maskW / 2 + l][0]
									- r) <= range
									&& Math.abs(mask[maskH / 2 + k][maskW / 2
											+ l][1]
											- g) <= range) {
								value++;
							}
							// System.out.println(value);
						}
					}
					/*
					 * if (peak < value) { peak = value; p.setY(rightY);
					 * p.setX(j); }
					 */
					if (Math.abs(sakelValue - peak) <= peakThreshold) {
						p.setX(j);
						p.setY(rightY);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;

				}
			}
			// lower row scan ended
			// left column scan starts
			k = previous.x - neighbour;
			if (k > maskW / 2) {
				// pw.println("left");
				for (i = leftY + 1; i < rightY - 1; i++) {
					// pw.print(i+" ");
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {

							int r = imageArray[i + k][leftX + l][0];
							int g = imageArray[i + k][leftX + l][1];

							if (Math.abs(mask[maskH / 2 + k][maskW / 2 + l][0]
									- r) <= range
									&& Math.abs(mask[maskH / 2 + k][maskW / 2
											+ l][1]
											- g) <= range) {
								value++;
							}
							// System.out.println(value);
						}
					}
					if (peak < value) {
						peak = value;
						p.setY(i);
						p.setX(leftX);
					}
					if (Math.abs(sakelValue - peak) <= peakThreshold) {
						p.setX(leftX);
						p.setY(i);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;
				}
			}
			// j is x axis and k is y axis

			// left column scan ended
			// right column scan starts
			k = previous.x + neighbour;
			if (k < imageW - maskW / 2) {
				// pw.println("right");
				for (i = leftY + 1; i < rightY - 1; i++) {
					// pw.print(i+" ");
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {

							int r = imageArray[i + k][rightX + l][0];
							int g = imageArray[i + k][rightX + l][1];

							if (Math.abs(mask[maskH / 2 + k][maskW / 2 + l][0]
									- r) <= range
									&& Math.abs(mask[maskH / 2 + k][maskW / 2
											+ l][1]
											- g) <= range) {
								value++;
							}
							// System.out.println(value);
						}
					}
					if (peak < value) {
						peak = value;
						p.setY(i);
						p.setX(rightX);
					}

					if (Math.abs(sakelValue - peak) <= peakThreshold) {
						p.setX(rightX);
						p.setY(i);
						System.out.println("p " + peak);
						return p;
					}

					value = 0;
				}
			}
			// j is x axis and k is y axis

			// right column scan ended
			if (Math.abs(sakelValue - peak) <= peakThreshold) {
				System.out.println("sakelvalue: " + sakelValue);
				System.out.println("peak: " + peak);
				return p;

			}

		}
		// pw.close();
		// p.x=-1;
		System.out.println("Whole scanned pValue" + peak);
		return p;
	}

	public Point SearchNeighbour(Point previous) {
		/*
		 * FileWriter file=null; try { file=new FileWriter("output1.txt"); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } PrintWriter pw=new PrintWriter(file);
		 */
		int i = 0, neighbour = 2, j, k, l, y, x;
		int peak = 0, value = 0, leftX, leftY, rightX, rightY;
		Point p = new Point();
		// setting the search area
		for (neighbour = 0;; neighbour += 1) {
			leftX = previous.x - neighbour;
			leftY = previous.y - neighbour;
			rightX = previous.x + neighbour;
			rightY = previous.y + neighbour;
			// System.out.println("leftX="+leftX+"leftY="+leftY);
			// System.out.println("rightX="+rightX+"rightY="+rightY);
			// System.out.println("up="+(imageH-maskH/2));
			if (leftX < maskW / 2 && leftY < maskH / 2
					&& rightY > (imageH - maskH / 2)
					&& rightX > (imageW - maskW / 2)) {
				// System.out.println("break");
				// System.out.println("y"+(imageH-maskH/2));
				// System.out.println("x"+(imageW-maskW/2));
				break;
			}

			if (leftX < maskW / 2)
				leftX = maskW / 2;
			if (rightX > imageW - maskW / 2)
				rightX = imageW - maskW / 2;
			if (leftY < maskH / 2)
				leftY = maskH / 2;
			if (rightY > imageH - maskH / 2)
				rightY = imageH - maskH / 2;
			// upper row start scanning
			k = previous.y - neighbour;
			// j is x axis
			// k is y axis
			if (k > maskH / 2) {
				// pw.println("upper");
				for (j = leftX; j <= rightX; j++) {
					// pw.print(j+" ");
					// points comparing with mask
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {
							int r = imageArray[leftY + k][j + l][0];
							int g = imageArray[leftY + k][j + l][1];
							if (mask[maskH / 2 + k][maskW / 2 + l][0] <= 100
									&& mask[maskH / 2 + k][maskW / 2 + l][0] >= 90
									&& mask[maskH / 2 + k][maskW / 2 + l][1] <= 30
									&& mask[maskH / 2 + k][maskW / 2 + l][1] >= 20) {
								value++;
							}
							// System.out.println(value);
						}
					}
					/*
					 * if (peak < value) { peak = value; p.setY(leftY);
					 * p.setX(j); }
					 */
					if (Math.abs(sakelValue - value) <= 10) {
						p.setY(leftY);
						p.setX(j);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;

				}
			}
			// upper row scan ended

			// lower row start scanning
			// j is x axis and k is y axis
			k = previous.y + neighbour;
			if (k < imageH - maskH / 2) {
				// pw.println("lower");
				for (j = leftX; j <= rightX; j++) {
					// pw.print(j+" ");
					// points comparing with mask
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {
							int r = imageArray[rightY + k][j + l][0];
							int g = imageArray[rightY + k][j + l][1];
							if (mask[maskH / 2 + k][maskW / 2 + l][0] <= 100
									&& mask[maskH / 2 + k][maskW / 2 + l][0] >= 90
									&& mask[maskH / 2 + k][maskW / 2 + l][1] <= 30
									&& mask[maskH / 2 + k][maskW / 2 + l][1] >= 20) {
								value++;
							}
							// System.out.println(value);
						}
					}
					/*
					 * if (peak < value) { peak = value; p.setY(rightY);
					 * p.setX(j); }
					 */
					if (Math.abs(sakelValue - peak) <= 10) {
						p.setX(j);
						p.setY(rightY);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;

				}
			}
			// lower row scan ended
			// left column scan starts
			k = previous.x - neighbour;
			if (k > maskW / 2) {
				// pw.println("left");
				for (i = leftY + 1; i < rightY - 1; i++) {
					// pw.print(i+" ");
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {

							int r = imageArray[i + k][leftX + l][0];
							int g = imageArray[i + k][leftX + l][1];

							if (mask[maskH / 2 + k][maskW / 2 + l][0] <= 100
									&& mask[maskH / 2 + k][maskW / 2 + l][0] >= 90
									&& mask[maskH / 2 + k][maskW / 2 + l][1] <= 30
									&& mask[maskH / 2 + k][maskW / 2 + l][1] >= 20) {
								value++;
							}
							// System.out.println(value);
						}
					}
					if (peak < value) {
						peak = value;
						p.setY(i);
						p.setX(leftX);
					}
					if (Math.abs(sakelValue - peak) <= 10) {
						p.setX(leftX);
						p.setY(i);
						System.out.println("p " + peak);
						return p;
					}
					value = 0;
				}
			}
			// j is x axis and k is y axis

			// left column scan ended
			// right column scan starts
			k = previous.x + neighbour;
			if (k < imageW - maskW / 2) {
				// pw.println("right");
				for (i = leftY + 1; i < rightY - 1; i++) {
					// pw.print(i+" ");
					for (k = -maskH / 2; k < maskH / 2; k++) {
						for (l = -maskW / 2; l < maskW / 2; l++) {

							int r = imageArray[i + k][rightX + l][0];
							int g = imageArray[i + k][rightX + l][1];

							if (mask[maskH / 2 + k][maskW / 2 + l][0] <= 100
									&& mask[maskH / 2 + k][maskW / 2 + l][0] >= 90
									&& mask[maskH / 2 + k][maskW / 2 + l][1] <= 30
									&& mask[maskH / 2 + k][maskW / 2 + l][1] >= 20) {
								value++;
							}
							// System.out.println(value);
						}
					}
					if (peak < value) {
						peak = value;
						p.setY(i);
						p.setX(rightX);
					}

					if (Math.abs(sakelValue - peak) <= 10) {
						p.setX(rightX);
						p.setY(i);
						System.out.println("p " + peak);
						return p;
					}

					value = 0;
				}
			}
			// j is x axis and k is y axis

			// right column scan ended
			if (Math.abs(sakelValue - peak) <= 10) {
				System.out.println("sakelvalue: " + sakelValue);
				System.out.println("peak: " + peak);
				return p;

			}

		}
		// pw.close();
		// p.x=-1;
		System.out.println("Whole scanned pValue" + peak);
		return p;

	}
	

}
