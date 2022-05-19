package objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import core.Layer;
import core.Render;
import core.Vec3;
import particlesystems.ParticleSystem;
import perspectives.Perspective;
import textures.Texture;

public abstract class PlanarRoot {
	Render c;
	public boolean selected = false;
	public Layer layer;
	public Texture texture;

	public Vec3 v[]; //Vertices
	public int x, y, z, w, h, ex, rot;
	public double dist, depth;
	
	public Color col; //Primary color

	public String tag; //Used for devalopment
	public boolean isSystemIndicator; //Whether its a fake object used to order a particle system
	public ParticleSystem system; //The particle system it is faking
	
	/**
	 * Constructs a new object in planar polygon format, with three vertices/points
	 * 
	 * @param p0
	 * @param p1
	 * @param p2
	 * @param layer
	 * @param pColor
	 * @param c
	 */
	public PlanarRoot(Vec3 p0, Vec3 p1, Vec3 p2, Layer layer, Color col, Texture texture, Render c) {
		v = new Vec3[3];
		v[0] = p0;
		v[1] = p1;
		v[2] = p2;
		this.layer = layer;
		this.col = col;
		this.texture = texture;
		this.c = c;
		
		this.isSystemIndicator = false;
	}
	
	
	/**
	 * Constructs a new object that is not in polygon format
	 * 
	 * @param x - the object's x value
	 * @param y - the object's y value
	 * @param z - the object's z value
	 * @param w - the object's width (on x plane)
	 * @param h - the object's height (on y plane)
	 * @param ex - the object's extrusion (on z plane)
	 * @param layer - the object's parent layer
	 * @param pColor - the object's primary color
	 * @param texture - any texture applied to the object
	 * @param c - the reference to Core
	 */
	public PlanarRoot(int x, int y, int z, int w, int h, int ex, Layer layer, Color col, Texture texture, Render c) {
		v = new Vec3[3];
		
		this.c = c;
		this.layer = layer;
		this.texture = texture;
		this.col = col;

		this.x = x;
		this.y = y;
		this.z = z;

		this.w = w;
		this.h = h;
		this.ex = ex;
		this.rot = 0;
		
		this.isSystemIndicator = false;
	}
	
	public void rotate(Vec3 origin, int angle) {
		for (int i=0; i<v.length; i++) {
			Vec3 temp = rotatePoint(origin, angle, v[i]);
			v[i] = temp;
		}
	}

	public Vec3 rotatePoint(Vec3 origin, double angle, Vec3 p) {
		double s = Math.sin(Math.toRadians(angle));
		double c = Math.cos(Math.toRadians(angle));
		//Translate point back to origin
		p.x -= origin.x;
		p.y -= origin.y;
		//Rotate point
		double xNew = (p.x*c)-(p.y*s);
		double yNew = (p.x*s)+(p.y*c);
		//Translate point back to offset
		p.x = (int) (xNew+origin.x);
		p.y = (int) (yNew+origin.y);
		return p;
	}

	/**
	 * Rotates the object by 90 degrees around a specified origin.
	 * 
	 * @param oX - the x value of specified origin
	 * @param oY - the y value of specified origin
	 */
	public void rotate90(int oX, int oY) {
		int tempW = w;
		int difX = x-oX;
		int difY = y-oY;
		
		w = h;
		h = tempW;
		x = oX-difY;
		y = oY+difX;
		
		if (rot==0||rot==180) x-=w;
		else x-=w;
		
		rot += 90;
		if (rot>=360) rot = 0;
	}

	public abstract void draw2D(Graphics2D g, Perspective p);
	public abstract void draw3D(Graphics2D g, BufferedImage b, Perspective p);

	public void select() {selected = true;}
	public void deselect() {selected = false;}
}
