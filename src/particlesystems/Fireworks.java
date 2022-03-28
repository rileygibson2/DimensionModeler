package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Vec3;
import core.Render;

public class Fireworks extends ParticleSystem {

	//Firework colors - secondary color is always more saturated than primary color
	static Color[][] colors = {
			{new Color(255, 180, 0), new Color(255, 0, 0)},
			{new Color(255, 180, 0), new Color(255, 0, 0)},
			{new Color(255, 180, 0), new Color(255, 0, 0)},
			{new Color(200, 255, 200), new Color(50, 255, 50)}, 
			{new Color(255, 100, 255), new Color(255, 20, 20)},
			{new Color(255, 255, 255), new Color(50, 50, 255)}
	};
	List<Particle> fireworks = new ArrayList<>();
	int delay; //Delay between clumbs of fireworks
	int trigTime; //Used to create delay between clumps of fireworks

	public Fireworks(Vec3 origin, int originW, Render c) {
		super("Fireworks", 20, 1000, origin, originW, c);
	}

	@Override
	public void cycle(int time) {
		if (time==0) {
			fireworks = new ArrayList<Particle>();
			tempLength = length;
			trigTime = 0;
			delay = 100;
		}

		//Make fireworks
		if (!killingGracefully) {
			if ((time>=trigTime&&time<=trigTime+100)&&time%5==0) {
				for (int z=0; z<1; z++) {
					int rS = random(1, 100); //Escape vector size component
					int rH = random(200, 500); //Escape vector height component
					int xDir = random(1, 2); //Random x direction
					if (xDir==2) xDir = -1;
					double yDir = (double) random(-10, 10)/10; //Random y direction
					int dT = time+random(50, 200); //Random detonation time
					int dR = random(30, 100); //Size of deonated bubble - 80 is normal
					int rPS = random(8, 12); //Random particle size
					int rC = random(0, colors.length-1); //Random color pair from presets
					Color prim = colors[rC][0];
					Color sec = colors[rC][1];
					
					fireworks.add(new FireworkParticle(this, time, length, rS, rH, xDir, yDir, dT, dR, new Vec3(origin.x, origin.y, origin.z-1), prim, sec, rPS, false));
					if (time%40==0) fireworks.get(fireworks.size()-1).flagged = true;
				}
				if (time==trigTime+100) trigTime = time+delay;
			}
		}

		Set<Particle> toRemove = new HashSet<>();
		for (Particle p : fireworks) {
			if (p.hasExpired(time)) toRemove.add(p);
			p.increase(time);
		}
		fireworks.removeAll(toRemove);

		if (fireworks.isEmpty()) kill();
	}

	@Override
	public void killGracefully() {
		killingGracefully = true;
		tempLength = time;
		for (Particle p : fireworks) ((FireworkParticle) p).dying = true;
	}

	@Override
	public void draw(Graphics2D g) {
		for (Particle p : fireworks) p.draw(g);
	}

	@Override
	public void shift(int xShift, int yShift) {}
}
