package guicomponents;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;

import core.LightSource;
import core.LightSource.Type;
import core.Render;

public class LightControlWindow extends Window {
	private static final long serialVersionUID = 9129658172388351089L;


	LightSource lS; //Current light source
	int lSI; //Current light source index
	
	int attrDragI; //The attribute currently being dragged
	boolean dragging; //If the pane is being dragged on right now
	int prevX;
	int prevY;

	public LightControlWindow(Render c) {
		super("Light Control", 0, c.sH-200, 800, 200, KeyEvent.VK_L, c);
		lSI = 0;
		lS = c.lightSources.get(lSI);
	}

	@Override
	public void doClick(int x, int y) {
		dragging = false;
		
		 //On plus/minus
		if (x>w*0.88) {
			if (y<=h*0.5) { //Plus
				if (lSI+1>=c.lightSources.size()) lSI = 0;
				else lSI++;
				lS = c.lightSources.get(lSI);
			}
			else { //Minus
				if (lSI-1<=0) lSI = c.lightSources.size()-1;
				else lSI--;
				lS = c.lightSources.get(lSI);
			}
		}
		else if (x>w*0.24) { //On sliders
			if (x<w*0.65) { //On first bank
				if (y<h*0.5) attrDragI = 1;
				else if (y<h*0.75) attrDragI = 2;
				else attrDragI = 3;
			}
			else { //On second bank
				if (y<h*0.5) attrDragI = 4;
				else if (y<h*0.75) attrDragI = 5;
				else attrDragI = 6;
			}
			dragging = true;
			prevX = x;
			prevY = y;
			//System.out.println(attrDragI);
		}
	}
	
	@Override
	public void doDrag(int x, int y) {
		if (!dragging) return;
		//int temp;
		
		switch (attrDragI) {
		case 1: 
			int temp = lS.col.getRed()+(x-prevX);
			if (temp>255) temp = 255; if (temp<0) temp = 0;
			lS.col = new Color(temp, lS.col.getGreen(), lS.col.getBlue());
			break;
		case 2: 
			temp = lS.col.getGreen()+(x-prevX);
			if (temp>255) temp = 255; if (temp<0) temp = 0;
			lS.col = new Color(lS.col.getRed(), temp, lS.col.getBlue());
			break;
		case 3: 
			temp = lS.col.getBlue()+(x-prevX);
			if (temp>255) temp = 255; if (temp<0) temp = 0;
			lS.col = new Color(lS.col.getRed(), lS.col.getGreen(), temp);
			break;
		case 4:
			double tempd = lS.vector.x+((double) (x-prevX)/100);
			if (tempd>1) tempd = 1; if (tempd<-1) tempd = -1;
			lS.vector.x = tempd;
			break;
		case 5:
			tempd = lS.vector.y+((double) (x-prevX)/100);
			if (tempd>1) tempd = 1; if (tempd<-1) tempd = -1;
			lS.vector.y = tempd;
			break;
		case 6:
			tempd = lS.vector.z+((double) (x-prevX)/100);
			if (tempd>1) tempd = 1; if (tempd<-1) tempd = -1;
			lS.vector.z = tempd;
			break;
		}
		
		prevX = x;
		prevY = y;
	}

	@Override
	public void drawContents(Graphics2D g) {
		int x = 2; //Little bit of wiggle room
		int y = headH+2;
		int h = this.h-(headH+2);
		g.setColor(baseCol);
		g.fillRect(x, y, w, h);

		//Light Type
		g.setColor(fillCol);
		g.fillRoundRect(10, y+10, (int) (h*0.2), (int) (h*0.2), 5, 5);
		g.fillRoundRect(10, y+10+(int) (h*0.23), (int) (h*0.2), (int) (h*0.2), 5, 5);
		g.fillRoundRect(10, y+10+(int) (h*0.46), (int) (h*0.2), (int) (h*0.2), 5, 5);
		g.fillRoundRect(10, y+10+(int) (h*0.69), (int) (h*0.2), (int) (h*0.2), 5, 5);
		g.setFont(new Font("Verdana", Font.BOLD, 25));
		if (lS.type==Type.Ambient) g.setColor(accentCol);
		else g.setColor(baseCol);
		g.drawString("A", 17, y+(int) (h*0.21));
		if (lS.type==Type.Directional) g.setColor(accentCol);
		else g.setColor(baseCol);
		g.drawString("D", 17, y+(int) (h*0.443));
		if (lS.type==Type.Point) g.setColor(accentCol);
		else g.setColor(baseCol);
		g.drawString("P", 17, y+(int) (h*0.675));
		if (lS.type==Type.Specular) g.setColor(accentCol);
		else g.setColor(baseCol);
		g.drawString("S", 17, y+(int) (h*0.90));

		//Light Color
		g.setStroke(new BasicStroke(2));
		g.setColor(shadowCol);
		g.fillRoundRect((int) (w*0.104), y+(int) (h*0.21), (int) (h*0.4), (int) (h*0.4), 10, 10);
		g.setColor(lS.getActualColor());
		g.fillRoundRect((int) (w*0.1), y+(int) (h*0.2), (int) (h*0.4), (int) (h*0.4), 10, 10);

		//Intensity
		g.setFont(new Font("Verdana", Font.BOLD, 30));
		g.setColor(shadowCol);
		g.drawString(Integer.toString(lS.intensity)+"%", (int) (w*0.092), y+10+(int) (h*0.83));
		g.setColor(accentCol);
		g.drawString(Integer.toString(lS.intensity)+"%", (int) (w*0.09), y+10+(int) (h*0.82));
		//Divider
		g.setColor(dividerCol);
		g.fillRoundRect((int) (w*0.23), y+(int) (h*0.1), 2, (int) (h*0.8), 2, 2);

		//Sliders
		drawSlider(g, (double) lS.col.getRed()/255, "R", new Point((int) (w*0.37), (int) (h*0.25)), new Point((int) (w*0.2), (int) (h*0.2)));
		drawSlider(g, (double) lS.col.getGreen()/255,"G", new Point((int) (w*0.37), (int) (h*0.55)), new Point((int) (w*0.2), (int) (h*0.2)));
		drawSlider(g, (double) lS.col.getBlue()/255, "B", new Point((int) (w*0.37), (int) (h*0.85)), new Point((int) (w*0.2), (int) (h*0.2)));
		drawSlider(g, (lS.vector.x+1)/2, "X", new Point((int) (w*0.65), (int) (h*0.25)), new Point((int) (w*0.2), (int) (h*0.2)));
		drawSlider(g, (lS.vector.y+1)/2, "Y", new Point((int) (w*0.65), (int) (h*0.55)), new Point((int) (w*0.2), (int) (h*0.2)));
		drawSlider(g, (lS.vector.z+1)/2, "Z", new Point((int) (w*0.65), (int) (h*0.85)), new Point((int) (w*0.2), (int) (h*0.2)));
		
		//Divider
		g.setColor(dividerCol);
		g.fillRoundRect((int) (w*0.88), y+(int) (h*0.1), 2, (int) (h*0.8), 2, 2);
		//Plus/Minus
		g.setColor(shadowCol);
		g.fillRoundRect((int) (w*0.942), y+(int) (h*0.214), (int) (h*0.04), (int) (h*0.25), 5, 5);
		g.fillRoundRect((int) (w*0.918), y+(int) (h*0.324), (int) (h*0.25), (int) (h*0.04), 5, 5);
		g.fillRoundRect((int) (w*0.918), y+(int) (h*0.684), (int) (h*0.25), (int) (h*0.04), 5, 5);
		g.setColor(accentCol);
		g.fillRoundRect((int) (w*0.939), y+(int) (h*0.21), (int) (h*0.04), (int) (h*0.25), 5, 5);
		g.fillRoundRect((int) (w*0.915), y+(int) (h*0.32), (int) (h*0.25), (int) (h*0.04), 5, 5);
		g.fillRoundRect((int) (w*0.915), y+(int) (h*0.68), (int) (h*0.25), (int) (h*0.04), 5, 5);
	}
	
	public void drawSlider(Graphics2D g, double sliderPos, String name, Point pos, Point dim) {
		//if (name.equals("X")) System.out.println(sliderPos);
		
		//Title
		g.setFont(new Font("Verdana", Font.BOLD, 30));
		g.setColor(shadowCol);
		g.drawString(name, pos.x-32, pos.y+30);
		g.setColor(accentCol);
		g.drawString(name, pos.x-35, pos.y+28);
		//Edges
		g.setColor(fillCol);
		g.fillRoundRect(pos.x, pos.y, (int) (dim.x*0.03), dim.y, 1, 1);
		g.fillRoundRect(pos.x+(int) (dim.x*0.981), pos.y, (int) (dim.x*0.03), dim.y, 1, 1);
		//Line
		g.fillRect(pos.x, pos.y+(int) (dim.y*0.46), dim.x, (int) (dim.y*0.08));
		//Indicator
		g.setColor(accentCol);
		g.fillRoundRect(pos.x+(int) (dim.x*sliderPos)-(int) (dim.x*0.04), pos.y, (int) (dim.x*0.08), dim.y, 2, 2);
		g.setColor(baseCol);
		g.fillRect(pos.x+(int) (dim.x*sliderPos)-(int) (dim.x*0.01)-1, pos.y, (int) (dim.x*0.02), dim.y);
	}
}
