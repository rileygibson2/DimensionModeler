package objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Clockwise winding order;
 * 1....inf = bottom left most point..... bottom left most point -1
 * 
 * Last point will connect to first point.
 * 
 * @author thesmileyone
 */
public class RasterPolygon {
	
	public List<Point> points;
	
	public RasterPolygon() {
		points = new ArrayList<Point>();
	}
	
	public RasterPolygon(List<Point> points) {
		if (points==null||points.size()<=1) throw new Error();
		this.points = points;
	}
	
	public int getXMin() {
		Point l = points.get(0);
		for (Point p : points) if (p.x<l.x) l = p;
		return l.x;
	}
	
	public int getXMax() {
		Point r = points.get(0);
		for (Point p : points) if (p.x>r.x) r = p;
		return r.x;
	}
	
	public int getYMin() {
		Point t = points.get(0);
		for (Point p : points) if (p.y<t.y) t = p;
		return t.y;
	}
	
	public int getYMax() {
		Point b = points.get(0);
		for (Point p : points) if (p.y>b.y) b = p;
		return b.y;
	}
	
	public void add(Point p) {points.add(p);}
	public Point get(int i) {return points.get(i);}
	public int getX(int i) {return points.get(i).x;}
	public int getY(int i) {return points.get(i).y;}
	public int size() {return points.size();}
	
}
