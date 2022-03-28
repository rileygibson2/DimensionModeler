package objects;

import java.awt.Color;
import java.util.ArrayList;

import core.Layer;
import core.Render;
import core.Vec3;
import textures.Texture;

public class Circle extends ObjectRoot {

	int res;

	public Circle(Vec3 pos, int rad, int gran, Layer layer, Color col, Texture texture, Render c) {
		super(pos, new Vec3(rad, gran, 0), layer, texture, col, c);
		this.res = 10;
		buildPolys();
	}

	public void buildPolys() {
		/*
		 * Make verticies in circle shape, with indexes:
		 * 	Make circular rings of vertices
		 * 	Adjust the radius of these rings based on another circle
		 * Make polygons out of vertexes
		 * Do the last wrap around and attach to top and bottom points
		 * Polygons will be different sizes
		 */

		ArrayList<ArrayList<Vec3>> verts1 = new ArrayList<>();
		ArrayList<ArrayList<Vec3>> verts2 = new ArrayList<>();
		polys = new ArrayList<PlanarRoot>();
		ArrayList<Vec3> temp;
		Polygon tempPoly1;

		int rad = this.w;
		int gran = this.h;
		int zInc = rad/gran;
		Vec3 top = new Vec3(this.x, this.y, -rad+this.z);
		Vec3 bottom = new Vec3(this.x, this.y, rad+this.z);
		Vec3 v1, v2, v3;

		//Find verticies

		for (int z=-rad+zInc; z<=rad-zInc; z+=zInc) {
			int ringRad = (int) Math.sqrt(Math.pow(rad, 2)-Math.pow((z), 2));
			double ringInc = ringRad/(double) gran;

			//One half
			temp = new ArrayList<Vec3>();
			for (double x=-ringRad; x<=ringRad+ringInc; x+=ringInc) {
				double y = Math.sqrt(Math.pow(ringRad, 2)-Math.pow((x), 2));
				temp.add(new Vec3((int) x+this.x, (int) y+this.y, z+this.z));
			}
			verts1.add(temp);
			//Other half
			temp = new ArrayList<Vec3>();
			for (double x=ringRad; x>=-ringRad-ringInc; x-=ringInc) {
				double y = Math.sqrt(Math.pow(ringRad, 2)-Math.pow((x), 2));
				temp.add(new Vec3((int) x+this.x, (int) -y+this.y, z+this.z));
			}
			verts2.add(temp);
		}

		//Build polygons for both sides of circle, taking care of winding order
		ArrayList<ArrayList<Vec3>> verts;
		for (int i=0; i<2; i++) {
			if (i==1) verts = verts1;
			else verts = verts2;
			
			for (int y=0; y<verts.size()-1; y++) {
				for (int x=0; x<verts.get(0).size()-1; x++) {
					//First tri
					v1 = verts.get(y).get(x);
					v2 = verts.get(y+1).get(x+1);
					v3 = verts.get(y).get(x+1);
					polys.add(new Polygon(v1, v2, v3, layer, col, c));
					//Second tri
					v1 = verts.get(y).get(x);
					v2 = verts.get(y+1).get(x);
					v3 = verts.get(y+1).get(x+1);
					polys.add(new Polygon(v1, v2, v3, layer, col, c));
				}
			}

			//Connect first band to top point
			v3 = top;
			for (int x=0; x<verts.get(0).size()-1; x++) {
				v1 = verts.get(0).get(x);
				v2 = verts.get(0).get(x+1);
				polys.add(new Polygon(v1, v2, v3, layer, col, c));
			}
			

			//Connect last band to bottom point
			v2 = bottom;
			for (int x=0; x<verts.get(verts.size()-1).size()-1; x++) {
				v1 = verts.get(verts.size()-1).get(x);
				v3 = verts.get(verts.size()-1).get(x+1);
				polys.add(new Polygon(v1, v2, v3, layer, col, c));
			}
			


			c.polyPool.addAll(polys);
		}
	}
	
	/*//Wrap around bottom band
	v1 = verts.get(verts.size()-1).get(verts.get(verts.size()-1).size()-1);
	v3 = verts.get(verts.size()-1).get(0);
	polys.add(new Polygon(v1, v2, v3, layer, col, c));
	//Wrap around top band
	v1 = verts.get(0).get(verts.get(0).size()-1);
	v3 = verts.get(0).get(0);
	polys.add(new Polygon(v1, v2, v3, layer, col, c));*/

	@Override
	public void rotate(Vec3 origin, int angle) {
		for (PlanarRoot p : polys) p.rotate(origin, angle);
	}

	@Override
	public void spin(int angle) {
		for (PlanarRoot p : polys) {
			p.rotate(new Vec3(x+w/2, y+h/2, z+ex/2), angle);
		}
	}
}
