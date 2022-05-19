package core;

import java.awt.Color;
import java.awt.image.BufferedImage;

import objects.RasterPolygon;

public class Rasterer {

	Render c;
	
	public Rasterer(Render c) {
		this.c = c;
	}
	
	public void drawRasterPolygon(BufferedImage b, Color c, RasterPolygon s) {
		//Find bounding functions
		double m1 = (double) (s.getY(1)-s.getY(0))/(s.getX(1)-s.getX(0));
		double c1 = -(m1*s.getX(0)-s.getY(0));

		double m2 = (double) (s.getY(2)-s.getY(1))/(s.getX(2)-s.getX(1));
		double c2 = -(m2*s.getX(1)-s.getY(1));

		double m3 = (double) (s.getY(0)-s.getY(2))/(s.getX(0)-s.getX(2));
		double c3 = -(m3*s.getX(2)-s.getY(2));
		
		if (m1==-0d) m1 = 0; if (m2==-0d) m2 = 0; if (m3==-0d) m3 = 0;
		
		int numInter = 0;
		int inter1, inter2, inter3;
		
		//Interpolate
		for (double y=s.getYMin(); y<s.getYMax(); y+=0.5) {
			//Find intersections for this band
			inter1 = (int) ((y-c1)/m1);
			inter2 = (int) ((y-c2)/m2);
			inter3 = (int) ((y-c3)/m3);
			
			for (int x=s.getXMin(); x<s.getXMax(); x++) {
				numInter = 0;
				if (inter1<s.getXMax()&&inter1>s.getXMin()&&inter1>x) numInter++;
				if (inter2<s.getXMax()&&inter2>s.getXMin()&&inter2>x) numInter++;
				if (inter3<s.getXMax()&&inter3>s.getXMin()&&inter3>x) numInter++;
				if (numInter==1) b.setRGB(x, (int) y, c.getRGB());
			}
		}
	}
}
