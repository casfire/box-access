package com.casfire.box.util;

import java.util.Collection;
import java.util.Iterator;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;

public interface BoxIndex<E extends BoundingBox3D> extends Collection<E> {
	
	// Return a minimum possible query superset
	public Iterable<E> contain(Point3D p);
	public Iterable<E> contain(BoundingBox3D box);
	public Iterable<E> overlap(BoundingBox3D box);
	
	public int size();
	public void clear();
	public boolean add(E e);
	public Iterator<E> iterator();
	
	public default boolean overlaps(BoundingBox3D box) {
		for (E e : overlap(box)) if (e.overlaps(box)) return true;
		return false;
	}
	
	@Override
	public default boolean contains(Object o) {
		if (o instanceof Point3D) {
			final Point3D p = (Point3D) o;
			for (E e : contain(p)) if (e.contains(p)) return true;
			return false;
		} else if (o instanceof BoundingBox3D) {
			final BoundingBox3D b = (BoundingBox3D) o;
			for (E e : contain(b)) if (e.contains(b)) return true;
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public default boolean remove(Object o) {
		if (!(o instanceof BoundingBox3D)) return false;
		Iterator<E> i = contain((BoundingBox3D) o).iterator();
		while (i.hasNext()) if (i.next().equals(o)) {
			i.remove();
			return true;
		}
		return false;
	}
	
	@Override
	public default boolean addAll(Collection<? extends E> c) {
		boolean change = false;
		for (E e : c) if (add(e)) change = true;
		return change;
	}
	
	@Override
	public default boolean containsAll(Collection<?> c) {
		for (Object o : c) if (!contains(o)) return false;
		return true;
	}
	
	@Override
	public default boolean isEmpty() {
		return size() == 0;
	}
	
	@Override
	public default boolean removeAll(Collection<?> c) {
		boolean change = false;
		for (Object o : c) if (remove(o)) change = true;
		return change;
	}
	
	@Override
	public default boolean retainAll(Collection<?> c) {
		boolean change = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				change = true;
			}
		}
		return change;
	}
	
	@Override
	public default Object[] toArray() {
		Object[] a = new Object[size()];
		Iterator<E> it = iterator();
		for (int i = 0; i < a.length; i++) {
			a[i] = it.next();
		}
		return a;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public default <T> T[] toArray(T[] a) {
		int size = size();
		Iterator<E> it = iterator();
		for (int i = 0; i < size; i++) {
			a[i] = (T) it.next();
		}
		return a;
	}
	
}
