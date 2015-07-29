package com.casfire.box.util;

import com.casfire.box.geometry.BoundingBox3D;

public interface BoxNode<E extends BoundingBox3D> extends BoundingBox3D {
	
	public Iterable<BoxNode<E>> children();
	public int childrenCount();
	public int height();
	public E entry();
	
}
