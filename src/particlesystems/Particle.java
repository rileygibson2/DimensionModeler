package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;

import core.Vec3;

public abstract class Particle {

	ParticleSystem parent;
	int ttl;
	int creationTime;
	Color c;
	Vec3 pos;
	int xDir; //Direction of travel on x axis
	int yDir; //Direction of travel on y axis
	int zDir; //Direction of travel on z axis
	int size; // Size of particle
	
	boolean flagged;
	
	/**
	 * 
	 * @param parent - the system this particle belongs to
	 * @param creationTime - local time in the system when this particle was born
	 * @param ttl - the length of the particle's life
	 * @param pos - the initial position of the particle
	 * @param xDir - the direction of travel along the x axis of the particle
	 * @param yDir - the direction of travel along the y axis of the particle
	 * @param zDir - the direction of travel along the z axis of the particle
	 * @param c - the primary colour of the particle (secondary colours may be used too)
	 * @param size - the size of the particle
	 * @param flagged - used for dev
	 */
	public Particle(ParticleSystem parent, int creationTime, int ttl, Vec3 pos, int xDir, int yDir, int zDir, Color c, int size, boolean flagged) {
		this.parent = parent;
		this.ttl = ttl;
		this.creationTime = creationTime;
		this.pos = pos;
		this.xDir = xDir;
		this.yDir = yDir;
		this.zDir = zDir;
		this.c = c;
		this.size = size;
		this.flagged = flagged;
	}
	
	/**
	 * Detirmine whether the particle is still alive or not.
	 * 
	 * @param time - the local time in the system
	 * @return - the life status of the particle
	 */
	public boolean hasExpired(int time) {
		if (time>creationTime+ttl) return true;
		return false;
	}
	
	/**
	 * A helpful random number generator. Allows for crossed arguments
	 * which is helpful for if you don't know which is bigger at runtime.
	 * 
	 * @param min - the minimum value that will be returned
	 * @param max - the maximum value that will be returned
	 * @return - the random integer
	 */
	public int random(double min, double max){
		if (min>max) {
			double temp = max;
			max = min;
			min = temp;
		}
		if (min==max) return (int) min;
	    return (int) ((Math.random()*((max-min)+1))+min);
	}
	
	/**
	 * Apply a change to the particles proportionate to one unit of time.
	 * The change applied depends on the system implementaiton.
	 * 
	 * @param cTime - the local time in the system
	 */
	public abstract void increase(int cTime);
	
	/**
	 * Draw the particles in their current state.
	 * 
	 * @param g - the current Graphics object
	 */
	public abstract void draw(Graphics2D g);
}
