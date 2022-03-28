package guicomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Render;
import core.Screen.ScreenMode;
import particlesystems.ParticleSystem;

public class SystemsTab extends Tab {
	private static final long serialVersionUID = 9129658172388351089L;

	public SystemsTab(Render c) {
		super("Systems", 300, 200, 120, 120, 6, c.systems.size(), KeyEvent.VK_0, c);
	}

	@Override
	public void doClick(int x, int y) {
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);
		
		if (i>=0&&i<c.systems.size()) {
			ParticleSystem pS = c.systems.get(i);
			if (!pS.isRunning()) {
				pS.run();
			}
			else if (!pS.isKilling()) pS.killGracefully();
			else pS.kill();
			//this.frame.setVisible(false);
			//Render.io.notifyMenuBar(name, false);
		}
	}
	
	public void updateTab() {
		totalNum = c.systems.size();
		super.updateDimensions();
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;

		int z = 0;
		for (int i=0; i<c.systems.size(); i++, z++) {
			ParticleSystem s = c.systems.get(i);
			if (z==numInRow) {
				dY += tileW;
				dX = 2;
				z = 0;
			}

			//Name
			g.setColor(new Color(0, 0, 0, 100));
			if (Render.s.mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 200));
			g.setFont(new Font("Verdana", Font.PLAIN, (int) (tileW*0.3)));
			g.drawString(Integer.toString(i+1), (int) (dX+tileW*0.1), (int) (dY+tileW*0.4));
			g.setFont(new Font("Verdana", Font.PLAIN, (int) (tileW*0.12)));
			g.drawString(s.name, (int) (dX+tileW*0.1), (int) (dY+tileW*0.9));

			//Draw border
			g.setColor(new Color(120, 120, 120));
			g.drawRect(dX, dY, tileW, tileW);

			if (s.isKilling()) {
				g.setColor(new Color(200, 20, 20, 90));
				g.fillRect(dX, dY, tileW, tileW);
			}
			else if (s.isRunning()) {
				g.setColor(new Color(20, 200, 20, 90));
				g.fillRect(dX, dY, tileW, tileW);
			}
			dX += tileW;
		}
	}
}
