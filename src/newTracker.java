import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class newTracker {
	public int[][][] mask;
	public int maskH;
	public int maskW;
	public int imageH;
	public int imageW;
	public int maskMean;
	public int hueRange;
	public int saturationRange;

	public Point previousP;
	public int peakThreshold;
	public int previouArea;
	public boolean firstTime = true;
	public int hue;
	public int saturation;
	public int centroidThreshold;
	public int incr;
	BufferedImage image;
	public int[][][] imageArray;
	public int[][] matchedValue;
	public boolean[][] check;
	// public boolean[][] visited;
	public int sakelValue;
	public int tempPeakValue;
	public int hueGreater;

	public newTracker() {
		System.out.println("constru");
		int x, y;
		incr = 5;
		// setting hue and saturation
		/*hue = 8;
		saturation = 71;// night
		// saturation=85//day
		hueGreater = 353;
		hueRange = 8;
		saturationRange = 11;*/

		peakThreshold = 350;
		centroidThreshold = peakThreshold;
		imageW = 640;
		imageH = 480;

		maskW = 41;
		maskH = 41;
		System.out.println("W" + maskW + "h" + maskH);
		previousP = new Point();
		previousP.setX(-1);

	}
	public void setHue(int tHue){
		this.hue=tHue;
	}
	public void setSaturation(int tSaturation){
		this.saturation=tSaturation;
	}
	public void setHueGreater(int tHueGreater){
		this.hueGreater=tHueGreater;
	}
	public void setHueRange(int tHueRange){
		this.hueRange=tHueRange;
	}
	public void setSaturationRange(int tSaturationRange){
		this.saturationRange=tSaturationRange;
	}
	

	public void setImage(BufferedImage im) {
		// TODO Auto-generated method stub
		image = im;

		imageH = image.getHeight(null);
		imageW = image.getWidth(null);
		matchedValue = new int[imageH][imageW];

		check = new boolean[imageH][imageW];
		imageArray = new int[imageH][imageW][2];

	}

	public void writeOnArray() {
		int x, y;
		System.out.println("hue="+hue+"saturation="+saturation);
		for (y = 0; y < imageH; y++) {
			for (x = 0; x < imageW; x++) {
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = (rgb) & 0xff;
				float[] hsb = new float[3];
				Color.RGBtoHSB(r, g, b, hsb);
				hsb[0] = hsb[0] * 360;
				hsb[1] = hsb[1] * 100;
				if (hsb[0] > hueGreater)
					hsb[0] = 360 - hsb[0];
				imageArray[y][x][0] = (int) (hsb[0]);
				imageArray[y][x][1] = (int) (hsb[1]);
			}
		}

	}

	public Point trackMarker() {
		//System.out.println("hue"+hue+"saturation"+saturation);
		firstTime = true;
		Point p = new Point();
		this.writeOnArray();
		int i, j = 0, k, l;
		int peak = 0, value = 0;
		boolean first = true;
		for (i = maskH / 2; i < imageH - maskH / 2 ; i += incr) {
			if (first == true) {
				matchedValue[i][maskW / 2] = curPointMatchedValue(i, maskW / 2);
				first = false;
			} else
				matchedValue[i][maskW / 2] = downPointMatchedValue(i,
						maskW / 2, incr);
			if (peak < matchedValue[i][maskW / 2]) {
				peak = matchedValue[i][maskW / 2];
				p.setY(i);
				p.setX((maskW / 2));
			}
			for (j = maskW / 2 + incr; j < imageW - maskW / 2 ; j += incr) {
				matchedValue[i][j] = rightPointMatchedValue(i, j, incr);
				//System.out.print(hue+" ");
				if (peak < matchedValue[i][j]) {
					//System.out.print("peak="+peak+"  ");
					peak = matchedValue[i][j];
					p.setY(i);
					p.setX(j);
				}
			}
			//System.out.println();

		}
		sakelValue = peak;
		System.out
				.print("matched=" + peak + " x= " + p.x + " y= " + p.y + "  ");
		p = this.getCentroid(p, peak);
		if (p.getX() == -1)
			return p;
		this.changeTemplate(p);
		// System.out.println("fL value"+matchedValue[200][233]);
		return p;
	}

	public Point sayemSearch(Point previous) {
		previousP = previous;
		// System.out.println("1st y="+previous.y+"x="+previous.x);
		int i = 0, j, k, l, y, x, h = 0, s = 0;

		int peak = 0, value = 0, leftX, leftY, rightX, rightY, neighbour = 0;
		Point p = new Point();
		// setting the search area
		for (neighbour = 0;; neighbour += incr) {
			leftX = previous.x - neighbour;
			leftY = previous.y - neighbour;
			rightX = previous.x + neighbour;
			rightY = previous.y + neighbour;
			// System.out.println("leftX="+leftX+"leftY="+leftY);
			// System.out.println("rightX="+rightX+"rightY="+rightY);
			// System.out.println("up="+(imageH-maskH/2));
			if (leftX < maskW / 2 && leftY < maskH / 2
					&& rightY > (imageH - maskH / 2 - 1)
					&& rightX > (imageW - maskW / 2 - 1)) {
				// System.out.println("break");
				// System.out.println("y"+(imageH-maskH/2));
				// System.out.println("x"+(imageW-maskW/2));
				break;
			}

			if (leftX < maskW / 2)
				leftX = maskW / 2;
			if (rightX > imageW - maskW / 2 - 1)
				rightX = imageW - maskW / 2 - 1;
			if (leftY < maskH / 2)
				leftY = maskH / 2;
			if (rightY > imageH - maskH / 2 - 1)
				rightY = imageH - maskH / 2 - 1;
			if (neighbour == 0) {
				matchedValue[previous.getY()][previous.getX()] = curPointMatchedValue(
						previous.getY(), previous.getX());

				if (Math.abs(sakelValue
						- matchedValue[previous.getY()][previous.getX()]) < peakThreshold) {

					// System.out.println("1st y="+p.y+"x="+p.x);
					/*
					 * System.out.println("p " +
					 * matchedValue[previous.getY()][previous.getX()]);
					 */

					p = this.getCentroid(previous, sakelValue);
					// System.out.println("1st y="+p.y+"x="+p.x);
					if (p.getX() == -1)
						return p;
					this.changeTemplate(p);
					/*
					 * System.out.println("first return sayem= value=" +
					 * matchedValue[previous.getY()][previous.getX()]);
					 */
					return p;
				} else if (peak < matchedValue[previous.getY()][previous.getX()]) {
					peak = matchedValue[previous.getY()][previous.getX()];
					p.setX(previous.getX());
					p.setY(previous.getY());
				} else
					continue;
			}
			// upper row start scanning
			k = previous.y - neighbour;

			// j is x axis
			// k is y axis
			// System.out.println("1st y="+leftY+"x="+leftX);
			if (k >= maskH / 2) {
				// pw.println("upper");
				matchedValue[leftY][leftX] = curPointMatchedValue(leftY, leftX);

				if (Math.abs(matchedValue[leftY][leftX] - sakelValue) < peakThreshold) {
					p.setY(leftY);
					p.setX(leftX);

					p = this.getCentroid(p, sakelValue);
					if (p.getX() == -1)
						return p;
					this.changeTemplate(p);
					/*
					 * System.out.println("second return bfupper value=+" +
					 * matchedValue[leftY][leftX]);
					 */
					return p;
				}
				if (matchedValue[leftY][leftX] > peak) {
					peak = matchedValue[leftY][leftX];
					p.setX(leftX);
					p.setY(leftY);

				}
				for (j = leftX + incr; j <= rightX; j += incr) {

					matchedValue[leftY][j] = rightPointMatchedValue(leftY, j,
							incr);

					if (Math.abs(sakelValue - matchedValue[leftY][j]) < peakThreshold) {

						p.setY(leftY);
						p.setX(j);
						// System.out.println("1st y="+p.y+"x="+p.x);
						System.out.println("p " + matchedValue[leftY][j]);

						p = this.getCentroid(p, sakelValue);
						// System.out.println("1st y="+p.y+"x="+p.x);
						if (p.getX() == -1)
							return p;
						this.changeTemplate(p);
						/*
						 * System.out.println("third return upper value=+" +
						 * matchedValue[leftY][j]);
						 */
						return p;
					}
					if (peak < matchedValue[leftY][j]) {
						peak = matchedValue[leftY][j];
						p.setY(leftY);
						p.setX(j);
					}

				}
			}
			// upper row scan ended

			// left column scan starts
			k = previous.x - neighbour;
			if (k >= maskW / 2) {
				// pw.println("left");

				/*if (matchedValue[leftY][leftX] == 0) {
					matchedValue[leftY][leftX] = curPointMatchedValue(leftY,
							leftX);
					System.out.println("leftC" + matchedValue[leftY][leftX]);
				}
*/
				for (i = leftY + incr; i <= rightY; i += incr) {
					// pw.print(i+" ");

					matchedValue[i][leftX] = downPointMatchedValue(i, leftX,
							incr);

					if (Math.abs(sakelValue - matchedValue[i][leftX]) < peakThreshold) {
						p.setX(leftX);
						p.setY(i);
						System.out.println("p " + matchedValue[i][leftX]);

						p = this.getCentroid(p, sakelValue);

						if (p.getX() == -1)
							return p;
						this.changeTemplate(p);
						/*
						 * System.out.println("4th return left value=+" +
						 * matchedValue[i][leftX]);
						 */
						return p;
					}
					if (peak < matchedValue[i][leftX]) {
						peak = matchedValue[i][leftX];
						p.setY(i);
						p.setX(leftX);
					}

				}
			}
			// j is x axis and k is y axis

			// left column scan ended
			// right column scan starts
			k = previous.x + neighbour;
			if (k <= imageW - maskW / 2 - 1) {
				// pw.println("right");

				/*if (matchedValue[leftY][rightX] == 0) {
					matchedValue[leftY][rightX] = curPointMatchedValue(leftY,
							leftX);
					System.out.println("rightC" + matchedValue[leftY][rightX]);
				}*/

				for (i = leftY + incr; i <= rightY; i += incr) {
					// pw.print(i+" ");

					matchedValue[i][rightX] = downPointMatchedValue(i, rightX,
							incr);

					if (Math.abs(sakelValue - matchedValue[i][rightX]) < peakThreshold) {
						p.setX(rightX);
						p.setY(i);
						System.out.println("p " + matchedValue[i][rightX]);

						p = this.getCentroid(p, sakelValue);
						if (p.getX() == -1)
							return p;
						this.changeTemplate(p);
						/*
						 * System.out.println("5th return right value=+" +
						 * matchedValue[i][rightX]);
						 */
						return p;
					}
					if (peak < matchedValue[i][rightX]) {
						peak = matchedValue[i][rightX];
						p.setY(i);
						p.setX(rightX);
					}

				}
			}
			// lower row start scanning
			// j is x axis and k is y axis
			k = previous.y + neighbour;
			if (k <= imageH - maskH / 2 - 1) {
				// pw.println("lower");
				for (j = leftX + incr; j < rightX - incr; j += incr) {
					// pw.print(j+" ");
					// points comparing with mask

					matchedValue[rightY][j] = rightPointMatchedValue(rightY, j,
							incr);

					if (Math.abs(sakelValue - matchedValue[rightY][j]) < peakThreshold) {
						p.setX(j);
						p.setY(rightY);
						System.out.println("p " + matchedValue[rightY][j]);

						p = this.getCentroid(p, sakelValue);
						if (p.getX() == -1)
							return p;
						this.changeTemplate(p);
						/*
						 * System.out.println("6th return bottom value=+" +
						 * matchedValue[rightY][j]);
						 */
						return p;
					}
					if (peak < matchedValue[rightY][j]) {
						peak = matchedValue[rightY][j];
						p.setY(rightY);
						p.setX(j);
					}

				}
			}
			// lower row scan ended

		}
		// pw.close();
		// p.x=-1;
		// System.out.println("sayem value="+matchedValue[200][233]);
		System.out.println("Whole scanned pValue" + peak);
		// sakelValue=sakelValue+(peak-sakelValue)*(20/100);
		System.out.println("www x=" + p.x + "y=" + p.y);
		// sakelValue = peak;
		p = this.getCentroid(p, peak);
		if (p.getX() == -1)
			return p;
		this.changeTemplate(p);
		return p;
	}

	public void changeTemplate(Point p) {
		if (p.x != 0)
			return;
		System.out.println("px=" + p.x + "py=" + p.y);
		int tHue = 0, tSaturation = 0;

		int k, l, x, y, h = 0, s = 0;
		x = p.getX();
		y = p.getY();
		for (k = -maskH / 2; k <= maskH / 2; k++) {
			for (l = -maskW / 2; l <= maskW / 2; l++) {
				if (check[k + y][x + l] == false) {
					int rgb = image.getRGB(x + l, k + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > hueGreater)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[k + y][x + l][0] = h;
					imageArray[k + y][x + l][1] = s;
					check[k + y][x + l] = true;
				} else {
					h = imageArray[k + y][x + l][0];
					s = imageArray[k + y][x + l][1];
				}
				tHue += h;
				tSaturation += s;

				// System.out.println(value);
			}
		}
		// System.out.println("count"+count);
		// System.out.println("updated hue="+hue+"satu="+saturation);
		tHue = tHue / (maskH * maskW);
		tSaturation = tSaturation / (maskH * maskW);

		hue = hue + (tHue - hue) * 5 / 100;
		saturation = saturation + (tSaturation - saturation) * 5 / 100;
		System.out.println("updated hue=" + hue + "satu=" + saturation);

	}

	public Point getCentroid(Point p, int pValue) {
		//if(pValue!=0)return p;
		/*
		 * System.out.println("called Y=" + p.y + "X=" + p.x + "sakel=" +
		 * sakelValue);
		 */
		/*
		 * FileWriter f = null; try { f = new FileWriter("output.txt"); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// PrintWriter pw = new PrintWriter(f);
		System.out.println("sakel="+sakelValue);

		// if (pValue > -1) return p;
		LinkedList<Point> list = new LinkedList<Point>();
		// int localIncr=3;
		boolean[][] visited = new boolean[imageH][imageW];

		int i = 0, k, l, sum = 0, h = 0, s = 0;
		int tempX = 0, tempY = 0, centX = 0, centY = 0;
		int[] x = { -1, 1, -1, 0, 1, -1, 0, 1 };
		int[] y = { 0, 0, -1, -1, -1, 1, 1, 1 };
		list.addLast(p);
		Point curP;
		int count = 0;
		// matchedValue[p.getY()][p.getX()]=curPointMatchedValue(p.getY(),p.getX());
		// int depth = 0;
		while (list.isEmpty() == false) {
			curP = list.removeFirst();
			visited[curP.getY()][curP.getX()] = true;
			// depth++;

			// matchedValue[tempY][tempX]=
			for (i = 0; i < 8; i++) {

				tempX = curP.getX() + x[i];
				tempY = curP.getY() + y[i];
				if (tempY > imageH - maskH / 2 - 1 || tempY < maskH / 2
						|| tempX > imageW - maskW / 2 - 1 || tempX < maskW / 2)
					continue;
				if (visited[tempY][tempX] == true) {
					continue;
				} else
					visited[tempY][tempX] = true;

				if (matchedValue[tempY][tempX] == 0) {
					if (i >= 2 && i <= 4) {
						//System.out.println("tempX="+tempX+"tempY="+tempY);
						matchedValue[tempY][tempX] = topPointMatchedValue(
								tempY, tempX, 1);
						/*
						 * System.out.println("m=" + matchedValue[tempY][tempX]
						 * + "c" + curPointMatchedValue(tempY, tempX));
						 */
						// System.out.print(matchedValue[tempY][tempX]+"  ");
					} else if (i == 0) {
						matchedValue[tempY][tempX] = leftPointMatchedValue(
								tempY, tempX, 1);

						// System.out.print(matchedValue[tempY][tempX]+"  ");
					} else if (i == 1) {
						matchedValue[tempY][tempX] = rightPointMatchedValue(
								tempY, tempX, 1);

						// System.out.print(matchedValue[tempY][tempX]+"  ");
					} else {
						matchedValue[tempY][tempX] = downPointMatchedValue(
								tempY, tempX, 1);

						// System.out.print(matchedValue[tempY][tempX]+"  ");
					}
				}
				// System.out.print(matchedValue[tempY][tempX]+"  ");
				/*
				 * pw.println("tempY" + tempY + "tempX" + tempX + "mat" +
				 * matchedValue[tempY][tempX]);
				 */
				if (Math.abs(matchedValue[tempY][tempX] - pValue) <= centroidThreshold) {
					/*
					 * pw.print("  x=" + tempX + "y=" + tempY + "value=" +
					 * matchedValue[tempY][tempX]);
					 */
					//sum += matchedValue[tempY][tempX];
					centX += tempX;
					centY += tempY;
					count++;

					Point neighP = new Point();

					neighP.setX(tempX);
					neighP.setY(tempY);
					list.addLast(neighP);
				}
				// System.out.print("matched" + matchedValue[tempY][tempX]
				// + "theres" + peakThreshold + "sakel" + sakelValue);
			}
			// pw.println();
			if(count>7000){
				p.x=-1;
				return p;
			}

		}
		System.out.println("count= " + count+"  s");
		// pw.close();

		if (Math.abs(previouArea - count) > 300 && firstTime == false) {
			p = trackMarker();
			p = getCentroid(p, pValue);
			p.setX(-1);
			return p;
		}

		if (count > 0) {
			// firstTime = false;
			previouArea = count;
			// System.out.println("count neighbour" + count);
			p.setX(centX / count);
			p.setY(centY / count);
			//int tSakelValue = sum / count;
			//sakelValue = sakelValue + (tSakelValue - sakelValue) * (5 / 100);
			//System.out.println("new sakelValue" + sakelValue);
			previousP = p;
		} else {
			System.out.println("countINelse" + count);
			/*p = trackMarker();
			p = getCentroid(p, sakelValue);*/
			p.setX(-1);
			return p;
		}

		count = 0;
		System.out.println("return Y=" + p.y + "X=" + p.x);
		// pw.close();
		return p;

	}

	public int rightPointMatchedValue(int i, int j, int localIncr) {
		j = j - localIncr;
		int value = 0, subtract = 0, add = 0, h = 0, s = 0;
		int x, y;
		for (y = -maskH / 2; y <= maskH / 2; y++) {
			for (x = -maskW / 2; x < -maskW / 2 + localIncr; x++) {
				// System.out.print("x="+(x+j)+"y="+(y+i));
				if (Math.abs(hue - imageArray[i + y][j + x][0]) <= hueRange
						&& Math.abs(saturation - imageArray[i + y][j + x][1]) <= saturationRange) {
					subtract++;
				}

			}
			// System.out.println();
		}
		// pw.close();
		for (y = -maskH / 2; y <= maskH / 2; y++) {
			for (x = maskW / 2 + 1; x <= maskW / 2 + localIncr; x++) {
				if (check[i + y][j + x] == false) {
					int rgb = image.getRGB(j + x, i + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > 180)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[i + y][j + x][0] = h;
					imageArray[i + y][j + x][1] = s;
					check[i + y][j + x] = true;
				} else {

					h = imageArray[i + y][j + x][0];
					s = imageArray[i + y][j + x][1];
				}

				if (Math.abs(hue - h) <= hueRange
						&& Math.abs(saturation - s) <= saturationRange) {
					add++;
				}
			}
		}

		return (matchedValue[i][j] + add - subtract);
	}

	public int leftPointMatchedValue(int i, int j, int localIncr) {
		j = j + localIncr;
		int value = 0, subtract = 0, add = 0, h = 0, s = 0;
		int x, y;
		for (y = -maskH / 2; y <= maskH / 2; y++) {
			for (x = -maskW / 2 - localIncr; x < -maskW / 2; x++) {

				if (check[i + y][j + x] == false) {
					int rgb = image.getRGB(j + x, i + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > 180)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[i + y][j + x][0] = h;
					imageArray[i + y][j + x][1] = s;
					check[i + y][j + x] = true;
				} else {

					h = imageArray[i + y][j + x][0];
					s = imageArray[i + y][j + x][1];
				}

				if (Math.abs(hue - h) <= hueRange
						&& Math.abs(saturation - s) <= saturationRange) {
					add++;
				}
			}
		}
		for (y = -maskH / 2; y <= maskH / 2; y++) {
			for (x = maskW / 2 - localIncr + 1; x <= maskW / 2; x++) {

				if (Math.abs(hue - imageArray[i + y][j + x][0]) <= hueRange
						&& Math.abs(saturation - imageArray[i + y][j + x][1]) <= saturationRange) {
					subtract++;
				}
			}
		}

		return (matchedValue[i][j] + add - subtract);
	}

	public int downPointMatchedValue(int i, int j, int localIncr) {
		// System.out.println("down i="+i+"j"+j);
		i = i - localIncr;
		int value = 0, subtract = 0, add = 0, h = 0, s = 0;
		int x, y;
		for (y = -maskH / 2; y < -maskH / 2 + localIncr; y++) {
			for (x = -maskW / 2; x <= maskW / 2; x++) {
				// System.out.print("x="+(x+j)+"y="+(i+y));
				if (Math.abs(hue - imageArray[i + y][j + x][0]) <= hueRange
						&& Math.abs(saturation - imageArray[i + y][j + x][1]) <= saturationRange) {
					subtract++;
				}
			}

		}
		for (y = maskH / 2 + 1; y <= maskH / 2 + localIncr; y++) {
			for (x = -maskW / 2; x <= maskW / 2; x++) {

				if (check[i + y][j + x] == false) {
					int rgb = image.getRGB(j + x, i + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > 180)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[i + y][j + x][0] = h;
					imageArray[i + y][j + x][1] = s;
					check[i + y][j + x] = true;
				} else {

					h = imageArray[i + y][j + x][0];
					s = imageArray[i + y][j + x][1];
				}

				if (Math.abs(hue - h) <= hueRange
						&& Math.abs(saturation - s) <= saturationRange) {
					add++;
				}
			}
		}

		return (matchedValue[i][j] + add - subtract);
	}

	public int topPointMatchedValue(int i, int j, int localIncr) {
		i = i + localIncr;
		int value = 0, subtract = 0, add = 0, h = 0, s = 0;
		int x, y;
		for (y = -maskH / 2 - localIncr; y < -maskH / 2; y++) {
			for (x = -maskW / 2; x <= maskW / 2; x++) {

				if (check[i + y][j + x] == false) {
					int rgb = image.getRGB(j + x, i + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > 180)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[i + y][j + x][0] = h;
					imageArray[i + y][j + x][1] = s;
					check[i + y][j + x] = true;
				} else {

					h = imageArray[i + y][j + x][0];
					s = imageArray[i + y][j + x][1];
				}

				if (Math.abs(hue - h) <= hueRange
						&& Math.abs(saturation - s) <= saturationRange) {
					add++;
				}
			}
		}
		for (y = maskH / 2 - localIncr + 1; y <= maskH / 2; y++) {
			for (x = -maskW / 2; x <= maskW / 2; x++) {

				if (Math.abs(hue - imageArray[i + y][j + x][0]) <= hueRange
						&& Math.abs(saturation - imageArray[i + y][j + x][1]) <= saturationRange) {
					subtract++;
				}
			}
		}

		return (matchedValue[i][j] + add - subtract);
	}

	public int curPointMatchedValue(int i, int j) {
		int value = 0, h = 0, s = 0;
		int x, y;
		for (y = -maskH / 2; y <= maskH / 2; y++) {
			for (x = -maskW / 2; x <= maskW / 2; x++) {
				if (check[i + y][j + x] == false) {
					int rgb = image.getRGB(j + x, i + y);
					int r = (rgb >> 16) & 0xff;
					int g = (rgb >> 8) & 0xff;
					int b = (rgb) & 0xff;
					float[] hsb = new float[3];
					Color.RGBtoHSB(r, g, b, hsb);
					hsb[0] = hsb[0] * 360;
					hsb[1] = hsb[1] * 100;
					if (hsb[0] > 180)
						hsb[0] = 360 - hsb[0];
					h = (int) (hsb[0]);
					s = (int) (hsb[1]);
					imageArray[i + y][j + x][0] = h;
					imageArray[i + y][j + x][1] = s;
					check[i + y][j + x] = true;
				} else {

					h = imageArray[i + y][j + x][0];
					s = imageArray[i + y][j + x][1];
				}

				if (Math.abs(hue - h) <= hueRange
						&& Math.abs(saturation - s) <= saturationRange) {
					value++;
				}
			}
		}
		return value;
	}
}
