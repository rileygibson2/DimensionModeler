package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Vec3;
import core.Render;

public class FireParticle extends Particle {

	Color start;
	Color end;
	int radius;

	//Concurrent modification avoidence variables
	int extOffX; //The old x store 
	int extOffY; //the old y store

	public FireParticle(ParticleSystem parent, int creationTime, int ttl, Vec3 pos, int xDir, int yDir, int zDir, Color start, Color end, int radius, int size, boolean flagged) {
		super(parent, creationTime, ttl, pos, xDir, yDir, zDir, start, size, flagged);
		this.start = start;
		this.end = end;
		this.radius = radius;
	}

	@Override
	public void increase(int cTime) {
		//Move
		pos = new Vec3(pos.x+xDir, pos.y+yDir, pos.z+zDir+zDir);

		//Randomise direction changing
		if (random(1, 20)==1) xDir = -xDir;
		if (random(1, 20)==1) yDir = -yDir;
		if (random(1, 30)==1) zDir = -zDir;

		//Shaping
		double width = Math.sqrt(Math.pow(radius, 2)-Math.pow(pos.z+radius, 2));
		if (-pos.z>radius) width = -0.003*Math.pow(pos.z+radius, 2)+radius;
		double topIntercept = Math.sqrt(-radius/-0.003)+radius;

		if (pos.x<-width+parent.origin.x+extOffX) pos.x = (int) (-width+1)+parent.origin.x+extOffX;
		if (pos.x>width+parent.origin.x+extOffX) pos.x = (int) (width-1)+parent.origin.x+extOffX;
		if (pos.y<-width+parent.origin.y+extOffY) pos.y = (int) (-width+1)+parent.origin.y+extOffY;
		if (pos.y>width+parent.origin.y+extOffY) pos.y = (int) (-width-1)+parent.origin.y+extOffY;
		//Bounce off top and bottom
		if (pos.z>0) {
			pos.z = -1;
			zDir = -zDir;
		}
		if (pos.z<-topIntercept) {
			pos.z = (int) (-topIntercept+1);
			zDir = -zDir;
		}

		if (flagged) {
			c = Color.BLUE;
			pos = new Vec3((int) width, 0, pos.z);
		}
		
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
		g.setColor(c);
		int oX = Render.sW/2+parent.c.p.xOffset;
		int oY = Render.sH/2+parent.c.p.yOffset;
		Point pX = parent.c.p.xAV(pos.x);
		Point pY = parent.c.p.yAV(pos.y);
		Point pZ = parent.c.p.zAV(pos.z);

		g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*size), (int) (parent.c.p.scale*size));
	}
}
