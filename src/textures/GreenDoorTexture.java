package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class GreenDoorTexture extends Texture {

	//Color base = new Color(235, 7, 75);
	//Color lAccent = new Color(255, 124, 175); //Light Accent
	//Color dAccent = new Color(105, 14, 35); //Dark Accent
	Color base = new Color(15, 227, 10);
	Color lAccent = new Color(50, 227, 50);
	Color dAccent = new Color(0, 150, 0);
	Color window = new Color(158, 234, 255, 50);

	public GreenDoorTexture(Render c) {
		super("FancyDoor", Arrays.asList(0,1,2,3,4,5), false, c);
	}

	@Override
	public void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum) {
		GeneralPath gP;
		g.setColor(new Color((base.getRed()/facesLength)*faceNum, (base.getGreen()/facesLength)*faceNum, (base.getBlue()/facesLength)*faceNum));
		
		int fN = 5; //The face to apply to - changes with rotation
		if (o.rot==90||o.rot==270) fN = 4;
		
		if (faceNum==fN) {
			//Borders
			gP = makePath(f, 0, 0, 1, 0.08);
			g.fill(gP);
			gP = makePath(f, 0, 0, 0.08, 1);
			g.fill(gP);
			gP = makePath(f, 0.9, 0, 0.1, 1);
			g.fill(gP);
			gP = makePath(f, 0, 0.6, 1, 0.08);
			g.fill(gP);
			gP = makePath(f, 0, 0.96, 1, 0.04);
			g.fill(gP);
			//Outside stripes
			g.setColor(lAccent);
			gP = makePath(f, 0, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.98, 0, 0.02, 1);
			g.fill(gP);
			//Window frame
			gP = makePath(f, 0.86, 0.08, 0.04, 0.52);
			g.fill(gP);
			gP = makePath(f, 0.1, 0.6, 0.8, 0.02);
			g.fill(gP);
			g.setColor(dAccent);
			gP = makePath(f, 0.06, 0.08, 0.04, 0.54);
			g.fill(gP);
			gP = makePath(f, 0.06, 0.06, 0.85, 0.02);
			g.fill(gP);
			//Windows
			g.setColor(window);
			gP = makePath(f, 0.1, 0.08, 0.8, 0.52);
			g.fill(gP);
			
			//Little windows
			for (double y=0.68; y<0.9; y+=0.14) {
				for (double x=0.08; x<0.6; x+=0.41) {
					//Window frame
					g.setColor(dAccent);
					gP = makePath(f, x, y, 0.03, 0.14);
					g.fill(gP);
					gP = makePath(f, x, y, 0.39, 0.01);
					g.fill(gP);
					g.setColor(lAccent);
					gP = makePath(f, x+0.39, y, 0.02, 0.14);
					g.fill(gP);
					gP = makePath(f, x+0.03, y+0.13, 0.38, 0.01);
					g.fill(gP);
					//Windows
					g.setColor(window);
					gP = makePath(f, x+0.03, y+0.01, 0.36, 0.12);
					g.fill(gP);
				}
			}

		}
		else if (faceNum!=0) {
			gP = makePath(f, 0, 0, 1, 1);
			g.fill(gP);
			g.setColor(new Color((lAccent.getRed()/facesLength)*faceNum, (lAccent.getGreen()/facesLength)*faceNum, (lAccent.getBlue()/facesLength)*faceNum));
			gP = makePath(f, 0, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.98, 0, 0.02, 1);
			g.fill(gP);
		}
	}
}
