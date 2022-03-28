package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import core.Render.Mode;
import core.Screen.ScreenMode;
import guicomponents.Tab;

public class Layer {

	Render c;
	public enum Visibility {Visible, Greyed, Hidden};
	public Visibility visibility;
	String name;
	boolean isMaster = false;


	public Layer(String name, Render c) {
		this.c = c;
		this.name = name;
		this.visibility = Visibility.Visible;
	}

	public Layer(boolean master, Render c) {
		this.c = c;
		this.name = "Master";
		this.visibility = Visibility.Visible;
		this.isMaster = true;
	}

	public String getName() {
		return this.name;
	}

	public Visibility getVisibility() {
		return this.visibility;
	}

	public boolean isVisible() {
		if (visibility!=Visibility.Hidden) return true;
		return false;
	}

	public void setVisibility(Visibility v) {
		if (isMaster&&v==Visibility.Greyed) return;
		this.visibility = v;
	}

	public Mode getMode() {return c.mode;}

	public void drawIcon(Graphics2D g, int x, int y, int w, int h) {
		//Draw border
		g.setColor(new Color(120, 120, 120));
		g.drawRect(x, y, w, h);

		//Draw visibility borders
		g.setColor(new Color(120, 120, 120));
		g.fillRect((int) (x+w*0.15), y, 2, h);
		g.fillRect((int) (x+w*0.3), y, 2, h);
		g.fillRect((int) (x+w*0.45), y, 2, h);

		//Draw name
		g.setColor(new Color(60, 60, 60));
		if (Render.s.mode==ScreenMode.Night) g.setColor(Tab.accentColNight);
		g.setFont(new Font("Verdana", Font.PLAIN, 20));
		g.drawString(name, (int) (x+w*0.5), (int) (y+h*0.7));

		switch (visibility) {
		case Greyed: x += (int) (w*0.15)+1; break;
		case Hidden: x += (int) (w*0.3)+2; break;
		default: break;
		}

		//Draw Iris
		if (visibility==Visibility.Visible) g.setColor(new Color(0, 150, 255));
		else g.setColor(new Color(250, 250, 250));
		g.fillOval(x+(int) (w*0.04), (int) (y+h*0.3), (int) (h*0.4), (int) (h*0.4));
		g.setStroke(new BasicStroke(1));
		//Draw Pupil
		if (Render.s.mode==ScreenMode.Day) {
			if (visibility==Visibility.Visible) g.setColor(new Color(20, 20, 20));
			else g.setColor(new Color(100, 100, 100));
		}
		else g.setColor(new Color(20, 20, 20));
		g.drawOval(x+(int) (w*0.04), (int) (y+h*0.3), (int) (h*0.4), (int) (h*0.4));
		g.fillOval(x+(int) (w*0.059), (int) (y+h*0.40), (int) (h*0.2), (int) (h*0.2));
		//Draw eye outline
		g.setStroke(new BasicStroke(2));
		g.drawOval(x+(int) (w*0.015), (int) (y+h*0.25), (int) (w*0.12), (int) (h*0.5));

		//Draw slash
		if (visibility==Visibility.Hidden) {
			g.setColor(new Color(20, 20, 20));
			if (Render.s.mode==ScreenMode.Night) g.setColor(Tab.accentColNight);
			g.drawLine((int) (x+w*0.01), (int) (y+h*0.15), (int) (x+w*0.13), (int) (y+h*0.85));
		}
	}
}
