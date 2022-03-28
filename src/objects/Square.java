package objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import core.Layer;
import core.Vec3;
import core.Render;
import textures.Texture;

public class Square extends ObjectRoot {

	public Square(Vec3 pos, Vec3 dim, Layer layer, Color col, Texture texture, Render c) {
		super(pos, dim, layer, texture, col, c);
		this.layer = layer;
		this.texture = texture;
		this.c = c;
		c.objectPool.add(this);
		buildPolys();
	}

	/**
	 * Builds the polygons that make up this square.
	 */
	public void buildPolys() {
		polys = new ArrayList<PlanarRoot>();

		/**
		 *     ------ 
		 *  -----   |
		 *  |  | |  |
		 *  |  --|--- 
		 *  ----- 
		 */

		//Front face
		polys.add(new Polygon(new Vec3(0, 0, 0), new Vec3(0, 0, -ex), new Vec3(w, 0, 0), c.getLayer("Dev"), new Color(255, 0, 0), null));
		polys.add(new Polygon(new Vec3(0, 0, -ex), new Vec3(w, 0, -ex), new Vec3(w, 0, 0), c.getLayer("Dev"), new Color(255, 0, 0), null));
		//Left face
		polys.add(new Polygon(new Vec3(0, 0, 0), new Vec3(0, 0, -ex), new Vec3(0, h, 0), c.getLayer("Dev"), new Color(255, 0, 255), null));
		polys.add(new Polygon(new Vec3(0, 0, -ex), new Vec3(0, h, -ex), new Vec3(0, h, 0), c.getLayer("Dev"), new Color(255, 0, 255), null));
		//Top face
		polys.add(new Polygon(new Vec3(0, 0, -ex), new Vec3(0, h, -ex), new Vec3(w, h, -ex), c.getLayer("Dev"), new Color(0, 255, 0), null));
		polys.add(new Polygon(new Vec3(0, 0, -ex), new Vec3(w, h, -ex), new Vec3(w, 0, -ex), c.getLayer("Dev"), new Color(0, 255, 0), null));
		//Back face
		polys.add(new Polygon(new Vec3(0, h, 0), new Vec3(0, h, -ex), new Vec3(w, h, 0), c.getLayer("Dev"), new Color(150, 150, 150), null));
		polys.add(new Polygon(new Vec3(0, h, -ex), new Vec3(w, h, -ex), new Vec3(w, h, 0), c.getLayer("Dev"), new Color(150, 150, 150), null));
		//Right face
		polys.add(new Polygon(new Vec3(w, 0, 0), new Vec3(w, 0, -ex), new Vec3(w, h, 0), c.getLayer("Dev"), new Color(255, 255, 0), null));
		polys.add(new Polygon(new Vec3(w, 0, -ex), new Vec3(w, h, -ex), new Vec3(w, h, 0), c.getLayer("Dev"), new Color(255, 255, 0), null));
		//Bottom face
		polys.add(new Polygon(new Vec3(0, 0, 0), new Vec3(0, h, 0), new Vec3(w, 0, 0), c.getLayer("Dev"), new Color(200, 50, 255), null));
		polys.add(new Polygon(new Vec3(0, h, 0), new Vec3(w, h, 0), new Vec3(w, 0, 0), c.getLayer("Dev"), new Color(200, 50, 255), null));

		//Add offsets
		for (PlanarRoot o : polys) {
			Polygon p = (Polygon) o;
			for (int i=0; i<3; i++) {
				p.v[i].x += this.x;
				p.v[i].y += this.y;
				p.v[i].z += this.z;
			}
		}
		c.polyPool.addAll(polys);
	}
	
	public void rotate(Vec3 origin, int angle) {
		for (PlanarRoot p : polys) p.rotate(origin, angle);
	}

	@Override
	public void spin(int angle) {
		for (PlanarRoot p : polys) {
			p.rotate(new Vec3(x+w/2, y+h/2, z+ex/2), angle);
		}
	}
}
