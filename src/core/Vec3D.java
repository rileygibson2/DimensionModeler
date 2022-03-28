package core;

public class Vec3D {
	//For a position 3D podouble
	public double x;
	public double y;
	public double z;
	//For a dimension 3D podouble
	public double w;
	public double h;
	public double ex;
	
	public Vec3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
		this.h = 0;
		this.ex = 0;
	}
	
	public Vec3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = x;
		this.h = y;
		this.ex = z;
	}
	
	@Override
	public boolean equals(Object o) {
		Vec3D p = (Vec3D) o;
		if (p.x==this.x&&p.y==this.y&&p.z==this.z) return true;
		return false;
	}
	
	@Override
	public String toString() {
		return x+", "+y+", "+z;
	}
}
