package core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import guicomponents.Tab;
import guicomponents.Window;
import objects.PlanarRoot;
import particlesystems.ParticleSystem;
import perspectives.V3D;

public class Screen {
	static Render c;
	public enum ScreenMode {Day, Night};
	public ScreenMode mode;

	//Selection
	public Color selectColor = new Color(252, 207, 3);
	public Rectangle selectionBox = null;
	public Color gColor = new Color(200, 200, 200); //Grayed object color
	public Color sColor = new Color(255, 255, 255, 100); //Selected object color

	//Gates
	public boolean showHUD = true;
	public boolean showLinks = false;
	public boolean showNormals = false;
	public boolean showLightSources = true;
	public boolean showDepthBuffer = false;
	public boolean showAxis = true;
	public boolean showAxisMarkings = false;
	public boolean showParticles = true;
	public boolean showMessage = false;
	public int messageAlpha;
	String message;

	//Non criticial, visual devalopment stuff
	public List<Point> links;
	public List<Point> panelLinks;
	int reach;
	public double hA = 1;
	public double vA = 0;
	//Stuff for a painting icon to see if a regular paint is occuring
	Color pI = new Color(255, 0, 0);
	int pIDir = 15;

	/**
	 * Construct a new Screen object.
	 * 
	 * @param c - the reference to the core object that created it
	 */
	public Screen(Render c) {
		Screen.c = c;
		this.mode = ScreenMode.Night;
	}

	/**
	 * Draw everything that needs to be draw to represent one frame of the screen.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void draw(Graphics2D g) {
		if (mode==ScreenMode.Day) g.setColor(new Color(239, 250, 237));
		else g.setColor(new Color(20, 20, 20));
		g.fillRect(0, 0, Render.sW, Render.sH);

		links = new ArrayList<Point>();
		panelLinks = new ArrayList<Point>();

		if (showAxis) {
			if (c.p instanceof V3D) draw3DAxis(g);
			else draw2DAxis(g);
		}
		if (showAxisMarkings && c.p instanceof V3D) drawAxisMarkings(g);

		//BufferedImage b = new BufferedImage(Render.sW, Render.sH, BufferedImage.TYPE_INT_RGB);
		drawObjects(null, g);
		//g.drawImage(b, 0, 0, null);
		if (showParticles && c.p instanceof V3D) drawParticleSystems(g);
		if (showAxisMarkings && c.p instanceof V3D) drawCircle(g);

		if (showLinks && c.p instanceof V3D) drawLinks(g);
		if (showNormals && c.p instanceof V3D) drawNormals(g);
		if (showLightSources && c.p instanceof V3D) drawLightSources(g);
		if (showDepthBuffer && c.p instanceof V3D) {
			drawDepthBufferVisualiser(g);
			drawTest(g);
		}
		drawSelection(g);
		if (showMessage) drawMessage(g);
		if (showHUD) drawHUD(g);
		drawGUIComponents(g);
	}

	/**
	 * Draw a message on the screen, with a nice in and out fade.
	 * Blurs the background almost all the way with and draws
	 * in a message with a yellow drop shadow.
	 * @param g
	 */
	public void drawMessage(Graphics2D g) {
		int bckgAlpha = messageAlpha;
		if (bckgAlpha>200) bckgAlpha = 200;
		if (mode==ScreenMode.Day) g.setColor(new Color(239, 250, 237, bckgAlpha));
		else g.setColor(new Color(20, 20, 20, bckgAlpha));
		g.fillRect(0, 0, Render.sW, Render.sH);

		//Formulate message
		char[] messChar = new char[message.length()];
		for (int i=0; i<message.length(); i++) messChar[i] = message.charAt(i);
		List<String> messList = new ArrayList<>();

		String temp = "";
		int count = 0;
		for (int i=0; i<messChar.length; i++) {
			temp += messChar[i];
			if (count==10&&i!=0) {//Exceeded limit
				boolean trig = true;
				while (trig) { //Find end of word
					if (i+1<messChar.length&&!Character.isWhitespace(messChar[i+1])) {
						i++;
						temp += messChar[i];
					}
					else {
						trig = false;
						if (i+1<messChar.length) i++; //Skip whitespace
					}
				}
				messList.add(temp);
				temp = "";
				count = 0;
			}
			count++;
		}
		if (temp.length()!=0) messList.add(temp);


		//Draw highlight
		int x = 103;
		int y = 203;
		g.setFont(new Font("Verdana", Font.BOLD, 100));
		g.setColor(new Color(230, 157, 0, messageAlpha));
		for (String s : messList) {
			g.drawString(s, x, y);
			y += 110;
		}

		//Draw message
		x = 100;
		y = 200;
		if (mode==ScreenMode.Day) g.setColor(new Color(0, 0, 0, 200));
		else g.setColor(new Color(220, 220, 220, messageAlpha));
		for (String s : messList) {
			g.drawString(s, x, y);
			y += 110;
		}
		g.setFont(new Font("Ariel", Font.PLAIN, 13));

	}

	/**
	 * Draw all running particle systems.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawParticleSystems(Graphics2D g) {
		for (ParticleSystem s : c.systems) {
			try {
				if (s.isRunning()&&!s.isSorted) s.draw(g);
			}
			catch (ConcurrentModificationException c) {}
		}
	}

	/**
	 * Draw helper links to objects.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawLinks(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		g.setStroke(new BasicStroke(1));
		g.setColor(new Color(255, 153, 0));

		//Draw View Point
		Point pX = c.p.xAV(0);
		Point pY = c.p.yAV(0);
		Point pZ = c.p.zAV(0);
		Point start = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
		g.fillRect(oX+start.x-3, oY+start.y-3, 6, 6);

		for (Point p : links) {
			g.fillRect(oX+p.x-3, oY+p.y-3, 6, 6);
			g.drawLine(oX+start.x, oY+start.y, oX+p.x, oY+p.y);
		}
	}

	/**
	 * Draw normal links to faces.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawNormals(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		g.setStroke(new BasicStroke(1));
		g.setColor(new Color(255, 50, 0));

		for (PlanarRoot p : c.polyPool) {
			if (!p.isSystemIndicator) {
				Vec3D norm = c.calculateNormal(p);

				if (norm!=null) {
					norm = new Vec3D(norm.x*50, norm.y*50, norm.z*50);

					Vec3 mid = c.findMidPoint(p);
					Point mX = c.p.xAV(mid.x);
					Point mY = c.p.yAV(mid.y);
					Point mZ = c.p.zAV(mid.z);
					Point cent = new Point(mX.x+mY.x+mZ.x, mX.y+mY.y+mZ.y);

					Vec3 nMid = new Vec3((int) (mid.x+norm.x), (int) (mid.y+norm.y), (int) (mid.z+norm.z));
					mX = c.p.xAV(nMid.x);
					mY = c.p.yAV(nMid.y);
					mZ = c.p.zAV(nMid.z);
					Point nCent = new Point(mX.x+mY.x+mZ.x, mX.y+mY.y+mZ.y);

					g.fillRect(oX+cent.x-3, oY+cent.y-3, 6, 6);
					g.fillRect(oX+nCent.x-3, oY+nCent.y-3, 6, 6);
					g.drawLine(oX+cent.x, oY+cent.y, oX+nCent.x, oY+nCent.y);
				}
			}
		}
	}

	/**
	 * Draw all lightsources in the modeler.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawLightSources(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		g.setStroke(new BasicStroke(1));
		g.setColor(new Color(255, 50, 0));

		for (LightSource lS : c.lightSources) {
			if (!lS.active) continue;
			
			Vec3 light = new Vec3((int) (500*lS.vector.x), (int) (500*lS.vector.y), (int) (500*lS.vector.z));
			Point lX = c.p.xAV(-light.x);
			Point lY = c.p.yAV(-light.y);
			Point lZ = c.p.zAV(-200-light.z);
			Point lightP = new Point(lX.x+lY.x+lZ.x, lX.y+lY.y+lZ.y);

			//Soft edge
			int am = 120;
			int amAdj;
			for (int i=0; i<am; i++) {
				g.setColor(new Color(lS.getActualRed(), lS.getActualGreen(), lS.getActualBlue(), i*(255/am)));
				amAdj = am/3;
				amAdj = (int) (c.p.scale*amAdj);
				g.fillOval(oX+lightP.x-(amAdj/2)+(i/2), oY+lightP.y-(amAdj/2)+(i/2), amAdj-i, amAdj-i);
			}
			//Center
			g.setColor(new Color(lS.getActualRed(), lS.getActualGreen(), lS.getActualBlue()));
			g.fillOval(oX+lightP.x-2, oY+lightP.y-2, 4, 4);
		}
	}

	/**
	 * Draw all GUI Components.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawGUIComponents(Graphics2D g) {
		for (Tab t : c.tabs) t.repaint();
		for (Window t : c.windows) t.repaint();
	}

	/**
	 * Print development values and information.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawHUD(Graphics2D g) {
		//Stats
		g.setColor(selectColor);
		g.drawString(c.currentTool.toolMode.toString(), 10, 20);
		g.drawString(c.mode.toString(), 10, 40);

		g.drawString("polys : "+Integer.toString(c.polyPool.size()), 10, (int) (Render.sH*0.83));
		g.drawString("objs : "+Integer.toString(c.objectPool.size()), 10, (int) (Render.sH*0.85));
		g.drawString("vertRot = "+Double.toString(c.p.vertRot), 10, (int) (Render.sH*0.88));
		g.drawString("horzRot = "+Double.toString(c.p.horzRot), 10, (int) (Render.sH*0.90));
		g.drawString("scale = "+Double.toString(c.p.scale), 10, (int) (Render.sH*0.92));

		g.drawString("xOffset = "+Double.toString(c.p.xOffset), 10, (int) (Render.sH*0.95));
		g.drawString("yOffset = "+Double.toString(c.p.yOffset), 10, (int) (Render.sH*0.97));

		g.drawString("hA = "+Double.toString(Math.round(hA*100.0)/100.0), 1235, 40);
		g.drawString("vA = "+Double.toString(Math.round(vA*100.0)/100.0), 1235, 60);
		
		//Painting icon
		if (pI.getBlue()+pIDir>255||pI.getBlue()+pIDir<0) pIDir = -pIDir;
		pI = new Color(pI.getRed()-pIDir, 0, pI.getBlue()+pIDir);
		g.setColor(pI);
		g.fillOval(1280, 10, 10, 10);
	}

	/**
	 * Draw a selection box.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawSelection(Graphics2D g) {
		if (selectionBox!=null) {
			g.setColor(selectColor);
			g.drawRect(selectionBox.x, selectionBox.y, selectionBox.width, selectionBox.height);
		}
	}

	/**
	 * Ordering and draw all objects, and any ordered particle systems
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawObjects(BufferedImage b, Graphics2D g) {
		for (PlanarRoot p : c.sortObjects()) {
			if (p.layer.isVisible() && c.masterLayer.isVisible()) {
				if (p.isSystemIndicator) {
					for (ParticleSystem s : c.systems) {
						if (s==p.system) {
							if (s.isRunning()&&showParticles&&c.p instanceof V3D) {
								try {s.draw(g);} catch (ConcurrentModificationException c) {}
							}
							break;
						}
					}
				}
				else {
					if (c.p instanceof V3D) p.draw3D(g, b, c.p);
					else p.draw2D(g, c.p);
					c.calculateObjectDistance(p); //For linking
				}
			}
		}
	}

	/**
	 * Draw the 3D axis relative to the perspective
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void draw3DAxis(Graphics2D g) {
		reach = (int) (-100*Math.pow(c.p.scale-1, 3)+1)*1000;
		if (reach<1000) reach = 1000;

		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;

		//X Axis
		Point start = c.p.xAV(-reach);
		Point end = c.p.xAV(reach);
		g.setColor(new Color(255, 0, 0, 200));
		g.drawLine(oX+start.x, oY+start.y, oX+end.x, oY+end.y);

		//Y Axis
		start = c.p.yAV(-reach);
		end = c.p.yAV(reach);
		g.setColor(new Color(0, 255, 0, 200));
		g.drawLine(oX+start.x, oY+start.y, oX+end.x, oY+end.y);

		//Z Axis
		start = c.p.zAV(-reach);
		end = c.p.zAV(reach);
		g.setColor(new Color(0, 0, 255, 200));
		g.drawLine(oX+start.x, oY+start.y, oX+end.x, oY+end.y);
	}

	/**
	 * Draw the 2D axis relative to the perspective
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void draw2DAxis(Graphics2D g) {
		double dX = c.p.xOffset;
		double dY = c.p.yOffset;

		//X axis
		g.setColor(new Color(255, 0, 0, 200));
		g.drawLine(0, (int) (dY+Render.sH/2-2), Render.sW, (int) (dY+Render.sH/2-2));

		//Y axis
		g.setColor(new Color(0, 255, 0, 200));
		g.drawLine((int) (dX+Render.sW/2-2), 0, (int) (dX+Render.sW/2-2), Render.sH);
	}

	/**
	 * Draw measurements on axis and rotations. Used for development
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawAxisMarkings(Graphics2D g) {
		int sens = 25;
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;

		if (mode==ScreenMode.Night) g.setColor(Color.WHITE);
		else g.setColor(new Color(0, 0, 0));
		//X Axis
		for (int i=-reach; i<reach; i+=sens) {
			Point start = c.p.xAV(i);
			g.fillOval(oX+start.x-2, oY+start.y-2, 4, 4);
		}
		//Y Axis
		for (int i=-reach; i<reach; i+=sens) {
			Point start = c.p.yAV(i);
			g.fillOval(oX+start.x-2, oY+start.y-2, 4, 4);
		}
		//Z Axis
		for (int i=-reach; i<reach; i+=sens) {
			Point start = c.p.zAV(i);
			g.fillOval(oX+start.x-2, oY+start.y-2, 4, 4);
		}
	}

	/**
	 * Draw circles based on the current perspective's rotation.
	 * Used for devalopment
	 * 
	 * @param g - the current Graphics2D object
	 */
	public void drawCircle(Graphics2D g) {
		if (mode==ScreenMode.Night) g.setColor(new Color(255, 255, 255, 40));
		else g.setColor(new Color(0, 0, 0, 40));

		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		int r = 300;

		for (int in=-r; in<r; in++) {
			double out1 = Math.sqrt(Math.pow(r, 2)-Math.pow(in, 2));
			double out2 = -Math.sqrt(Math.pow(r, 2)-Math.pow(in, 2));

			c.p.zAV((int) out1);
			c.p.zAV((int) out2);


			c.p.xAV((int) out1);
			c.p.xAV((int) out2);
			c.p.zAV(in);

			//Circles at 45 degrees
			Point fX1 = c.p.xAV((int) out1);
			Point fX2 = c.p.xAV((int) out2);
			Point fY1 = c.p.yAV((int) out1);
			Point fY2 = c.p.yAV((int) out2);
			Point fZ = c.p.zAV(in);

			g.fillRect(oX+fX1.x+fY1.x+fZ.x, oY+fX1.y+fY1.y+fZ.y, 2, 2);
			g.fillRect(oX+fX2.x+fY2.x+fZ.x, oY+fX2.y+fY2.y+fZ.y, 2, 2);

			g.fillRect(oX+fX1.x-fY1.x+fZ.x, oY+fX1.y-fY1.y+fZ.y, 2, 2);
			g.fillRect(oX+fX2.x-fY2.x+fZ.x, oY+fX2.y-fY2.y+fZ.y, 2, 2);
		}

		//Draw perspective location points

		double a = c.p.vertRot;
		int in = (int) -(a*r);

		double out1 = Math.sqrt(Math.pow(r, 2)-Math.pow(in, 2));
		double out2 = -Math.sqrt(Math.pow(r, 2)-Math.pow(in, 2));

		Point fX1 = c.p.xAV((int) out1);
		Point fX2 = c.p.xAV((int) out2);
		Point fY1 = c.p.yAV((int) out1);
		Point fY2 = c.p.yAV((int) out2);
		Point fZ = c.p.zAV(in);

		g.setColor(new Color(255, 0, 0, 150));
		g.fillOval(oX+fX2.x+fY2.x+fZ.x-4, oY+fX2.y+fY2.y+fZ.y-4, 8, 8);
		g.fillOval(oX+fX1.x-fY1.x+fZ.x-4, oY+fX1.y-fY1.y+fZ.y-4, 8, 8);
	}

	/*
	 * Draws a visualised version of the depth buffer for testing. Uses the dummy
	 * hA and vA values that user can control to see how the buffer works
	 * on specific elements. Used to diagnose and fix problems with the buffer/
	 * general poly-ordering problems.
	 * 
	 * 
	 * Make a non-rastered line to represent the field of view.
	 * Line must be calculated out of 3D co-ordinates before rasterisation
	 * so that objects can measure off of it using their 3D position.
	 * 
	 * Line is used so the depth of objects can be determined relative to the
	 * viewer, and as such the line should be movable to sit over an object's
	 * x and y, without changing the angle
	 * 
	 * Observations
	 * vertrot = 1 - order from z->-z (regardless of horzRot)
	 * vertrot = 0 {
	 *     horzRot = 1 - order from -x->x
	 *     horzRot = 0 - order from y->-y
	 *     horzRot = 1 - order from x->-x
	 * }
	 * vertrot = -1 - order from -z->z (regardless of horzRot)

	 * 
	 * From -1 -> -0.5 - y = -(2(a+0.5)+1)*x
	 * From -0.5 -> 0 - x = 2a*y
	 * From 0 -> 0.5 - x = 2a*y
	 * From 0.5 -> 1 - y = -2((a-0.5)-1)*x
	 */
	public void drawDepthBufferVisualiser(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		int limit = c.depthLimit;
		g.setColor(new Color(255, 153, 0));
		double a = c.p.horzRot;
		int y = 0;

		for (PlanarRoot o : c.polyPool) {
			if (o.layer.isVisible()&&!o.isSystemIndicator) {
				Vec3 mid = c.findMidPoint(o);

				//Find xIntercept
				int xIntercept = 0;
				if (a>=-1&&a<-0.5) xIntercept = (int) (-mid.y/-(2*(a+0.5)+1)+mid.x);
				if (a>=-0.5&&a<0.5) xIntercept = (int) ((-mid.y*(2*a))+mid.x);
				if (a>=0.5&&a<=1) xIntercept = (int) (-mid.y/-(2*(a-0.5)-1)+mid.x);
				//Equalise limit
				Point eqLimit = new Point((int) (-limit+xIntercept*(1-a)), 0);
				if (a<0) eqLimit.x = (int) (limit+xIntercept*(1+a));

				//Draw limit point
				if (a>=-1&&a<-0.5) y = (int) ((-(2*(a+0.5)+1)*(eqLimit.x-mid.x))+mid.y);
				if (a>=-0.5&&a<0.5) y = (int) (((eqLimit.x-mid.x)/(2*a))+mid.y);
				if (a>=0.5&&a<=1) y = (int) ((-(2*(a-0.5)-1)*(eqLimit.x-mid.x))+mid.y);
				Point pX = c.p.xAV(eqLimit.x);
				Point pY = c.p.yAV(y);
				Point pZ = c.p.zAV(0);
				Point start = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+start.x-3, oY+start.y-3, 6, 6);

				//Draw object midpoint
				pX = c.p.xAV(mid.x);
				pY = c.p.yAV(mid.y);
				pZ = c.p.zAV(0);
				Point end = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+end.x-3, oY+end.y-3, 6, 6);

				//Draw connecting line
				g.drawLine(oX+start.x, oY+start.y, oX+end.x, oY+end.y);

			}
		}
	}



	/*
	 * Do the horizontal calculations. But then put in z too.
	 * Put a squisher on z out of phase so that it has no effect at vA==0
	 * but full effect at vA==-1||1.
	 * 
	 * Then also need to put a squisher on x and y values so that they lose
	 * priority the higher and lower we get, and have full/highest priority at
	 * vA = 0;
	 * 
	 * 
	 * Model view depth line
	 * Calculate distance down line to object by calculating hyp of right angled
	 * triangle down that line.
	 */

	public void drawTest(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;
		int reach = 400;
		int limit = 200; //Measuring distance

		//double hA = c.p.horzRot;
		//double vA = c.p.vertRot;
		//if (c.p.horzRot<0) vA = -vA;

		double nHA = hA;
		double nVA = vA;
		if (hA<0) nVA = -nVA;

		int x = 0;
		int y = 0;
		int z = 0;


		//&&(o.name.equals("Purple")||o.name.equals("Red"))
		for (PlanarRoot o : c.polyPool) {
			if (o.layer.isVisible()&&!o.isSystemIndicator) {
				Vec3 mid = c.findMidPoint(o);
				g.setColor(new Color(255, 166, 0, 80));

				//PHASE 1 - FINDING LINES
				//XZ PLANE
				x = -reach;
				if (nVA>=-1&&nVA<-0.5) z = (int) (x/(-2*(nVA+1)));
				if (nVA>=-0.5&&nVA<0.5) z = (int) ((2*nVA)*x);
				if (nVA>=0.5&&nVA<=1) z = (int) (x/(-2*(nVA-1)));
				int xS1 = x;
				int zS1 = z;


				x = reach;
				if (nVA>=-1&&nVA<-0.5) z = (int) (x/(-2*(nVA+1)));
				if (nVA>=-0.5&&nVA<0.5) z = (int) ((2*nVA)*x);
				if (nVA>=0.5&&nVA<=1) z = (int) (x/(-2*(nVA-1)));
				int xE1 = x;
				int zE1 = z;

				//XY PLANE
				x = -reach;
				if (nHA>=-1&&nHA<-0.5) y = (int) (-(2*(nHA+0.5)+1)*x);
				if (nHA>=-0.5&&nHA<0.5) y = (int) (x/(2*nHA));
				if (nHA>=0.5&&nHA<=1) y = (int) (-(2*(nHA-0.5)-1)*x);
				int yS2 = y;

				x = reach;
				if (nHA>=-1&&nHA<-0.5) y = (int) ((-(2*(nHA+0.5)+1)*x));
				if (nHA>=-0.5&&nHA<0.5) y = (int) (x/(2*nHA));
				if (nHA>=0.5&&nHA<=1) y = (int) (-(2*(nHA-0.5)-1)*x);
				int yE2 = y;

				//COMBINE
				Point pX = c.p.xAV(xS1+mid.x);
				Point pY = c.p.yAV(yS2+mid.y);
				Point pZ = c.p.zAV(zS1+mid.z);
				Point s3 = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				pX = c.p.xAV(xE1+mid.x);
				pY = c.p.yAV(yE2+mid.y);
				pZ = c.p.zAV(zE1+mid.z);
				Point e3 = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);

				g.drawLine(oX+s3.x, oY+s3.y, oX+e3.x, oY+e3.y);
				//g.fillRect(oX+e3.x-4, oY+e3.y-4, 8, 8);


				//PHASE 2 - CALCULATING nVANTAGE POINT
				//FINDING X INTERCEPT ON XY PLANE
				int xIntercept = 0;
				x = 0;
				if (nHA>=-1&&nHA<-0.5) xIntercept = (int) (-mid.y/-(2*(nHA+0.5)+1)+mid.x);
				if (nHA>=-0.5&&nHA<0.5) xIntercept = (int) ((-mid.y*(2*nHA))+mid.x);
				if (nHA>=0.5&&nHA<=1) xIntercept = (int) (-mid.y/-(2*(nHA-0.5)-1)+mid.x);
				pX = c.p.xAV(xIntercept);
				pY = c.p.yAV(0);
				pZ = c.p.zAV(mid.z);
				Point inter = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+inter.x-4, oY+inter.y-4, 8, 8);

				//Equalise for differences on the xy plane
				int eqLimit = (int) (-limit+xIntercept*(1-nHA));
				if (nHA<0) eqLimit = (int) (limit+xIntercept*(1+nHA));

				//Equalise for differences on the xz plane
				if (nVA>0) eqLimit -= mid.z/2;
				else eqLimit += mid.z/2;
				if (nHA<0) eqLimit += mid.z/2;

				//Find top point
				x = eqLimit;
				if (nHA>=-1&&nHA<-0.5) y = (int) ((-(2*(nHA+0.5)+1)*(x-mid.x))+mid.y);
				if (nHA>=-0.5&&nHA<0.5) y = (int) (((x-mid.x)/(2*nHA))+mid.y);
				if (nHA>=0.5&&nHA<=1) y = (int) ((-(2*(nHA-0.5)-1)*(x-mid.x))+mid.y);

				if (nVA>=-1&&nVA<-0.5) z = (int) ((x-mid.x)/(-2*(nVA+1))+mid.z);
				if (nVA>=-0.5&&nVA<0.5) z = (int) ((2*nVA)*(x-mid.x)+mid.z);
				if (nVA>=0.5&&nVA<=1) z = (int) ((x-mid.x)/(-2*(nVA-1))+mid.z);
				Vec3 top = new Vec3(x, y, z);

				//Find bottom point
				x = eqLimit;
				if (nHA>=-1&&nHA<-0.5) y = (int) ((-(2*(nHA+0.5)+1)*(x-mid.x))+mid.y);
				if (nHA>=-0.5&&nHA<0.5) y = (int) (((x-mid.x)/(2*nHA))+mid.y);
				if (nHA>=0.5&&nHA<=1) y = (int) ((-(2*(nHA-0.5)-1)*(x-mid.x))+mid.y);
				Vec3 bottom = new Vec3(x, y, mid.z);

				//Draw points
				g.setColor(Color.ORANGE);
				pX = c.p.xAV(top.x);
				pY = c.p.yAV(top.y);
				pZ = c.p.zAV(top.z);
				Point p1 = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+p1.x-4, oY+p1.y-4, 8, 8);

				pX = c.p.xAV(bottom.x);
				pY = c.p.yAV(bottom.y);
				pZ = c.p.zAV(bottom.z);
				Point p2 = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+p2.x-4, oY+p2.y-4, 8, 8);

				pX = c.p.xAV(mid.x);
				pY = c.p.yAV(mid.y);
				pZ = c.p.zAV(mid.z);
				Point p3 = new Point(pX.x+pY.x+pZ.x, pX.y+pY.y+pZ.y);
				g.fillRect(oX+p3.x-4, oY+p3.y-4, 8, 8);

				//Draw links
				g.drawLine(oX+p1.x, oY+p1.y, oX+p2.x, oY+p2.y);
				g.drawLine(oX+p2.x, oY+p2.y, oX+p3.x, oY+p3.y);
				g.drawLine(oX+p1.x, oY+p1.y, oX+p3.x, oY+p3.y);

				//Calculate depth
				/*double depth;

				//All condition's reach are widened slightly to account for artifacting
				if (nVA<0.01&&nVA>-0.01) {
					System.out.println("trig");
					if (nHA<=-0.95) depth = limit-mid.x;
					else if (nHA>=0.95) depth = limit+mid.x;
					else if (nHA<0.01&&nHA>-0.01) depth = limit+mid.y; 
					else { //Calculate RAT based off only XY plane
						depth = Math.sqrt(Math.pow(mid.x-top.x, 2)+Math.pow(mid.y-top.y, 2));
					}
				}
				else if (nVA>=0.95) depth = limit+mid.z;
				else if (nVA<=-0.95) depth = limit-mid.z;
				else { //Calculate RAT on xyz plane
					depth = Math.sqrt(Math.pow(top.x-mid.x, 2)+Math.pow(top.z-mid.z, 2));
				}

				o.depth = depth;*/
			}
		}
	}
}





/*if (nVA==1) depth = limit+mid.z;
				else if (nVA==-1) depth = limit-mid.z;
				else if (nHA==-1) depth = limit-mid.x;
				else if (nHA==1) depth = limit+mid.x;
				else if (nHA<0.01&&nHA>-0.01) depth = limit+mid.y; //This condition's reach is widened slightly to account for artifacting 
				else if (nVA==0) { //Calculate RAT based off only XY plane
					depth = Math.sqrt(Math.pow(mid.x-top.x, 2)+Math.pow(mid.y-top.y, 2));
				}
				else { //Calculate RAT on xyz plane
					depth = Math.sqrt(Math.pow(top.x-mid.x, 2)+Math.pow(top.z-mid.z, 2));
				}*/
