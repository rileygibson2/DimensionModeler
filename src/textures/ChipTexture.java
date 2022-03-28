package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class ChipTexture extends Texture {

	public double effectPos; //The position of the matrix style effect
	Color lColor = new Color(50, 255, 50); //Color of green section

	public ChipTexture(Render c) {
		super("Chip", Arrays.asList(0,1,2,3,4,5), true, c);
		effectPos = 0;
	}

	@Override
	public void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum) {
		/*
		 * Draw a green section then mask it off.
		 * Draw position based on facenumber, account
		 * for fact that the position could be overflowing
		 * so hide it (this is so faces can be displayed
		 * at different times).
		 */
		
		double y = effectPos-faceNum/2;
		double h = 0.5;
		if (y<0) { //Over top edge
			h = y+h; 
			y = 0;
		}
		if (y<1&&y>-h) {
			if (y>1-h) h = 1-y; //Over bottom edge
			
			//Green
			g.setColor(new Color((lColor.getRed()/facesLength)*faceNum, (lColor.getGreen()/facesLength)*faceNum, (lColor.getBlue()/facesLength)*faceNum));
			GeneralPath gP = makePath(f, 0.05, y, 0.85, h);
			g.fill(gP);

			//Masks
			g.setColor(new Color((o.col.getRed()/facesLength)*faceNum, (o.col.getGreen()/facesLength)*faceNum, (o.col.getBlue()/facesLength)*faceNum));
			//g.setColor(Color.RED);
			gP = makePath(f, 0.1, 0, 0.2, 0.45);
			g.fill(gP);

			gP = makePath(f, 0, 0.55, 0.3, 0.45);
			g.fill(gP);

			//gP = makePath(f, 0.35, 0.8, 0.17, 0.2);
			//g.fill(gP);
			gP = makePath(f, 0.35, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.39, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.43, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.47, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.51, 0, 0.02, 1);
			g.fill(gP);
			gP = makePath(f, 0.53, 0, 0.1, 1);
			g.fill(gP);

			gP = makePath(f, 0.65, 0, 0.05, 1);
			g.fill(gP);

			gP = makePath(f, 0.6, 0, 0.3, 0.3);
			g.fill(gP);

			gP = makePath(f, 0.75, 0.7, 0.2, 0.3);
			g.fill(gP);

			gP = makePath(f, 0.8, 0.4, 0.15, 0.3);
			g.fill(gP);
		}
	}
}
