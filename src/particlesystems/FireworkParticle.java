package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import core.Vec3;
import core.Render;

public class FireworkParticle extends Particle {

	/*
	 * The escape vector of the particle.
	 * Has a size multiplier component and a height component.
	 */
	double vectorS;
	double vectorH;
	double xSpeed; //Amount to increase x by each cycle;
	double ySpeed; //Amount to increase y by each cycle;
	int detonationTime;
	//Phases
	boolean dying; //If the particle doesnt detonate then it dies when it hits the ground
	boolean detonating; //Alternative stage of motion, it explodes

	List<Vec3> tail; //The points that make up the tail.
	List<Vec3> bubble; //The tails that make up the bubble of sparks as it detonates.
	final static int tailSize = 50;
	int currentBubbleRadius;
	int detonateRadius; //The final radius the bubble will get to
	int bubbleSize; //Size of a point in the bubble.
	Color sColor; //Secondary color

	/**
	 * 
	 * @param parent - The system that created the particle
	 * @param creationTime - The local time in the system when it was created
	 * @param ttl - The particles lifetime
	 * @param vectorS - The size of the vector
	 * @param vectorH - The height multiplier of the vector
	 * @param pos - The initial position of the particle
	 * @param c - Reference to Render
	 * @param size - The size of a particle
	 * @param flagged - Used for dev
	 */
	public FireworkParticle(ParticleSystem parent, int creationTime, int ttl, double vectorS, double vectorH, double xDir, double yDir, int detonationTime, int detonateRadius, Vec3 pos, Color c, Color sColor, int size, boolean flagged) {
		super(parent, creationTime, ttl, pos, 0, 0, 0, c, size, flagged);
		this.vectorS = vectorS;
		this.vectorH = vectorH;
		this.xSpeed = 2*xDir;
		this.ySpeed = 3*yDir;
		this.bubbleSize = 3;
		this.detonationTime = detonationTime;
		this.dying = false;
		this.detonating = false;
		this.detonateRadius = detonateRadius;
		this.sColor = sColor;
		tail = new ArrayList<>();
		bubble = new ArrayList<>();
	}

	@Override
	public void increase(int cTime) {
		if (dying) { //Fade out if near end of life
			int a = c.getAlpha()-2;
			if (a<=0) {
				a = 0;
				ttl = 0;
			}
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}

		//Initiate detonation if its time
		if (cTime==detonationTime&&!dying) {
			if (pos.z<-50) { //If close to ground then dont detonate because it looks weird
				detonating = true;
				tail.clear();
				buildBubble(2);
			}
		}

		if (!detonating) { //Initial stage - moving firework accross sky
			int nZ = -quadraticMotion(Math.abs(pos.x))+parent.origin.z;
			int thresh = 0;
			if (nZ<=thresh) {
				pos.x += xSpeed;
				pos.y += ySpeed;
				pos.z = nZ;
			}
			else if (nZ>thresh) {
				dying = true;
				pos.z = thresh;
			}

			if (Math.abs(pos.x)<tailSize) tail.add(new Vec3(0, 0, 0)); //Make tail
			for (Vec3 p : tail) { //Update tail
				p.x += xSpeed;
				p.y += ySpeed;
				p.z = -quadraticMotion(Math.abs(p.x))+parent.origin.z;
			}

			if (dying) {//Check if tail exceeds threshold
				List<Vec3> toRemove = new ArrayList<>();
				for (Vec3 p : tail) if (p.z>thresh) toRemove.add(p);
				tail.removeAll(toRemove);
			}
		}
		else { //Second stage - detonating firework
			if (currentBubbleRadius<detonateRadius) buildBubble(currentBubbleRadius+3);
			else { //Remove some of the bubbles
				List<Vec3> toRemove = new ArrayList<>();
				for (Vec3 p : bubble) if (random(1, 8)==1) toRemove.add(p);
				bubble.removeAll(toRemove);
			}
		}

		//Kill particle because fully exploded
		if (detonating&&bubble.isEmpty()) ttl = 0;
	}
	/**
	 * The equation that defines the quadratic motion of the particle.
	 * 
	 * @param the input value
	 * @return the output value
	 */
	public int quadraticMotion(int in) {
		double s = vectorS/10000;
		double intercept = Math.sqrt(-vectorH/-s);
		return (int) (-(s*Math.pow((in-intercept), 2))+vectorH);
	}

	/**
	 * Builds one layer of the detonated shell
	 * 
	 * @param rM - the radius of this layer of the shell
	 */
	public void buildBubble(int rM) {
		bubble = new ArrayList<Vec3>();
		int r;
		currentBubbleRadius = rM;

		for (int i=-rM; i<=rM; i+=8) {
			r = (int) Math.sqrt(Math.pow(rM, 2)-Math.pow(i, 2));
			for (int z=-r; z<=r; z+=8) {

				double out1 = Math.sqrt(Math.pow(r, 2)-Math.pow(z, 2)); //First half
				double out2 = -Math.sqrt(Math.pow(r, 2)-Math.pow(z, 2)); //Second half

				bubble.add(new Vec3(pos.x+z, (int) (-out1+pos.y), pos.z-i));
				bubble.add(new Vec3(pos.x+z, (int) (-out2+pos.y), pos.z-i));
			}
		}
	}

	@Override
	public void draw(Graphics2D g) {
		int oX = Render.sW/2+parent.c.p.xOffset;
		int oY = Render.sH/2+parent.c.p.yOffset;
		Point pX, pY, pZ;

		//Draw head and tail
		if (!detonating) {
			double i = 0;
			int rd = c.getRed();
			int gr = c.getGreen();
			int bl = c.getBlue();
			double iInc = size/((double) tail.size());
			//Find difference between primary and secondary colours and make it proportionate to tail
			double rdInc = ((double) sColor.getRed()-(double) c.getRed())/((double) tail.size());
			double grInc = ((double) sColor.getGreen()-(double) c.getGreen())/((double) tail.size());
			double blInc = ((double) sColor.getBlue()-(double) c.getBlue())/((double) tail.size());

			for (Vec3 p : tail) {
				i += iInc;
				rd += rdInc;
				gr += grInc;
				bl += blInc;
				if (rd>255) rd = 255; if (rd<0) rd = 0;
				if (gr>255) gr = 255; if (gr<0) gr = 0;
				if (bl>255) bl = 255; if (bl<0) bl = 0;
				g.setColor(new Color(rd, gr, bl, c.getAlpha()));

				pX = parent.c.p.xAV(p.x);
				pY = parent.c.p.yAV(p.y);
				pZ = parent.c.p.zAV(p.z);
				g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*(size-i)), (int) (parent.c.p.scale*(size-i)));
			}
		}
		else {
			if (bubble.size()>200) {
				g.setColor(c);
				pX = parent.c.p.xAV(pos.x);
				pY = parent.c.p.yAV(pos.y);
				pZ = parent.c.p.zAV(pos.z);
				g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*(size/2)), (int) (parent.c.p.scale*(size/2)));
			}

			for (Vec3 p : bubble) {
				//Main point
				pX = parent.c.p.xAV(p.x);
				pY = parent.c.p.yAV(p.y);
				pZ = parent.c.p.zAV(p.z);
				//Shadow
				g.setColor(new Color(255, 255, 255, 20));
				g.fillOval(oX+pX.x+pY.x+pZ.x-2, oY+pX.y+pY.y+pZ.y-2, (int) (parent.c.p.scale*(bubbleSize+4)), (int) (parent.c.p.scale*(bubbleSize+4)));
				//Main spark
				g.setColor(new Color(random(c.getRed(), sColor.getRed()), random(c.getGreen(), sColor.getGreen()), random(c.getBlue(), sColor.getBlue()), c.getAlpha()));
				g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*(bubbleSize)), (int) (parent.c.p.scale*(bubbleSize)));
			}
		}
	}
}
