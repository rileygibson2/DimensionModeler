package guicomponents;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Render;
import perspectives.Perspective;

public class ViewsTab extends Tab {
	private static final long serialVersionUID = 701549134815696289L;

	public ViewsTab(Render c) {
		super("Views", 10, 60, 50, 50, 4, c.perspectives.size(), KeyEvent.VK_5, c);
	}

	@Override
	public void doClick(int x, int y) {
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);
		if (i>=0&&i<c.perspectives.size()) {
			c.p = c.perspectives.get(i);
		}
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;

		int i = 0;
		for (Perspective p : c.perspectives) {
			if (i==numInRow) {
				dY += tileW;
				dX = 2;
				i = 0;
			}
			p.drawIcon(g, dX, dY, tileW);
			if (p==c.p) {
				g.setColor(new Color(100, 100, 100, 100));
				g.fillRect(dX, dY, tileW, tileW);
			}
			dX+=tileW;
			i++;
		}
	}
}
