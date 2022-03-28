package guicomponents;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Render;
import core.Screen.ScreenMode;

public abstract class Tab extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	public JFrame frame;
	public int actionKey;
	Render c;
	public String name;
	public boolean collapsed = false; //Whether the tab is collapsed or not
	public int w;
	public int h;
	public int x;
	public int y;

	public int numInRow; //Number of tiles in one row
	public int totalNum; //Total number of tiles
	public int tileW; //Width of one tile
	public int tileH; //Width of one tile
	static int headH = 25; //Height of the tab head
	Color baseCol;
	Color accentCol;
	Color selectCol;
	
	//Color stores;
	public static Color baseColDay = Color.WHITE;
	public static Color baseColNight = new Color(20, 20, 20);
	public static Color accentColDay = new Color(100, 100, 100);
	public static Color accentColNight = new Color(250, 250, 250);
	public static Color selectColDay = new Color(100, 100, 100, 100);
	public static Color selectColNight = new Color(200, 200, 200, 100);
	
	Color headCol = new Color(100, 100, 100);

	//Dragging variables
	boolean dragging = false;
	int prevX;
	int prevY;

	public void toggle() {
		collapsed = !collapsed;
		if (collapsed) frame.setSize(new Dimension(w, headH+5));
		else frame.setSize(new Dimension(w, h));
	}

	public boolean isOnHead(int cX, int cY) {
		if (cX>=0&&cX<=w&&cY>=0&&cY<=headH) return true;
		return false;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (!isOnHead(x, y)) doClick(x, y);
		prevX = x;
		prevY = y;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int xDif = x-prevX;
		int yDif = y-prevY;

		if (dragging) {
			this.x += xDif;
			this.y += yDif;
			frame.setLocation(c.getLocationOnScreen().x+this.x, c.getLocationOnScreen().y+this.y);
		}
		else if (isOnHead(x, y)) dragging = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isOnHead(e.getX(), e.getY())&&!dragging) toggle();
		dragging = false;
	}

	@Override
	public void paintComponent(Graphics g1D) {
		Graphics2D g = (Graphics2D) g1D;
		if (Render.s.mode==ScreenMode.Day) {
			baseCol = baseColDay;
			accentCol = accentColDay;
			selectCol = selectColDay;
		}
		else {
			baseCol = baseColNight;
			accentCol = accentColNight;
			selectCol = selectColNight;
		}

		if (!collapsed) {
			//Base
			g.setColor(baseCol);
			g.fillRoundRect(0, 0, w, h, 10, 10);
			//Border
			g.setColor(headCol);
			g.setStroke(new BasicStroke(2));
			g.drawRoundRect(0, 0, w, h, 10, 10);
		}

		//Head
		g.setColor(headCol);
		g.fillRoundRect(0, 0, w, headH, 10, 10);
		if (!collapsed) g.fillRect(0, headH-2, w, 4);
		
		g.setColor(baseColDay);
		if (w>100&&w<500) {//Title and close sign on right
			g.fillRoundRect((int) (w*0.8), headH/2-3, (int) (w*0.15), 6, 10, 10);
			g.setFont(new Font("Verdana", Font.PLAIN, 20));
			g.drawString(name, (int) (w*0.08), 20);
		}
		else if (w>500) {//Fix title and close sign closer to sides
			g.fillRoundRect((int) (w*0.92), headH/2-3, (int) (w*0.05), 6, 10, 10);
			g.setFont(new Font("Verdana", Font.PLAIN, 20));
			g.drawString(name, (int) (w*0.04), 20);
		}
		else {//Just close in center
			g.fillRoundRect((int) (w*0.2), headH/2-3, (int) (w*0.6), 6, 10, 10);
		}

		if (!collapsed) drawContents(g);
	}
	
	public void updateDimensions() {
		this.w = tileW*numInRow+4;
		this.h = (int) (numInRow*Math.ceil((double) totalNum/(double) numInRow))/numInRow*tileH+headH+4;
		this.setPreferredSize(new Dimension(w, h));
		frame.pack();
	}

	public abstract void doClick(int x, int y);
	public abstract void drawContents(Graphics2D g);

	public Tab(String name, int x, int y, int tileW, int tileH, int numInRow, int totalNum, int actionKey, Render c) {
		this.c = c;
		this.actionKey = actionKey;
		this.name = name;
		this.w = tileW*numInRow+4; //Little bit of wiggle room
		this.h = (int) (numInRow*Math.ceil((double) totalNum/(double) numInRow))/numInRow*tileH+headH+4;
		this.x = x;
		this.y = y;
		this.tileW = tileW;
		this.tileH = tileH;
		this.numInRow = numInRow;
		this.totalNum = totalNum;
		
		if (Render.s.mode==ScreenMode.Day) {
			baseCol = baseColDay;
			accentCol = accentColDay;
		}
		else {
			baseCol = baseColNight;
			accentCol = accentColNight;
		}

		//Set up frame
		frame = new JFrame();
		frame.setTitle(name);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		frame.setLocation(x, y);
		this.setPreferredSize(new Dimension(w, h));
		//frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setFocusableWindowState(false);

		//Set main frame background to transparent
		frame.setBackground(new Color(0, 0, 0, 0));
		//Sets the content pane to be a fake pane with no background
		frame.setContentPane(new FakePane());
		frame.getContentPane().setBackground(new Color(0, 0, 0, 0));

		frame.getContentPane().add(this);
		frame.setVisible(true);
		frame.pack();

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}

/**
 * The fake backgroundless panel that becomes the base for the tab's
 * content pane.
 * 
 * @author thesmileyone
 *
 */
class FakePane extends JPanel {
	private static final long serialVersionUID = 1L;

	public FakePane() {setOpaque(false);}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); //Pass paint responsibility back to parent
		Graphics2D g2d = (Graphics2D) g.create(); //Apply unique own painting effect
		//Set transparent alpha
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f));
		g2d.setColor(getBackground());
		g2d.fill(getBounds());
		g2d.dispose();

	}

}
