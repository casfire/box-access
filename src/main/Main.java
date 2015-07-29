package main;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Random;

import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.grid.BoxGrid;
import com.casfire.box.grid.BoxGridTree;
import com.casfire.box.list.BoxList;
import com.casfire.box.rtree.BoxRStar;
import com.casfire.box.rtree.BoxRTree;
import com.casfire.box.util.BoxIndex;
import com.casfire.box.util.BoxTree;

public class Main {
	
	public static void main(String args[]) throws Exception {
		
		Point3D minSize = new Point3D(0,    0,    0   );
		Point3D maxSize = new Point3D(10,   10,   10  );
		Point3D minPos  = new Point3D(0,    0,    0   );
		Point3D maxPos  = new Point3D(1000, 1000, 1000);
		int entries   = 10000;
		int testPoint = 100000;
		int testRange = 10000;
		long seed = System.currentTimeMillis();
		
		Data data = Generator.scatter(new Random(seed), new Box3D(minPos, maxPos), new Box3D(minSize, maxSize), entries);
		//Data data = Generator.group(new Random(seed), new Box3D(minPos, maxPos), new Box3D(minSize, maxSize), entries);
		//Data data = Generator.grid(new Box3D(minPos, maxPos), new Point3D(20, 20, 20), new Point3D(-3));
		
		System.out.println("Entries: " + entries + " Point queries: " + testPoint + " Range queries: " + testRange);
		BoxIndex<Entry> index;
		
		index = new BoxList<Entry>();
		data.insert(index);
		System.out.println("Linear:");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		index = new BoxGrid<Entry>(data.bound, new Point3D(64, 64, 64));
		data.insert(index);
		System.out.println("Grid:");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		index = new BoxGridTree<Entry>(data.bound, 6, new Point3D(2, 2, 2));
		data.insert(index);
		System.out.println("Octree:");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		index = new BoxRTree<Entry>();
		data.insert(index);
		System.out.println("R tree (Overlap: " + overlap(index) + "):");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		data.reinsert(new Random(seed), index, data.length * 4, data.length / 100);
		System.out.println("Optimized R tree (Overlap: " + overlap(index) + "):");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		index = new BoxRStar<Entry>();
		data.insert(index);
		System.out.println("R* tree (Overlap: " + overlap(index) + "):");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		data.reinsert(new Random(seed), index, data.length * 4, data.length / 100);
		System.out.println("Optimized R* tree (Overlap: " + overlap(index) + "):");
		measure(new Tester(index, data), seed, testPoint, testRange);
		
		System.out.println("Writing optimized 2D R* tree to tree.html");
		outputHTML("tree.html");
		
		System.out.println("Done.");
		
	}
	
	private static void outputHTML(String filename) throws FileNotFoundException {
		Point3D minSize = new Point3D(0,    0,    0   );
		Point3D maxSize = new Point3D(10,   1000, 10  );
		Point3D minPos  = new Point3D(0,    0,    0   );
		Point3D maxPos  = new Point3D(1000, 1000, 1000);
		Data data = Generator.scatter(new Random(), new Box3D(minPos, maxPos), new Box3D(minSize, maxSize), 1000);
		BoxTree<Entry> tree = new BoxRStar<Entry>();
		data.insert(tree);
		data.reinsert(new Random(), tree, data.length * 4, data.length / 100);
		new Evaluator(tree).exportHTML(new PrintStream(filename));
	}
	
	private static String overlap(BoxIndex<Entry> tree) {
		Evaluator eval = new Evaluator((BoxTree<Entry>) tree);
		return String.format(Locale.US, "%7.4f%%", eval.overlap() * 100);
	}
	
	private static void measure(Tester tester, long seed, int point, int range) {
		tester.test(new Random(), tester.data.length * 2);
		tester.measurePointParallel(new Random(seed), point * 2);
		System.out.println("Point parallel:   " + tester.measurePointParallel(new Random(seed), point));
		tester.measurePoint(new Random(seed), point * 2);
		System.out.println("Point sequential: " + tester.measurePoint(new Random(seed), point));
		tester.measureRangeParallel(new Random(seed), range * 2);
		System.out.println("Range parallel:   " + tester.measureRangeParallel(new Random(seed), range));
		tester.measureRange(new Random(seed), range * 2);
		System.out.println("Range sequential: " + tester.measureRange(new Random(seed), range));
	}
	
}
