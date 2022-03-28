package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import core.Vec3;
import core.Render;
import core.Render.Mode;

/**
 * @implNote to make standing wave, all you have to do is take
 * the pi gate off the wave mode!!!! Will likely have to remove
 * the edge bouncing and the decay too.
 * 
 * @author thesmileyone
 */
public class WaterParticle extends Particle {

	double force = 0;
	Vec3 posStore; //Used to help adjust for external offset
	Point index;

	public WaterParticle(ParticleSystem parent, int creationTime, int ttl, Vec3 pos, Color c, Point index) {
		super(parent, creationTime, ttl, pos, 0, 0, 0, c, 1, false);
		this.posStore = new Vec3(pos.x, pos.y, pos.z);
		this.index = index;
	}

	@Override
	public void increase(int cTime) {
		//Find max wave amp for colour
		double maxAmp = 0;
		Water p = ((Water) parent);
		for (Wave w : p.waves) if (w.amp>maxAmp) maxAmp = w.amp;


		//Move
		pos.z = parent.origin.z;
		pos.x = posStore.x+parent.extOffX;
		pos.y = posStore.y+parent.extOffY;

		for (Wave w : p.waves) {
			switch (w.mode) {
			case Diag1 :
				int cX = (int) (posStore.x+posStore.y); //int cX = 0; //The "x value" on the new angled plane
				if (!w.infinite) { //Move in single wave form
					if (cX>25*w.i&&cX<25*(w.i+2*Math.PI)) {
						pos.z += (int) (w.amp*Math.sin((0.04*cX)-w.i+Math.PI/2)-w.amp);
					}
				}
				else pos.z += (int) (w.amp*Math.sin((0.04*cX)-w.i+Math.PI/2)-w.amp);
				break;

			case Diag2 :
				cX = (int) (posStore.x-posStore.y); //int cX = 0; //The "x value" on the new angled plane
				if (!w.infinite) {
					if (cX>25*w.i&&cX<25*(w.i+2*Math.PI)) {
						pos.z += (int) (w.amp*Math.sin((0.04*cX)-w.i+Math.PI/2)-w.amp);
					}
				}
				else pos.z += (int) (w.amp*Math.sin((0.04*cX)-w.i+Math.PI/2)-w.amp);
				break;

			case X :
				if (!w.infinite) {
					if (posStore.x>10*w.i&&posStore.x<10*(w.i+2*Math.PI)) {
						pos.z += (int) (w.amp*Math.sin((0.1*posStore.x)-w.i+Math.PI/2)-w.amp);
					}
				}
				else pos.z += (int) (w.amp*Math.sin((0.1*posStore.x)-w.i+Math.PI/2)-w.amp);
				break;

			case Y :
				if (!w.infinite) {
					if (posStore.y>10*w.i&&posStore.y<10*(w.i+2*Math.PI)) {
						pos.z += (int) (w.amp*Math.sin((0.1*posStore.y)-w.i+Math.PI/2)-w.amp);
					}
				}
				else pos.z += (int) (w.amp*Math.sin((0.1*posStore.y)-w.i+Math.PI/2)-w.amp);
			}

			int g = (int) Math.abs((pos.z-parent.origin.z)*(70/maxAmp))+10;
			if (g>255) g = 255;
			if (parent.c.mode==Mode.Wireframe) c = new Color(0, g, 255);
			else c = new Color(0, g, 255, 200);
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

		g.fillRect(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y, 1, 1);
	}
}

/*int g = (int) Math.abs(pos.z*(70/maxAmp))+10;
			if (g>255) g = 255;
			c = new Color(0, g, 255);*/
