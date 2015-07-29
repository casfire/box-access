package com.casfire.box.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxIndex;

public final class BoxList<E extends BoundingBox3D> implements BoxIndex<E> {
	
	private final Collection<E> list;
	
	public BoxList() {
		this(new ArrayList<E>());
	}
	
	public BoxList(Collection<E> list) {
		this.list = list;
	}
	
	@Override
	public final Iterable<E> contain(Point3D p) {
		return list;
	}
	
	@Override
	public final Iterable<E> contain(BoundingBox3D box) {
		return list;
	}
	
	@Override
	public final Iterable<E> overlap(BoundingBox3D box) {
		return list;
	}
	
	@Override
	public final int size() {
		return list.size();
	}
	
	@Override
	public final void clear() {
		list.clear();
	}
	
	@Override
	public final boolean add(E e) {
		return list.add(e);
	}
	
	@Override
	public final Iterator<E> iterator() {
		return list.iterator();
	}
	
	@Override
	public final Spliterator<E> spliterator() {
		return list.spliterator();
	}
	
	@Override
	public final Stream<E> stream() {
		return list.stream();
	}
	
	@Override
	public final Stream<E> parallelStream() {
		return list.parallelStream();
	}
	
}
