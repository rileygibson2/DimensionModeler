package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Vec3;
import core.Render;

public class SmokeParticle extends Particle {

	Color start;
	Color end;
	int w; //Width of smoke stream - so it can be proportionate to fire

	//Concurrent modification avoidence variables
	int extOffX; //The old x store 
	int extOffY; //the old y store

	public SmokeParticle(ParticleSystem parent, int creationTime, int ttl, Vec3 pos, int xDir, int yDir, int zDir, Color start, Color end, int w, int size, boolean flagged) {
		super(parent, creationTime, ttl, pos, xDir, yDir, zDir, start, size, flagged);
		this.start = start;
		this.end = end;
		this.w = w;
	}

	@Override
	public void increase(int cTime) {
		//Fade out if near end of life
		if (cTime-creationTime>=ttl-255) {
			int a = c.getAlpha()-1;
			if (a<0) a = 0;
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}

		//Adjust color for time
		if (cTime%2==0) {
			int r = c.getRed()+1;
			int g = c.getGreen()+1;
			int b = c.getBlue()+1;
			if (r>255) r = 255; if (g>255) g = 255; if (b>255) b = 255;
			c = new Color(r, g, b, c.getAlpha());
		}

		//Move
		pos = new Vec3(pos.x+xDir, pos.y+yDir, pos.z+zDir);

		//Randomise direction changing
		if (random(1, 20)==1) xDir = -xDir;
		if (random(1, 20)==1) yDir = -yDir;


		//Shaping
		double width = 0.001*Math.pow(cTime-creationTime, 2)+w;
		if (pos.x<-width+parent.origin.x+extOffX) pos.x = (int) -width+parent.origin.x+extOffX;
		if (pos.x>width+parent.origin.x+extOffX) pos.x = (int) width+parent.origin.x+extOffX;
		if (pos.y<-width+parent.origin.y+extOffY) pos.y = (int) -width+parent.origin.y+extOffY;
		if (pos.y>width+parent.origin.y+extOffY) pos.y = (int) -width+parent.origin.y+extOffY;

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
