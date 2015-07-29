package com.casfire.box.geometry;

public interface BoundingBox3D {
	
	public Point3D min();
	public Point3D max();
	
	public default double sizeX() { return max().x - min().x; }
	public default double sizeY() { return max().y - min().y; }
	public default double sizeZ() { return max().z - min().z; }
	
	public default Point3D center() {
		return new Point3D(
			(min().x + max().x) / 2,
			(min().y + max().y) / 2,
			(min().z + max().z) / 2
		);
	}
	
	public default Box3D toBox3D() {
		return new Box3D(min(), max());
	}
	
	public default double volume() { return sizeX() * sizeY() * sizeZ(); }
	public default double margin() { return sizeX() + sizeY() + sizeZ(); }
	
	public default boolean containsX(double x) { return x >= min().x && x <= max().x; }
	public default boolean containsY(double y) { return y >= min().y && y <= max().y; }
	public default boolean containsZ(double z) { return z >= min().z && z <= max().z; }
	public default boolean containsX(BoundingBox3D box) { return box.min().x >= min().x && box.max().x <= max().x; }
	public default boolean containsY(BoundingBox3D box) { return box.min().y >= min().y && box.max().y <= max().y; }
	public default boolean containsZ(BoundingBox3D box) { return box.min().z >= min().z && box.max().z <= max().z; }
	public default boolean overlapsX(BoundingBox3D box) { return box.min().x <= max().x && box.max().x >= min().x; }
	public default boolean overlapsY(BoundingBox3D box) { return box.min().y <= max().y && box.max().y >= min().y; }
	public default boolean overlapsZ(BoundingBox3D box) { return box.min().z <= max().z && box.max().z >= min().z; }
	public default boolean overlaps (BoundingBox3D box) { return overlapsX(box) && overlapsY(box) && overlapsZ(box); }
	public default boolean contains (BoundingBox3D box) { return containsX(box) && containsY(box) && containsZ(box); }
	public default boolean contains (Point3D         p) { return containsX(p.x) && containsY(p.y) && containsZ(p.z); }
	
}
