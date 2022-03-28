package perspectives;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import core.Render;
import core.Screen.ScreenMode;

public class TopView extends V3D {

	public TopView() {
		super("TopView");
		vertRot = 1;
	}

	@Override
	public void drawIcon(Graphics2D g, int x, int y, int size) {
		g.setColor(Color.RED);
		g.drawLine((int) (x+size*0.3), (int) (y+size*0.3), (int) (x+size*0.8), (int) (y+size*0.8));
		g.setColor(Color.GREEN);
		g.drawLine((int) (x+size*0.8), (int) (y+size*0.3), (int) (x+size*0.3), (int) (y+size*0.8));

		g.setColor(new Color(0, 0, 0, 100));
		if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
		g.setFont(new Font("Verdana", Font.PLAIN, (int) (size*0.4)));
		g.drawString("T", x+2, (int) (y+size*0.4));

		//Draw border
		g.setColor(new Color(120, 120, 120));
		g.drawRect(x, y, size, size);
	}
}
