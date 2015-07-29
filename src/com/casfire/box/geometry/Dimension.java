package com.casfire.box.geometry;

import java.util.Comparator;

public enum Dimension {
	
	X (
			(Point3D       a, Point3D       b) -> Double.compare(a.x, b.x),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.min().x, b.min().x),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.max().x, b.max().x)
	),
	
	Y (
			(Point3D       a, Point3D       b) -> Double.compare(a.y, b.y),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.min().y, b.min().y),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.max().y, b.max().y)
	),
	
	Z (
			(Point3D       a, Point3D       b) -> Double.compare(a.z, b.z),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.min().z, b.min().z),
			(BoundingBox3D a, BoundingBox3D b) -> Double.compare(a.max().z, b.max().z)
	);
	
	public final Comparator<Point3D> compare;
	public final Comparator<BoundingBox3D> compare_min;
	public final Comparator<BoundingBox3D> compare_max;
	
	private Dimension(
			Comparator<Point3D> cmp,
			Comparator<BoundingBox3D> min,
			Comparator<BoundingBox3D> max
	) {
		compare     = cmp;
		compare_min = min;
		compare_max = max;
	}
	
}
