package com.casfire.box.grid;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxGridIterator<E extends BoundingBox3D> implements Iterator<E> {
	
	private final Iterator<Iterable<E>> iter;
	private final BoxGrid<E> grid;
	private Iterator<E> i, p;
	
	BoxGridIterator(BoxGrid<E> grid, List<Iterable<E>> iter) {
		this.iter = iter.iterator();
		this.grid = grid;
		p = null;
		i = this.iter.next().iterator();
		advance();
	}
	
	private final void advance() {
		while (!i.hasNext() && iter.hasNext()) {
			i = iter.next().iterator();
		}
	}
	
	@Override
	public final boolean hasNext() {
		return i.hasNext();
	}
	
	@Override
	public final E next() {
		final E next = (p = i).next();
		advance();
		return next;
	}
	
	@Override
	public final void remove() {
		if (p == null) throw new NoSuchElementException();
		p.remove();
		grid.size--;
	}
	
}
