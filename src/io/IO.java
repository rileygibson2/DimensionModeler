package io;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import core.Implementation.Direction;
import core.Implementation.Level;
import core.Layer;
import core.Layer.Visibility;
import core.Render;
import core.Render.Mode;
import core.Tool;
import core.Tool.ToolMode;
import guicomponents.OptionsTab;
import guicomponents.Tab;
import guicomponents.Window;
import io.AudioManager.sFX;

public class IO implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, ActionListener {
	Render c;
	Map<JMenu, List<JMenuItem>> menus;
	final static boolean initWindowState = true;

	boolean dragging = false;
	int prevX;
	int prevY;

	public IO(Render c) {
		this.c = c;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int xDif = x-prevX;
		int yDif = y-prevY;
		ToolMode m = c.currentTool.toolMode;

		if (dragging) {
			//Deal with viewport drag
			if (m==ToolMode.Move) {
				c.movePerspective(xDif, yDif);
			}
			//Deal with viewport angle drag
			else if (m==ToolMode.Angle) {
				c.changeVerticalPerspective(yDif);
				c.changeHorizontalPerspective(xDif);
			}
			//Deal with selection box drag
			else if (m==ToolMode.Select) {
				if (prevX<x&&prevY<y) Render.s.selectionBox = new Rectangle(prevX, prevY, x-prevX, y-prevY);
				else if (prevX>x&&prevY<y) Render.s.selectionBox = new Rectangle(x, prevY, prevX-x, y-prevY);
				else if (prevX<x&&prevY>y) Render.s.selectionBox = new Rectangle(prevX, y, x-prevX, prevY-y);
				else Render.s.selectionBox = new Rectangle(x, y, prevX-x, prevY-y);
			}
		}
		else dragging = true;

		//Everything but select wants to save prev positions on drag, so this stops code repitition
		if (m!=ToolMode.Select) {
			prevX = x;
			prevY = y;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		prevX = e.getX();
		prevY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//Finish seleciton drag
		/*if (c.currentTool.toolMode==ToolMode.Select) {
			if (dragging&&Render.s.selectionBox!=null) c.selectObjects(Render.s.selectionBox);
			else c.selectObjects(e.getX(), e.getY());
		}*/

		dragging = false;
		Render.s.selectionBox = null;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (c.currentTool.toolMode!=ToolMode.Select) {
			c.zoom(e.getPreciseWheelRotation());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char ch = e.getKeyChar();
		if (e.getKeyCode()==KeyEvent.VK_ESCAPE) c.clearSelection();
		
		switch (ch) {
		case 'i' : Render.aM.forceStopAll(); break;
		case 'o' : Render.aM.fadeAll(); break;
		case 'p' : Render.aM.run(sFX.Winning); break;
		case 'g' : c.runSystem("Water"); break;
		case 'h' : c.currentTool = c.getTool(ToolMode.Move); break;
		case 'b' : c.currentTool = c.getTool(ToolMode.Angle); break;

		case '9' : if (Render.s.hA-0.1>=-1) Render.s.hA -= 0.1; break;
		case '0' : if (Render.s.hA+0.1<=1) Render.s.hA += 0.1; break;
		case '-' : if (Render.s.vA-0.1>=-1) Render.s.vA -= 0.1; break;
		case '=' : if (Render.s.vA+0.1<=1) Render.s.vA += 0.1; break;

		case 'w' : if (!Render.a.isRunning("playerMoving")) c.im.movePlayer(Direction.Forward); break;
		case 's' : if (!Render.a.isRunning("playerMoving")) c.im.movePlayer(Direction.Back); break;
		case 'a' : if (!Render.a.isRunning("playerMoving")) c.im.movePlayer(Direction.Left); break;
		case 'd' : if (!Render.a.isRunning("playerMoving")) c.im.movePlayer(Direction.Right); break;
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		//Prompt the tabs and windows to move by setting their pos to their current pos
		for (Tab t : c.tabs) t.frame.setLocation(c.getLocationOnScreen().x+t.x, c.getLocationOnScreen().y+t.y);
		for (Window w : c.windows) w.frame.setLocation(c.getLocationOnScreen().x+w.x, c.getLocationOnScreen().y+w.y);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JMenu menu = null;
		String command = e.getActionCommand();
		for (Map.Entry<JMenu, List<JMenuItem>> m : menus.entrySet()) {
			if (m.getValue()!=null&&m.getValue().contains(e.getSource())) menu = m.getKey();
		}
		//System.out.println(menu.getActionCommand()+" -> "+command);

		if (menu!=null) {
			switch (menu.getActionCommand()) {
			case "Window" :
				for (Tab t : c.tabs) if (t.name.equals(command)) t.frame.setVisible(((JCheckBoxMenuItem) e.getSource()).isSelected());
				for (Window w : c.windows) if (w.name.equals(command)) w.frame.setVisible(((JCheckBoxMenuItem) e.getSource()).isSelected());
				switch (command) {
				case "Hide All" :
					for (Tab t : c.tabs) t.frame.setVisible(false);
					for (JMenuItem j : menus.get(menu)) j.setSelected(false);
					break;
				case "Show All" :
					for (Tab t : c.tabs) t.frame.setVisible(true);
					for (JMenuItem j : menus.get(menu)) j.setSelected(true);
					break;
				}
				break;

			case "Tools" :
				for (Tool t : c.tools) if (t.toolMode.name().equals(command)) c.currentTool = t;
				break;

			case "Levels" :
				for (Level l : Level.values()) if (l.name().equals(command)) c.im.build(true, l);
				break;

			case "Options" :
				switch (command) {
				case "Wireframe Mode" : 
					c.mode = Mode.Wireframe;
					for (JMenuItem j : menus.get(menu)) {
						if (j.getActionCommand().equals("Panelled Mode")) j.setSelected(false);
					}
					break;
				case "Panelled Mode" : 
					c.mode = Mode.Panelled;
					for (JMenuItem j : menus.get(menu)) {
						if (j.getActionCommand().equals("Wireframe Mode")) j.setSelected(false);
					}
					break;
				case "Show HUD" : Render.s.showHUD = !Render.s.showHUD; break;
				case "Show Axis" : Render.s.showAxis = !Render.s.showAxis; break;
				case "Show LightSources" : Render.s.showLightSources = !Render.s.showLightSources; break;
				case "Select All Objects" : c.selectAll(); break;
				case "Clear Selection" : c.clearSelection(); break;
				case "Wipe Polygons" : 
					c.cleanPolyPool(null, false);
					c.cleanObjectPool(null);
					break;
				}
				break;
				
			case "Layers" :
				switch (command) {
				case "Show All" :
					for (Layer l : c.layers) l.setVisibility(Visibility.Visible);
					break;
				case "Grey All" :
					c.masterLayer.setVisibility(Visibility.Visible);
					for (Layer l : c.layers) l.setVisibility(Visibility.Greyed);
					break;
				case "Hide All" :
					for (Layer l : c.layers) l.setVisibility(Visibility.Hidden);
					break;
				}
				break;
				
			default : break;
			}
		}
	}
	
	/**
	 * Used so that tabs can update the menubar when they are updated
	 * outside of a menubar or command key press.
	 * 
	 * @param name - the name of the tab
	 * @param value - the visibility of the window
	 */
	public void notifyMenuBar(String name, boolean value) {
		for (List<JMenuItem> m : menus.values()) {
			for (JMenuItem j : m) {
				if (j.getActionCommand().equals(name)) {
					if (j instanceof JCheckBoxMenuItem) j.setSelected(value);
					return;
				}
			}
 		}
	}

	public void makeMenuBar() {
		//Menu bar stuff
		JMenuBar menuBar = new JMenuBar();
		menus = new LinkedHashMap<JMenu, List<JMenuItem>>();
		JMenu tempM;
		List<JMenuItem> tempMIL;
		JMenuItem j;

		//Tools
		tempM = new JMenu("Tools"); 
		tempMIL = new ArrayList<JMenuItem>();
		for (Tool t : c.tools) {
			j = new JMenuItem(t.toolMode.name());
			j.addActionListener(this);
			j.setAccelerator(KeyStroke.getKeyStroke(t.actionKey, KeyEvent.META_DOWN_MASK));
			tempMIL.add(j);
			tempM.add(j);
		}
		menus.put(tempM, tempMIL);

		//Options
		tempM = new JMenu("Options");
		tempMIL = new ArrayList<JMenuItem>();
		tempMIL.add(new JCheckBoxMenuItem("Wireframe Mode", true));
		tempMIL.add(new JCheckBoxMenuItem("Panelled Mode", false));
		tempMIL.add(new JCheckBoxMenuItem("Show HUD", true));
		tempMIL.add(new JCheckBoxMenuItem("Show Axis", true));
		tempMIL.add(new JCheckBoxMenuItem("Show LightSources", true));
		tempMIL.add(new JMenuItem("Select All Objects"));
		tempMIL.add(new JMenuItem("Clear Selection"));
		tempMIL.add(new JMenuItem("Wipe Polygons"));
		for (int i=0; i<tempMIL.size(); i++) {
			j = tempMIL.get(i);
			j.addActionListener(this);
			if (i<4)j.setAccelerator(KeyStroke.getKeyStroke((char)(49+i), KeyEvent.META_DOWN_MASK));
			tempM.add(j);
			if (i==1||i==4||i==5) tempM.add(new JSeparator());
		}
		menus.put(tempM, tempMIL);

		//Levels
		tempM = new JMenu("Levels");
		tempMIL = new ArrayList<JMenuItem>();
		for (Level l : Level.values()) {
			j = new JMenuItem(l.name());
			j.addActionListener(this);
			tempMIL.add(j);
			tempM.add(j);
		}
		menus.put(tempM, tempMIL);

		//Layers
		tempM = new JMenu("Layers"); 
		tempMIL = new ArrayList<JMenuItem>();
		j = new JMenuItem("Show All");
		j.addActionListener(this);
		tempMIL.add(j);
		tempM.add(j);
		j = new JMenuItem("Grey All");
		j.addActionListener(this);
		tempMIL.add(j);
		tempM.add(j);
		j = new JMenuItem("Hide All");
		j.addActionListener(this);
		tempMIL.add(j);
		tempM.add(j);
		menus.put(tempM, tempMIL);

		//Window
		tempM = new JMenu("Window"); 
		tempMIL = new ArrayList<JMenuItem>();
		boolean state;
		for (Tab t : c.tabs) {
			if (!(t instanceof OptionsTab)) state = false;
			else state = initWindowState;
			j = new JCheckBoxMenuItem(t.name, state);
			j.addActionListener(this);
			j.setAccelerator(KeyStroke.getKeyStroke(t.actionKey, KeyEvent.META_DOWN_MASK));
			t.frame.setVisible(state);

			tempMIL.add(j);
			tempM.add(j);
		}
		for (Window w : c.windows) {
			state = true;
			j = new JCheckBoxMenuItem(w.name, state);
			j.addActionListener(this);
			j.setAccelerator(KeyStroke.getKeyStroke(w.actionKey, KeyEvent.META_DOWN_MASK));
			w.frame.setVisible(state);
			
			tempMIL.add(j);
			tempM.add(j);
		}
		
		tempM.add(new JSeparator());
		j = new JMenuItem("Hide All");
		j.addActionListener(this);
		tempMIL.add(j);
		tempM.add(j);
		j = new JMenuItem("Show All");
		j.addActionListener(this);
		tempMIL.add(j);
		tempM.add(j);
		menus.put(tempM, tempMIL);

		for (JMenu m : menus.keySet()) menuBar.add(m);
		Render.frame.setJMenuBar(menuBar);
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void componentResized(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
}
