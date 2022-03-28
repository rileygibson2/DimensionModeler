package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class WoodTexture extends Texture {

	Color[] panels = {new Color(148, 78, 50), new Color(157, 90, 53), new Color(131, 72, 51),
			new Color(171, 99, 58), new Color(169, 102, 55), new Color(192, 128, 74), new Color(137, 73, 29)};

	public WoodTexture(Render c) {
		super("Wood", Arrays.asList(0,1,2,3,4,5), true, c);
	}

	@Override
	public void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum) {
		GeneralPath gP;

		//Lines
		for (int i=0; i<7; i++) {
			g.setColor(new Color((panels[i].getRed()/facesLength)*faceNum, (panels[i].getGreen()/facesLength)*faceNum, (panels[i].getBlue()/facesLength)*faceNum));
			gP = makePath(f, i*(0.14), 0, 0.14, 1);
			g.fill(gP);
		}
	}
}
