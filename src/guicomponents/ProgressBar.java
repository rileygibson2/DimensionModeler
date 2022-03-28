package guicomponents;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import core.Render;

/**
 * @author thesmileyone
 * 
 * Just a visual representation of the progress of a component.
 * All logic is driven by the calling component. For example, it is
 * the components job to increase and to update the stage when it finishes.
 * This means that the visuals will always be accurate, even if there is a pause
 * after the stage is visually completed on this bar, while also meaning the
 * progress bar can be used in a wider range of implementations.
 * 
 * One exception is that pB does control it's own destruction though, once all stages
 * have been completed to avoid unnescarary visual cluttering or grossness from having a
 * pop up stick around for to long. But it also allows calling classes to destruct early.
 *
 */
public class ProgressBar extends JPanel {

	private static final long serialVersionUID = 1L;

	//Critical
	private Render c;
	private JDialog box;
	private Thread paintThread;
	private int stages;
	private int[] boundaries;
	private boolean destroying;
	private boolean destroyed;
	private Point dim;
	private String[] stageNames;
	

	private int stage;
	private int current;

	//Styling
	Color background = new Color(80, 80, 80);
	Color barBackground = new Color(200, 200, 200);
	Color barOutline = new Color(50, 50, 50);
	Color barFill = Render.s.selectColor;
	int offsetY = 30; //Offset from top of dialog
	int offsetX; //Offset from left wall of dialog
	int bufferY = 20; //Buffer between bars
	Point barDim = new Point(0, 28);
	int nameSize = 22; //Portion of each bar taken up by stage name


	public ProgressBar(Render c, int stages, String title, String[] stageNames, int[] boundaries) {
		super();
		this.c = c;
		this.stages = stages;
		this.boundaries = new int[stages];

		if (boundaries.length==stages) this.boundaries = boundaries;
		else throw new Error("Progress Bar boundaries do not match");

		this.dim = new Point(400, offsetY*2+((barDim.y+nameSize+bufferY)*stages));
		this.stageNames = stageNames;
		this.destroyed = false;
		box = new JDialog(Render.frame, title, true);

		Point loc = Render.frame.getLocation();
		box.setLocation(loc.x+(Render.sW/2-(dim.x/2)), loc.y+(Render.sH/2-(dim.y/2)));
		box.setPreferredSize(new Dimension(dim.x, dim.y));
		box.setResizable(false);
		box.setModalityType(ModalityType.MODELESS);

		offsetX = (int) (0.05*dim.x);
		barDim.x = (int) (0.9*dim.x);
		this.stage = 0;
		this.current = 0;

		box.getContentPane().add(this);
		box.pack();
		
		paintThread = new Thread() {
			@Override
			public void run() {
				while (!destroyed&&!destroying) {
					try {Thread.sleep(100);}
					catch (InterruptedException er) {throw new Error("Sleep error");}
					box.repaint();
				}
			}
		};
	}

	public void run() {
		box.setVisible(true);
		box.repaint();
		c.pBActive = true;
		
		//Starting paint thread
		paintThread.start();
	}

	/**
	 * Destroys the progress bar, and runs a brief indication of whether the
	 * task was completed or not, by triggering all the bars to turn green, then sleeping
	 * to show this for a moment.
	 */
	public void destroyNicely() {
		//System.out.println("pB destroying itself");
		destroying = true;
		box.repaint();
		try {Thread.sleep(500);} catch (InterruptedException e) {}
		box.setVisible(false);
		box.dispose();
		destroyed = true;
		c.pBActive = false;
	}

	public void destroyQuickly() {
		destroying = true;
		box.setVisible(false);
		box.dispose();
		destroyed = true;
		c.pBActive = false;
	}

	public boolean isDestroyed() {
		return destroyed||destroying;
	}

	public int getPercent() {
		int percent =  (int) Math.ceil(((double) current/(double) boundaries[stage])*100);
		if (percent>100) percent = 100;
		else if (percent<0) percent = 0;
		return percent;
	}

	public void increase(int inc) {
		if (current+inc<=boundaries[stage]) current+= inc;
		else current = boundaries[stage];
	}

	public void increaseStage() {
		if (stage<stages-1) {
			current = 0;
			stage++;
		}
		else destroyNicely();
	}

	private void drawBar(Graphics2D g, Point pos, Point dim, int num) {
		//Stage name
		String toPrint = "...";
		if (num>=0&&num<stageNames.length) toPrint = stageNames[num]+"...";

		g.setColor(barOutline);
		g.setFont(new Font("Verdana", Font.ITALIC, 20));
		g.drawString(toPrint, pos.x+18, pos.y+17);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Verdana", Font.ITALIC, 20));
		g.drawString(toPrint, pos.x+20, pos.y+15);

		pos.y += nameSize;

		//Outline
		g.setColor(barOutline);
		g.fillRoundRect(pos.x+3, pos.y+5, dim.x, dim.y, 30, 30);

		//Bar
		int calcPercent = getPercent();
		g.setColor(barBackground);
		g.fillRoundRect(pos.x, pos.y, dim.x, dim.y, 30, 30);
		if (destroying) g.setColor(new Color(18, 219, 41));
		else g.setColor(barFill);
		if (num<stage) g.fillRoundRect(pos.x, pos.y, dim.x, dim.y, 30, 30);
		else if (num==stage&&calcPercent>6) {
			//Shitty curved look at under 6%
			g.fillRoundRect(pos.x, pos.y, (int) (((double) calcPercent/100)*barDim.x), dim.y, 30, 30);
		}

		//Percentage
		if (num<stage) toPrint = "100%";
		else toPrint = Integer.toString(calcPercent)+"%";
		g.setColor(new Color(255, 255, 255, 180));
		g.drawString(toPrint, pos.x+20, pos.y+20);
	}

	@Override
	public void paintComponent(Graphics gr) {
		//System.out.println("repainting pB");
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(background);
		g.fillRect(0, 0, dim.x, dim.y);

		//Figure out spacings for each bar
		int y = offsetY;

		//Draw each bar
		for (int i=1; i<=stage+1; i++) {
			drawBar(g, new Point(offsetX, y), barDim, i-1);
			y += barDim.y+bufferY+nameSize;
		}
	}

}
