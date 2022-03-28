package core;

import java.awt.Color;
import java.awt.datatransfer.SystemFlavorMap;
import java.util.ArrayList;
import java.util.List;

import objects.PlanarRoot;
import objects.SquareOld;
import particlesystems.Fire;
import particlesystems.Sparks;
import particlesystems.Water;

public class Implementation {
	Render c;
	public enum Level {One, Two};
	Level currLevel;

	/*
	 * Collections of polyPool that make things. The order of 
	 * polyPool is often crucial in these sets as they may be
	 * used in animations.
	 */
	public List<List<PlanarRoot>> chips;
	public List<PlanarRoot> entranceElevator;
	public List<PlanarRoot> exitElevator;
	public List<PlanarRoot> player;
	public List<List<PlanarRoot>> doors;
	public List<List<PlanarRoot>> keys;
	public List<PlanarRoot> transition;
	public List<List<PlanarRoot>> info;
	public List<List<PlanarRoot>> jellyfish;
	public List<PlanarRoot> dirt;
	public List<PlanarRoot> shoes;
	public List<PlanarRoot> gate;

	//Player variables
	Vec3 playerPos; //Position of player
	Vec3 playerDim; //Dimensions of player
	Color skin = new Color(255, 232, 196);
	Color shirt = new Color(100, 220, 255);
	Color tie = new Color(180, 180, 180);
	Color belt = new Color(50, 50, 50);
	Color shorts = new Color(156, 97, 6);
	Color socks = new Color(255, 255, 255);
	Color sandals = new Color(100, 100, 100);
	Color fireSocks = new Color(255, 50, 0);
	Color fireSandals = new Color(255, 255, 0);

	//Key variables
	Color greenKey = new Color(15, 227, 10);
	Color redKey = new Color(224, 24, 7);
	Color yellowKey = new Color(255, 211, 0);

	//Bug variables
	int jellyfishInitZ = -2000; //The build z value of a bug before the drop in animation

	public enum Direction {Left, Right, Forward, Back};

	public Implementation(Render c) {
		this.c = c;
		this.playerPos = new Vec3(0, 0, 0);
		this.playerDim = new Vec3(100, 100, 150);
	}

	public void build(boolean initial, Level lev) {
		currLevel = lev;
		List<PlanarRoot> ommit = new ArrayList<>();
		if (transition!=null) ommit.addAll(c.im.transition);
		if (entranceElevator!=null) ommit.addAll(entranceElevator);

		c.cleanPolyPool(ommit, true);
		Render.a.killAllAnimations("levelTransition");

		try {Thread.sleep(40);}
		catch (InterruptedException er) {throw new Error("Sleep error");}

		c.killAllSystems();
		c.setUpParticleSystems();
		System.out.println("\nLevel "+lev.toString()+":");

		chips = new ArrayList<List<PlanarRoot>>();
		exitElevator = new ArrayList<PlanarRoot>();
		player = new ArrayList<PlanarRoot>();
		doors = new ArrayList<List<PlanarRoot>>();
		keys = new ArrayList<List<PlanarRoot>>();
		info = new ArrayList<List<PlanarRoot>>();
		jellyfish = new ArrayList<List<PlanarRoot>>();
		dirt = new ArrayList<PlanarRoot>();
		shoes = new ArrayList<PlanarRoot>();
		gate = new ArrayList<PlanarRoot>();

		switch (lev) {
		//case One: buildLevel(initial, LevelMaps.level1); break;
		//case Two: buildLevel(initial, LevelMaps.level2); break;
		}

		buildJellyFish(0, 0, 0);
		buildPlayer(150, 0, 0);
	}

	public void buildLevel(boolean initial, String[][] level) {
		int w = 100; //For one tile
		int h = 100;
		int x, y;
		int xOff = 0; //Offsets for centering
		int yOff = 0;
		boolean drawFloor = true; //Used for overriding floor

		//Parse level to find entry tile and make them the axis center
		parse:
			for (y=0; y<level.length; y++) {
				for (x=0; x<level[0].length; x++) {
					if (level[y][x]=="entry") {
						xOff = -(x*w)-(w/4);
						yOff = -((level.length-y)*h)-(h/2);
						System.out.println(x+", "+y);
						break parse;
					}
				}
			}

		for (int iY=0; iY<level.length; iY++) {
			for (int iX=0; iX<level[0].length; iX++) {
				x = xOff+iX*w;
				y = yOff+(level.length-iY)*h+2;
				drawFloor = true;

				switch (level[iY][iX]) {
				case "w" :
					//Up Down
					if ((iY-1>0&&level[iY-1][iX]=="w")||iY+1<level.length&&level[iY+1][iX]=="w") {
						//If wall on right but not above, then turn
						if (((iY-1>0&&level[iY-1][iX]!="w")||iY-1<0)&&(iX+1<level[0].length&&level[iY][iX+1]=="w")) {
							buildWall(x, y, 0, 1);
						}
						//If wall on right then add turned wall to fill corner
						else if (iX+1<level[0].length&&level[iY][iX+1]=="w") {
							buildWall(x, y, 0, 0);
							buildWall(x, y, 0, 1);
						}
						//If no wall above then shove down
						if ((iY-1<0)||(iY-1>=0&&level[iY-1][iX]!="w")) {
							buildWall(x, y-50, 0, 0);
						}
						else buildWall(x, y, 0, 0);
					}
					//Left Right
					else if ((iX-1>=0&&level[iY][iX-1]=="w")||iX+1<level[0].length&&level[iY][iX+1]=="w") {
						buildWall(x, y, 0, 1);
					}
					break;
				case "c" :
					buildChip(x, y+(h/4), 0);
					if ((iX-1>=0&&level[iY][iX-1]=="l")||iX+1<level[0].length&&level[iY][iX+1]=="l") {
						//Place water underneath
						Water wt = new Water(new Vec3(x-(w/2), y, -10), 120, 120, c);
						c.systems.add(wt);
						wt.run();
						drawFloor = false;
					}
					break;
				case "k1" :
					buildKey(x-25, y, 0, redKey);
					break;
				case "k2" :
					buildKey(x-25, y, 0, yellowKey);
					break;
				case "k3" :
					buildKey(x-25, y, 0, greenKey);
					break;
				case "d1" :
					if ((iY-1>0&&level[iY-1][iX]=="w")&&(iY+1<level.length&&level[iY+1][iX]=="w")) {
						buildRedDoor(x-75, y, 0, 1); //Up Down
						buildWall(x, y-110, 0, 0); //Extra wall fill
					}
					else {
						buildRedDoor(x, y+22, 0, 0); //Left Right
						buildWall(x-100, y, 0, 1); //Extra wall fill
					}
					break;
				case "d2" :
					if ((iY-1>0&&level[iY-1][iX]=="w")&&(iY+1<level.length&&level[iY+1][iX]=="w")) {
						buildYellowDoor(x-75, y, 0, 1); //Up Down
						buildWall(x, y-110, 0, 0); //Extra wall fill
					}
					else {
						buildYellowDoor(x, y+22, 0, 0); //Left Right
						buildWall(x-100, y, 0, 1); //Extra wall fill
					}
					break;
				case "d3" :
					if ((iY-1>0&&level[iY-1][iX]=="w")&&(iY+1<level.length&&level[iY+1][iX]=="w")) {
						buildGreenDoor(x-75, y, 0, 1); //Up Down
						buildWall(x, y-110, 0, 0); //Extra wall fill
					}
					else {
						buildGreenDoor(x, y+22, 0, 0); //Left Right
						buildWall(x-100, y, 0, 1); //Extra wall fill
					}
					break;
				case "e" :
					if ((iY-1>0&&level[iY-1][iX]=="w")||(iY+1<level.length&&level[iY+1][iX]=="w")) {
						buildElevator(x-(w/4), y, 0, 3);
					}
					else buildElevator(x-(w/4), y, 0, 0);
					break;
				case "q" :
					buildInfo(x-(w/4), y, 0);
					break;
				case "l" :
					int nX = w/2;
					int nY = 0;
					int nW = 120;
					int nH = 120;
					if (iX+1<level[0].length&&(level[iY][iX+1]=="w"||level[iY][iX+1]=="s")) { //Fill right gap
						nW = 160;
					}
					if (iY+1<level.length&&level[iY+1][iX]=="w") { //Fill bottom gap
						nY = 50;
						nH = 170;
					}
					Water wt = new Water(new Vec3(x-nX, y-nY, -10), nW, nH, c);
					c.systems.add(wt);
					wt.run();
					drawFloor = false;
					break;
				case "bf" :
					buildJellyFish(x-(w/4), y, 0);
					break;
				case "bb" :
					buildJellyFish(x-(w/4), y, 0);
					break;
				case "bl" :
					buildJellyFish(x-(w/4), y, 0);
					break;
				case "br" :
					buildJellyFish(x-(w/4), y, 0);
					break;
				case "f" : 
					Fire f = new Fire(new Vec3(x+25, y+50, 0), 100, c);
					c.systems.add(f);
					//f.run();
					break;
				case "g" : buildDirt(x-25, y, 0); break;
				case "s" : break;
				default : drawFloor = false; break;
				}

				//Transparent grid 'tile'
				if (drawFloor) c.polyPool.add(new SquareOld(x-25, y, 20, 100, 100, 20, c.getLayer("Wall"), new Color(120, 120, 120, 50), null, c));
			}
		}

		//If it's the first level to be built then finish setup immediatly
		if (initial) finishBuild();
		c.depthLimit += 500; //Adjust object sort depth sample limit
	}

	/**
	 * Allows player to be inserted and the animations and systems
	 * to be run at a time that is potentially after the level is built.
	 * Useful for animations that require a staggered build. For initial
	 * level load, this method will likely be run immediatly after the initial
	 * build.
	 */
	public void finishBuild() {
		//Insert player
		buildPlayer(-50, -50, 0);

		//Run relevant particle systems and initial animations
		switch(currLevel) {
		case One :
			Render.a.runChipBob();
			Render.a.runBrokenElevator();
			break;
		case Two :
			Render.a.runChipBob();
			break;
		}
	}


	public void movePlayer(Direction dir) {
		int target = 0; //Rotation target
		switch (dir) {
		case Forward : target = 180; break;
		case Back : target = 0; break;
		case Left: target = 270; break;
		case Right: target = 90; break;
		}

		while ( player.get(0).rot!=target) {
			for (PlanarRoot o :  player) o.rotate90(playerPos.x+playerDim.x/2, playerPos.y+playerDim.y/2);
		}

		//Run movement animation
		Render.a.runPlayerMove(dir, false);
	}

	public void buildExitGate(int x, int y, int z) {
		Color rivet = new Color(150, 150, 150);
		for (int i=0; i<100; i+=10) {
			gate.add(new SquareOld(x+i, y, z, 3, 3, 80, c.getLayer("Player"), rivet, null, c));
		}
		c.polyPool.addAll(gate);
	}

	public void buildFireShoes(int x, int y, int z) {
		int w = 100;
		int h = 100;
		int ex = 150;
		//Socks
		shoes.add(new SquareOld((int) (x+w*0.28), (int) (y+h*0.425), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.14), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.28), (int) (y+h*0.4), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.28), (int) (y+h*0.35), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.03), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.28), (int) (y+h*0.31), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		//Sandles
		shoes.add(new SquareOld((int) (x+w*0.27), (int) (y+h*0.38), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), fireSandals, null, c));
		shoes.add(new SquareOld((int) (x+w*0.27), (int) (y+h*0.33), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), fireSandals, null, c));
		shoes.add(new SquareOld((int) (x+w*0.27), (int) (y+h*0.305), (int) 0, (int) (w*0.13), (int) (h*0.27), (int) (ex*0.02), c.getLayer("Player"), fireSandals, null, c));
		//Socks
		shoes.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.425), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.14), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.4), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.35), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.03), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		shoes.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.31), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), fireSocks, null, c));
		//Sandles
		shoes.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.38), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), fireSandals, null, c));
		shoes.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.33), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), fireSandals, null, c));
		shoes.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.305), (int) 0, (int) (w*0.13), (int) (h*0.27), (int) (ex*0.02), c.getLayer("Player"), fireSandals, null, c));

		c.polyPool.addAll(shoes);
	}

	public void buildDirt(int x, int y, int z) {
		dirt.add(new SquareOld(x+12, y+12, z, 75, 75, 120, c.getLayer("Dirt"), new Color(109, 76, 53), c.getTexture("Dirt"), c));
		c.polyPool.addAll(dirt);
	}

	public void buildJellyFish(int x, int y, int z) {
		Color bodyL1 = new Color(13, 198, 255, 80);
		Color bodyL2 = new Color(255, 43, 250, 100);
		if (random(1, 2)==1) {
			bodyL1 = new Color(255, 43, 250, 100);
			bodyL2 = new Color(13, 198, 255, 80);
		}
		Color tentacle1 = new Color(41, 198, 255, 80);
		Color tentacle2 = new Color(252, 187, 251, 100);

		//if (z==0) z = jellyfishInitZ;

		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x, y, z, 100, 100, 100, c.getLayer("JellyFish"), new Color(255, 255, 255, 0), null, c));

		//Body
		temp.add(new SquareOld(x+20, y+20, z-150, 60, 60, 60, c.getLayer("JellyFish"), bodyL1, null, c));
		temp.add(new SquareOld(x+25, y+25, z-155, 50, 50, 50, c.getLayer("JellyFish"), bodyL2, null, c));
		temp.add(new SquareOld(x+30, y+30, z-160, 40, 40, 40, c.getLayer("JellyFish"), bodyL2, null, c));

		for (int i=0; i<25; i++) {
			int rX = random(-5, 40);
			int rY = random (0, 40);
			int type = random(1, 3);
			int trigPoint = random(0, 80);
			//type = 1;
			//rX = 0;
			//rY = 0;
			Color col = tentacle1;
			if (random(1, 2)==1) col = tentacle2;
			switch (type) {
			case 1 :
				//Tentacles
				temp.add(new SquareOld(x+30+rX, y+30+rY, z-120+trigPoint, 3, 3, 30+trigPoint, c.getLayer("JellyFish"), col, null, c));
				if (random(1, 2)==1) {
					temp.add(new SquareOld(x+30+rX, y+30+rY, z-116+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+43+rX, y+30+rY, z-86+trigPoint, 3, 3, 30, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+30+rX, y+30+rY, z-83+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
				}
				else {
					temp.add(new SquareOld(x+18+rX, y+30+rY, z-116+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+18+rX, y+30+rY, z-86+trigPoint, 3, 3, 30, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+18+rX, y+30+rY, z-83+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
				}
				temp.add(new SquareOld(x+30+rX, y+30+rY, z-30+trigPoint, 3, 3, 55, c.getLayer("JellyFish"), col, null, c));
				break;

			case 2 :
				//Tentacles
				temp.add(new SquareOld(x+30+rX, y+30+rY, z-120+trigPoint, 3, 3, 30+trigPoint, c.getLayer("JellyFish"), col, null, c));
				if (random(1, 2)==1) {
					temp.add(new SquareOld(x+30+rX, y+30+rY, z-116+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+43+rX, y+30+rY, z-67+trigPoint, 3, 3, 50, c.getLayer("JellyFish"), col, null, c));
				}
				else {
					temp.add(new SquareOld(x+18+rX, y+30+rY, z-116+trigPoint, 16, 3, 3, c.getLayer("JellyFish"), col, null, c));
					temp.add(new SquareOld(x+18+rX, y+30+rY, z-67+trigPoint, 3, 3, 50, c.getLayer("JellyFish"), col, null, c));
				}
				break;

			case 3 :
				//Tentacles
				temp.add(new SquareOld(x+30+rX, y+30+rY, z-30, 2, 2, 120, c.getLayer("JellyFish"), col, null, c));
			}
		}

		jellyfish.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildInfo(int x, int y, int z) {
		Color qColor = new Color(255, 255, 255);
		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x+20, y+20, -15, 60, 60, 80, c.getLayer("Info"), new Color(255, 255, 255, 100), null, c));

		temp.add(new SquareOld(x+32, y+45, -80, 35, 10, 10, c.getLayer("Info"), qColor, null, c));
		temp.add(new SquareOld(x+32, y+45, -70, 10, 10, 10, c.getLayer("Info"), qColor, null, c));
		temp.add(new SquareOld(x+57, y+45, -60, 10, 10, 20, c.getLayer("Info"), qColor, null, c));
		temp.add(new SquareOld(x+42, y+45, -50, 25, 10, 10, c.getLayer("Info"), qColor, null, c));
		temp.add(new SquareOld(x+42, y+45, -35, 10, 10, 15, c.getLayer("Info"), qColor, null, c));

		temp.add(new SquareOld(x+42, y+45, -15, 10, 10, 10, c.getLayer("Info"), qColor, null, c));

		info.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildKey(int x, int y, int z, Color main) {
		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x, y, 0, 100, 100, 100, c.getLayer("Keys"), new Color(255, 255, 255, 10), null, c));
		//Head
		temp.add(new SquareOld(x+30, y+45, -35, 4, 4, 20, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+41, y+45, -35, 4, 4, 20, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+30, y+45, -55, 15, 4, 4, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+30, y+45, -31, 15, 4, 4, c.getLayer("Keys"), main, null, c));
		//Backbone
		temp.add(new SquareOld(x+45, y+45, -45, 36, 4, 4, c.getLayer("Keys"), main, null, c));
		//Spokes
		temp.add(new SquareOld(x+70, y+48, -41, 6, 4, 4, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+65, y+45, -39, 16, 3, 3, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+65, y+45, -33, 4, 2, 6, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+72, y+45, -33, 2, 2, 6, c.getLayer("Keys"), main, null, c));
		temp.add(new SquareOld(x+77, y+45, -33, 4, 2, 6, c.getLayer("Keys"), main, null, c));

		keys.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildRedDoor(int x, int y, int z, int rotNum) {
		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x, y, -2, 100, 10, 110, c.getLayer("Doors"), new Color(194, 24, 7), c.getTexture("WoodDoor"), c));
		temp.add(new SquareOld(x+10, y-5, -70, 8, 5, 8, c.getLayer("Doors"), new Color(120, 120, 120), null, c));

		for (int i=0; i<rotNum; i++) {
			for (PlanarRoot o : temp) o.rotate90(x+50, y+50);
		}

		doors.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildYellowDoor(int x, int y, int z, int rotNum) {
		Color bars = new Color(255, 211, 0);
		Color lock = new Color(160, 160, 160);
		List<PlanarRoot> temp = new ArrayList<>();
		//Vert bars
		for (int i=0; i<10; i++) {
			temp.add(new SquareOld(x+(10*i), y, -2, 4, 3, 120, c.getLayer("Doors"), bars, null, c));
		}
		//Horz bars
		temp.add(new SquareOld(x, y, -120, 93, 4, 4, c.getLayer("Doors"), bars, null, c));
		temp.add(new SquareOld(x, y, -100, 93, 4, 4, c.getLayer("Doors"), bars, null, c));
		temp.add(new SquareOld(x, y, -40, 93, 4, 4, c.getLayer("Doors"), bars, null, c));
		temp.add(new SquareOld(x, y, 0, 93, 3, 4, c.getLayer("Doors"), bars, null, c));
		//Lock
		temp.add(new SquareOld(x, y-2, -60, 13, 2, 17, c.getLayer("Doors"), lock, null, c));
		temp.add(new SquareOld(x+5, y-4, -63, 3, 2, 10, c.getLayer("Doors"), bars, null, c));

		for (int i=0; i<rotNum; i++) {
			for (PlanarRoot o : temp) o.rotate90(x+50, y+50);
		}

		doors.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildGreenDoor(int x, int y, int z, int rotNum) {
		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x, y, -2, 50, 10, 120, c.getLayer("Doors"), Color.WHITE, c.getTexture("FancyDoor"), c));
		temp.add(new SquareOld(x+50, y, -2, 50, 10, 120, c.getLayer("Doors"), Color.WHITE, c.getTexture("FancyDoor"), c));

		temp.add(new SquareOld(x+46, y-4, -60, 3, 4, 15, c.getLayer("Doors"), new Color(255, 179, 0), null, c));
		temp.add(new SquareOld(x+54, y-4, -60, 3, 4, 15, c.getLayer("Doors"), new Color(255, 179, 0), null, c));

		for (int i=0; i<rotNum; i++) {
			for (PlanarRoot o : temp) o.rotate90(x+50, y+50);
		}

		doors.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildWall(int x, int y, int z, int rotNum) {
		c.polyPool.add(new SquareOld(x, y, 0, 50, 100, 110, c.getLayer("Wall"), new Color(120, 120, 120), c.getTexture("Wall"), c));

		for (int i=0; i<rotNum; i++) {
			c.polyPool.get(c.polyPool.size()-1).rotate90(x+50, y+50);
		}
	}

	public void buildChip(int x, int y, int z) {
		List<PlanarRoot> temp = new ArrayList<>();
		temp.add(new SquareOld(x, y, 0, 50, 50, 50, c.getLayer("Chip"), new Color(80, 80, 80), c.getTexture("Chip"), c));
		temp.add(new SquareOld(x-5, y+25, -22, 5, 5, 5, c.getLayer("Chip"), new Color(200, 200, 200), null, c));
		temp.add(new SquareOld(x+50, y+25, -22, 5, 5, 5, c.getLayer("Chip"), new Color(200, 200, 200), null, c));
		chips.add(temp);
		c.polyPool.addAll(temp);
	}

	public void buildElevator(int x, int y, int z, int rotNum) {
		int w = 100;
		int h = 100;
		int ex = 150;
		Color eColor = new Color(220, 220, 220);
		Color dColor = new Color(212, 175, 55);
		dColor = new Color(100, 100, 100);

		//Doors
		exitElevator.add(new SquareOld(x+10, y, 0, 35, 10, ex, c.getLayer("Elevator"), dColor, null, c));
		exitElevator.add(new SquareOld(x+45, y, 0, 5, 10, ex, c.getLayer("Elevator"), new Color(150, 150, 150), null, c));
		exitElevator.add(new SquareOld(x+w/2+5, y, 0, 35, 10, ex, c.getLayer("Elevator"), dColor, null, c));
		exitElevator.add(new SquareOld(x+w/2, y, 0, 5, 10, ex, c.getLayer("Elevator"), new Color(150, 150, 150), null, c));
		//Light
		exitElevator.add(new SquareOld(x+29, y-8, -ex-8, 39, 3, 14, c.getLayer("Elevator"), new Color(255, 0, 0), null, c));
		//Body
		exitElevator.add(new SquareOld(x, y, 0, 10, h, ex, c.getLayer("Elevator"), eColor, null, c));
		exitElevator.add(new SquareOld(x+w-10, y, 0, 10, h, ex, c.getLayer("Elevator"), eColor, null, c));
		exitElevator.add(new SquareOld(x, y+h-10, 0, w, 10, ex, c.getLayer("Elevator"), new Color(100, 100, 100), null, c));
		exitElevator.add(new SquareOld(x, y, 10, w, h, 10, c.getLayer("Elevator"), eColor, null, c));
		exitElevator.add(new SquareOld(x, y, -ex, w, h, 30, c.getLayer("Elevator"), eColor, null, c));
		exitElevator.add(new SquareOld(x+25, y-5, -ex-6, 45, 5, 18, c.getLayer("Elevator"), new Color(180, 180, 180), null, c));

		for (int i=0; i<rotNum; i++) {
			for (PlanarRoot o : exitElevator) o.rotate90(x+50, y+50);
		}
		c.polyPool.addAll(exitElevator);

		//Attached spark system
		c.systems.add(new Sparks(new Vec3(x, y, -180), 100, c));
	}

	/**
	 * Build the arm which descends and picks up the elevator in the
	 * transition. Initially build out of sight so it can decend.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void buildTransitionShaft(int x, int y, int z) {
		transition = new ArrayList<PlanarRoot>();
		//Color tColor = new Color(237, 182, 2);
		Color tColor = new Color(200, 200, 200);
		Color hColor = new Color(200, 200, 200);

		//Shaft
		transition.add(new SquareOld(x, y, z, 20, 20, 1000, c.getLayer("Elevator"), tColor, null, c));
		//Arms Out
		transition.add(new SquareOld(x, y, z+10, 20, 20, 20, c.getLayer("Elevator"), hColor, null, c));
		//Arms Down
		transition.add(new SquareOld(x, y, z+10, 20, 20, 20, c.getLayer("Elevator"), hColor, null, c));
		transition.add(new SquareOld(x, y, z+10, 20, 20, 20, c.getLayer("Elevator"), hColor, null, c));

		c.polyPool.addAll(transition);
	}

	public void buildPlayer(int x, int y, int z) {
		playerPos = new Vec3(x, y, z);
		int w = playerDim.x;
		int h = playerDim.y;
		int ex = playerDim.z;

		player.add(new SquareOld(x, y, 1, w, h, ex, c.getLayer("Player"), new Color(255, 255, 255, 0), null, c));

		//Head
		player.add(new SquareOld((int) (x+w*0.37), (int) (y+h*0.4), (int) -(ex*0.75), (int) (w*0.25), (int) (h*0.25), (int) (ex*0.18), c.getLayer("Player"), skin, null, c));
		//Glasses
		player.add(new SquareOld((int) (x+w*0.37), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.45), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.39), (int) (y+h*0.37), (int) -(ex*0.87), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.39), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.52), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.6), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.54), (int) (y+h*0.37), (int) -(ex*0.87), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.54), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		//Glasses Arms & Bridge
		player.add(new SquareOld((int) (x+w*0.47), (int) (y+h*0.37), (int) -(ex*0.85), (int) (w*0.05), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.35), (int) (y+h*0.38), (int) -(ex*0.84), (int) (w*0.02), (int) (h*0.2), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.62), (int) (y+h*0.38), (int) -(ex*0.84), (int) (w*0.02), (int) (h*0.2), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));
		//Neck
		player.add(new SquareOld((int) (x+w*0.475), (int) (y+h*0.45), (int) -(ex*0.73), (int) (w*0.05), (int) (h*0.1), (int) (ex*0.02), c.getLayer("Player"), skin, null, c));
		player.add(new SquareOld((int) (x+w*0.45), (int) (y+h*0.4), (int) -(ex*0.63), (int) (w*0.1), (int) (h*0.2), (int) (ex*0.1), c.getLayer("Player"), skin, null, c));
		//Shirt
		player.add(new SquareOld((int) (x+w*0.15), (int) (y+h*0.4), (int) -(ex*0.63), (int) (w*0.3), (int) (h*0.2), (int) (ex*0.1), c.getLayer("Player"), shirt, null, c));
		player.add(new SquareOld((int) (x+w*0.55), (int) (y+h*0.4), (int) -(ex*0.63), (int) (w*0.3), (int) (h*0.2), (int) (ex*0.1), c.getLayer("Player"), shirt, null, c));
		player.add(new SquareOld((int) (x+w*0.3), (int) (y+h*0.4), (int) -(ex*0.41), (int) (w*0.4), (int) (h*0.2), (int) (ex*0.22), c.getLayer("Player"), shirt, null, c));
		//Tie
		player.add(new SquareOld((int) (x+w*0.45), (int) (y+h*0.35), (int) -(ex*0.63), (int) (w*0.1), (int) (h*0.05), (int) (ex*0.07), c.getLayer("Player"), tie, null, c));
		player.add(new SquareOld((int) (x+w*0.48), (int) (y+h*0.35), (int) -(ex*0.51), (int) (w*0.04), (int) (h*0.05), (int) (ex*0.18), c.getLayer("Player"), tie, null, c));
		//Arms
		player.add(new SquareOld((int) (x+w*0.17), (int) (y+h*0.45), (int) -(ex*0.33), (int) (w*0.1), (int) (h*0.1), (int) (ex*0.3), c.getLayer("Player"), skin, null, c));
		player.add(new SquareOld((int) (x+w*0.73), (int) (y+h*0.45), (int) -(ex*0.33), (int) (w*0.1), (int) (h*0.1), (int) (ex*0.3), c.getLayer("Player"), skin, null, c));
		//Belt
		player.add(new SquareOld((int) (x+w*0.3), (int) (y+h*0.4), (int) -(ex*0.39), (int) (w*0.4), (int) (h*0.2), (int) (ex*0.03), c.getLayer("Player"), belt, null, c));
		player.add(new SquareOld((int) (x+w*0.48), (int) (y+h*0.38), (int) -(ex*0.39), (int) (w*0.05), (int) (h*0.02), (int) (ex*0.03), c.getLayer("Player"), new Color(200, 200, 200), null, c));
		//Shorts Crossbar
		player.add(new SquareOld((int) (x+w*0.3), (int) (y+h*0.4), (int) -(ex*0.35), (int) (w*0.4), (int) (h*0.2), (int) (ex*0.04), c.getLayer("Player"), shorts, null, c));

		//LEFT LEG
		//Shorts
		player.add(new SquareOld((int) (x+w*0.3), (int) (y+h*0.4), (int) -(ex*0.21), (int) (w*0.15), (int) (h*0.2), (int) (ex*0.14), c.getLayer("Player"), shorts, null, c));
		//Legs
		player.add(new SquareOld((int) (x+w*0.32), (int) (y+h*0.425), (int) -(ex*0.16), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.05), c.getLayer("Player"), skin, null, c));
		//Socks
		player.add(new SquareOld((int) (x+w*0.32), (int) (y+h*0.425), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.14), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.32), (int) (y+h*0.4), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.32), (int) (y+h*0.35), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.03), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.32), (int) (y+h*0.31), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		//Sandles
		player.add(new SquareOld((int) (x+w*0.31), (int) (y+h*0.38), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.31), (int) (y+h*0.33), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.31), (int) (y+h*0.305), (int) 0, (int) (w*0.13), (int) (h*0.27), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));

		//RIGHT LEG
		//Shorts
		player.add(new SquareOld((int) (x+w*0.55), (int) (y+h*0.4), (int) -(ex*0.21), (int) (w*0.15), (int) (h*0.2), (int) (ex*0.14), c.getLayer("Player"), shorts, null, c));
		//Legs
		player.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.425), (int) -(ex*0.16), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.05), c.getLayer("Player"), skin, null, c));
		//Socks
		player.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.425), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.15), (int) (ex*0.14), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.4), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.35), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.03), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		player.add(new SquareOld((int) (x+w*0.58), (int) (y+h*0.31), (int) -(ex*0.02), (int) (w*0.11), (int) (h*0.02), (int) (ex*0.05), c.getLayer("Player"), socks, null, c));
		//Sandles
		player.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.38), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.33), (int) -(ex*0.02), (int) (w*0.13), (int) (h*0.02), (int) (ex*0.06), c.getLayer("Player"), sandals, null, c));
		player.add(new SquareOld((int) (x+w*0.57), (int) (y+h*0.305), (int) 0, (int) (w*0.13), (int) (h*0.27), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));

		c.polyPool.addAll(player);
	}

	public List<PlanarRoot> buildDeadPlayer() {
		int x = playerPos.x;
		int y = playerPos.y;
		int w = playerDim.x;
		int h = playerDim.y;
		int ex = playerDim.z;

		List<PlanarRoot> deadplayer = new ArrayList<>();

		//Glasses - these stay intact
		deadplayer.add(new SquareOld((int) (x+w*0.37), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.45), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.39), (int) (y+h*0.37), (int) -(ex*0.87), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.39), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.52), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.6), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.02), (int) (h*0.03), (int) (ex*0.07), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.54), (int) (y+h*0.37), (int) -(ex*0.87), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.54), (int) (y+h*0.37), (int) -(ex*0.82), (int) (w*0.06), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		//Glasses Arms & Bridge
		deadplayer.add(new SquareOld((int) (x+w*0.47), (int) (y+h*0.37), (int) -(ex*0.85), (int) (w*0.05), (int) (h*0.03), (int) (ex*0.015), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.35), (int) (y+h*0.38), (int) -(ex*0.84), (int) (w*0.02), (int) (h*0.2), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));
		deadplayer.add(new SquareOld((int) (x+w*0.62), (int) (y+h*0.38), (int) -(ex*0.84), (int) (w*0.02), (int) (h*0.2), (int) (ex*0.02), c.getLayer("Player"), sandals, null, c));

		//Range of ash colours - 100, 140
		Color dead;
		int maxD = 180;
		int minD = 140;
		int r;

		//Head
		for (double iZ=0; iZ<0.18; iZ+=0.06) {
			for (double iY=0; iY<0.25; iY+=0.05) {
				for (double iX=0; iX<0.25; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.37+iX)), (int) (y+h*(0.4+iY)), (int) -(ex*(0.75+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.06), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		//Shouldors
		for (double iZ=0; iZ<0.1; iZ+=0.05) {
			for (double iY=0; iY<0.2; iY+=0.05) {
				for (double iX=0; iX<0.7; iX+=0.07) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.15+iX)), (int) (y+h*(0.4+iY)), (int) -(ex*(0.63+iZ)), (int) (w*0.07), (int) (h*0.05), (int) (ex*0.05), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		//Chest
		for (double iZ=0; iZ<0.22; iZ+=0.055) {
			for (double iY=0; iY<0.2; iY+=0.05) {
				for (double iX=0; iX<0.4; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.3+iX)), (int) (y+h*(0.4+iY)), (int) -(ex*(0.41+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.055), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		//Arms
		for (double iZ=0; iZ<0.3; iZ+=0.05) {
			for (double iY=0; iY<0.1; iY+=0.05) {
				for (double iX=0; iX<0.1; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.17+iX)), (int) (y+h*(0.45+iY)), (int) -(ex*(0.33+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.05), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		for (double iZ=0; iZ<0.3; iZ+=0.05) {
			for (double iY=0; iY<0.1; iY+=0.05) {
				for (double iX=0; iX<0.1; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.73+iX)), (int) (y+h*(0.45+iY)), (int) -(ex*(0.33+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.05), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		//Legs
		for (double iZ=0; iZ<0.35; iZ+=0.05) {
			for (double iY=0; iY<0.2; iY+=0.05) {
				for (double iX=0; iX<0.15; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.3+iX)), (int) (y+h*(0.4+iY)), (int) -(ex*(0.05+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.05), c.getLayer("Player"), dead, null, c));
				}
			}
		}
		for (double iZ=0; iZ<0.35; iZ+=0.05) {
			for (double iY=0; iY<0.2; iY+=0.05) {
				for (double iX=0; iX<0.15; iX+=0.05) {
					r = (int) ((Math.random()*((maxD-minD)+1))+minD);
					dead = new Color(r, r, r);
					deadplayer.add(new SquareOld((int) (x+w*(0.55+iX)), (int) (y+h*(0.4+iY)), (int) -(ex*(0.05+iZ)), (int) (w*0.05), (int) (h*0.05), (int) (ex*0.05), c.getLayer("Player"), dead, null, c));
				}
			}
		}

		return deadplayer;
	}

	/**
	 * Useful random integer getter.
	 * 
	 * @param min - the minimum int to be returned
	 * @param max - the maximum int to be returned
	 * @return - the random int
	 */
	public int random(double min, double max){
		return (int) ((Math.random()*((max-min)+1))+min);
	}
}
