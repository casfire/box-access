package com.casfire.box.geometry;

import java.util.Collection;
import java.util.Iterator;

public final class Box3D implements BoundingBox3D {
	
	public final Point3D min, max;
	
	public Box3D(Point3D min, Point3D max) {
		if (min == null || max == null) {
			throw new NullPointerException();
		}
		this.min = min;
		this.max = max;
	}
	
	@Override
	public final Point3D min() {
		return min;
	}
	
	@Override
	public final Point3D max() {
		return max;
	}
	
	@Override
	public final String toString() {
		return "Box3D[" + min + ", " + max + "]";
	}
	
	public static final Box3D bound(Point3D ... c) {
		Point3D a = Point3D.POSITIVE_INFINITY, b = Point3D.NEGATIVE_INFINITY;
		for (Point3D p : c) {
			a = Point3D.min(a, p);
			b = Point3D.max(b, p);
		}
		return new Box3D(a, b);
	}
	
	public static final Box3D bound(BoundingBox3D ... c) {
		Point3D a = Point3D.POSITIVE_INFINITY, b = Point3D.NEGATIVE_INFINITY;
		for (BoundingBox3D box : c) {
			a = Point3D.min(a, box.min());
			b = Point3D.max(b, box.max());
		}
		return new Box3D(a, b);
	}
	
	public static final Box3D bound(Iterator<? extends BoundingBox3D> c) {
		Point3D a = Point3D.POSITIVE_INFINITY, b = Point3D.NEGATIVE_INFINITY;
		while (c.hasNext()) {
			BoundingBox3D box = c.next();
			a = Point3D.min(a, box.min());
			b = Point3D.max(b, box.max());
		}
		return new Box3D(a, b);
	}
	
	public static final Box3D bound(Iterator<? extends BoundingBox3D> c, int max) {
		Point3D a = Point3D.POSITIVE_INFINITY, b = Point3D.NEGATIVE_INFINITY;
		for (int i = 0; i < max && c.hasNext(); i++) {
			BoundingBox3D box = c.next();
			a = Point3D.min(a, box.min());
			b = Point3D.max(b, box.max());
		}
		return new Box3D(a, b);
	}
	
	public static final Box3D bound(Collection<? extends BoundingBox3D> c) {
		return bound(c.iterator(), c.size());
	}
	
	// Intersection (resulting volume can be negative)
	public static final Box3D overlap(BoundingBox3D a, BoundingBox3D b) {
		Point3D A = Point3D.max(a.min(), b.min());
		Point3D B = Point3D.min(a.max(), b.max());
		return new Box3D(A, B);
	}
	
}
