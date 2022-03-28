package objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import core.Layer;
import core.Layer.Visibility;
import core.LightSource;
import core.Vec3;
import core.Render;
import core.Render.Mode;
import perspectives.Perspective;

public class Polygon extends PlanarRoot {

	public Polygon(Vec3 p0, Vec3 p1, Vec3 p2, Layer layer, Color col, Render c) {
		super(p0, p1, p2, layer, col, null, c);
	}

	/**
	 * Recalculates the colour of the polygon (flat shading) based off of the
	 * active light sources.
	 * For multiple, different colored light sources, compare vectors,
	 * find out the intensity of each light source once these vectors have been
	 * factored in, then average out all the values.
	 */
	public void calculateColor() {
		double sim; //The similarity between the two relevant vectors
		int vR = 0; int vG = 0; int vB = 0; //Vector influenced colors
		int rA = 0; int gA = 0; int bA = 0; //Average colors accross all light sources
		int count = 0;
		
		for (LightSource lS : c.lightSources) {
			if (!lS.active) continue;
			count++;

			//Calculate similarity and rectify into a normal intensity adjustment
			sim = c.compareVectors(c.calculateNormal(this), lS.vector);
			if (sim>-0.01) sim = 0;
			else if (sim<=-1) sim = 1;
			else sim = Math.abs(sim);

			//Take the light color and adjust it based on the vector similarity, then add it to the average
			vR = (int) (lS.getActualRed()*sim);
			vG = (int) (lS.getActualGreen()*sim);
			vB = (int) (lS.getActualBlue()*sim);
			if (vR>255) vR = 255; else if (vR<0) vR = 0;
			if (vG>255) vG = 255; else if (vG<0) vG = 0;
			if (vB>255) vB = 255; else if (vB<0) vB = 0;
			rA += vR; gA += vG; bA += vB;
		}

		//Average all the light sources and check bounds
		//rA /=count; gA /=count; bA /=count;
		if (rA>255) rA = 255; else if (rA<0) rA = 0;
		if (gA>255) gA = 255; else if (gA<0) gA = 0;
		if (bA>255) bA = 255; else if (bA<0) bA = 0;

		this.col = new Color(rA, gA, bA);
	}

	/**
	 *CLOCKWISE VERTICES NUMBERING
	 * 1-        0-----1       - 0
	 * |  -       -    |      -  |
	 * |    -       -  |    -    |
	 * 0-----2        -2   2-----1
	 */

	@Override
	public void draw3D(Graphics2D g, Perspective p) {
		calculateColor();

		//Centering variables
		int oX = Render.sW/2+p.xOffset;
		int oY = Render.sH/2+p.yOffset;

		//Rasterize vertices
		Point po[] = new Point[3];

		for (int i=0; i<3; i++) {
			Point rX = p.xAV(v[i].x);
			Point rY = p.yAV(v[i].y);
			Point rZ = p.zAV(v[i].z);
			po[i] = new Point(rX.x+rY.x+rZ.x, rX.y+rY.y+rZ.y);
		}

		int ln[][] = {{0,1}, {1,2}, {2,0}};


		g.setStroke(new BasicStroke((float) 1.6));

		if (layer.getMode()==Mode.Wireframe) {
			for (int i=0; i<3; i++) {
				//Deal with greying and highlight
				if (layer.getVisibility()==Visibility.Greyed) g.setColor(Render.s.gColor);
				else g.setColor(col);

				g.drawLine(oX+po[ln[i][0]].x, oY+po[ln[i][0]].y, oX+po[ln[i][1]].x, oY+po[ln[i][1]].y);
			}
		}
		else if (layer.getMode()==Mode.Panelled) {
			GeneralPath gP = new GeneralPath();
			gP.moveTo(oX+po[0].x, oY+po[0].y);
			gP.lineTo(oX+po[1].x, oY+po[1].y);
			gP.lineTo(oX+po[2].x, oY+po[2].y);

			if (layer.getVisibility()==Visibility.Greyed) g.setColor(new Color(150, 150, 150, 40));
			else g.setColor(col);
			g.fill(gP);
		}
	}

	/**
	 * Essentially finds average of all components of the three vertices.
	 * @return the mid point of the poly
	 */
	public Vec3 findMidPoint() {
		return new Vec3((v[0].x+v[1].x+v[2].x)/3, (v[0].y+v[1].y+v[2].y)/3, (v[0].z+v[1].z+v[2].z)/3);
	}

	@Override
	public void draw2D(Graphics2D g, Perspective p) {}
}