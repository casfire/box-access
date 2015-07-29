package com.casfire.box.grid;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.list.BoxList;
import com.casfire.box.util.BoxIndex;

public final class BoxGridTree<E extends BoundingBox3D> implements BoxIndex<E> {
	
	private final BoxGrid<E> grid;
	
	public BoxGridTree(BoundingBox3D bound, int depth) {
		this(bound, depth, new Point3D(2), b -> new BoxList<E>());
	}
	
	public BoxGridTree(BoundingBox3D bound, int depth, Function<Box3D, BoxIndex<E>> nodes) {
		this(bound, depth, new Point3D(2), nodes);
	}
	
	public BoxGridTree(BoundingBox3D bound, int depth, Point3D degree) {
		this(bound, depth, degree, b -> new BoxList<E>());
	}
	
	public BoxGridTree(BoundingBox3D bound, int depth, Point3D degree, Function<Box3D, BoxIndex<E>> nodes) {
		Function<Box3D, BoxIndex<E>> s = b -> new BoxGrid<E>(b, degree, nodes, nodes.apply(b));
		while (depth-- > 0) {
			final Function<Box3D, BoxIndex<E>> f = s;
			s = b -> new BoxGrid<E>(b, degree, f, nodes.apply(b));
		}
		grid = (BoxGrid<E>) s.apply(bound.toBox3D());
	}
	
	@Override
	public final Iterable<E> contain(Point3D p) {
		return grid.contain(p);
	}
	
	@Override
	public final Iterable<E> contain(BoundingBox3D box) {
		return grid.contain(box);
	}
	
	@Override
	public final Iterable<E> overlap(BoundingBox3D box) {
		return grid.overlap(box);
	}
	
	@Override
	public final int size() {
		return grid.size();
	}
	
	@Override
	public final void clear() {
		grid.clear();
	}
	
	@Override
	public final boolean add(E e) {
		return grid.add(e);
	}
	
	@Override
	public final Iterator<E> iterator() {
		return grid.iterator();
	}
	
	@Override
	public final Spliterator<E> spliterator() {
		return grid.spliterator();
	}
	
	@Override
	public final Stream<E> stream() {
		return grid.stream();
	}
	
	@Override
	public final Stream<E> parallelStream() {
		return grid.parallelStream();
	}
	
}
