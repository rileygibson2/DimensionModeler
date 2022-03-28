package objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;

import core.Layer;
import core.Layer.Visibility;
import core.Render;
import core.Render.Mode;
import core.Screen.ScreenMode;
import perspectives.Perspective;
import textures.Texture;

public class Triangle extends PlanarRoot {

	public enum Face {Side, Ramp}
	Face face;

	public Triangle(Face face, int x, int y, int z, int w, int h, int ex, Layer layer, Color col, Texture texture, Render c) {
		super (x, y, z, w, h, ex, layer, col, texture, c);
		this.face = face;
	}

	@Override
	public void draw2D(Graphics2D g, Perspective p) {
		int oX = Render.sW/2+p.xOffset;
		int oY = Render.sH/2+p.yOffset;
		int am = 3;
		if (face==Face.Ramp) am = 4;

		//Deal with greying and highlight
		if (layer.getVisibility()==Visibility.Greyed) g.setColor(Render.s.gColor);
		else if (selected) g.setColor(Render.s.sColor);
		else if (col==Color.WHITE&&Render.s.mode==ScreenMode.Day) g.setColor(Color.BLACK);
		else g.setColor(col);

		g.setStroke(new BasicStroke(2));
		int ln[][] = {{0,1}, {0,2}, {1,2}};

		Point po[] = new Point[am];
		po[0] = new Point(x, y);
		po[1] = new Point(x+w, y);
		po[2] = new Point(x+w, y-h);

		if (face==Face.Ramp) {
			po[3] = new Point(x, y-h);
			ln = new int[][]{{0,1}, {0,3}, {1,2}, {2, 3}};
		}

		for (int i=0; i<po.length; i++) {
			g.drawLine(oX+po[ln[i][0]].x, oY+po[ln[i][0]].y, oX+po[ln[i][1]].x, oY+po[ln[i][1]].y);
		}
	}

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
		Point po[] = new Point[6];

		po[0] = new Point(xX+yX+zX, xY+yY+zY);

		t = p.zAV(z-ex);
		po[1] = new Point(t.x+xX+yX, t.y+xY+yY);

		t = p.xAV(x+w);
		po[3] = new Point(t.x+yX+zX, t.y+yY+zY);

		t = p.zAV(z-ex);
		po[2] = new Point(t.x+po[3].x-zX, t.y+po[3].y-zY);

		t = p.yAV(y+h);
		po[5] = new Point(t.x+po[3].x-yX, t.y+po[3].y-yY);

		int ln[][] = {{0,1}, {0,3}, {1,2}, {2,3}, {1,4}, {4,5}, {0,4}, {2,5}, {3,5}};

		if (face==Face.Ramp) {
			t = p.yAV(y+h);
			po[4] = new Point(t.x+xX+zX, t.y+xY+zY);
		}
		else if (face==Face.Side) {
			t = p.yAV(y+h);
			po[4] = new Point(t.x+po[2].x-yX, t.y+po[2].y-yY);

			ln[6][0] = 0; ln[6][1] = 5;
			ln[7][0] = 3; ln[7][1] = 5;
			ln[8][0] = 2; ln[8][1] = 4;
		}

		g.setStroke(new BasicStroke(2));

		if (layer.getMode()==Mode.Wireframe) {
			for (int i=0; i<ln.length; i++) {
				//Deal with greying and highlight
				if (layer.getVisibility()==Visibility.Greyed) g.setColor(Render.s.gColor);
				else if (selected) g.setColor(Render.s.sColor);
				else if (col==Color.WHITE&&Render.s.mode==ScreenMode.Day) g.setColor(Color.BLACK);
				else g.setColor(col);

				//Deal with shadow
				if ((c.p.vertRot>=0&&i==6) || (c.p.vertRot<0&&i==4)) g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), 80));
				g.drawLine(oX+po[ln[i][0]].x, oY+po[ln[i][0]].y, oX+po[ln[i][1]].x, oY+po[ln[i][1]].y);
			}
		}
		else if (layer.getMode()==Mode.Panelled) {
			/*For each face, build a general path out of it's points,
			then adjust the color for lightsource and fill it.*/
			int faces[][] = null;

			if (face==Face.Ramp) {
				if (c.p.horzRot<=0) {
					if (c.p.vertRot>=0) faces = new int[][]{{0,4,5,3}, {0,1,4}, {1,4,5,2}, {0,1,2,3},
						{3,2,5}};
						if (c.p.vertRot<0) faces = new int[][]{{1,4,5,2}, {0,1,4}, {0,4,5,3}, {0,1,2,3},
							{3,2,5}};
				}
				if (c.p.horzRot>0) {
					if (c.p.vertRot>=0) faces = new int[][]{{0,4,5,3}, {3,2,5}, {1,4,5,2}, {0,1,2,3},
						{0,1,4}};
						if (c.p.vertRot<0) faces = new int[][]{{1,4,5,2}, {3,2,5}, {0,4,5,3}, {0,1,2,3},
							{0,1,4}};
				}
			}
			if (face==Face.Side) {
				if (c.p.horzRot<=0) {
					if (c.p.vertRot>=0) faces = new int[][]{{0,5,3}, {0,1,4,5}, {0,1,2,3}, {1,4,2}, {3,2,4,5}};
						if (c.p.vertRot<0) faces = new int[][]{{1,4,2}, {0,1,4,5}, {0,1,2,3}, {0,5,3}, {3,2,4,5}};
				}
				if (c.p.horzRot>0) {
					if (c.p.vertRot>=0) faces = new int[][]{{0,5,3}, {3,2,4,5}, {0,1,4,5}, {0,1,2,3}, {1,4,2}};
					if (c.p.vertRot<0) faces = new int[][]{{1,4,2}, {3,2,4,5}, {0,1,4,5}, {0,1,2,3}, {0,5,3}};
				}
			}

			for (int i=0; i<faces.length; i++) {
				GeneralPath gP = new GeneralPath();
				gP.moveTo(oX+po[faces[i][0]].x, oY+po[faces[i][0]].y);
				gP.lineTo(oX+po[faces[i][1]].x, oY+po[faces[i][1]].y);
				gP.lineTo(oX+po[faces[i][2]].x, oY+po[faces[i][2]].y);
				if (faces[i].length==4) gP.lineTo(oX+po[faces[i][3]].x, oY+po[faces[i][3]].y);

				if (layer.getVisibility()==Visibility.Greyed) g.setColor(new Color((255/faces.length)*i, (255/faces.length)*i, (255/faces.length)*i, 40));
				else if (selected) g.setColor(new Color((Render.s.sColor.getRed()/faces.length)*i, (Render.s.sColor.getGreen()/faces.length)*i, (Render.s.sColor.getBlue()/faces.length)*i));
				else g.setColor(new Color((col.getRed()/faces.length)*i, (col.getGreen()/faces.length)*i, (col.getBlue()/faces.length)*i));

				g.fill(gP);
			}
		}
	}

	//Draw reference point
	/*g.setColor(Color.RED);
		Point gg = p.zAV(z);
		g.fillRect(oX+gg.x+xX+yX, oY+gg.y+xY+yY, 5, 5);*/


}
