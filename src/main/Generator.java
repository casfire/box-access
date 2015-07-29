package main;

import java.util.Random;

import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;

public class Generator {
	
	private static final Point3D TWO = new Point3D(2);
	
	public static Data group(Random random, Box3D canvas, Box3D size, int count) {
		Entry[] e = new Entry[count];
		for (int i = 0; i < count; i++) {
			Box3D b = randGauss(random, canvas, size);
			e[i] = new Entry(i, b.min, b.max);
		}
		return new Data(e);
	}
	
	public static Data scatter(Random random, Box3D canvas, Box3D size, int count) {
		Entry[] e = new Entry[count];
		for (int i = 0; i < count; i++) {
			Box3D b = rand(random, canvas, size);
			e[i] = new Entry(i, b.min, b.max);
		}
		return new Data(e);
	}
	
	public static Data grid(Box3D canvas, Point3D count, Point3D overlap) {
		int cx = (int) count.x, cy = (int) count.y, cz = (int) count.z, i = 0;
		Point3D cell = Point3D.div (
				Point3D.sub(canvas.max, canvas.min),
				new Point3D(cx, cy, cz)
		);
		Entry[] e = new Entry[cx * cy * cz];
		for (int x = 0; x < cx; x++)
		for (int y = 0; y < cy; y++)
		for (int z = 0; z < cz; z++) {
			Point3D min = Point3D.mul(new Point3D(x, y, z), cell);
			Point3D max = Point3D.add(min, cell);
			e[i] = new Entry(i, Point3D.sub(min, overlap), Point3D.add(max, overlap));
			i++;
		}
		return new Data(e);
	}
	
	public static double rand(Random random, double min, double max) {
		return min + random.nextDouble() * (max - min);
	}
	
	public static double randGuass(Random random, double min, double max) {
		return (max + min) / 2 + random.nextGaussian() * (max - min) / 8;
	}
	
	public static Point3D rand(Random random, Point3D min, Point3D max) {
		return new Point3D(
				rand(random, min.x, max.x),
				rand(random, min.y, max.y),
				rand(random, min.z, max.z)
		);
	}
	
	public static Point3D randGuass(Random random, Point3D min, Point3D max) {
		return new Point3D(
				randGuass(random, min.x, max.x),
				randGuass(random, min.y, max.y),
				randGuass(random, min.z, max.z)
		);
	}
	
	public static Box3D randGauss(Random random, Box3D canvas, Box3D size) {
		Point3D s = rand(random, size.min, size.max);
		Point3D c = randGuass(random, canvas.min, canvas.max);
		Point3D a = Point3D.sub(c, Point3D.div(s, TWO));
		Point3D b = Point3D.add(c, Point3D.div(s, TWO));
		return new Box3D(a, b);
	}
	
	public static Box3D rand(Random random, Box3D canvas, Box3D size) {
		Point3D s = rand(random, size.min, size.max);
		Point3D a = rand(random, canvas.min, Point3D.sub(canvas.max, s));
		return new Box3D(a, Point3D.add(a, s));
	}
	
	public static Box3D rand(Random random, Box3D canvas) {
		Point3D a = rand(random, canvas.min, canvas.max);
		Point3D b = rand(random, canvas.min, canvas.max);
		return new Box3D(Point3D.min(a, b), Point3D.max(a, b));
	}
	
}
