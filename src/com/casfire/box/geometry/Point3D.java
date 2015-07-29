package com.casfire.box.geometry;

public final class Point3D {
	
	public final double x, y, z;
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(double v) {
		x = y = z = v;
	}
	
	@Override
	public final int hashCode() {
		final int prime = 31;
		final long a = Double.doubleToLongBits(x); final int e = (int) (a ^ (a >>> 32));
		final long b = Double.doubleToLongBits(y); final int f = (int) (b ^ (b >>> 32));
		final long c = Double.doubleToLongBits(z); final int g = (int) (c ^ (c >>> 32));
		return prime * (prime * (prime + e) + f) + g;
	}
	
	@Override
	public final boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final Point3D p = (Point3D) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(p.x)) return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(p.y)) return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(p.z)) return false;
		return true;
	}
	
	@Override
	public final String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
	
	public static final Point3D NEGATIVE_INFINITY = new Point3D(Double.NEGATIVE_INFINITY);
	public static final Point3D POSITIVE_INFINITY = new Point3D(Double.POSITIVE_INFINITY);
	
	public static final Point3D min(Point3D a, Point3D b) {
		return new Point3D(
			Math.min(a.x, b.x),
			Math.min(a.y, b.y),
			Math.min(a.z, b.z)
		);
	}
	
	public static final Point3D max(Point3D a, Point3D b) {
		return new Point3D(
			Math.max(a.x, b.x),
			Math.max(a.y, b.y),
			Math.max(a.z, b.z)
		);
	}
	
	public static final Point3D floor(Point3D a) {
		return new Point3D(
			Math.floor(a.x),
			Math.floor(a.y),
			Math.floor(a.z)
		);
	}
	
	public static final Point3D round(Point3D a) {
		return new Point3D(
			Math.round(a.x),
			Math.round(a.y),
			Math.round(a.z)
		);
	}
	
	public static final double distance(Point3D a, Point3D b) {
		double x = a.x - b.x;
		double y = a.y - b.y;
		double z = a.z - b.z;
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	public static final double distanceSqr(Point3D a, Point3D b) {
		double x = a.x - b.x;
		double y = a.y - b.y;
		double z = a.z - b.z;
		return x*x + y*y + z*z;
	}
	
	public static final Point3D sub(Point3D a, Point3D b) {
		return new Point3D(a.x - b.x, a.y - b.y, a.z - b.z);
	}
	
	public static final Point3D add(Point3D a, Point3D b) {
		return new Point3D(a.x + b.x, a.y + b.y, a.z + b.z);
	}
	
	public static final Point3D div(Point3D a, Point3D b) {
		return new Point3D(a.x / b.x, a.y / b.y, a.z / b.z);
	}
	
	public static final Point3D mul(Point3D a, Point3D b) {
		return new Point3D(a.x * b.x, a.y * b.y, a.z * b.z);
	}
	
	public static final Point3D mod(Point3D a, Point3D b) {
		return new Point3D(a.x % b.x, a.y % b.y, a.z % b.z);
	}
	
}
