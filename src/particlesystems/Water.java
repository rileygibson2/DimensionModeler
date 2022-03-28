package particlesystems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.Vec3;
import core.Render;
import objects.SquareOld;
import particlesystems.Wave.Mode;

public class Water extends ParticleSystem {

	int originH;
	WaterParticle particles[][];
	int size;
	List<Wave> waves;

	public Water(Vec3 origin, int originW, int originH, Render c) {
		super("Water", 20, 10000, origin, originW, c);
		this.originH = originH;
		this.isSorted = true;
		//Insert false object into sorted object list so this system can be draw at correct depth
		indicator = new SquareOld(origin.x, origin.y, 0, originW, originW, 100, c.getLayer("SystInds"), Color.BLUE, null, c);
		indicator.isSystemIndicator = true;
		indicator.system = this;
		c.polyPool.add(indicator);
	}

	@Override
	public void cycle(int time) {
		//Make
		if (time==0) {
			int sizeW = (int) (((double) originW/100d)*8d);
			int sizeH = (int) (((double) originH/100d)*8d);
			//size = 8;
			particles = new WaterParticle[sizeH][sizeW];
			waves = new ArrayList<Wave>();

			for (int yI=0; yI<sizeH; yI++) {
				int y = yI*(originH/sizeH);

				for (int xI=0; xI<sizeW; xI++) {
					int x = xI*(originW/sizeW);
					particles[yI][xI] = new WaterParticle(this, time, length, new Vec3(origin.x+x, origin.y+y, origin.z), new Color(0, 10, 255, 100), new Point(xI, yI));

				}
			}
		}

		//Create waves
		if (time==0) waves.add(new Wave(-Math.PI, 1, 20, 0, Mode.Diag1, true));
		if (time==60) waves.add(new Wave(-Math.PI, 1, 5, 0, Mode.Diag2, true));
		//if (time==80) waves.add(new Wave(-Math.PI*2, 1, 5, 0, Mode.X, true));


		//Move waves - relies on water being square
		Set<Wave> wavesRemove = new HashSet<>();
		for (Wave w : waves) {
			//Change wave dir
			if (!w.infinite) {
				if (w.mode==Mode.Diag1) {
					if (w.i>30-Math.PI||w.i<-Math.PI) w.inc = -w.inc;
				}
				else if (w.mode==Mode.Diag2) {
					if (w.i>15-Math.PI||w.i<-15-Math.PI) w.inc = -w.inc;
				}
				else if (w.i>(originW/10)-Math.PI||(w.i<-Math.PI*2)) w.inc = -w.inc;
			}
			w.i += 0.1*w.inc; //Move

			w.amp = w.amp*(1-w.decayRate); //Decay
			if (w.amp<=0) wavesRemove.add(w); //Remove

		}
		waves.removeAll(wavesRemove);

		//Increase
		for (int y=0; y<particles.length; y++) {
			for (int x=0; x<particles[0].length; x++) {
				particles[y][x].increase(time);
			}
		}
	}

	@Override
	public void killGracefully() {
		if (waves!=null) for (Wave w : waves) w.decayRate = 0.01;
		kill();
	}

	@Override
	public void draw(Graphics2D g) {
		if (particles!=null) {

			if (c.mode==Render.Mode.Wireframe) {
				for (int y=0; y<particles.length; y++) {
					for (int x=0; x<particles[0].length; x++) {
						if (particles[y][x]!=null) particles[y][x].draw(g);
					}
				}
			}
			else if (c.mode==Render.Mode.Panelled) {
				int oX = Render.sW/2+c.p.xOffset;
				int oY = Render.sH/2+c.p.yOffset;
				GeneralPath gP = new GeneralPath();
				if (c.p.vertRot>=1) drawWaterOutlines(g, oX, oY); //If looking from above, draw outline first

				//Top wave
				for (int y=0; y<particles.length; y++) {
					for (int x=0; x<particles[0].length; x++) {
						if (y+1<particles.length&&x+1<particles[0].length) {
							g.setColor(particles[y][x].c);
							gP = new GeneralPath();

							//Move to bottom left
							Particle pa = particles[y][x];
							Point pX = c.p.xAV(pa.pos.x);
							Point pY = c.p.yAV(pa.pos.y);
							Point pZ = c.p.zAV(pa.pos.z);
							gP.moveTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y);

							//Move to top left point
							pa = particles[y+1][x];
							pX = c.p.xAV(pa.pos.x);
							pY = c.p.yAV(pa.pos.y);
							pZ = c.p.zAV(pa.pos.z);
							gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y);

							//Move to top right point
							pa = particles[y+1][x+1];
							pX = c.p.xAV(pa.pos.x);
							pY = c.p.yAV(pa.pos.y);
							pZ = c.p.zAV(pa.pos.z);
							gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y);

							//Move to bottom right point
							pa = particles[y][x+1];
							pX = c.p.xAV(pa.pos.x);
							pY = c.p.yAV(pa.pos.y);
							pZ = c.p.zAV(pa.pos.z);
							gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y);

							//Move to bottom left point
							pa = particles[y][x];
							pX = c.p.xAV(pa.pos.x);
							pY = c.p.yAV(pa.pos.y);
							pZ = c.p.zAV(pa.pos.z);
							gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y);

							g.fill(gP);
						}
					}
				}

				if (c.p.vertRot<1) drawWaterOutlines(g, oX, oY); //If looking from below, draw outline last
			}
		}
	}

	public void drawWaterOutlines(Graphics2D g, int oX, int oY) {
		GeneralPath gP = new GeneralPath();

		//Draw Front Outline
		Point pX = c.p.xAV(particles[0][0].pos.x);
		Point pY = c.p.yAV(particles[0][0].pos.y);
		Point pZ = c.p.zAV(0);
		gP.moveTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Move to start

		for (int x=0; x<particles[0].length; x++) {
			Particle pa = particles[0][x];
			pX = c.p.xAV(pa.pos.x);
			pY = c.p.yAV(pa.pos.y);
			pZ = c.p.zAV(pa.pos.z);
			gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Meet all points
		}

		pX = c.p.xAV(particles[0][particles[0].length-1].pos.x);
		pY = c.p.yAV(particles[0][particles[0].length-1].pos.y);
		pZ = c.p.zAV(0);
		gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Move to end
		g.setColor(new Color(0, 20, 255, 200));
		g.fill(gP);

		//Draw Side Outline
		int s = 0;
		if (c.p.horzRot<0) s = particles[0].length-1;

		gP = new GeneralPath();
		pX = c.p.xAV(particles[0][s].pos.x);
		pY = c.p.yAV(particles[0][s].pos.y);
		pZ = c.p.zAV(0);
		gP.moveTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Move to start

		for (int y=0; y<particles.length; y++) {
			Particle pa = particles[y][s];
			pX = c.p.xAV(pa.pos.x);
			pY = c.p.yAV(pa.pos.y);
			pZ = c.p.zAV(pa.pos.z);
			gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Meet all points
		}

		pX = c.p.xAV(particles[particles.length-1][s].pos.x);
		pY = c.p.yAV(particles[particles.length-1][s].pos.y);
		pZ = c.p.zAV(0);
		gP.lineTo(oX+pX.x+pY.x+pZ.x, oY+pX.y+pY.y+pZ.y); //Move to end
		g.setColor(new Color(0, 80, 255, 200));
		g.fill(gP);
	}

	@Override
	public void shift(int xShift, int yShift) {
		extOffX += xShift;
		extOffY += yShift;
	}
}

/*if (time%60==0&&time<300) {
	waves.add(new Wave(-Math.PI*2, 1, 15, 0.003, Mode.X, false));
	waves.add(new Wave(-Math.PI*2, 1, 15, 0.002, Mode.Y, false));
	waves.add(new Wave(-Math.PI, 1, 15, 0.002, Mode.Diag1, false));
	waves.add(new Wave(-15, 1, 20, 0.005, Mode.Diag2));
}*/
