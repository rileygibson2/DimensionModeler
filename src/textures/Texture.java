package textures;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.List;

import core.Render;
import objects.PlanarRoot;

public abstract class Texture {
	Render c;
	String name;
	public List<Integer> faces; //The faces of the object the texture will affect
	public boolean drawBase; //Wether object being textured should draw it's base color

	public Texture(String name, List<Integer> faces, boolean drawBase, Render c) {
		this.name = name;
		this.faces = faces;
		this.c = c;
		this.drawBase = drawBase;
	}

	/**
	 * Draws a texture on a given face on an object
	 * 
	 * @param o - the object being textured
	 * @param f - the collection of points that make up the face
	 * @param facesLength - the number of faces on the object being textured
	 * @param faceNum - the number of the current face on the object being textured
	 */
	public abstract void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum);

	public String getName() {
		return this.name;
	}
	
	public GeneralPath makePath(Point[] f, double x, double y, double w, double h) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;

		GeneralPath gP = new GeneralPath();
		gP.moveTo(oX+(f[0].x*(1-x)+f[3].x*x)*y+(f[1].x*(1-x)+f[2].x*x)*(1-y),
				oY+(f[0].y*(1-x)+f[3].y*x)*y+(f[1].y*(1-x)+f[2].y*x)*(1-y));
		gP.lineTo(oX+(f[0].x*(1-(w+x))+f[3].x*(w+x))*y+(f[1].x*(1-(w+x))+f[2].x*(w+x))*(1-y),
				oY+(f[0].y*(1-(w+x))+f[3].y*(w+x))*y+(f[1].y*(1-(w+x))+f[2].y*(w+x))*(1-y));
		gP.lineTo(oX+(f[0].x*(1-(w+x))+f[3].x*(w+x))*(h+y)+(f[1].x*(1-(w+x))+f[2].x*(w+x))*(1-(h+y)),
				oY+(f[0].y*(1-(w+x))+f[3].y*(w+x))*(h+y)+(f[1].y*(1-(w+x))+f[2].y*(w+x))*(1-(h+y)));
		gP.lineTo(oX+(f[0].x*(1-x)+f[3].x*x)*(h+y)+(f[1].x*(1-x)+f[2].x*x)*(1-(h+y)),
				oY+(f[0].y*(1-x)+f[3].y*x)*(h+y)+(f[1].y*(1-x)+f[2].y*x)*(1-(h+y)));

		return gP;
	}
	
	/**
	 * A handy random number method.
	 * 
	 * @param the inclusive lower value
	 * @param the inclusive upper value
	 * @return the random integer
	 */
	public int random(double min, double max){
		return (int) ((Math.random()*((max-min)+1))+min);
	}
	
	@Override
	public boolean equals(Object o) {
		if (((Texture)o).name.equals(this.name)) return true;
		return false;
	}
}
