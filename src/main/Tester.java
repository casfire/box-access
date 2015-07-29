package main;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxIndex;

public class Tester {
	
	public final BoxIndex<Entry> index;
	public final Data data;
	
	public Tester(BoxIndex<Entry> index, Data data) {
		this.index = index;
		this.data  = data;
	}
	
	private void test(Iterable<Entry> query, Predicate<Entry> p, Consumer<String> err) {
		Set<Entry> h = new HashSet<Entry>(); int size = 0;
		for (Entry e :  data) if (p.test(e) &&  h.add   (e)) size++;
		for (Entry e : query) if (p.test(e) && !h.remove(e)) err.accept("Unknown " + e);
		if (!h.isEmpty()) err.accept("Missed " + h.size() + "/" + size + " entries");
	}
	
	private void test0(Random random) {
		Point3D p = Generator.rand(random, data.bound.min, data.bound.max);
		test(index.contain(p), e -> e.contains(p), err -> {
			throw new IllegalStateException(err + " for contains " + p);
		});
	}
	
	private void test1(Random random) {
		Box3D box = Generator.rand(random, data.bound);
		test(index.contain(box), e -> e.contains(box), err -> {
			throw new IllegalStateException(err + " for contains " + box);
		});
	}
	
	private void test2(Random random) {
		Box3D box = Generator.rand(random, data.bound);
		test(index.overlap(box), e -> e.overlaps(box), err -> {
			throw new IllegalStateException(err + " for overlaps " + box);
		});
	}
	
	public void test(Random random, int count) {
		for (int c = 0; c < count; c++) {
			switch (random.nextInt(3)) {
				default:
				case 0: test0(random); break;
				case 1: test1(random); break;
				case 2: test2(random); break;
			}
		}
	}
	
	private Iterable<Entry> measure(Result res, Supplier<Iterable<Entry>> q) {
		long start = System.nanoTime();
		Iterable<Entry> query = q.get();
		res.build += System.nanoTime() - start;
		return query;
	}
	
	private void measure(Result res, Supplier<Iterable<Entry>> q, Predicate<Entry> p) {
		Iterable<Entry> query = measure(res, q);
		long start = System.nanoTime();
		for (Entry e : query) {
			if (p.test(e)) res.hits++;
			res.iter++;
		}
		res.work += System.nanoTime() - start;
		res.size += data.length;
	}
	
	private void measureParallel(Result res, Supplier<Iterable<Entry>> q, Predicate<Entry> p) {
		Iterable<Entry> query = measure(res, q);
		long start = System.nanoTime();
		Spliterator<Entry> split = query.spliterator();
		long size = split.getExactSizeIfKnown();
		if (size < 0) {
			size = StreamSupport.stream(split, true).count();
			split = query.spliterator();
		}
		res.iter += size;
		res.hits += StreamSupport.stream(split, true).filter(p).count();
		res.work += System.nanoTime() - start;
		res.size += data.length;
	}
	
	public Result measurePoint(Random random, int count) {
		Result res = new Result();
		for (int c = 0; c < count; c++) {
			Point3D p = Generator.rand(random, data.bound.min, data.bound.max);
			measure(res, () -> index.contain(p), e -> e.contains(p));
		}
		return res;
	}
	
	public Result measurePointParallel(Random random, int count) {
		Result res = new Result();
		for (int c = 0; c < count; c++) {
			Point3D p = Generator.rand(random, data.bound.min, data.bound.max);
			measureParallel(res, () -> index.contain(p), e -> e.contains(p));
		}
		return res;
	}
	
	public Result measureRange(Random random, int count) {
		Result res = new Result();
		for (int c = 0; c < count; c++) {
			Box3D box = Generator.rand(random, data.bound);
			measure(res, () -> index.contain(box), e -> e.contains(box));
			measure(res, () -> index.overlap(box), e -> e.overlaps(box));
		}
		return res;
	}
	
	public Result measureRangeParallel(Random random, int count) {
		Result res = new Result();
		for (int c = 0; c < count; c++) {
			Box3D box = Generator.rand(random, data.bound);
			measureParallel(res, () -> index.contain(box), e -> e.contains(box));
			measureParallel(res, () -> index.overlap(box), e -> e.overlaps(box));
		}
		return res;
	}
	
}
