package guicomponents;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import core.Render;
import core.Screen.ScreenMode;

public class AnimationsTab extends Tab {
	private static final long serialVersionUID = 9129658172388351089L;

	public AnimationsTab(Render c) {
		super("Animations", 300, 200, 110, 110, 6, Render.a.animations.size(), KeyEvent.VK_9, c);
	}

	@Override
	public void doClick(int x, int y) {
		List<String> names = new ArrayList<String>(Render.a.animations.keySet());
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);
		
		if (i>=0&&i<Render.a.animations.size()) {
			if (Render.a.isRunning(names.get(i))) Render.a.set(names.get(i), false);
			else {
				try {
					String camelAlter = "run"+names.get(i).substring(0, 1).toUpperCase()+names.get(i).substring(1, names.get(i).length());
					Class<?> c = Render.a.getClass();
					Method method = c.getDeclaredMethod(camelAlter);
					method.invoke(Render.a);
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new Error("Animation reflection error");
				}
			}
			this.frame.setVisible(false);
			Render.io.notifyMenuBar(name, false);
		}
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;
		List<String> names = new ArrayList<String>(Render.a.animations.keySet());
		List<Boolean> values = new ArrayList<Boolean>(Render.a.animations.values());

		int z = 0;
		for (int i=0; i<values.size(); i++, z++) {
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
			g.drawString(names.get(i), (int) (dX+tileW*0.1), (int) (dY+tileW*0.9));

			//Draw border
			g.setColor(new Color(120, 120, 120));
			g.drawRect(dX, dY, tileW, tileW);

			if (values.get(i)) {
				g.setColor(new Color(20, 200, 20, 90));
				g.fillRect(dX, dY, tileW, tileW);
			}
			dX += tileW;
		}
	}
}
