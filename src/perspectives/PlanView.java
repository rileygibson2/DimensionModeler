package perspectives;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import core.Render;
import core.Screen.ScreenMode;

public class PlanView extends V2D {

	public PlanView() {
		super("Plan");
	}

	@Override
	public void drawIcon(Graphics2D g, int x, int y, int size) {
		g.setColor(Color.BLUE);
		g.drawRect((int) (x+size*0.2), (int) (y+size*0.2), (int) (size*0.6), (int) (size*0.6));
		
		g.setColor(new Color(0, 0, 0, 100));
		if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
		g.setFont(new Font("Verdana", Font.PLAIN, (int) (size*0.4)));
		g.drawString("2D", (int) (x+size*0.2), (int) (y+size*0.65));
		
		//Draw border
		g.setColor(new Color(120, 120, 120));
		g.drawRect(x, y, size, size);
	}
}
