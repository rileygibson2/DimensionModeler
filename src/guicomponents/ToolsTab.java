package guicomponents;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;

import core.Render;
import core.Tool;
import core.Screen.ScreenMode;

public class ToolsTab extends Tab {
	private static final long serialVersionUID = 9129658172388351089L;

	public ToolsTab(Render c) {
		super("Tools", 1100, 700, 65, 65, 3, c.tools.size(), KeyEvent.VK_8, c);
	}

	@Override
	public void doClick(int x, int y) {
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);
		if (i>=0&&i<c.tools.size()) {
			c.currentTool = c.tools.get(i);
		}
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;

		int i = 0;
		for (Tool t : c.tools) {
			if (i==numInRow) {
				dY += tileW;
				dX = 2;
				i = 0;
			}

			//Draw border
			g.setColor(new Color(120, 120, 120));
			g.drawRect(dX, dY, tileW, tileW);

			//Draw Icons
			switch (t.toolMode) {
			case Select: 
				GeneralPath mouse = new GeneralPath();
				//Body
				mouse.moveTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.1));
				mouse.lineTo((int) (dX+tileW*0.3), (int) (dY+tileW*0.55));
				mouse.lineTo((int) (dX+tileW*0.45), (int) (dY+tileW*0.55));
				//Tail
				mouse.lineTo((int) (dX+tileW*0.45), (int) (dY+tileW*0.8));
				mouse.lineTo((int) (dX+tileW*0.55), (int) (dY+tileW*0.8));
				//Body
				mouse.lineTo((int) (dX+tileW*0.55), (int) (dY+tileW*0.55));
				mouse.lineTo((int) (dX+tileW*0.7), (int) (dY+tileW*0.55));
				mouse.lineTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.1));
				
				g.rotate(Math.toRadians(-40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				g.setColor(new Color(240, 240, 240));
				if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(120, 120, 120));
				g.fill(mouse);
				g.setColor(new Color(20, 20, 20));
				if (Render.s.mode==ScreenMode.Night) g.setColor(accentCol);
				g.draw(mouse);
				g.rotate(Math.toRadians(40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				break;
			
			case Move:
				g.setColor(new Color(20, 20, 20));
				if (Render.s.mode==ScreenMode.Night) g.setColor(accentCol);

				//Arrows
				g.fillRect((int) (dX+tileW*0.2), (int) (dY+tileW*0.5)-1, (int) (tileW*0.6), 2);
				g.fillRect((int) (dX+tileW*0.5)-1, (int) (dY+tileW*0.2), 2, (int) (tileW*0.6));

				//Arrow heads
				int angle = 0;
				for (int z=0; z<4; z++, angle+=90) {
					g.rotate(Math.toRadians(angle), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
					GeneralPath head = new GeneralPath();
					head.moveTo((int) (dX+tileW*0.4), (int) (dY+tileW*0.3));
					head.lineTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.15));
					head.lineTo((int) (dX+tileW*0.6), (int) (dY+tileW*0.3));
					g.fill(head);
					g.rotate(Math.toRadians(-angle), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				}
				break;
				
			/*case VertAngle:
				//Angle
				g.setColor(new Color(20, 20, 20));
				g.drawArc((int) (dX+tileW*0.2), (int) (dY+tileW*0.2), (int) (tileW*0.6), (int) (tileW*0.6), 90, 180);
				
				//Angle Heads
				GeneralPath head = new GeneralPath();
				head.moveTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.1));
				head.lineTo((int) (dX+tileW*0.7), (int) (dY+tileW*0.2));
				head.lineTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.3));
				g.fill(head);
				head = new GeneralPath();
				head.moveTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.7));
				head.lineTo((int) (dX+tileW*0.7), (int) (dY+tileW*0.8));
				head.lineTo((int) (dX+tileW*0.5), (int) (dY+tileW*0.9));
				g.fill(head);
				break;*/
			
			case Angle:
				//Angle
				g.setColor(new Color(20, 20, 20));
				if (Render.s.mode==ScreenMode.Night) g.setColor(accentCol);
				g.drawArc((int) (dX+tileW*0.2), (int) (dY+tileW*0.15), (int) (tileW*0.6), (int) (tileW*0.6), 0, -180);
				
				//Angle Heads
				GeneralPath head = new GeneralPath();
				head.moveTo((int) (dX+tileW*0.1), (int) (dY+tileW*0.45));
				head.lineTo((int) (dX+tileW*0.2), (int) (dY+tileW*0.25));
				head.lineTo((int) (dX+tileW*0.3), (int) (dY+tileW*0.45));
				g.fill(head);
				head = new GeneralPath();
				head.moveTo((int) (dX+tileW*0.7), (int) (dY+tileW*0.45));
				head.lineTo((int) (dX+tileW*0.8), (int) (dY+tileW*0.25));
				head.lineTo((int) (dX+tileW*0.9), (int) (dY+tileW*0.45));
				g.fill(head);
				break;
			}


			if (t==c.currentTool) {
				g.setColor(selectCol);
				g.fillRect(dX, dY, tileW, tileW);
			}
			dX+=tileW;
			i++;
		}
	}
}
