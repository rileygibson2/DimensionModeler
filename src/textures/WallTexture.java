package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class WallTexture extends Texture {

	public WallTexture(Render c) {
		super("Wall", Arrays.asList(0,1,2,3,4,5), true, c);
	}

	@Override
	public void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum) {
		Color lColor = new Color(240, 240, 240);
		g.setColor(new Color((lColor.getRed()/facesLength)*faceNum, (lColor.getGreen()/facesLength)*faceNum, (lColor.getBlue()/facesLength)*faceNum));

		/*
		 * This is a bit cooked because I designed textures so 
		 * they could apply to one block then be stacked. But runs
		 * too hot if you stack multiple blocks to make a wall, so 
		 * have had to adapt texture to work on one wall block.
		 * 
		 */

		if (faceNum==4) {
			if (o.rot==0||o.rot==180) drawQuad(g, f);
			else drawDoubleV(g, f);
		}
		if (faceNum==5) {
			if (o.rot==0||o.rot==180) drawDoubleV(g, f);
			else drawQuad(g, f);
		}
		if (faceNum==3) {
			if (o.rot==0||o.rot==180) drawDoubleV(g, f);
			else drawDoubleH(g, f);
		}
	}

	public void drawSingle(Graphics2D g, Point[] f) {
		GeneralPath gP = makePath(f, 0, 0.48, 1, 0.04);
		g.fill(gP);
		gP = makePath(f, 0, 0.96, 1, 0.04);
		g.fill(gP);
		gP = makePath(f, 0.96, 0, 0.04, 0.5);
		g.fill(gP);
		gP = makePath(f, 0.48, 0.5, 0.04, 0.5);
		g.fill(gP);
	}

	//Draw two instances of the texture next to each other
	public void drawDoubleH(Graphics2D g, Point[] f) {
		//Left
		GeneralPath gP = makePath(f, 0, 0.48, 0.5, 0.04);
		g.fill(gP);
		gP = makePath(f, 0, 0.96, 0.5, 0.04);
		g.fill(gP);
		gP = makePath(f, 0.49, 0, 0.02, 0.5);
		g.fill(gP);
		gP = makePath(f, 0.24, 0.5, 0.02, 0.5);
		g.fill(gP);
		
		//Right
		gP = makePath(f, 0.5, 0.48, 0.5, 0.04);
		g.fill(gP);
		gP = makePath(f, 0.5, 0.96, 0.5, 0.04);
		g.fill(gP);
		gP = makePath(f, 0.98, 0, 0.02, 0.5);
		g.fill(gP);
		gP = makePath(f, 0.74, 0.5, 0.02, 0.5);
		g.fill(gP);
	}
	
	//Draw two instances of the texture on top of each other
		public void drawDoubleV(Graphics2D g, Point[] f) {
			//Top
			GeneralPath gP = makePath(f, 0, 0.74, 1, 0.02);
			g.fill(gP);
			gP = makePath(f, 0, 0.98, 1, 0.02);
			g.fill(gP);
			gP = makePath(f, 0.98, 0.5, 0.02, 0.25);
			g.fill(gP);
			gP = makePath(f, 0.49, 0.75, 0.02, 0.25);
			g.fill(gP);

			//Bottom
			gP = makePath(f, 0, 0.24, 1, 0.02);
			g.fill(gP);
			gP = makePath(f, 0, 0.48, 1, 0.02);
			g.fill(gP);
			gP = makePath(f, 0.98, 0, 0.02, 0.25);
			g.fill(gP);
			gP = makePath(f, 0.49, 0.25, 0.02, 0.25);
			g.fill(gP);
		}

	public void drawQuad(Graphics2D g, Point[] f) {
		GeneralPath gP = makePath(f, 0, 0.74, 0.5, 0.02);
		//Left half
		g.fill(gP);
		gP = makePath(f, 0, 0.98, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.49, 0.5, 0.01, 0.25);
		g.fill(gP);
		gP = makePath(f, 0.24, 0.75, 0.01, 0.25);
		g.fill(gP);

		gP = makePath(f, 0, 0.24, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0, 0.48, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.49, 0, 0.01, 0.25);
		g.fill(gP);
		gP = makePath(f, 0.24, 0.25, 0.01, 0.25);
		g.fill(gP);

		//Right half
		gP = makePath(f, 0.5, 0.74, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.5, 0.98, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.99, 0.5, 0.01, 0.25);
		g.fill(gP);
		gP = makePath(f, 0.74, 0.75, 0.01, 0.25);
		g.fill(gP);

		gP = makePath(f, 0.5, 0.24, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.5, 0.48, 0.5, 0.02);
		g.fill(gP);
		gP = makePath(f, 0.99, 0, 0.01, 0.25);
		g.fill(gP);
		gP = makePath(f, 0.74, 0.25, 0.01, 0.25);
		g.fill(gP);
	}
}
