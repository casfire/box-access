package com.casfire.box.list;

import java.util.ArrayList;
import java.util.Collections;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;

final class BoxTreeListRoot<E extends BoundingBox3D> implements BoxNode<E> {
	
	final ArrayList<BoxTreeListNode<E>> list;
	Point3D min, max;
	
	BoxTreeListRoot() {
		list = new ArrayList<BoxTreeListNode<E>>();
		min  = Point3D.POSITIVE_INFINITY;
		max  = Point3D.NEGATIVE_INFINITY;
	}
	
	@Override
	public final Point3D min() {
		return min;
	}
	
	@Override
	public Point3D max() {
		return max;
	}
	
	@Override
	public final Iterable<BoxNode<E>> children() {
		return Collections.unmodifiableList(list);
	}
	
	@Override
	public final int childrenCount() {
		return list.size();
	}
	
	@Override
	public final int height() {
		return 1;
	}
	
	@Override
	public final E entry() {
		return null;
	}
	
}
