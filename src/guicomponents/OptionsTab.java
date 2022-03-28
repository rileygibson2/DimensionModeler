package guicomponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;

import core.Render;
import core.Render.Mode;
import core.Screen.ScreenMode;

/*
 * A tab of miscelanious options. As a result this one will have to be semi
 * hard coded which sucks. Because there can't be just a collection of options
 * at the moment because their functions are so spread and varied, but still need
 * to be accessed from the interface.
 */
public class OptionsTab extends Tab {
	private static final long serialVersionUID = 3543240754787275015L;

	public enum Options {WireFrame, Panelled, ShowLinks, ShowPanelLinks, 
		Center, AxisMarkings, Day, Night, ShowDepthLinks, ShowParticles};

	public OptionsTab(Render c) {
		super("Options", (int) (Render.sW-70), 70, 60, 60, 1, Options.values().length, KeyEvent.VK_7, c);
	}

	@Override
	public void doClick(int x, int y) {
		int ix = x/tileW;
		int iy = (y-headH)/tileW;
		int i = ix+(iy*numInRow);

		if (i==0) c.mode = Mode.Wireframe;
		if (i==1) c.mode = Mode.Panelled;
		if (i==2) Render.s.showLinks = !Render.s.showLinks;
		if (i==3) Render.s.showNormals = !Render.s.showNormals;
		if (i==4) {
			c.p.xOffset = 0;
			c.p.yOffset = 0;
		}
		if (i==5) Render.s.showAxisMarkings = !Render.s.showAxisMarkings;
		if (i==6) c.setScreenMode(ScreenMode.Day);
		if (i==7) c.setScreenMode(ScreenMode.Night);
		if (i==8) Render.s.showDepthBuffer = !Render.s.showDepthBuffer;
		if (i==9) Render.s.showParticles = !Render.s.showParticles;
	}

	@Override
	public void drawContents(Graphics2D g) {
		int dX = 2; //Little bit of wiggle room
		int dY = headH+2;

		int z = 0;

		for (Options o : Options.values()) {
			if (z>=numInRow) {
				dY += tileH;
				dX = 2;
				z = 0;
			}

			switch (o) {
			case WireFrame:
				g.setStroke(new BasicStroke(2));
				if (Render.s.mode==ScreenMode.Day) g.setColor(new Color(20, 20, 20));
				else g.setColor(Color.WHITE);

				g.drawLine((int) (dX+tileW*0.25), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.25));
				g.drawLine((int) (dX+tileW*0.5), (int) (dY+tileW*0.2), (int) (dX+tileW*0.8), (int) (dY+tileW*0.5));
				g.drawLine((int) (dX+tileW*0.5), (int) (dY+tileW*0.3), (int) (dX+tileW*0.7), (int) (dY+tileW*0.5));
				g.drawLine((int) (dX+tileW*0.3), (int) (dY+tileW*0.4), (int) (dX+tileW*0.6), (int) (dY+tileW*0.7));
				g.drawLine((int) (dX+tileW*0.3), (int) (dY+tileW*0.5), (int) (dX+tileW*0.5), (int) (dY+tileW*0.7));

				if (c.mode==Mode.Wireframe) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case Panelled:
				//Front
				g.setColor(new Color(240, 240, 240));
				GeneralPath gP = new GeneralPath();
				gP.moveTo((int) (dX+tileW*0.12), (int) (dY+tileW*0.35));
				gP.lineTo((int) (dX+tileW*0.72), (int) (dY+tileW*0.35));
				gP.lineTo((int) (dX+tileW*0.72), (int) (dY+tileW*0.85));
				gP.lineTo((int) (dX+tileW*0.12), (int) (dY+tileW*0.85));
				g.fill(gP);

				//Top
				g.setColor(new Color(180, 180, 180));
				gP = new GeneralPath();
				gP.moveTo((int) (dX+tileW*0.12), (int) (dY+tileW*0.35));
				gP.lineTo((int) (dX+tileW*0.32), (int) (dY+tileW*0.15));
				gP.lineTo((int) (dX+tileW*0.87), (int) (dY+tileW*0.15));
				gP.lineTo((int) (dX+tileW*0.72), (int) (dY+tileW*0.35));
				g.fill(gP);

				//Side
				g.setColor(new Color(100, 100, 100));
				gP = new GeneralPath();
				gP.moveTo((int) (dX+tileW*0.72), (int) (dY+tileW*0.35));
				gP.lineTo((int) (dX+tileW*0.87), (int) (dY+tileW*0.15));
				gP.lineTo((int) (dX+tileW*0.87), (int) (dY+tileW*0.6));
				gP.lineTo((int) (dX+tileW*0.72), (int) (dY+tileW*0.85));
				g.fill(gP);

				if (c.mode==Mode.Panelled) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case ShowLinks:
				g.setStroke(new BasicStroke(2));
				g.setColor(new Color(255, 153, 0));
				g.fillOval((int) (dX+tileW*0.25)-5, (int) (dY+tileW*0.75)-5, 10, 10);
				g.fillOval((int) (dX+tileW*0.75)-5, (int) (dY+tileW*0.25)-5, 10, 10);
				g.drawLine((int) (dX+tileW*0.25), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.25));

				if (Render.s.showLinks) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case ShowPanelLinks:
				g.setStroke(new BasicStroke(2));
				g.setColor(new Color(255, 20, 0));
				g.fillOval((int) (dX+tileW*0.25)-5, (int) (dY+tileW*0.75)-5, 10, 10);
				g.fillOval((int) (dX+tileW*0.75)-5, (int) (dY+tileW*0.25)-5, 10, 10);
				g.drawLine((int) (dX+tileW*0.25), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.25));

				if (Render.s.showNormals) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;
				
			case ShowDepthLinks:
				g.setStroke(new BasicStroke(2));
				g.setColor(new Color(255, 153, 0));
				g.fillOval((int) (dX+tileW*0.25)-5, (int) (dY+tileW*0.75)-5, 10, 10);
				g.fillOval((int) (dX+tileW*0.75)-5, (int) (dY+tileW*0.75)-5, 10, 10);
				g.fillOval((int) (dX+tileW*0.75)-5, (int) (dY+tileW*0.25)-5, 10, 10);
				g.drawLine((int) (dX+tileW*0.25), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.75));
				g.drawLine((int) (dX+tileW*0.75), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.25));
				g.drawLine((int) (dX+tileW*0.25), (int) (dY+tileW*0.75), (int) (dX+tileW*0.75), (int) (dY+tileW*0.25));

				
				if (Render.s.showDepthBuffer) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case Center:
				g.setStroke(new BasicStroke(2));
				if (Render.s.mode==ScreenMode.Day) g.setColor(new Color(20, 20, 20));
				else g.setColor(baseColDay);

				g.fillRect((int) (dX+tileW*0.2), (int) (dY+tileW*0.5)-1, (int) (tileW*0.6), 2);
				g.fillRect((int) (dX+tileW*0.5)-1, (int) (dY+tileW*0.2), 2, (int) (tileW*0.6));
				g.setColor(new Color(255, 20, 0));
				g.fillOval((int) (dX+tileW*0.4), (int) (dY+tileW*0.4), (int) (tileW*0.2), (int) (tileW*0.2));

				if (c.p.xOffset==0&&c.p.yOffset==0) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;
				
			case AxisMarkings: 
				g.setStroke(new BasicStroke(2));
				
				//Axis
				g.setColor(Color.GREEN);
				g.drawLine((int) (dX+tileW*0.5), (int) (dY+tileW*0.9), (int) (dX+tileW*0.5), (int) (dY+tileW*0.1));
				g.setColor(Color.RED);
				g.drawLine((int) (dX+tileW*0.1), (int) (dY+tileW*0.5), (int) (dX+tileW*0.9), (int) (dY+tileW*0.5));
				g.setColor(accentCol);
				for (int i=(int) (dY+tileW*0.1); i<(int) (dY+tileW*0.9); i+=(int) (tileW*0.1)) g.fillOval((int) (dX+tileW*0.47), i, (int) (tileW*0.05), (int) (tileW*0.05));
				for (int i=(int) (dX+tileW*0.1); i<(int) (dX+tileW*0.9); i+=(int) (tileW*0.1)) g.fillOval(i, (int) (dY+tileW*0.47), (int) (tileW*0.05), (int) (tileW*0.05));
				
				//Circles
				if (Render.s.mode==ScreenMode.Day) g.setColor(new Color(100, 100, 100, 120));
				else g.setColor(new Color(200, 200, 200, 180));
				g.rotate(Math.toRadians(-40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				g.drawOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.35), (int) (tileW*0.7), (int) (tileW*0.3));
				g.rotate(Math.toRadians(40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				g.rotate(Math.toRadians(40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				g.drawOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.35), (int) (tileW*0.7), (int) (tileW*0.3));
				g.rotate(Math.toRadians(-40), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				
				
				if (Render.s.showAxisMarkings) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case Day:
				//Cones
				int i = 0;
				for (int a=0; a<360; a+=30, i++) {
					if (i%2==0) g.setColor(new Color(255, 170, 0));
					else g.setColor(new Color(255, 208, 115));
					g.rotate(Math.toRadians(a), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
					g.fillArc((int) (dX+tileW*0.35), (int) (dY-tileW*0.1), (int) (tileW*0.3), (int) (tileW*0.4), -132, 90);
					g.rotate(Math.toRadians(-a), (int) (dX+tileW*0.5), (int) (dY+tileW*0.5));
				}
				//Outline
				g.setColor(new Color(255, 170, 0));
				g.fillOval((int) (dX+tileW*0.2), (int) (dY+tileW*0.2), (int) (tileW*0.6), (int) (tileW*0.6));
				//Fill
				g.setColor(new Color(245, 185, 66));
				g.fillOval((int) (dX+tileW*0.25), (int) (dY+tileW*0.25), (int) (tileW*0.5), (int) (tileW*0.5));
				g.setColor(Color.RED);
				
				
				if (Render.s.mode==ScreenMode.Day) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;

			case Night:
				g.setStroke(new BasicStroke(2));

				//Outline
				g.setColor(new Color(100, 100, 100));
				g.fillOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.15), (int) (tileW*0.7), (int) (tileW*0.7));
				//Fill
				g.setColor(new Color(160, 160, 160));
				g.fillOval((int) (dX+tileW*0.17), (int) (dY+tileW*0.17), (int) (tileW*0.64), (int) (tileW*0.64));
				//Highlight
				g.setColor(new Color(200, 200, 200));
				g.fillOval((int) (dX+tileW*0.2), (int) (dY+tileW*0.2), (int) (tileW*0.55), (int) (tileW*0.55));
				g.fillOval((int) (dX+tileW*0.22), (int) (dY+tileW*0.6), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.66), (int) (dY+tileW*0.58), (int) (tileW*0.1), (int) (tileW*0.1));
				//Craters
				g.setColor(new Color(200, 200, 200));
				g.fillOval((int) (dX+tileW*0.39), (int) (dY+tileW*0.61), (int) (tileW*0.2), (int) (tileW*0.2));
				g.setColor(new Color(160, 160, 160));
				g.fillOval((int) (dX+tileW*0.45), (int) (dY+tileW*0.35), (int) (tileW*0.19), (int) (tileW*0.19));
				g.fillOval((int) (dX+tileW*0.58), (int) (dY+tileW*0.34), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.4), (int) (dY+tileW*0.63), (int) (tileW*0.16), (int) (tileW*0.16));
				g.fillOval((int) (dX+tileW*0.22), (int) (dY+tileW*0.35), (int) (tileW*0.14), (int) (tileW*0.14));
				g.fillOval((int) (dX+tileW*0.5), (int) (dY+tileW*0.25), (int) (tileW*0.08), (int) (tileW*0.08));
				g.fillOval((int) (dX+tileW*0.24), (int) (dY+tileW*0.62), (int) (tileW*0.07), (int) (tileW*0.07));
				g.fillOval((int) (dX+tileW*0.65), (int) (dY+tileW*0.59), (int) (tileW*0.07), (int) (tileW*0.07));

				if (Render.s.mode==ScreenMode.Night) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;
				
			case ShowParticles:
				g.setStroke(new BasicStroke(2));
				
				//Little particles
				g.setColor(new Color(90, 90, 90));
				g.fillOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.15), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.3), (int) (dY+tileW*0.4), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.7), (int) (dY+tileW*0.8), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.75), (int) (dY+tileW*0.6), (int) (tileW*0.1), (int) (tileW*0.1));
				g.setColor(new Color(100, 100, 100));
				g.fillOval((int) (dX+tileW*0.3), (int) (dY+tileW*0.2), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.2), (int) (dY+tileW*0.6), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.33), (int) (dY+tileW*0.15), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.5), (int) (dY+tileW*0.45), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.5), (int) (dY+tileW*0.6), (int) (tileW*0.1), (int) (tileW*0.1));
				g.setColor(new Color(150, 150, 150));
				g.fillOval((int) (dX+tileW*0.3), (int) (dY+tileW*0.7), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.25), (int) (dY+tileW*0.25), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.75), (int) (dY+tileW*0.65), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.5), (int) (dY+tileW*0.2), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.5), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.8), (int) (dY+tileW*0.82), (int) (tileW*0.1), (int) (tileW*0.1));
				g.setColor(new Color(180, 180, 180));
				g.fillOval((int) (dX+tileW*0.4), (int) (dY+tileW*0.7), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.65), (int) (dY+tileW*0.25), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.15), (int) (dY+tileW*0.78), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.7), (int) (dY+tileW*0.8), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.25), (int) (dY+tileW*0.5), (int) (tileW*0.1), (int) (tileW*0.1));
				g.fillOval((int) (dX+tileW*0.7), (int) (dY+tileW*0.82), (int) (tileW*0.1), (int) (tileW*0.1));
				
				if (Render.s.showParticles) {
					g.setColor(selectCol);
					g.fillRect(dX, dY, tileW, tileW);
				}
				break;
			}

			//Draw border
			g.setColor(new Color(120, 120, 120));
			g.drawRect(dX, dY, tileW, tileW);

			dX+=tileW;
			z++;
		}	
	}
}
