package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import core.Implementation.Direction;
import core.Implementation.Level;
import core.Layer.Visibility;
import io.AudioManager.sFX;
import objects.PlanarRoot;
import particlesystems.Electricity;
import particlesystems.ParticleSystem;
import textures.ChipTexture;

public class Animations {

	Render c;
	Implementation im; //Just for ease of programing
	public Map<String, Boolean> animations;

	public Animations(Render c) {
		this.c = c;
		this.im = c.im;

		animations = new LinkedHashMap<String, Boolean>();
		animations.put("chipBob", false);
		animations.put("brokenElevator", false);
		animations.put("fixedElevator", false);
		animations.put("playerMoving", false);
		animations.put("playerKill", false);
		animations.put("levelTransition", false);
		animations.put("infoShow", false);
		animations.put("infoHide", false);
		animations.put("bugMove", false);
		animations.put("bugDrop", false);

		animations.put("lightSpin", false);
		animations.put("lightChase", false);
	}

	public void set(String name, boolean b) {
		for (Map.Entry<String, Boolean> a : animations.entrySet()) {
			if (a.getKey().equals(name)) {
				a.setValue(b);
				return;
			}
		}
	}

	public boolean isRunning(String name) {
		if (animations.containsKey(name)) return animations.get(name);
		return false;
	}

	public void runLightSpin() {
		set("lightSpin", true);

		Thread animationThread = new Thread() {
			public void run() {
				List<Integer> directions = new ArrayList<>();
				for (LightSource lS : c.lightSources) {
					if (lS.vector.x<0) directions.add(-1);
					else directions.add(1);
				}
				int count = 0;
				double rate = 0.02;
				
				try {
					while (isRunning("lightSpin")) {
						count++;
						
						for (LightSource lS : c.lightSources) {
							lS.vector.x = Math.sin(rate*count);
							lS.vector.y = Math.sin((rate*count)+(Math.PI/2));
						}

						Thread.sleep(10);
					}
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}
				set("lightSpin", false);
			}
		};
		animationThread.start();
	}
	
	public void runLightChase() {
		set("lightChase", true);

		Thread animationThread = new Thread() {
			public void run() {
				int count = 0;
				double rate = 0.02;
				
				try {
					while (isRunning("lightChase")) {
						//for (LightSource lS : c.lightSources) {
							c.lightSources.get(0).intensity = (int) (50*Math.sin(rate*count)+50);
							count ++;
						//}
						Thread.sleep(10);
					}
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}
				set("lightChase", false);
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that drops the bugs into the level.
	 */
	public void runBugDrop() {
		set("bugDrop", true);
		int speed = 60;
		int delay = 2;
		int[] hStore = new int[im.jellyfish.size()];
		Arrays.fill(hStore, c.im.jellyfishInitZ); //The height of each bug, not it's components 

		Thread animationThread = new Thread() {
			public void run() {
				int time = 0;
				int running = 0; //The number of currently dropping bugs.
				try {
					c.getLayer("Bugs").setVisibility(Visibility.Visible);
					while (isRunning("bugDrop")) {
						//Drop all running bugs
						for (int i=0; i<hStore.length; i++) {
							if (i<=running) {
								if (hStore[i]+speed<=0) {
									hStore[i] += speed;
									for (PlanarRoot o : im.jellyfish.get(i)) {
										o.z += speed;
									}
								}
							}
						}
						//Increase running
						if (time%delay==0) running++;
						//Kill when time exceeds delay*(numberofbugs-1) + the length of one event (a full drop for one bug)
						if (time>=(delay*(im.jellyfish.size()-1))+(Math.abs(c.im.jellyfishInitZ)/speed)) {
							set("bugDrop", false);
						}
						time++;
						Thread.sleep(40);
					}
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that fades the info graphic in.
	 */
	public void runInfoShow() {
		if (!isRunning("infoHide")) {
			set("infoShow", true);

			Render.s.messageAlpha = 0;
			Thread animationThread = new Thread() {
				public void run() {
					try {
						Render.s.showMessage = true;
						boolean temp = true;
						while (temp) {
							Render.s.messageAlpha += 20;
							if (Render.s.messageAlpha>=200) {
								Render.s.messageAlpha = 200;
								temp = false;
							}
							Thread.sleep(40);
						}
					}
					catch (InterruptedException er) {throw new Error("Sleep error");}
				}
			};
			animationThread.start();
		}
	}

	/**
	 * Runs an animation that fades the info graphic out.
	 */
	public void runInfoHide() {
		set("infoShow", false);
		set("infoHide", true);

		Thread animationThread = new Thread() {
			public void run() {
				try {
					while (isRunning("infoHide")) {
						Render.s.messageAlpha -= 20;
						if (Render.s.messageAlpha<=0) {
							Render.s.messageAlpha = 0;
							set("infoHide", false);
						}
						Thread.sleep(40);
					}
					Render.s.showMessage = false;
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that simulates the player being killed
	 * and disintegrating into pieces.
	 * 
	 * @throws InterruptedException
	 */
	public void runPlayerKill() {
		set("playerKill", true);

		Thread animationThread = new Thread() {
			public void run() {
				int time = 0;

				//Build squares in shape of player and speeds to each block
				List<PlanarRoot> deadplayer = c.im.buildDeadPlayer();
				int[] speeds = new int[deadplayer.size()];
				for (int i=0; i<deadplayer.size(); i++) {
					if (i<11) speeds[i] = 5; //Glasses
					else speeds[i] = random(7, 12);
				}

				//Remove original and add dead player
				c.polyPool.removeAll(im.player);
				c.polyPool.addAll(deadplayer);

				try {
					while (isRunning("playerKill")) {
						for (int i=0; i<deadplayer.size(); i++) {
							PlanarRoot o = deadplayer.get(i);
							if (i==0) {
								//Handle glasses in one iteration
								if (o.z<-10) {
									for (int z=0; z<11; z++) {
										PlanarRoot oZ = deadplayer.get(z);
										oZ.z += speeds[i];
									}
								}
								i = 10;
							}
							//Everything else
							else {
								if (o.z<0) o.z += speeds[i];
								if (o.z>0) o.z = 0;
							}
						}
						time++;
						Thread.sleep(40);
					}
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}

				//Reset player
				c.polyPool.removeAll(deadplayer);
				c.polyPool.addAll(im.player);

			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that simulates the player walking.
	 * Unlike other animations, the playerMoving boolean is also
	 * a gate used by other modules. So must implement a temporary boolean
	 * to control the main animation loop, as if playerMoving was used, it would
	 * be set to false to kill the loop and another movement could potentially
	 * be called before the limbs are reset, causing artifacts.
	 * 
	 * @throws InterruptedException
	 */
	public void runPlayerMove(Direction dir, boolean armsOut) {
		set("playerMoving", true);
		Render.aM.run(sFX.Walking);

		Thread animationThread = new Thread() {
			public void run() {
				int time = 0;
				int lAdj; //Left side adj
				int rAdj; //Right side adj
				int lStart = 25; //Left leg start index
				int lEnd = 34; //Left leg end index
				int lArm = 20; //Left arm index;
				boolean xAxis = false; //Animate x or y value
				if (im.player.get(lArm).rot==90||im.player.get(lArm).rot==270) xAxis = true;

				int walkSpeed = 10; //Speed player walks at;
				double limbSpeed = 1; //Speed limbs move at;

				//Store pre animation values
				List<Integer> legStore = new ArrayList<>();
				for (int i=lStart; i<im.player.size(); i++) {
					if (xAxis) legStore.add(im.player.get(i).x);
					else legStore.add(im.player.get(i).y);
				}
				//Stores position and dimensions of arms
				Vec3 lArmStore = new Vec3(im.player.get(lArm).x, im.player.get(lArm).y, im.player.get(lArm).z);
				Vec3 lArmDimStore = new Vec3(im.player.get(lArm).w, im.player.get(lArm).h, im.player.get(lArm).ex);
				Vec3 rArmStore = new Vec3(im.player.get(lArm+1).x, im.player.get(lArm+1).y, im.player.get(lArm+1).z);
				Vec3 rArmDimStore = new Vec3(im.player.get(lArm+1).w, im.player.get(lArm+1).h, im.player.get(lArm+1).ex);

				//Put arms out
				if (armsOut) {
					if (dir==Direction.Forward||dir==Direction.Back) {
						im.player.get(lArm).ex = 13;
						im.player.get(lArm+1).ex = 13;
						im.player.get(lArm).z -= 45;
						im.player.get(lArm+1).z -= 45;
						im.player.get(lArm).h = 50;
						im.player.get(lArm+1).h = 50;
						im.player.get(lArm).y += 15;
						im.player.get(lArm+1).y += 15;
						if (dir==Direction.Back) {
							im.player.get(lArm).y -= 70;
							im.player.get(lArm+1).y -= 70;
						}
					}
					else {
						im.player.get(lArm).ex = 13;
						im.player.get(lArm+1).ex = 13;
						im.player.get(lArm).z -= 45;
						im.player.get(lArm+1).z -= 45;
						im.player.get(lArm).w = 50;
						im.player.get(lArm+1).w = 50;
						im.player.get(lArm).x += 15;
						im.player.get(lArm+1).x += 15;
						if (dir==Direction.Left) {
							im.player.get(lArm).x -= 70;
							im.player.get(lArm+1).x -= 70;
						}
					}
				}

				boolean temp = true;

				try {
					while (temp) {
						//Player movement is done by moving the world around the player such that they are always at the origin.

						if (dir!=null) {
							switch (dir) {
							case Forward : 
								for (PlanarRoot o : c.polyPool)  if (!im.player.contains(o)) o.y -= walkSpeed;
								for (ParticleSystem pS : c.systems) pS.shift(0, -walkSpeed); break;
							case Back : 
								for (PlanarRoot o : c.polyPool)  if (!im.player.contains(o)) o.y += walkSpeed;
								for (ParticleSystem pS : c.systems) pS.shift(0, walkSpeed); break;
							case Left: 
								for (PlanarRoot o : c.polyPool)  if (!im.player.contains(o)) o.x += walkSpeed;
								for (ParticleSystem pS : c.systems) pS.shift(walkSpeed, 0); break;
							case Right: 
								for (PlanarRoot o : c.polyPool)  if (!im.player.contains(o)) o.x -= walkSpeed;
								for (ParticleSystem pS : c.systems) pS.shift(-walkSpeed, 0); break;
							}

							if (walkSpeed*time>=90) temp = false;
						}

						if (xAxis) {
							//Left leg
							lAdj = (int) ((c.im.playerDim.x*0.09)*Math.sin(limbSpeed*time));
							for (int i=lStart; i<lEnd; i++) {
								im.player.get(i).x = legStore.get(i-lStart)+lAdj;
							}
							//Right leg
							rAdj = (int) ((c.im.playerDim.x*0.09)*Math.sin((limbSpeed*time)+Math.PI));
							for (int i=lEnd; i<im.player.size(); i++) {
								im.player.get(i).x = legStore.get(i-lEnd)+rAdj;
							}
							//Arms
							if (!armsOut) {
								im.player.get(lArm).x = (int) (lArmStore.x+rAdj);
								im.player.get(lArm+1).x = (int) (rArmStore.x+lAdj);
							}
						}
						else {
							//Left leg
							lAdj = (int) ((c.im.playerDim.x*0.09)*Math.sin(limbSpeed*time));
							for (int i=lStart; i<lEnd; i++) {
								im.player.get(i).y = legStore.get(i-lStart)+lAdj;
							}
							//Right leg
							rAdj = (int) ((c.im.playerDim.x*0.09)*Math.sin((limbSpeed*time)+Math.PI));
							for (int i=lEnd; i<im.player.size(); i++) {
								im.player.get(i).y = legStore.get(i-lEnd)+rAdj;
							}
							//Arms
							if (!armsOut) {
								im.player.get(lArm).y = (int) (lArmStore.y+rAdj);
								im.player.get(lArm+1).y = (int) (rArmStore.y+lAdj);
							}
						}

						time++;
						Thread.sleep(40);
					}

					//Reset everything to regular position
					im.player.get(lArm).x = (int) lArmStore.x;
					im.player.get(lArm+1).x = (int) rArmStore.x;
					im.player.get(lArm).y = (int) lArmStore.y;
					im.player.get(lArm+1).y = (int) rArmStore.y;
					im.player.get(lArm).z = (int) lArmStore.z;
					im.player.get(lArm+1).z = (int) rArmStore.z;
					im.player.get(lArm).w = (int) lArmDimStore.x;
					im.player.get(lArm+1).w = (int) rArmDimStore.x;
					im.player.get(lArm).h = (int) lArmDimStore.y;
					im.player.get(lArm+1).h = (int) rArmDimStore.y;
					im.player.get(lArm).ex = (int) lArmDimStore.z;
					im.player.get(lArm+1).ex = (int) rArmDimStore.z;

					if (xAxis) for (int i=lStart; i<im.player.size(); i++) im.player.get(i).x = legStore.get(i-lStart);
					else for (int i=lStart; i<im.player.size(); i++) im.player.get(i).y = legStore.get(i-lStart);
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}

				//Render.aM.stop(sFX.Walking);
				set("playerMoving", false);
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that makes the computer chips bob up
	 * and down, throw electricity and have the moving pixel texture look.
	 * 
	 * @throws InterruptedException
	 */
	public void runChipBob() {
		set("chipBob", true);
		List<ParticleSystem> toRemove = new ArrayList<>();
		for (ParticleSystem pS : c.systems) if (pS instanceof Electricity) toRemove.add(pS);
		c.systems.removeAll(toRemove);

		//Run electricity and set to first chip;
		c.systems.add(new Electricity(new Vec3(10, 0, -10), 60, c));
		c.runSystem("Electricity");
		((Electricity) c.getSingleSystem("Electricity")).attachedTo = im.chips.get(0).get(0);
		c.getSingleSystem("Electricity").origin.x = im.chips.get(0).get(0).x+im.chips.get(0).get(0).w;
		c.getSingleSystem("Electricity").origin.y = im.chips.get(0).get(0).y+im.chips.get(0).get(0).h/2;
		c.getSingleSystem("Electricity").origin.z = im.chips.get(0).get(0).z-im.chips.get(0).get(0).ex/2;


		Thread animationThread = new Thread() {
			public void run() {
				int time = 0;
				int tempT = 0; //A temporary time store
				ChipTexture t = (ChipTexture) c.getTexture("Chip");
				Electricity e;

				try {
					while (isRunning("chipBob")) {
						e = ((Electricity) c.getSingleSystem("Electricity"));

						//Update electricity's z to match floating chips z
						e.origin.z = e.attachedTo.z-e.attachedTo.ex/2;

						//Maybe move electricty to another chip
						for (List<PlanarRoot> l : im.chips) {
							if (random(1, 50)==1) {
								PlanarRoot chip = l.get(0);
								e.attachedTo = chip;
								if (random(1, 2)==1) e.origin.x = chip.x-e.originW;
								else e.origin.x = chip.x+chip.w;
								e.origin.y = chip.y+chip.h/2;
								break;
							}
						}

						//Maybe show electricity
						if (time>tempT+10&&random(1, 40)==1) {
							tempT = time;
							e.drawNow = true;
						}
						else if (time>tempT+10) e.drawNow = false;

						//Update the chip
						int i = 0;
						for (List<PlanarRoot> l : im.chips) {
							l.get(0).z = (int) -(25*Math.sin(0.1*(time+i))+40); //Chip body
							l.get(1).z = (int) -(25*Math.sin(0.1*(time+i))+40)-22; //Left contact
							l.get(2).z = (int) -(25*Math.sin(0.1*(time+i))+40)-22; //Right contact
							i+=20;
						}

						t.effectPos += 0.08;
						if (t.effectPos>3) t.effectPos = 0;

						//Stop time from overflowing but do it when chip is at bottom of curve
						time++;
						if (time==1000) {
							time = 0;
							tempT = 0;
						}
						Thread.sleep(40);
					}
					if (c.getSingleSystem("Electricity")!=null) {
						c.getSingleSystem("Electricity").kill(); //Kill electricity
					}
					t.effectPos = 0;
				}
				catch (InterruptedException er) {throw new Error("Sleep error");}
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that makes the elevator throw off sparks and flash
	 * a red light like it is broken.
	 * 
	 * @throws InterruptedException
	 */
	public void runBrokenElevator() {
		set("brokenElevator", true);

		Thread animationThread = new Thread() {
			public void run() {
				c.runSystem("Sparks"); //Sparks
				int time = 0;
				try {
					while (isRunning("brokenElevator")) {
						//Change light color
						if (time%10==0) {
							if (im.exitElevator.get(4).col.equals(new Color(255, 0, 0))) im.exitElevator.get(4).col = new Color(255, 255, 255, 150);
							else im.exitElevator.get(4).col = new Color(255, 0, 0);

						}

						time++;
						if (time==100) time = 0; //Stop time int from overflowing
						Thread.sleep(60);
					}
					if (c.getAllSystems("Sparks")!=null) {
						for (ParticleSystem pS : c.getAllSystems("Sparks")) {
							pS.kill();
						}
					}
				}
				catch (InterruptedException e) {throw new Error("Sleep error");}
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that makes the elevator's light green and
	 * opens the doors to allow the player to enter.
	 */
	public void runFixedElevator() {
		set("brokenElevator", false);
		set("fixedElevator", true);
		im.exitElevator.get(4).col = new Color(0, 255, 0);

		Thread animationThread = new Thread() {
			public void run() {
				double dir = -2; //Door direction
				try {
					while (isRunning("fixedElevator")) {
						//Move left door
						im.exitElevator.get(0).w += dir;
						im.exitElevator.get(1).x += dir;

						//Move right door
						im.exitElevator.get(2).x -= dir;
						im.exitElevator.get(2).w += dir;
						im.exitElevator.get(3).x -= dir;

						if (im.exitElevator.get(0).w<=0) set("fixedElevator", false);

						Thread.sleep(60);
					}
				}
				catch (InterruptedException e) {throw new Error("Sleep error");}
			}
		};
		animationThread.start();
	}

	/**
	 * Runs an animation that transitions between the levels.
	 * Starts by stopping all running animations, then
	 * closing the doors, descend shaft, open arms, then pick up
	 * elevator.
	 * After the old level is out of sight, load in the new level.
	 */
	public void runLevelTransition() {
		set("chipBob", false);
		set("brokenElevator", false);
		set("fixedElevator", false);
		set("levelTransition", true);

		//Put current exitElevator into entranceElevator
		im.entranceElevator = new ArrayList<>();
		im.entranceElevator.addAll(im.exitElevator);
		im.exitElevator.clear();

		im.entranceElevator.get(4).col = new Color(0, 255, 0);

		Thread animationThread = new Thread() {
			String aniName = "levelTransition";
			public void run() {
				int time = 0;
				boolean temp = true;
				try {
					//Phase 1 - reset perspective to correct scale and angle
					c.p.scale = Math.round(c.p.scale*10d)/10d; //Round perspective variables
					c.p.vertRot = Math.round(c.p.vertRot*10d)/10d;
					c.p.horzRot = Math.round(c.p.horzRot*10d)/10d;
					while (temp&&isRunning(aniName)) {
						if (c.p.scale>1.1) c.p.scale -= 0.05;
						if (c.p.scale<1.1) c.p.scale += 0.05;
						if (c.p.vertRot>0.6) c.p.vertRot -= 0.05;
						if (c.p.vertRot<0.6) c.p.vertRot += 0.05;
						if (c.p.horzRot>-0.6) c.p.horzRot -= 0.05;
						if (c.p.horzRot<-0.6) c.p.horzRot += 0.05;

						if (c.p.scale>1.05&&c.p.scale<=1.15) {
							if (c.p.vertRot>0.55&&c.p.vertRot<=0.65) {
								if (c.p.horzRot>-0.65&&c.p.horzRot<=-0.55) {
									temp = false;
								}
							}
						}
						//Failsafe
						//if (time>50) temp = false;
						time++;
						Thread.sleep(50);
					}

					//Phase 2 - close doors and remove player
					temp = true;
					while (temp&&isRunning(aniName)) {
						//Move Left door
						im.entranceElevator.get(0).w += 2;
						im.entranceElevator.get(1).x += 2;
						//Move Right door
						im.entranceElevator.get(2).x -= 2;
						im.entranceElevator.get(2).w += 2;
						im.entranceElevator.get(3).x -= 2;
						if (im.entranceElevator.get(0).w>=35) temp = false;
						Thread.sleep(60);
					}
					c.polyPool.removeAll(im.player);
					im.entranceElevator.get(4).col = new Color(255, 0, 0);

					//Phase 3 - bring in shaft
					c.im.buildTransitionShaft(-10, 0, -1000);
					temp = true;
					while (temp&&isRunning(aniName)) {
						for (PlanarRoot o : im.transition) o.z += 20;
						if (im.transition.get(0).z>=-200) temp = false;
						Thread.sleep(40);
					}

					//Phase 4 - extend shaft arms out
					temp = true;
					time = 0;
					while (temp&&isRunning(aniName)) {
						im.transition.get(1).x -= 6;
						im.transition.get(1).w += 12;
						im.transition.get(2).x += 6;
						im.transition.get(3).x -= 6;

						if (time==10) temp = false;
						time++;
						Thread.sleep(40);
					}

					//Phase 5 - extend shaft arms down
					temp = true;
					time = 0;
					while (temp&&isRunning(aniName)) {
						im.transition.get(2).z += 6;
						im.transition.get(2).ex += 6;
						im.transition.get(3).z += 6;
						im.transition.get(3).ex += 6;

						if (time==10) temp = false;
						time++;
						Thread.sleep(40);
					}

					//Phase 6 - pick up
					Thread.sleep(200);
					temp = true;
					time = 0;
					while (temp&&isRunning(aniName)) {
						for (PlanarRoot o : c.polyPool)  {
							if (im.entranceElevator.contains(o)) continue;
							if (!im.transition.contains(o)) {
								o.z += 10;
							}
						}
						if (c.p.yOffset>200) c.p.yOffset -= 2;
						if (c.p.yOffset<200) c.p.yOffset += 2;

						if (time==100) temp = false;
						time++;
						Thread.sleep(30);
					}

					//Phase 7 - load new level at height
					Level lev = Level.Two;
					if (im.currLevel==Level.Two) lev = Level.One;
					im.build(false, lev);

					for (PlanarRoot o : c.polyPool)  {
						if (im.entranceElevator.contains(o)) continue;
						if (!im.transition.contains(o)) {
							o.z -= 800;
						}
					}

					//Phase 8 - lower new level in
					temp = true;
					time = 0;
					while (temp&&isRunning(aniName)) {
						for (PlanarRoot o : c.polyPool)  {
							if (im.entranceElevator.contains(o)) continue;

							if (!im.transition.contains(o)) {
								o.z += 10;
							}
						}
						if (time==80) temp = false;
						time++;
						Thread.sleep(30);
					}

					//Phase 9 - bring out shaft
					c.getSingleSystem("Fireworks").run();
					time = 0;
					temp = true;
					while (temp&&isRunning(aniName)) {
						for (PlanarRoot o : im.transition) o.z -= 20;
						if (im.transition.get(0).z<=-1000) temp = false;
						Thread.sleep(30);
					}

					//Phase 10 - open doors
					im.entranceElevator.get(4).col = new Color(0, 255, 0);
					time = 0;
					temp = true;
					while (temp&&isRunning(aniName)) {
						//Move Left door
						im.entranceElevator.get(0).w -= 2;
						im.entranceElevator.get(1).x -= 2;
						//Move Right door
						im.entranceElevator.get(2).x += 2;
						im.entranceElevator.get(2).w -= 2;
						im.entranceElevator.get(3).x += 2;
						if (im.entranceElevator.get(0).w<=0) temp = false;
						Thread.sleep(60);
					}

					//Phase 11 - insert player and run animations
					Thread.sleep(1000);
					im.finishBuild();

					//Phase 12 - reset offsets
					temp = true;
					time = 0;
					while (temp&&isRunning(aniName)) {
						if (c.p.yOffset>0) c.p.yOffset -= 5;
						if (c.p.yOffset<0) c.p.yOffset += 5;
						if (c.p.yOffset<5&&c.p.yOffset>-5) temp = false;
						//Failsafe
						if (time>50) temp = false;
						time++;
						Thread.sleep(40);
					}
					c.getSingleSystem("Fireworks").killGracefully();

					//Reset transition
					c.polyPool.removeAll(im.transition);
					im.transition = new ArrayList<PlanarRoot>();
					set(aniName, false);
				}
				catch (InterruptedException e) {throw new Error("Sleep error");}
				runPlayerMove(Direction.Back, false);
			}
		};
		animationThread.start();
	}

	/**
	 * A handy random number method.
	 * 
	 * @param the inclusive lower value
	 * @param the inclusive upper value
	 * @return the random integer
	 */
	public int random(double min, double max){
		return (int) ((Math.random()*((max-min)+1))+min);
	}

	/**
	 * Kills all running animations. An animation can be ommitted from the
	 * kill by providing it's name.
	 * 
	 * @param ommit - the name of the animation to ommit
	 */
	public void killAllAnimations(String ommit) {
		for (Map.Entry<String, Boolean> a : animations.entrySet()) {
			if (ommit!=null&&!a.getKey().equals(ommit)) a.setValue(false);
			else if (ommit==null) a.setValue(false);
		}
	}
}