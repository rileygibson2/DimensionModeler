package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Vec3;
import core.Render;

public class EmberParticle extends Particle {

	static Color ember = new Color(255, 150, 0, 255);

	//Concurrent modification avoidence variables
	int extOffX; //The old x store 
	int extOffY; //the old y store

	public EmberParticle(ParticleSystem parent, int creationTime, int ttl, Vec3 pos, int xDir, int yDir, int zDir, int size, boolean flagged) {
		super(parent, creationTime, ttl, pos, xDir, yDir, zDir, ember, size, flagged);

	}

	@Override
	public void increase(int cTime) {
		//Fade out if near end of life
		if (cTime-creationTime>=ttl-255) {
			int a = c.getAlpha()-1;
			if (a<0) a = 0;
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}

		//Move
		pos = new Vec3(pos.x+3*xDir, pos.y+3*yDir, pos.z+3*zDir);

		//Randomise direction changing
		if (random(1, 30)==1) xDir = -xDir;
		if (random(1, 30)==1) yDir = -yDir;

		/*
		 * If the external offset is bigger than the cached offset
		 * then add the difference and resave.
		 */
		if (parent.extOffX!=this.extOffX) {
			pos.x += (parent.extOffX-this.extOffX);
			this.extOffX = parent.extOffX;
		}
		if (parent.extOffY!=this.extOffY) {
			pos.y += parent.extOffY-this.extOffY;
			this.extOffY = parent.extOffY;
		}
	}

	@Override
	public void draw(Graphics2D g) {
		int oX = Render.sW/2+parent.c.p.xOffset;
		int oY = Render.sH/2+parent.c.p.yOffset;
		Point pX = parent.c.p.xAV(pos.x);
		Point pY = parent.c.p.yAV(pos.y);
		Point pZ = parent.c.p.zAV(pos.z);

		g.setColor(c);
		g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*size), (int) (parent.c.p.scale*size));
	}
}
