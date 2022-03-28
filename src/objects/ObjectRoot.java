package objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import core.Layer;
import core.Vec3;
import core.Render;
import textures.Texture;

/**
 * Defines the concept of an object, which is made up of polygons. Object can then be used
 * to manipulate a group of polygons which logically form a thing, togather.
 * @author thesmileyone
 *
 */
public abstract class ObjectRoot {
	
	Render c;
	int x, y, z, w, h, ex;
	List<PlanarRoot> polys;
	Layer layer;
	Texture texture;
	Color col;
	
	public ObjectRoot(Vec3 pos, Vec3 dim, Layer layer, Texture texture, Color col, Render c) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		this.w = dim.w;
		this.h = dim.h;
		this.ex = dim.ex;
		this.layer = layer;
		this.texture = texture;
		this.col = col;
		this.c = c;
		polys = new ArrayList<PlanarRoot>();
	}
	
	/**
	 * Constructs the polygons which make up this object or thing
	 */
	public abstract void buildPolys();
	/**
	 * Rotates around something in the world.
	 * 
	 * @param origin - the origin of this rotation
	 * @param angle - the angle to rotate by
	 */
	public abstract void rotate(Vec3 origin, int angle);
	/**
	 * Spins the object, essentially a rotation centered on itself.
	 * 
	 * @param angle - the angle to spin by
	 */
	public abstract void spin(int angle);
}
