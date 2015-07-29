package com.casfire.box.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;

public abstract class BoxTree<E extends BoundingBox3D> implements BoxIndex<E> {
	
	public abstract BoxNode<E> root();
	public abstract boolean add(E e);
	public abstract void clear();
	public abstract int size();
	
	protected abstract void delete(BoxNode<E> from);
	
	public int height() {
		return root().height();
	}
	
	public Iterable<E> search(Predicate<BoxNode<E>> decend) {
		return new BoxTreeIterable<E>(this, root(), decend);
	}
	
	@Override
	public Iterable<E> contain(Point3D p) {
		return search(n -> n.contains(p));
	}
	
	@Override
	public Iterable<E> contain(BoundingBox3D box) {
		return search(n -> n.overlaps(box));
	}
	
	@Override
	public Iterable<E> overlap(BoundingBox3D box) {
		return search(n -> n.overlaps(box));
	}
	
	@Override
	public Iterator<E> iterator() {
		return new BoxTreeIterator<E>(this, root(), n -> true);
	}
	
	@Override
	public Spliterator<E> spliterator() {
		return new BoxTreeSpliterator<E>(root(), n -> true);
	}
	
	@Override
	public Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	@Override
	public Stream<E> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}
	
}
