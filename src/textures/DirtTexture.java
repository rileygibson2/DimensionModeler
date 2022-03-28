package textures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.Arrays;

import core.Render;
import objects.PlanarRoot;

public class DirtTexture extends Texture {

	Color[] dirtColors = {new Color(109, 76, 53), new Color(144, 101, 70), new Color(85, 57, 39), new Color(184, 137, 94)};
	Color pebbleColor = new Color(103, 105, 105);
	Color grassColor = new Color(106, 183, 76);
	
	Color[][] panels;
	
	public DirtTexture(Render c) {
		super("Dirt", Arrays.asList(0,1,2,3,4,5), true, c);
		//Pick colours
		panels = new Color[7][7];
		for (int y=0; y<7; y++) {
			for (int x=0; x<7; x++) {
				if (random(1, 10)==1) { //Maybe make pebble or grass
					if (random(1, 2)==1) panels[y][x] = grassColor;
					else panels[y][x] = pebbleColor;
				}
				else {
					panels[y][x] = dirtColors[random(0, dirtColors.length-1)];
				}
			}
		}
	}

	@Override
	public void drawTexture(Graphics2D g, PlanarRoot o, Point[] f, int facesLength, int faceNum) {
		GeneralPath gP;

		//Lines
		for (int y=0; y<7; y++) {
			for (int x=0; x<7; x++) {
				g.setColor(new Color((panels[y][x].getRed()/facesLength)*faceNum, (panels[y][x].getGreen()/facesLength)*faceNum, (panels[y][x].getBlue()/facesLength)*faceNum));
				gP = makePath(f, x*(0.14), y*(0.14), 0.14, 0.14);
				g.fill(gP);
			}
		}
	}
}
