package perspectives;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Render;
import core.Screen.ScreenMode;

public class RightIsoView extends V3D {

	public RightIsoView() {
		super("RightIso");
		vertRot = 0.6;
	}
	
	@Override
	public Point xAV(int p) {
		return new Point(p, (int)(vertRot*p));
	}

	@Override
	public Point yAV(int p) {
		return new Point(p, (int)(vertRot*-p));
	}
	
	@Override
	public Point zAV(int p) {
		return new Point(0, p);
	}

	@Override
	public void drawIcon(Graphics2D g, int x, int y, int size) {
		g.setColor(Color.BLUE);
		g.drawLine(x+size/2, (int) (y+size*0.1), x+size/2, (int) (y+size*0.8));
		g.setColor(Color.GREEN);
		g.drawLine((int) (x+size*0.1), (int) (y+size*0.8), (int) (x+size*0.8), (int) (y+size*0.5));
		g.setColor(Color.RED);
		g.drawLine((int) (x+size*0.9), (int) (y+size*0.8), (int) (x+size*0.2), (int) (y+size*0.5));

		g.setColor(new Color(0, 0, 0, 100));
		if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
		g.setFont(new Font("Verdana", Font.PLAIN, (int) (size*0.4)));
		g.drawString("R", x+2, (int) (y+size*0.4));

		//Draw border
		g.setColor(new Color(120, 120, 120));
		g.drawRect(x, y, size, size);
	}
}
