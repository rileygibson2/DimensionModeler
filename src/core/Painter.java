package core;

import particlesystems.ParticleSystem;

/**
 * Paint and Particle System handler.
 * Because so many different things that want to paint at different points,
 * movement animations, perspective changes, particle systems, tabs that want
 * to trigger paints, it is actually more efficient right now to just repaint
 * at a set fps, rather then each thing triggering it's own repaint.
 * 
 * Also cycles running particle systems, so essentially everything but animations
 * is consolidated on one thread.
 * 
 * @author thesmileyone
 *
 */
public class Painter extends Thread {
	Render c;

	public Painter(Render c) {
		this.c = c;
	}

	@Override
	public void run() {
		while (c.paint) {
			try {Thread.sleep(40);}
			catch (InterruptedException er) {throw new Error("Sleep error");}
			//Cycle particle system
			if (c.systems!=null) {
				for (ParticleSystem pS : c.systems) {
					if (pS.isRunning()) {
						pS.cycle(pS.time);
						pS.time++;
						if (pS.speed==20) { //If system runs in double time
							pS.cycle(pS.time);
							pS.time++;
						}
					}
				}
			}
			//Paint screen if no progress bar current
			if (!c.pBCheck()) c.repaint();
		}
	}
}
