package objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import core.Layer;
import core.Vec3;
import core.Render;
import textures.Texture;

public class Shape extends ObjectRoot {


	public Shape(Vec3 pos, Vec3 dim, Layer layer, Color col, Texture texture, Render c) {
		super(pos, dim, layer, texture, col, c);
		buildPolys();
	}

	public void buildPolys() {
		polys = new ArrayList<PlanarRoot>();
	}

	@Override
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
