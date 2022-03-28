package core;

public class Vec3 {
	//For a position 3D point
	public int x;
	public int y;
	public int z;
	//For a dimension 3D point
	public int w;
	public int h;
	public int ex;
	
	public Vec3() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
		this.h = 0;
		this.ex = 0;
	}
	
	public Vec3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = x;
		this.h = y;
		this.ex = z;
	}
	
	@Override
	public boolean equals(Object o) {
		Vec3 p = (Vec3) o;
		if (p.x==this.x&&p.y==this.y&&p.z==this.z) return true;
		return false;
	}
	
	@Override
	public String toString() {
		return x+", "+y+", "+z;
	}
}
