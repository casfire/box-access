package com.casfire.box.grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxGridIterable<E extends BoundingBox3D> implements Iterable<E> {
	
	private final ArrayList<Iterable<E>> iter;
	private final BoxGrid<E> grid;
	private int length;
	
	BoxGridIterable(BoxGrid<E> grid) {
		this.iter = new ArrayList<Iterable<E>>();
		this.grid = grid;
		length = 0;
	}
	
	final void add(Iterable<E> i) {
		if (i instanceof BoxGridIterable) {
			for (Iterable<E> c : ((BoxGridIterable<E>) i).iter) {
				add(c);
			}
		} else if (i.spliterator().getExactSizeIfKnown() != 0) {
			iter.add(i);
			length++;
		}
	}
	
	@Override
	public final Iterator<E> iterator() {
		if (length == 0) {
			return Collections.<E>emptyList().iterator();
		} else {
			return new BoxGridIterator<E>(grid, iter);
		}
	}
	
	@Override
	public final Spliterator<E> spliterator() {
		if (length == 0) {
			return Collections.<E>emptyList().spliterator();
		} else if (length == 1) {
			return iter.get(0).spliterator();
		} else {
			return new BoxGridSpliterator<E>(iter);
		}
	}
	
}
