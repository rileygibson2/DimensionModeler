package guicomponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import core.Layer;
import core.Layer.Visibility;
import core.Render;

public class LayersTab extends Tab {
	private static final long serialVersionUID = 3380188630758779900L;

	public LayersTab(Render c) {
		super("Layers", 10, 200, 200, 38, 1, c.layers.size(), KeyEvent.VK_6, c);
	}

	@Override
	public void doClick(int x, int y) {
		int i = (y-headH)/tileH;
		int v = x/(int) (w*0.15);
		
		if (i>=0&&i<c.layers.size()) {
			c.selectedLayer = c.layers.get(i);
			if (v<3) c.selectedLayer.setVisibility(Visibility.values()[v]);
		}
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dY = headH+2;

		for (Layer l : c.layers) {
			l.drawIcon(g, 0, dY, w, tileH);
			if (l==c.selectedLayer) {
				g.setColor(selectCol);
				g.fillRect(0, dY, w, tileH);
			}
			dY += tileH;
		}
	}
}
