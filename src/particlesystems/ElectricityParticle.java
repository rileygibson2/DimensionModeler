package particlesystems;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import core.Vec3;

public class ElectricityParticle extends Particle {

	int width = 10;
	int adj = 0; //Adjustment for width based on big moves
	int difZ = 0; //Difference between origin z and z;
	int difX = 0; //Initial difference between origin x and x;
	boolean hasBranch;
	boolean isOnBranch;
	List<ElectricityParticle> branch;

	public ElectricityParticle(ParticleSystem parent, int creationTime, int ttl, int difX) {
		super(parent, creationTime, ttl, new Vec3(), 0, 0, 0, null, 0, false);
		this.difX = difX;
		this.isOnBranch = false;
	}

	@Override
	public void increase(int cTime) {
		//Randomise movement
		if (random(1, 5)==1) difZ += 1;
		else if (random(1, 5)==1) difZ -= 1;

		//Randomly change acceptable width
		if (random(1, 10)==1) width = random(0, 50);
		if (difZ<adj-width) difZ = adj-width;
		if (difZ>adj+width) difZ = adj+width;
		
		//Update branches adj value
		if (hasBranch) for (ElectricityParticle p : branch) p.adj = adj+p.difZ;
		
		pos.z = parent.origin.z+difZ;
		pos.x = parent.origin.x+difX;
		pos.y = parent.origin.y;
	}
	
	public void branch() {
		hasBranch = true;
		branch = new ArrayList<ElectricityParticle>();
		int z = 0;
		int bLength = random(50, 80); //The reach of the new branch
		int bAngle = random(-4, 4); //Angle of first part of branch
		if (bAngle==0) bAngle = 1;
		int bAngleCutoff = (int) (bLength*(random(2, 6)*0.1)); //Point down branch where cutoff happens
		
		for (int i=0; i<bLength; i+=2) {
			if (i<bAngleCutoff) z = bAngle*i;
			else z = branch.get(branch.size()-1).difZ;
			
			branch.add(new ElectricityParticle(parent, parent.time, parent.length, difX+i));
			branch.get(branch.size()-1).adj = adj+z;
			branch.get(branch.size()-1).difZ = difZ+z;
			branch.get(branch.size()-1).isOnBranch = true;
		}
	}
	
	public void increaseBranch() {
		for (Particle p : branch) p.increase(adj);
	}
	
	public void killBranch() {
		hasBranch = false;
		branch = new ArrayList<ElectricityParticle>();
	}
	
	

	@Override
	public void draw(Graphics2D g) {}
}
