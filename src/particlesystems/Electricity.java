package particlesystems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Vec3;
import core.Render;
import objects.PlanarRoot;

public class Electricity extends ParticleSystem {

	List<ElectricityParticle> particles = new ArrayList<>();
	int amount;
	public PlanarRoot attachedTo; //Thing electricity is sparking off

	public boolean drawNow; //Allows animations to control when electricity is drawn
	/*drawW specifies the amount of particles to actually draw.
	 * This allows you to change width of system to connect two
	 * objects without actually altering the number of particles inside.*/
	public int drawW;

	public Electricity(Vec3 origin, int originW, Render c) {
		super("Electricity", 20, 10000, origin, originW, c);
		drawW = originW;
		drawNow = true;
	}

	@Override
	public void cycle(int time) {
		//Make
		if (time==0) {
			particles = new ArrayList<ElectricityParticle>();

			amount = originW/4;
			for (int i=0; i<amount; i++) {
				int rTTL = random(length/2, length); //Random ttl
				if (i==0||i==amount-1) rTTL = length;
				int x = i*(originW/amount);

				particles.add(new ElectricityParticle(this, time, rTTL, x));
			}
		}

		//Branch a section
		if (random(1, 25)==1) {
			ElectricityParticle p = particles.get(random(1, particles.size()-2));
			if (p.hasBranch) p.killBranch();
			else p.branch();
		}
		//Kill branches
		for (ElectricityParticle p : particles) {
			if (p.hasBranch&&random(1, 15)==1) p.killBranch();
		}

		Set<Particle>  toRemove = new HashSet<>();
		for (ElectricityParticle p : particles) {
			if (p.hasExpired(time)) toRemove.add(p);
			else {
				p.increase(time);
				if (p.hasBranch) p.increaseBranch();
			}
		}
		particles.removeAll(toRemove);

		if (particles.isEmpty()) kill();
	}

	@Override
	public void killGracefully() {
		kill();
	}

	@Override
	public void draw(Graphics2D g) {
		int oX = Render.sW/2+c.p.xOffset;
		int oY = Render.sH/2+c.p.yOffset;

		//Only draw sometimes for strobe effect
		if (drawNow) {
			if (!particles.isEmpty()&&random(1, 3)==1) {
				//Glow 1
				g.setColor(new Color(141, 147, 224, 50));
				g.setStroke(new BasicStroke((int)(c.p.scale)*8));
				drawRecur(g, particles, 1, oX, oY, 8);

				//Glow 2
				g.setColor(new Color(96, 100, 157, 100));
				g.setStroke(new BasicStroke((int)(c.p.scale)*5));
				drawRecur(g, particles, 1, oX, oY, 5);

				//Center
				g.setColor(new Color(222, 224, 255));
				g.setStroke(new BasicStroke((int)(c.p.scale)*2));
				drawRecur(g, particles, 1, oX, oY, 2);
			}
		}

	}

	public void drawRecur(Graphics2D g, List<ElectricityParticle> particles, int i, int oX, int oY, int strokeW) {
		ElectricityParticle next = particles.get(i);
		ElectricityParticle last = particles.get(i-1);
		i++;

		//Check particle is within the current draw width but exclude branches from this check
		if (last.difX<drawW||last.isOnBranch) {
			g.drawLine(oX+c.p.xAV(next.pos.x).x+c.p.yAV(next.pos.y).x+c.p.zAV(next.pos.z).x,
					oY+c.p.xAV(next.pos.x).y+c.p.yAV(next.pos.y).y+c.p.zAV(next.pos.z).y,
					oX+c.p.xAV(last.pos.x).x+c.p.yAV(last.pos.y).x+c.p.zAV(last.pos.z).x,
					oY+c.p.xAV(last.pos.x).y+c.p.yAV(last.pos.y).y+c.p.zAV(last.pos.z).y);

			if (next.hasBranch) {
				g.setStroke(new BasicStroke((int)(c.p.scale)*(strokeW-2)));
				drawRecur(g, next.branch, 1, oX, oY, strokeW-2);
				g.setStroke(new BasicStroke((int)(c.p.scale)*strokeW));
			}
			if (i<particles.size()-1) {
				drawRecur(g, particles, i, oX, oY, strokeW);
			}
		}
	}

	@Override
	public void shift(int xShift, int yShift) {}
}
