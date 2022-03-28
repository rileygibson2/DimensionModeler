package perspectives;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import core.Render;
import core.Screen.ScreenMode;

public class CustomView extends V3D {

	public CustomView(String name, double vertRot) {
		super(name);
		this.vertRot = vertRot;
	}

	@Override
	public void drawIcon(Graphics2D g, int x, int y, int size) {
		g.setColor(new Color(120, 120, 120));
		//if (Render.s.mode==ScreenMode.Night) g.setColor(Color.WHITE);
		g.drawLine(x+size/2, (int) (y+size*0.1), x+size/2, (int) (y+size*0.8));
		g.drawLine((int) (x+size*0.1), (int) (y+size*0.8), (int) (x+size*0.8), (int) (y+size*0.5));
		g.drawLine((int) (x+size*0.9), (int) (y+size*0.8), (int) (x+size*0.2), (int) (y+size*0.5));

		g.setColor(new Color(0, 0, 0, 100));
		if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
		g.drawString("C", x+2, (int) (y+size*0.4));

		//Draw border
		g.setColor(new Color(120, 120, 120));
		g.drawRect(x, y, size, size);
	}
}
