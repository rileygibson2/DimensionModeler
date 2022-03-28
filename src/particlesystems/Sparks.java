package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Vec3;
import core.Render;

public class Sparks extends ParticleSystem {

	List<Particle> sparks = new ArrayList<>();
	int rTime = 0; //Random interval between sparks being thrown

	public Sparks(Vec3 origin, int originW, Render c) {
		super("Sparks", 20, 1000, origin, originW, c);
		this.extOffX = 0;
		this.extOffY = 0;
	}

	@Override
	public void cycle(int time) {
		if (time==0) {
			sparks = new ArrayList<Particle>();
			tempLength = length;
			rTime = 0;
		}

		//Sparks
		if (time>=rTime&&time<=rTime+10) {
			for (int y=0; y<originW; y+=20) {
				for (int i=0; i<3; i++) {
					int rZ = random(-80, origin.z); //Random height between origin and ground
					int rX = origin.x; //Only spawn at either x extreme
					if (random(1, 2)==1) rX = origin.x+originW; 
					int rV = random(5, 100); //Escape vector vertical component
					double rH = ((double) random(10, 50))/100; //Escape vector horizontal component
					sparks.add(new VelocityParticle(this, time, 100, rH, rV, rZ, new Vec3(rX, origin.y+y, origin.z), new Color(255, 150, 0), 3, false));

					//Make one in each row be on top
					if (i==2) {
						((VelocityParticle) sparks.get(sparks.size()-1)).initialZ = origin.z-10;
						((VelocityParticle) sparks.get(sparks.size()-1)).pos.x = random(origin.x, origin.x+originW);
						((VelocityParticle) sparks.get(sparks.size()-1)).onTop = true;
					}
				}
			}
			
			//Make some more in a bit
			if (time==rTime+10) rTime = time+random(200, 500);
		}

		Set<Particle> toRemove = new HashSet<>();
		for (Particle p : sparks) {
			if (p.hasExpired(time)) toRemove.add(p);
			p.increase(time);
		}
		sparks.removeAll(toRemove);
		
		if (isKilling()&&sparks.isEmpty()) kill();
	}

	@Override
	public void shift(int xShift, int yShift) {
		extOffX += xShift;
		extOffY += yShift;
	}

	@Override
	public void killGracefully() {
		killingGracefully = true;
		tempLength = time;
		for (Particle p : sparks) p.ttl = 112;
	}

	@Override
	public void draw(Graphics2D g) {
		for (Particle p : sparks) p.draw(g);
	}
}
