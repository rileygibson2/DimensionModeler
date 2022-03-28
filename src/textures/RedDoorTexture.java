package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class RedDoorTexture extends Texture {
	Color[] panels = {new Color(158, 0, 0), new Color(232, 52, 51), new Color(154, 10, 2),
			new Color(214, 15, 10), new Color(171, 2, 31), new Color(208, 34, 34), new Color(224, 24, 7)};
	
	Color decor = new Color(150, 150, 150);
	Color studs = new Color(110, 110, 110);

	public RedDoorTexture(Render c) {
		super("WoodDoor", Arrays.asList(0,1,2,3,4,5), true, c);
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

		//Decor
		int fN = 5; //The face to apply decor to - changes with rotation
		if (o.rot==90||o.rot==270) fN = 4;
		
		if (faceNum==fN) {
			g.setColor(new Color((decor.getRed()/facesLength)*faceNum, (decor.getGreen()/facesLength)*faceNum, (decor.getBlue()/facesLength)*faceNum));
			gP = makePath(f, 0, 0.15, 1, 0.08);
			g.fill(gP);
			
			g.setColor(new Color((studs.getRed()/facesLength)*faceNum, (studs.getGreen()/facesLength)*faceNum, (studs.getBlue()/facesLength)*faceNum));
			for (int i=0; i<8; i++) {
				gP = makePath(f, i*0.12+0.05, 0.17, 0.04, 0.04);
				g.fill(gP);
			}
			
			g.setColor(new Color((decor.getRed()/facesLength)*faceNum, (decor.getGreen()/facesLength)*faceNum, (decor.getBlue()/facesLength)*faceNum));
			gP = makePath(f, 0, 0.8, 1, 0.08);
			g.fill(gP);
			
			g.setColor(new Color((studs.getRed()/facesLength)*faceNum, (studs.getGreen()/facesLength)*faceNum, (studs.getBlue()/facesLength)*faceNum));
			for (int i=0; i<8; i++) {
				gP = makePath(f, i*0.12+0.05, 0.82, 0.04, 0.04);
				g.fill(gP);
			}
		}
	}
}
