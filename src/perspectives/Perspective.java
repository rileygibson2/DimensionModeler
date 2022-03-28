package perspectives;

import java.awt.Graphics2D;
import java.awt.Point;

public abstract class Perspective {

	String name;
	
	public int xOffset;
	public int yOffset;
	public double scale;

	//Floating camera angle stuff
	public double vertRot; //Vetical round rotation. Rotation on the Z plane.
	public double horzRot; //Horizontal rotation. Rotation on the X plane.

	/**
	 * Constructs a new perspective.
	 * 
	 * @param name - the name of the perspective (helpful for set/saved perspecitives)
	 */
	public Perspective(String name) {
		this.name = name;
		xOffset = 0;
		yOffset = 0;
		scale = 1;
		horzRot = -0.6;
		vertRot = 0.6;
	}
	
	public abstract void drawIcon(Graphics2D g, int x, int y, int size);

	/*
	 * These methods return the 2D x&y value of a point on a 3D xAxis.
	 * This is also relative to the view, so that changes to these equations
	 * will change the position and angle of the camera.
	 */
	
	/**
	 * Calculates the raster 2D point of a value on the 3D x axis.
	 * This is calculated relative to the position and angle of the perspective.
	 * @implNote - accounts for unusual warping in x axis - will fix
	 * 
	 * @param p - the point on the 3D x axis
	 * @return a raster point on the screen
	 */
	public Point xAV(int p) {
		double x = 1-Math.pow(horzRot, 2);

		double y = vertRot*-horzRot;

		//Scale
		y = scale*y;
		x = scale*x;

		//Warp adjust
		double a = 0.3*Math.abs(horzRot)+1;
		if (Math.abs(horzRot)>0.6) a = -0.45*(Math.abs(horzRot)-0.6)+1.18;
		y = a*y;
		x = a*x;

		return new Point((int) (x*p), (int) (y*p));
	}

	/**
	 * Calculates the raster 2D point of a value on the 3D y axis.
	 * This is calculated relative to the position and angle of the perspective.
	 * @implNote - accounts for unusual warping in y axis - will fix
	 * 
	 * @param p - the point on the 3D y axis
	 * @return a raster point on the screen
	 */
	public Point yAV(int p) {
		double x = -horzRot;
		double y = -vertRot*Math.pow(horzRot, 2)+vertRot;
		y = -y;

		//Scale
		y = scale*y;
		x = scale*x;

		//Warp adjust
		double a = 0.3*Math.abs(horzRot)+1;
		if (Math.abs(horzRot)>0.6) a = -0.45*(Math.abs(horzRot)-0.6)+1.18;
		y = a*y;
		x = a*x;

		return new Point((int) (x*p), (int)(y*p));
	}

	/**
	 * Calculates the raster 2D point of a value on the 3D z axis.
	 * This is calculated relative to the position and angle of the perspective.
	 * 
	 * @param p - the point on the 3D z axis
	 * @return a raster point on the screen
	 */
	public Point zAV(int p) {
		double y = Math.pow(vertRot, 4);
		y = 1-y;

		//Scale
		y = scale*y;

		return new Point(0, (int) (y*p));
	}
}
