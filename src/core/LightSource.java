package core;

import java.awt.Color;

/**
 * SHADING METHODOLOGY - If there are adjacent normals, then Goruad shade.
 * If there are not then forced to flat shaed.
 * Flat shade is finding color off face normal.
 * Goruad shade is finding gradient based on the normals calculated for
 * the vertices of the polygon.
 * These are calculated by averaging the adjecent face's normals.
 * @author thesmileyone
 *
 */
public class LightSource {

	public enum Type {
		Ambient, //Directionless
		Directional, //Comes from specified direction from infinatly far away point
		Point, //Comes from specified point and has decay and shape
		Specular //Calculated based on view position 
	};
	public Type type;
	public int intensity;
	public Vec3D vector;
	public Vec3 pos;
	public Color col;
	public boolean active;
	
	public LightSource(Type type, int intensity, Vec3D vector, Color col) {
		this.intensity = intensity;
		this.vector = vector;
		this.col = col;
		this.type = type;
		this.active = true;
	}
	
	public void activate() {this.active = true;}
	
	public void deactivate() {this.active = false;}
	
	/**
	 * Returns the actual color of the light source with the intensity factored in.
	 * @return the color or the light source currently
	 */
	public Color getActualColor() {
		return new Color((int) (((double) intensity/100)*col.getRed()), (int) (((double) intensity/100)*col.getGreen()), (int) (((double) intensity/100)*col.getBlue()));
	}
	
	public int getActualRed() {return (int) (((double) intensity/100)*col.getRed());}
	
	public int getActualGreen() {return (int) (((double) intensity/100)*col.getGreen());}
	
	public int getActualBlue() {return (int) (((double) intensity/100)*col.getBlue());}
}
