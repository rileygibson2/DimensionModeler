package particlesystems;

import java.awt.Graphics2D;

import core.Vec3;
import core.Render;
import objects.PlanarRoot;

public abstract class ParticleSystem {
	//Critical variables
	Render c;
	public String name;
	public int speed;
	boolean running;
	public Vec3 origin; //Origin point of the system
	public int originW; //Width of the system, used in different ways
	int length; //Max amount of time system runs for
	public int time; //Curent time in system
	public boolean isSorted; //If the system is sorted and drawn like a real object
	public boolean killingGracefully; //If in the middle of a graceful shutdown
	
	//Helper variables
	int tempLength; //Used so length can be changed for graceful kill without overwriting proper length
	int extOffX; //Used if there is a need to externally shift the whole system
	int extOffY;
	PlanarRoot indicator; //The object in the 3D Object heap that is used to represent this system for ordering

	/**
	 * Constructs a new particle system
	 * 
	 * @param name - the name of the system
	 * @param speed - the refresh rate of the system
	 * @param length - the max time the system will live for
	 * @param origin - the origin point of the system
	 * @param originW - the width of the system about the origin (not explicitly tied to any plane)
	 * @param c - reference to Core
	 */
	public ParticleSystem(String name,int speed, int length, Vec3 origin, int originW, Render c) {
		this.name = name;
		this.speed = speed;
		this.length= length;
		this.origin = origin;
		this.originW = originW;
		this.running = false;
		this.c = c;
		this.isSorted = false;
		this.tempLength = length;
		this.extOffX = 0;
		this.extOffY = 0;
	}

	/**
	 * Shares if the system is running or not.
	 * 
	 * @return - running status of system
	 */
	public boolean isRunning() {return this.running;}
	
	/**
	 * Shares if the system is being killed or not.
	 * 
	 * @return - killing status of system
	 */
	public boolean isKilling() {return this.killingGracefully;}

	/**
	 * Violently and immediatly kills the system.
	 */
	public void kill() {
		//if (isRunning()) System.out.println(name+" Kill");
		this.killingGracefully = false;
		this.running = false;
	}

	/**
	 * Runs the system. Painter will handle the actual thread component.
	 */
	public void run() {
		running = true;
		killingGracefully = false;
		time = 0;
		//System.out.println(name+" Run");
	}
	
	/**
	 * Shifts the entire particle system by some values. Useful for shifting after creation
	 * or while the system is currently running and origin cannot be altered
	 * 
	 * @param xShift - the amount to shift on x axis
	 * @param yShift - the amount to shift on y axis
	 */
	public abstract void shift(int xShift, int yShift);
	
	/**
	 * Cycles the system once. Responsibility falls to implementations of
	 * this method to check if particles are empty and therefore wether to end
	 * system, as there may be more than one list of particles in an implementation
	 * so is impossible to check in the abstract parent.
	 * 
	 * @param time - the current time since the creation of the system
	 */
	public abstract void cycle(int time);
	
	/**
	 * Kills the system nicely without immediatly shutting it off. This
	 * will look different for every implementation.
	 */
	public abstract void killGracefully();
	
	/**
	 * Draws the system in it's current state.
	 * 
	 * @param g - the current Graphics2D object
	 */
	public abstract void draw(Graphics2D g);
	
	/**
	 * Useful random integer getter.
	 * 
	 * @param min - the minimum int to be returned
	 * @param max - the maximum int to be returned
	 * @return - the random int
	 */
	public int random(double min, double max){
		return (int) ((Math.random()*((max-min)+1))+min);
	}
}
