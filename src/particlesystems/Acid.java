package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Vec3;
import core.Render;

public class Acid extends ParticleSystem {

	List<Particle> acid = new ArrayList<>();

	public Acid(Vec3 origin, int originW, Render c) {
		super("Acid", 20, 1000, origin, originW, c);
		this.extOffX = 0;
		this.extOffY = 0;
	}

	@Override
	public void cycle(int time) {
		if (time==0) {
			acid = new ArrayList<Particle>();
			tempLength = length;
		}

		//acid
		if (time%2==0&&time<=20) {
			for (int z=0; z<20; z++) {
				int rY = random(origin.y, origin.y+originW);
				int rV = random(5, 10); //Escape vector vertical component
				double rH = ((double) random(1, 20))/100; //Escape vector horizontal component
				acid.add(new VelocityParticle(this, time, 100, rH, rV, origin.z, new Vec3(origin.x, origin.y+rY, origin.z), new Color(45, 181, 0), 3, false));
			}
		}

		Set<Particle> toRemove = new HashSet<>();
		for (Particle p : acid) {
			if (p.hasExpired(time)) toRemove.add(p);
			p.increase(time);
		}
		acid.removeAll(toRemove);
		
		if (acid.isEmpty()) kill();
	}

	@Override
	public void killGracefully() {
		killingGracefully = true;
		tempLength = time;
		for (Particle p : acid) p.ttl = 112;
	}

	@Override
	public void draw(Graphics2D g) {
		for (Particle p : acid) p.draw(g);
	}

	@Override
	public void shift(int xShift, int yShift) {}
}
