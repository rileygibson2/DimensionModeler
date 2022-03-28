package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Vec3;
import core.Render;

public class VelocityParticle extends Particle {

	/*
	 * The escape vector of the particle.
	 * Has a vertical component and a horizontal component.
	 */
	double vectorV;
	double vectorH;
	int initialZ;
	boolean onTop = false; //If particles should be fired straight up

	//Concurrent modification avoidence variables
	int extOffX; //The old x store 
	int extOffY; //the old y store
	
	public VelocityParticle(ParticleSystem parent, int creationTime, int ttl, double vectorH, double vectorV, int initialZ, Vec3 pos, Color c, int size, boolean flagged) {
		super(parent, creationTime, ttl, pos, 0, 0, 0, c, size, flagged);
		this.vectorH = vectorH;
		this.vectorV = vectorV;
		this.initialZ = initialZ;
	}

	@Override
	public void increase(int cTime) {
		//Fade out if near end of life
		if (cTime-creationTime>=ttl-112) {
			int a = c.getAlpha()-2;
			if (a<0) a = 0;
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
		}

		//Move
		int thresh = 0; //Defines the 'ground' which the particle should stop at
		if (onTop) thresh = parent.origin.z+1;
		if (pos.z<thresh) {
			if (!onTop) {
				if (pos.x<=parent.origin.x) pos.x-=1;
				else pos.x+=1;
			}

			double intercept = Math.sqrt(-vectorV/-vectorH);
			int inc = (int) (-vectorH*Math.pow((cTime-creationTime)-intercept, 2)+vectorV);
			pos.z = initialZ-inc;
		}
		else if (pos.z>thresh) {
			ttl = (cTime-creationTime)+112;
			pos.z = thresh;
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
		int oX = Render.sW/2+parent.c.p.xOffset;
		int oY = Render.sH/2+parent.c.p.yOffset;
		Point pX = parent.c.p.xAV(pos.x);
		Point pY = parent.c.p.yAV(pos.y);
		Point pZ = parent.c.p.zAV(pos.z-2);

		g.setColor(c);
		g.fillOval(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, (int) (parent.c.p.scale*size), (int) (parent.c.p.scale*size));
	}
}
