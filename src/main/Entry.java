package main;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;

public class Entry implements BoundingBox3D {
	
	public final int ID;
	public final Point3D min, max;
	
	public Entry(int ID, Point3D min, Point3D max) {
		this.ID  = ID;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Point3D min() {
		return min;
	}
	
	@Override
	public Point3D max() {
		return max;
	}
	
	@Override
	public String toString() {
		return "Entry[" + ID + "][" + min + ", " + max + "]";
	}
	
}
