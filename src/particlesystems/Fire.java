package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Render;
import objects.SquareOld;
import core.Vec3;

public class Fire extends ParticleSystem {


	Color smokeS = new Color(0, 0, 0, 100); //Smoke start color
	Color smokeE = new Color(255, 255, 255, 100); //Smoke end color
	Color rFireS = new Color(255, 0, 0, 50);
	Color rFireE = new Color(255, 0, 0);
	Color yFireS = new Color(255, 255, 0, 100);
	Color yFireE = new Color(255, 255, 0);
	Color wFireS = new Color(255, 255, 220);
	Color wFireE = new Color(255, 255, 220);

	List<Particle> smoke = new ArrayList<>();
	List<Particle> embers = new ArrayList<>();
	List<Particle> rFire = new ArrayList<>();
	List<Particle> yFire = new ArrayList<>();
	List<Particle> wFire = new ArrayList<>();

	int tempLength; //Used so length can be changed for graceful kill without overwriting proper length

	public Fire(Vec3 origin, int originW, Render c) {
		super("Fire", 40, 1000, origin, originW, c);
		this.isSorted = true;
		//Insert false object into sorted object list so this system can be draw at correct depth
		indicator = new SquareOld(origin.x-originW/2, origin.y-originW/2, 0, originW, originW, 100, c.getLayer("SystInds"), Color.BLUE, null, c);
		indicator.isSystemIndicator = true;
		indicator.system = this;
		c.polyPool.add(indicator);
	}

	@Override
	public void cycle(int time) {
		/*- For each component of the fire, make the next group
		 * of particles
		 * - Then go through all particles, removing the ones that
		 * are dead and increasing the rest.
		 * - Then check wether to end thread.
		 */
		if (time==0) {
			smoke = new ArrayList<Particle>();
			rFire = new ArrayList<Particle>();
			yFire = new ArrayList<Particle>();
			wFire = new ArrayList<Particle>();
			embers = new ArrayList<Particle>();
			tempLength = length*10000;
		}

		//Smoke
		if (time%8==0&&time>50&&time<tempLength) {
			for (int i=0; i<originW/30; i++) {
				int rX = random(-originW, originW/4); //Random x along the width of the origin
				int rXDir = random(1, 2); //Random direction for it's x axis
				int rYDir = random(1, 2); //Random direction for it's y axis
				if (rXDir==2) rXDir = -1;
				if (rYDir==2) rYDir = -1;
				int rTTL = random(800/2, 800); //Random ttl

				smoke.add(new SmokeParticle(this, time, rTTL, new Vec3(origin.x+rX, origin.y, origin.z-90), rXDir, rYDir, -1, smokeS, smokeE, originW/4, 8, false));
			}
			//smoke.get(smoke.size()-1).flagged = true;
		}

		//Embers
		if (time%8==0&&time>50&&time<tempLength) {
			int rX = random(-originW, originW); //Random x along the width of the origin
			int rXDir = random(1, 2); //Random direction for it's x axis
			int rYDir = random(1, 2); //Random direction for it's y axis
			if (rXDir==2) rXDir = -1;
			if (rYDir==2) rYDir = -1;
			int rTTL = random(255, 400); //Random ttl

			embers.add(new EmberParticle(this, time, rTTL, new Vec3(origin.x+rX, origin.y, origin.z-50), rXDir, rYDir, -1, 4, false));
		}

		//Red Fire
		if (time<80) {
			for (int i=0; i<originW/10; i++) {
				int rXDir = random(1, 2); //Random direction for it's x axis
				int rYDir = random(1, 2); //Random direction for it's y axis
				if (rXDir==2) rXDir = -1;
				if (rYDir==2) rYDir = -1;
				int rad = originW/2;

				rFire.add(new FireParticle(this, time, tempLength, origin, rXDir, rYDir, -1, rFireS, rFireE, rad, 15, false));
			}
			//rFire.get(rFire.size()-1).flagged = true;
		}

		//Yellow Fire
		if (time<20) {
			for (int i=0; i<originW/10; i++) {
				int rXDir = random(1, 2); //Random direction for it's x axis
				int rYDir = random(1, 2); //Random direction for it's y axis
				if (rXDir==2) rXDir = -1;
				if (rYDir==2) rYDir = -1;
				int rad = (originW/2)/2;

				yFire.add(new FireParticle(this, time, tempLength, origin, rXDir, rYDir, -1, yFireS, yFireE, rad, 10, false));
			}
			//yFire.get(rFire.size()-1).flagged = true;
		}

		//White Fire
		if (time<15) {
			for (int i=0; i<originW/10; i++) {
				int rXDir = random(1, 2); //Random direction for it's x axis
				int rYDir = random(1, 2); //Random direction for it's y axis
				if (rXDir==2) rXDir = -1;
				if (rYDir==2) rYDir = -1;
				int rad = (originW/2)/4;

				wFire.add(new FireParticle(this, time, tempLength, origin, rXDir, rYDir, -1, wFireS, wFireE, rad, 5, false));
			}
			//wFire.get(rFire.size()-1).flagged = true;
		}


		Set<Particle>  toRemove = new HashSet<>();
		for (Particle p : smoke) {
			if (p.hasExpired(time)) toRemove.add(p);
			else p.increase(time);
		}
		smoke.removeAll(toRemove);

		toRemove = new HashSet<>();
		for (Particle p : rFire) {
			if (p.hasExpired(time)) toRemove.add(p);
			else p.increase(time);
		}
		rFire.removeAll(toRemove);

		toRemove = new HashSet<>();
		for (Particle p : yFire) {
			if (p.hasExpired(time)) toRemove.add(p);
			else p.increase(time);
		}
		yFire.removeAll(toRemove);

		toRemove = new HashSet<>();
		for (Particle p : wFire) {
			if (p.hasExpired(time)) toRemove.add(p);
			p.increase(time);
		}
		wFire.removeAll(toRemove);

		toRemove = new HashSet<>();
		for (Particle p : embers) {
			if (p.hasExpired(time)) toRemove.add(p);
			p.increase(time);
		}
		embers.removeAll(toRemove);


		if (smoke.isEmpty()&&embers.isEmpty()&&rFire.isEmpty()&&yFire.isEmpty()&&wFire.isEmpty()) kill();
	}

	@Override
	public void killGracefully() {
		killingGracefully = true;
		tempLength = time;
		for (Particle p : embers) p.ttl = (time-p.creationTime)+112;
		for (Particle p : smoke) p.ttl = (time-p.creationTime)+112;
		for (Particle p : rFire) p.ttl = time;
		for (Particle p : yFire) p.ttl = time;
		for (Particle p : wFire) p.ttl = time;
	}

	@Override
	public synchronized void draw(Graphics2D g) {
		for (Particle p : smoke) p.draw(g);
		for (Particle p : rFire) p.draw(g);
		for (Particle p : yFire) p.draw(g);
		for (Particle p : wFire) p.draw(g);
		for (Particle p : embers) p.draw(g);
	}

	@Override
	public void shift(int xShift, int yShift) {
		extOffX += xShift;
		extOffY += yShift;
	}
}
