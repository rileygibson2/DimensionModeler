package core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Taskbar;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import core.Layer.Visibility;
import core.Screen.ScreenMode;
import core.Tool.ToolMode;
import guicomponents.AnimationsTab;
import guicomponents.LayersTab;
import guicomponents.LightControlWindow;
import guicomponents.ObjectsTab;
import guicomponents.OptionsTab;
import guicomponents.ProgressBar;
import guicomponents.SystemsTab;
import guicomponents.Tab;
import guicomponents.ToolsTab;
import guicomponents.ViewsTab;
import guicomponents.Window;
import io.AudioManager;
import io.IO;
import objects.Circle;
import objects.ObjectRoot;
import objects.PlanarRoot;
import objects.Polygon;
import particlesystems.Acid;
import particlesystems.Electricity;
import particlesystems.Fire;
import particlesystems.Fireworks;
import particlesystems.ParticleSystem;
import particlesystems.Sparks;
import particlesystems.Water;
import perspectives.CustomView;
import perspectives.Perspective;
import perspectives.PlanView;
import perspectives.RightIsoView;
import perspectives.TestView;
import perspectives.TopView;
import perspectives.UnderIsoView;
import textures.ChipTexture;
import textures.DirtTexture;
import textures.GreenDoorTexture;
import textures.RedDoorTexture;
import textures.Texture;
import textures.WallTexture;
import textures.WoodTexture;

public class Render extends JPanel {

	private static final long serialVersionUID = 1L;
	public static int sW = 1300;
	public static int sH = 800;
	public static JFrame frame;

	public enum Mode {Wireframe, Panelled};
	public static Screen s;
	public Perspective p;
	public static IO io;
	public static Animations a;
	public static Painter painter;
	public static AudioManager aM;
	public List<LightSource> lightSources; //The collection of all light sources
	public Implementation im; //The implementation
	public int depthLimit = 1000; //Point down depth line to calculate depth from
	public boolean paint;
	public List<PlanarRoot> polyPool;
	public List<ObjectRoot> objectPool;
	public boolean pBActive; //Check for active progress bar

	public Mode mode; //Drawing mode
	public Tool currentTool;
	public List<Tab> tabs;
	public List<Window> windows;
	public List<Perspective> perspectives;
	public List<Layer> layers;
	public List<Tool> tools;
	public List<ParticleSystem> systems;
	public List<Texture> textures;
	List<PlanarRoot> selectedObjects;
	public Layer selectedLayer;
	public Layer masterLayer;

	/**
	 * Initialises the Dimension Modeller
	 */
	public void initialise() {
		this.mode = Mode.Wireframe;
		polyPool = new ArrayList<PlanarRoot>();
		objectPool = new ArrayList<ObjectRoot>();

		setUpPerspectives();
		setUpTools();
		setUpLayers();
		setUpLightSources();
		setUpParticleSystems();
		setUpTextures();
		setUpGUIComponents();
		buildObjects();
		s.message = "No message";
	}

	/**
	 * Makes a set of test objects.
	 * Useful while devaloping.
	 */
	public void buildObjects() {
		/*new Square(new Point3D(-100, 0, 0), new Point3D(50, 50, 50), getLayer("Dev"), Color.WHITE, null, this);
		new Circle(new Point3D(0, 0, 0), new Point3D(200, 200, 200), getLayer("Dev"), Color.WHITE, null, this);
		new ObjectParser(ParseMode.Build, new File("assets/objectfiles/teapot.txt"), this);
		 */

		//polyPool.add(new Polygon(new Vec3(0, 200, 0), new Vec3(0, 0, -100), new Vec3(0, 0, 0), getLayer("Dev"), Color.PINK, this));
		new Circle(new Vec3(0, 0, 0), 200, 40, getLayer("Dev"), Color.WHITE, null, this);
	}

	/**
	 * Instantiates and stores all textures so they can be used later.
	 */
	public void setUpTextures() {
		textures = new ArrayList<Texture>();
		textures.add(new DirtTexture(this));
		textures.add(new WoodTexture(this));
		textures.add(new RedDoorTexture(this));
		textures.add(new GreenDoorTexture(this));
		textures.add(new WallTexture(this));
		textures.add(new ChipTexture(this));
	}

	/**
	 * Sets up and stores all development tabs.
	 */
	public void setUpGUIComponents() {
		tabs = new ArrayList<Tab>();
		windows = new ArrayList<Window>();
		tabs.add(new ViewsTab(this));
		tabs.add(new LayersTab(this));
		tabs.add(new ToolsTab(this));
		tabs.add(new OptionsTab(this));
		tabs.add(new AnimationsTab(this));
		tabs.add(new SystemsTab(this));
		tabs.add(new ObjectsTab(this));
		windows.add(new LightControlWindow(this));
	}

	/**
	 * Sets up and stores all layers so they can be called later.
	 */
	public void setUpLayers() {
		layers = new ArrayList<Layer>();
		layers.add(new Layer(true, this)); //Master layer
		layers.add(new Layer("Dev", this));
		layers.add(new Layer("SystInds", this));
		layers.add(new Layer("Wall", this));
		layers.add(new Layer("Chip", this));
		layers.add(new Layer("Player", this));
		layers.add(new Layer("Elevator", this));
		layers.add(new Layer("Doors", this));
		layers.add(new Layer("Keys", this));
		layers.add(new Layer("Info", this));
		layers.add(new Layer("JellyFish", this));
		layers.add(new Layer("Dirt", this));

		masterLayer = layers.get(0);
		selectedLayer = layers.get(0);

		/*layers.get(1).setVisibility(Visibility.Hidden);
		layers.get(2).setVisibility(Visibility.Hidden);
		layers.get(3).setVisibility(Visibility.Hidden);
		layers.get(4).setVisibility(Visibility.Hidden);
		layers.get(5).setVisibility(Visibility.Hidden);
		layers.get(6).setVisibility(Visibility.Hidden);
		layers.get(7).setVisibility(Visibility.Hidden);
		layers.get(8).setVisibility(Visibility.Hidden);
		layers.get(9).setVisibility(Visibility.Hidden);
		layers.get(10).setVisibility(Visibility.Hidden);
		layers.get(11).setVisibility(Visibility.Hidden);*/
	}

	/*
	 * Sets up all views and assigns one to the modeller.
	 */
	public void setUpPerspectives() {
		perspectives = new ArrayList<Perspective>();
		perspectives.add(new RightIsoView());
		perspectives.add(new UnderIsoView());
		perspectives.add(new TopView());
		perspectives.add(new PlanView());
		perspectives.add(new CustomView("MyView1", 0.6));
		perspectives.add(new CustomView("MyView2", 0.2));
		perspectives.add(new CustomView("MyView3", 0.42));
		perspectives.add(new TestView());
		p = perspectives.get(perspectives.size()-1);
	}

	/**
	 * Sets up and stores all tools so they can be called upon later.
	 */
	public void setUpTools() {
		tools = new ArrayList<Tool>();
		tools.add(new Tool(ToolMode.Select, KeyEvent.VK_X));
		tools.add(new Tool(ToolMode.Move, KeyEvent.VK_H));
		tools.add(new Tool(ToolMode.Angle, KeyEvent.VK_B));
		currentTool = tools.get(1);
	}

	/**
	 * Builds and stores all particle systems so they can be run later.
	 */
	public void setUpParticleSystems() {
		systems = new ArrayList<ParticleSystem>();
		systems.add(new Fire(new Vec3(0, 0, 0), 100, this));
		systems.add(new Fire(new Vec3(110, 0, 0), 100, this));
		systems.add(new Fire(new Vec3(0, 110, 0), 100, this));
		systems.add(new Fire(new Vec3(110, 110, 0), 100, this));
		systems.add(new Electricity(new Vec3(0, 0, 0), 100, this));
		systems.add(new Acid(new Vec3(-3, 18, -70), 30, this));
		systems.add(new Water(new Vec3(0, 0, 0), 100, 100, this));
		systems.add(new Fireworks(new Vec3(0, 0, 0), 100, this));
		systems.add(new Sparks(new Vec3(100, 0, -180), 100, this));
	}

	/**
	 * Initialise all basic light sources for the scene.
	 */
	public void setUpLightSources() {
		lightSources = new ArrayList<LightSource>();
		lightSources.add(new LightSource(LightSource.Type.Directional, 100, new Vec3D(0.5, 0.5, 0), new Color(255, 0, 0)));
		lightSources.add(new LightSource(LightSource.Type.Directional, 100, new Vec3D(0 , 1, 0), new Color(0, 0, 255)));
		lightSources.add(new LightSource(LightSource.Type.Directional, 50, new Vec3D(1 , 0, 0), new Color(255, 197, 143)));
		lightSources.add(new LightSource(LightSource.Type.Ambient, 5, new Vec3D(-1 , 0, 0), new Color(220, 197, 143)));
		//lightSources.get(0).deactivate();
		//lightSources.get(1).deactivate();
		lightSources.get(2).deactivate();
		lightSources.get(3).deactivate();
		
		//Tungseton - 255, 197, 143
	}

	/**
	 * Returns a layer with the name if one exists
	 * 
	 * @param name - the name to search for
	 */
	public Layer getLayer(String name) {
		for (Layer l : layers) {
			if (l.getName().equals(name)) {
				return l;
			}
		}
		throw new Error("No layer named "+name);
	}

	/**
	 * Returns a texture with the name if one exists
	 * 
	 * @param name - the name to search for
	 */
	public Texture getTexture(String name) {
		for (Texture t : textures) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		throw new Error("No texture named "+name);
	}

	/**
	 * Returns a tool of the toolmode if one exists
	 * 
	 * @param m - the mode of the toole to search for
	 */
	public Tool getTool(ToolMode m) {
		for (Tool t : tools) if (t.toolMode==m) return t;
		throw new Error("No tool of mode "+m.toString());
	}

	public ParticleSystem getSingleSystem(String name) {
		for (ParticleSystem pS : systems) {
			if (pS.name.equals(name)) return pS;
		}
		return null;
	}

	public List<ParticleSystem> getAllSystems(String name) {
		List<ParticleSystem> s = new ArrayList<>();
		for (ParticleSystem pS : systems) {
			if (pS.name.equals(name)) s.add(pS);
		}
		if (s.isEmpty()) throw new Error("No system named "+name+" exists.");
		return s;
	}

	public void killAllSystems() {
		if (systems!=null) {
			for (ParticleSystem pS : systems) pS.kill();
		}
	}

	public void runSystem(String s) {
		for (ParticleSystem pS : systems) {
			if (pS.name.equals(s)) {
				if (!pS.isRunning()) {
					pS.run();
				}
				else if (!pS.isKilling()) pS.killGracefully();
				else pS.kill();
			}
		}
		//Use as opertunity to update number of systems in tab
		for (Tab t : tabs) {
			if (t instanceof SystemsTab) {
				((SystemsTab) t).updateTab();
				return;
			}
		}
	}

	/**
	 * Calculates distance between the center of an object and another point
	 * (currently the light source), then stores that distance in the object.
	 * 
	 * Also adds the center point of the object to a collection 
	 * in Screen for link drawing.
	 * 
	 * @param o - The object to find distance for
	 */
	public void calculateObjectDistance(PlanarRoot o) {
		Vec3 mid = findMidPoint(o);
		Point sCX = p.xAV(mid.x);
		Point sCY = p.yAV(mid.y);
		Point sCZ = p.zAV(mid.z);
		Point lSX = p.xAV(0);
		Point lSY = p.yAV(0);
		Point lSZ = p.zAV(0);
		Point start = new Point(lSX.x+lSY.x+lSZ.x, lSX.y+lSY.y+lSZ.y);

		Point end = new Point(sCX.x+sCY.x+sCZ.x, sCX.y+sCY.y+sCZ.y);
		s.links.add(end);
		double dist = Math.sqrt(Math.pow(end.x-start.x, 2)+Math.pow(end.y-start.y, 2));
		o.dist = dist;
	}

	/**
	 * Calculates distance between a point and the light source.
	 * 
	 * @param end - the end point to calculate to
	 */
	public double calculatePointDistance(Point end) {
		Point lSX = p.xAV(0);
		Point lSY = p.yAV(0);
		Point lSZ = p.zAV(0);
		Point start = new Point(lSX.x+lSY.x+lSZ.x, lSX.y+lSY.y+lSZ.y);

		return Math.sqrt(Math.pow(end.x-start.x, 2)+Math.pow(end.y-start.y, 2));
	}

	/** 
	 * Calculates the midpoint of an object
	 * 
	 * @return - the 3Dimensional midpoint
	 */
	public Vec3 findMidPoint(PlanarRoot o) {
		if (o instanceof Polygon) return ((Polygon) o).findMidPoint();
		else return new Vec3(o.x+o.w/2, o.y+o.h/2, o.z-o.ex/2);
	}

	/**
	 * Uses the cross product to find the normal of the poly, which is
	 * essentially the vector perpendicular with two of it's lines.
	 * 
	 * @param p - the planar object to calculate the normal for
	 * @return - the calculated normal (which will consist of doubles)
	 */
	public Vec3D calculateNormal(PlanarRoot p) {

		Vec3D normal = new Vec3D();
		Vec3 lineA, lineB;

		//Find two reference vectors
		lineA = new Vec3(p.v[1].x-p.v[0].x, p.v[1].y-p.v[0].y, p.v[1].z-p.v[0].z);
		lineB = new Vec3(p.v[2].x-p.v[0].x, p.v[2].y-p.v[0].y, p.v[2].z-p.v[0].z);

		//Find normal vector
		normal.x = (lineA.y*lineB.z)-(lineA.z*lineB.y);
		normal.y = (lineA.z*lineB.x)-(lineA.x*lineB.z);
		normal.z = (lineA.x*lineB.y)-(lineA.y*lineB.x);

		//Make it a unit vector
		int mag = (int) Math.sqrt(Math.pow(normal.x, 2)+Math.pow(normal.y, 2)+Math.pow(normal.z, 2));
		normal.x /= mag;
		normal.y /= mag;
		normal.z /= mag;

		return normal;
	}

	/**
	 * Uses the dot product to return the similarity between two vectors.
	 * 
	 * @param a the first vector to compare
	 * @param b the second vector to compare
	 * @return the similarity between the two given vectors
	 */
	public double compareVectors(Vec3D a, Vec3D b) {
		return (a.x*b.x)+(a.y*b.y)+(a.z*b.z);
	}

	public void calculateObjectDepth(PlanarRoot o) {
		double hA = p.horzRot;
		double vA = p.vertRot;
		if (p.horzRot<0) vA = -vA;
		Vec3 mid = findMidPoint(o);

		int limit = depthLimit; //Measuring distance
		double depth;
		int x = 0;
		int y = 0;
		int z = 0;

		//Find X Intercept
		int xIntercept = 0;
		x = 0;
		if (hA>=-1&&hA<-0.5) xIntercept = (int) (-mid.y/-(2*(hA+0.5)+1)+mid.x);
		if (hA>=-0.5&&hA<0.5) xIntercept = (int) ((-mid.y*(2*hA))+mid.x);
		if (hA>=0.5&&hA<=1) xIntercept = (int) (-mid.y/-(2*(hA-0.5)-1)+mid.x);

		//Equalise for differences on the xy plane
		int eqLimit = (int) (-limit+xIntercept*(1-hA));
		if (hA<0) eqLimit = (int) (limit+xIntercept*(1+hA));

		//Equalise for differences on the xz plane
		if (vA>0) eqLimit -= mid.z/2;
		else eqLimit += mid.z/2;
		if (hA<0) eqLimit += mid.z/2;

		//Find top point
		x = eqLimit;
		if (hA>=-1&&hA<-0.5) y = (int) ((-(2*(hA+0.5)+1)*(x-mid.x))+mid.y);
		if (hA>=-0.5&&hA<0.5) y = (int) (((x-mid.x)/(2*hA))+mid.y);
		if (hA>=0.5&&hA<=1) y = (int) ((-(2*(hA-0.5)-1)*(x-mid.x))+mid.y);

		if (vA>=-1&&vA<-0.5) z = (int) ((x-mid.x)/(-2*(vA+1))+mid.z);
		if (vA>=-0.5&&vA<0.5) z = (int) ((2*vA)*(x-mid.x)+mid.z);
		if (vA>=0.5&&vA<=1) z = (int) ((x-mid.x)/(-2*(vA-1))+mid.z);
		Vec3 top = new Vec3(x, y, z);

		/*
		 * Calculate depth. All condition's reach are widened slightly
		 * to account for artifacting.
		 */
		if (p.horzRot>0) vA = -vA; //Invert previous inversion
		if (vA<=0.01&&vA>=-0.01) {
			if (hA<=-0.95) depth = limit-mid.x;
			else if (hA>=0.95) depth = limit+mid.x;
			else if (hA<=0.01&&hA>=-0.01) depth = limit+mid.y; 
			else { //Calculate Right Angled Triangle based off only XY plane
				depth = Math.sqrt(Math.pow(mid.x-top.x, 2)+Math.pow(mid.y-top.y, 2));
			}
		}
		else if (vA>=0.95) depth = limit-mid.z;
		else if (vA<=-0.95) depth = limit+mid.z;
		else { //Calculate RAT on xyz plane
			depth = Math.sqrt(Math.pow(top.x-mid.x, 2)+Math.pow(top.z-mid.z, 2));
		}

		//Assign calculated depth to object
		o.depth = depth;
	}

	/**
	 * Sort objects according to depth. Depth must be dynamically calculated
	 * based on the perspective and angle and each object's depth calculation
	 * should not be affected by the existence of other objects.
	 * 
	 * @return - The sorted list of objects
	 */
	public List<PlanarRoot> sortObjects() {
		List<PlanarRoot> result = new ArrayList<>();
		for (PlanarRoot o : polyPool) if (o.layer.isVisible()) result.add(o);
		for (PlanarRoot o : result) calculateObjectDepth(o);

		Collections.sort(result, (PlanarRoot a, PlanarRoot b) -> {
			if (a.depth>b.depth) return 1;
			if (a.depth<b.depth) return -1;
			return 0;
		});
		Collections.reverse(result);

		return result;
	}

	/**
	 * Sorts the faces of an object based on their distance to the perspective
	 * 
	 * @deprecated
	 * @param faces - The map of distances paired with faces to sort
	 * @return - The sorted list of faces
	 */
	public List<Point[]> sortFaces(Map<Double, Point[]> faces) {
		//Extract the distances and sort them in their own list
		List<Double> distances = new ArrayList<>();
		distances.addAll(faces.keySet());
		Collections.sort(distances);
		Collections.reverse(distances);

		//Put the faces in order into a list
		List<Point[]> oFaces = new ArrayList<>();
		for (Double d : distances) oFaces.add(faces.get(d));
		return oFaces;
	}

	/**
	 * Moves the perspective accross the world. Equivilent to moving the viewport.
	 * 
	 * @param xInc - x amount to move by
	 * @param yInc - y amount to move by
	 */
	public void movePerspective(int xInc, int yInc) {
		p.xOffset += xInc;
		p.yOffset += yInc;
	}

	/**
	 * Change the vertical angle of the current perspective if that is permitted.
	 * Adjust sensitivity to match the scale.
	 * 
	 * @param inc - the amount to increase the angle by
	 */
	public void changeVerticalPerspective(double inc) {
		if (p instanceof CustomView || p instanceof TestView) {
			p.vertRot += inc*((p.scale/2)*0.01);
			if (p.vertRot>1) p.vertRot = 1;
			if (p.vertRot<-1) p.vertRot = -1;
		}
	}

	/**
	 * Change the horizontal angle of the current perspective if that is permitted
	 * Adjust sensitivity to match the scale.
	 * 
	 * @param inc - the amount to increase the angle by
	 */
	public void changeHorizontalPerspective(double inc) {
		if (p instanceof TestView) {
			p.horzRot += inc*((p.scale/2)*0.01);
			if (p.horzRot>1) p.horzRot = 1;
			if (p.horzRot<-1) p.horzRot = -1;
		}
	}

	/**
	 * Scale the current perspective by an amount.
	 * 
	 * @param inc
	 */
	public void zoom(double inc) {
		if (!a.isRunning("levelTransition")) {
			inc = inc*0.02;
			p.scale -= inc;
			if (p.scale<0.02) p.scale = 0.02;
		}
	}

	/**
	 * Select all objects that are within a 2D selection box
	 * 
	 * @deprecated
	 * @param s - the selection box
	 */
	/*public void selectObjects(Rectangle s) {
		selectedObjects = new ArrayList<PlanarRoot>();
		//Adjust selectionbox for scale and origin offset
		double x = p.scale*(s.x-(sW/2));
		double y = p.scale*(s.y-(sH/2));
		int w = s.width;
		int h = s.height;

		s = new Rectangle((int) x, (int) y, w, h);
		for (PlanarRoot o : polyPool) {
			o.deselect();
			if (o.isIn(s))  {
				selectedObjects.add(o);
				o.select();
			}
		}
	}*/

	/**
	 * Select all objects with an x and y value
	 * 
	 * @deprecated
	 * @param x - the x value to discern by
	 * @param y - the y value to discern by
	 */
	/*public void selectObjects(int x, int y) {
		selectedObjects = new ArrayList<PlanarRoot>();
		//Adjust selectionbox for scale and origin offset
		x = x-(sW/2);
		y = y-(sH/2);
		for (PlanarRoot o : polyPool) {
			o.deselect();
			if (o.isIn(x, y))  {
				selectedObjects.add(o);
				o.select();
			}
		}
	}*/

	/**
	 * Move all selectedObjects by an increment
	 * 
	 * @param x - x value to move by
	 * @param y - y value to move by
	 * @param z - z value to move by
	 */
	public void moveObject(int x, int y, int z) {
		if (selectedObjects!=null) {
			for (PlanarRoot o : selectedObjects) {
				o.x = o.x+x;
				o.y = o.y+y;
				o.z = o.z+z;
			}
		}
	}

	/**
	 * Select all visible, editable objects
	 */
	public void selectAll() {
		selectedObjects = new ArrayList<PlanarRoot>();
		for (PlanarRoot o : polyPool) {
			if (o.layer.getVisibility()==Visibility.Visible) {
				selectedObjects.add(o);
				o.select();
			}
		}
	}

	/**
	 * Clear the selection of all objects
	 */
	public void clearSelection() {
		if (selectedObjects!=null) for (PlanarRoot o : selectedObjects) o.deselect();
		selectedObjects = new ArrayList<PlanarRoot>();
	}

	/**
	 * Clears the polygon pool pool. Can be provided with a list of polygons
	 * to ommit from the clean.
	 * 
	 * @param ommit - the list of objects to be ommited when cleaning
	 * @param ommitDev - ommit the Dev layer from the clean
	 */
	public void cleanPolyPool(List<PlanarRoot> ommit, boolean ommitDev) {
		List<PlanarRoot> toRemove = new ArrayList<>();
		for (PlanarRoot o : polyPool) {
			if (ommitDev&&o.layer!=getLayer("Dev")) {
				if (ommit!=null&&!ommit.contains(o)) toRemove.add(o);
				else if (ommit==null) toRemove.add(o);
			}
			else {
				if (ommit!=null&&!ommit.contains(o)) toRemove.add(o);
				else if (ommit==null) toRemove.add(o);
			}
		}
		polyPool.removeAll(toRemove);
	}

	public void cleanObjectPool(List<ObjectRoot> ommit) {
		objectPool = new ArrayList<ObjectRoot>();
	}

	public void setScreenMode(ScreenMode m) {
		s.mode = m;
		try {
			Image icon;
			if (m==ScreenMode.Day) icon = ImageIO.read(new File("assets/icons/iconDay.png"));
			else icon = ImageIO.read(new File("assets/icons/iconNight.png"));
			frame.setIconImage(icon); //On menu bar for Windows
			Taskbar.getTaskbar().setIconImage(icon); //On dock for mac
		} catch (IOException ex) {throw new Error("Icon initialise error");}
	}

	public boolean pBCheck() {
		return pBActive;
	}

	@Override
	public void paintComponent(Graphics g) {
		s.draw((Graphics2D) g);
	}

	/**
	 * Construct the core of the Dimension Modeller, construct
	 * all key compontents, attach all IO sensors to the frame,
	 * call the initialiser and do an initial paint.
	 */
	public Render() {
		Render.s = new Screen(this);
		Render.io = new IO(this);
		this.im = new Implementation(this);
		Render.a = new Animations(this);
		Render.painter = new Painter(this);
		Render.aM = new AudioManager(this);
		pBActive = false;

		addKeyListener(io);
		addMouseListener(io);
		addMouseMotionListener(io);
		addMouseWheelListener(io);
		frame.addComponentListener(io);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		initialise();
		io.makeMenuBar();
		this.paint = true;
		painter.start();
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//System.out.println(LocalTime.now().getHour());

				//Initialise
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				frame = new JFrame();
				Render panel = new Render();
				panel.setPreferredSize(new Dimension(sW, sH));
				frame.getContentPane().add(panel);

				//Icon
				try {
					int hour = LocalTime.now().getHour();
					Image icon;
					if (hour>0&&hour<12) icon = ImageIO.read(new File("assets/icons/iconDay.png"));
					else icon = ImageIO.read(new File("assets/icons/iconNight.png"));
					frame.setIconImage(icon); //On menu bar for Windows
					Taskbar.getTaskbar().setIconImage(icon); //On dock for mac
				} catch (IOException ex) {throw new Error("Program initialise error");}

				//Label and build
				frame.setTitle("Modeler");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//Close audio streams on close
				frame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.out.println("Closing everything");
						if (aM!=null) aM.closeAll();
					}
				});

				//Finish up
				frame.setVisible(true);
				frame.pack();

				//pBTester(panel);
			}
		});
	}

	public static void pBTester(Render c) {
		ProgressBar pB = new ProgressBar(c, 4, "Tester", new String[] {"Initialising", "Building", "Constructing", "Finishing Up"}, new int[] {500, 50, 100, 100});

		Thread testerThread = new Thread() {
			public void run() {
				while (!pB.isDestroyed()) {
					if (pB.getPercent()>=100) pB.increaseStage();
					else pB.increase(1);
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {}
				}
			}
		};
		testerThread.start();
		pB.run();
	}
}

/*public List<ObjectRoot> sortObjects() {
		List<ObjectRoot> result = new ArrayList<>();
		for (ObjectRoot o : objects) if (o.layer.isVisible()) result.add(o);
		Collections.sort(result, (ObjectRoot a, ObjectRoot b) -> {
			if (a.z<b.z) {
				if (p.vertRot<0) return -1;
				return 1;
			}
			if (a.z>b.z) {
				if (p.vertRot<0) return 1;
				return -1;
			}
			if (a.z==b.z) {
				if (a.y<b.y) return 1;
				if (a.y>b.y) return -1;
				if (a.y==b.y) {
					if (p.horzRot<0) {
						if (a.x<b.x) return -1;
						if (a.x>b.x) return 1;
					}
					else {
						if (a.x<b.x) return 1;
						if (a.x>b.x) return -1;
					}
				}
			}
			return 0;
		});
		return result;
	}

 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *public void calculateObjectDepth(ObjectRoot o) {
		double hA = p.horzRot;
		double vA = p.vertRot;
		Point3D mid = findMidPoint(o);
		double depth;
		int limit = depthLimit;

		if (vA==1) depth = limit+mid.z; 
		else if (vA==-1) depth = limit-mid.z; 
		else if (hA==-1) depth = limit-mid.x;
		else if (hA==1) depth = limit+mid.x;
		else if (hA<0.01&&hA>-0.01) depth = limit+mid.y; //This condition's reach is widened slightly to account for artifacting 
		else {
			//Calculate xIntecept
			int xIntercept = 0;
			if (hA>=-1&&hA<-0.5) xIntercept = (int) (-mid.y/-(2*(hA+0.5)+1)+mid.x);
			if (hA>=-0.5&&hA<0.5) xIntercept = (int) ((-mid.y*(2*hA))+mid.x);
			if (hA>=0.5&&hA<=1) xIntercept = (int) (-mid.y/-(2*(hA-0.5)-1)+mid.x);

			//Equalise limit
			Point eqLimit = new Point((int) (-limit+xIntercept*(1-hA)), 0);
			if (hA<0) eqLimit.x = (int) (limit+xIntercept*(1+hA));

			//Properly calculate limit point
			if (hA>=-1&&hA<-0.5) eqLimit.y = (int) ((-(2*(hA+0.5)+1)*(eqLimit.x-mid.x))+mid.y);
			if (hA>=-0.5&&hA<0.5) eqLimit.y = (int) (((eqLimit.x-mid.x)/(2*hA))+mid.y);
			if (hA>=0.5&&hA<=1) eqLimit.y = (int) ((-(2*(hA-0.5)-1)*(eqLimit.x-mid.x))+mid.y);

			//Calculate depth from limit
			depth = Math.sqrt(Math.pow(mid.x-eqLimit.x, 2)+Math.pow(mid.y-eqLimit.y, 2));
		}

		o.depth = depth;
	}*/

/*Assign an aribtraty limit to test forwards from. An approximation of th position
 * of the perspective will work or prehaps the largest value along the vector.
 * 
 * If it's an extreme like this where the right angles triangle wont work, then it can
 * be done by just going along an axis. Therefore we don't need a 2D limit point,
 * just a value.
 * If vertRot = 1 then depth = -y;
 * If vertRot = -1 then depth = y;
 * If horzRot = -1 then depth = x;
 * If horzRot = 1 then depth = -x;
 * If horzRot = 0 then depth = y;

 * Other wise find length of hyp between a 2D limit point and the midpoint:
 * 1: Find x intercept of line. The xIntercept is used to equalise
 * the lines so that the limit is in the same positon for every line. This way the differences
 * are relative and actually show up.
 * 2: Apply the xIntercept to the limit to make the adjustment
 * 3: Figure out the y position of the limit
 * 3: Find the hyp of the right angled triangle between the mid point
 * and the adjusted limit point
 */
