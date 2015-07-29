package com.casfire.box.list;

import java.util.Collections;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;

final class BoxTreeListNode<E extends BoundingBox3D> implements BoxNode<E> {
	
	private final E entry;
	
	public BoxTreeListNode(E e) {
		entry = e;
	}
	
	@Override
	public final Point3D min() {
		return entry.min();
	}
	
	@Override
	public final Point3D max() {
		return entry.max();
	}
	
	@Override
	public final Iterable<BoxNode<E>> children() {
		return Collections.emptyList();
	}
	
	@Override
	public final int childrenCount() {
		return 0;
	}
	
	@Override
	public final int height() {
		return 0;
	}
	
	@Override
	public final E entry() {
		return entry;
	}
	
}
