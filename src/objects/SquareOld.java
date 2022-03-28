package objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.Layer;
import core.Layer.Visibility;
import core.Render;
import core.Render.Mode;
import core.Screen.ScreenMode;
import perspectives.Perspective;
import textures.Texture;

public class SquareOld extends PlanarRoot {

	public SquareOld(int x, int y, int z, int w, int h, int ex, Layer layer, Color col, Texture texture, Render c) {
		super (x, y, z, w, h, ex, layer, col, texture, c);
	}

	@Override
	public void draw2D(Graphics2D g, Perspective p) {
		int oX = Render.sW/2+p.xOffset;
		int oY = Render.sH/2+p.yOffset;

		Point po[] = new Point[4];
		po[0] = new Point((int) (p.scale*x), (int) (p.scale*-y));
		po[1] = new Point((int) (p.scale*(x-w)), (int) (p.scale*-y));
		po[2] = new Point((int) (p.scale*x), (int) (p.scale*(-y-h)));
		po[3] = new Point((int) (p.scale*(x-w)), (int) (p.scale*(-y-h)));

		//Deal with greying and highlight
		if (layer.getVisibility()==Visibility.Greyed) g.setColor(Render.s.gColor);
		else if (selected) g.setColor(Render.s.sColor);
		else if (col==Color.WHITE&&Render.s.mode==ScreenMode.Day) g.setColor(Color.BLACK);
		else g.setColor(col);

		g.setStroke(new BasicStroke(2));

		int ln[][] = {{0,1}, {0,2}, {1,3}, {2,3}};
		for (int i=0; i<po.length; i++) {
			g.drawLine(oX+po[ln[i][0]].x, oY+po[ln[i][0]].y, oX+po[ln[i][1]].x, oY+po[ln[i][1]].y);
		}
	}

	/*
	 * Map all points.
	 * Remember that any adjustmenents or offsets on a 3D axis require
	 * adjustments on both 2D axis to look right. So there must be an x&y adjustment
	 * value for any offset to any single 3D axis
	 * 
	 * Position adjustment must be used for the axis that arent
	 * the main one calculated. For example if a point is calculated from
	 * xAV, then yX and yY and zX and zY will have to be used.
	 * Likewise if any points are inhereted as a point is being made, then
	 * you may have to cancel out/add in those adjusters.
	 */

	@Override
	public void draw3D(Graphics2D g, Perspective p) {
		//Centering variables
		int oX = Render.sW/2+p.xOffset;
		int oY = Render.sH/2+p.yOffset;

		//Position adjustment variables
		int xX = p.xAV(x).x;
		int xY = p.xAV(x).y;
		int yX = p.yAV(y).x;
		int yY = p.yAV(y).y;
		int zX = p.zAV(z).x;
		int zY = p.zAV(z).y;

		Point t;
		Point po[] = new Point[8];

		po[0] = new Point(xX+yX+zX, xY+yY+zY);

		t = p.zAV(z-ex);
		po[1] = new Point(t.x+xX+yX, t.y+xY+yY);

		t = p.xAV(x+w);
		po[3] = new Point(t.x+yX+zX, t.y+yY+zY);

		t = p.zAV(z-ex);
		po[2] = new Point(t.x+po[3].x-zX, t.y+po[3].y-zY);

		t = p.yAV(y+h);
		po[5] = new Point(t.x+xX+zX, t.y+xY+zY);

		t = p.yAV(y+h);
		po[4] = new Point(t.x+po[1].x-yX, t.y+po[1].y-yY);

		t = p.yAV(y+h);
		po[7] = new Point(t.x+po[3].x-yX, t.y+po[3].y-yY);

		t = p.zAV(z-ex);
		po[6] = new Point(t.x+po[7].x-zX, t.y+po[7].y-zY);

		int ln[][] = {{0,1}, {0,3}, {0,5}, {1,2}, {1,4}, {2,3}, {2,6},
				{3,7}, {4,5}, {4,6}, {5,7}, {6,7}};

		/**
		 *    4------6
		 * 1-----2  |
		 *  |  | |  |
		 *  | 5--|---7
		 * 0-----3
		 */
		
		g.setStroke(new BasicStroke((float) 1.6));

		if (layer.getMode()==Mode.Wireframe) {
			for (int i=0; i<12; i++) {
				//Deal with greying and highlight
				if (layer.getVisibility()==Visibility.Greyed) g.setColor(Render.s.gColor);
				else if (selected) g.setColor(Render.s.sColor);
				else if (col==Color.WHITE&&Render.s.mode==ScreenMode.Day) g.setColor(Color.BLACK);
				else g.setColor(col);

				//Deal with shadow
				if ((c.p.vertRot>=0&&(i==2||i==8||i==10)) ||
						(c.p.vertRot<0&&(i==4||i==8||i==9))) g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 50));
				g.drawLine(oX+po[ln[i][0]].x, oY+po[ln[i][0]].y, oX+po[ln[i][1]].x, oY+po[ln[i][1]].y);
			}
		}
		else if (layer.getMode()==Mode.Panelled) {
			int faces[][] = null;

			//Order faces and preform backface culling
			if (c.p.horzRot<=0) {
				if (c.p.vertRot>=0) faces = new int[][]{{1,4,6,2}, {3,2,6,7}, {0,1,2,3}};
					if (c.p.vertRot<0) faces = new int[][]{{0,5,7,3}, {3,2,6,7}, {0,1,2,3}};
			}
			if (c.p.horzRot>0) {
				if (c.p.vertRot>=0) faces = new int[][]{{1,4,6,2}, {0,1,4,5}, {0,1,2,3}};
					if (c.p.vertRot<0) faces = new int[][]{{0,5,7,3}, {0,1,4,5}, {0,1,2,3}};
			}

			List<Point[]> oFaces = new ArrayList<>();
			for (int i=0; i<faces.length; i++) {
				Point face[] = {po[faces[i][0]],po[faces[i][1]],po[faces[i][2]],po[faces[i][3]]};
				oFaces.add(face);
			}

			//For each face, build a general path out of it's points, then adjust the color for lightsource and fill it.
			 
			int i = 0;
			int cheatFL = 6; //Cheating the number of faces for the colour to account for backface culling
			for (Point[] f : oFaces) {
				GeneralPath gP = new GeneralPath();
				gP.moveTo(oX+f[0].x, oY+f[0].y);
				gP.lineTo(oX+f[1].x, oY+f[1].y);
				gP.lineTo(oX+f[2].x, oY+f[2].y);
				gP.lineTo(oX+f[3].x, oY+f[3].y);

				if (layer.getVisibility()==Visibility.Greyed) g.setColor(new Color((255/cheatFL)*(i+3), (255/cheatFL)*(i+3), (255/cheatFL)*(i+3), 40));
				else if (selected) g.setColor(new Color((Render.s.sColor.getRed()/cheatFL)*(i+3), (Render.s.sColor.getGreen()/cheatFL)*(i+3), (Render.s.sColor.getBlue()/cheatFL)*(i+3), Render.s.sColor.getAlpha()));
				else g.setColor(new Color((col.getRed()/cheatFL)*(i+3), (col.getGreen()/cheatFL)*(i+3), (col.getBlue()/cheatFL)*(i+3), col.getAlpha()));

				if (texture==null||(texture!=null&&texture.drawBase)) g.fill(gP);
				if (texture!=null&&texture.faces.contains(i)&&layer.getVisibility()!=Visibility.Greyed) {
					texture.drawTexture(g, this, f, cheatFL, (i+3));
				}
				i++;
			}

			mapLightSource(faces, po);
		}
	}

	/*
	 * Measuring the distance of each face to the perspective is now
	 * a function for light/colour grading, because it doesn't work for face
	 * ordering.
	 */
	public void mapLightSource(int[][] faces, Point po[]) {
		/*Sort the faces in the correct order from furtherest from 
		perspective to closest*/
		Map<Double, Point[]> order = new HashMap<>();

		for (int i=0; i<faces.length; i++) {
			Point face[] = {po[faces[i][0]],po[faces[i][1]],po[faces[i][2]],po[faces[i][3]]};
			Point mid = getFaceMidPoint(face);
			order.put(c.calculatePointDistance(mid), face);
		}
		//List<Point[]> oFaces = c.sortFaces(order);
	}

	/*
	 * Returns the mid point of a face given an array of points.
	 * The points provided are already calculated off the axis and include
	 * the offsets, so there is no need to worry about any of that stuff here
	 */
	public Point getFaceMidPoint(Point[] po) {
		int cX = po[0].x+(po[2].x-po[0].x)/2;
		int cY = po[0].y+(po[2].y-po[0].y)/2;

		Point p = new Point(cX, cY);
		Render.s.panelLinks.add(p);

		return p;
	}
}

/*
 * //Order faces
			if (c.p.horzRot<=0) {
				if (c.p.vertRot>=0) faces = new int[][]{{5,4,6,7}, {0,1,4,5}, {0,5,7,3},
					{1,4,6,2}, {3,2,6,7}, {0,1,2,3}};
					if (c.p.vertRot<0) faces = new int[][]{{5,4,6,7}, {0,1,4,5}, {1,4,6,2},
						{0,5,7,3}, {3,2,6,7}, {0,1,2,3}};
			}
			if (c.p.horzRot>0) {
				if (c.p.vertRot>=0) faces = new int[][]{{5,4,6,7}, {3,2,6,7}, {0,5,7,3},
					{1,4,6,2}, {0,1,4,5}, {0,1,2,3}};
					if (c.p.vertRot<0) faces = new int[][]{{5,4,6,7}, {3,2,6,7}, {1,4,6,2},
						{0,5,7,3}, {0,1,4,5}, {0,1,2,3}};
			}*/
