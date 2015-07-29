package com.casfire.box.rtree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;

final class Node<E extends BoundingBox3D> implements BoxNode<E> {
	
	final List<Node<E>> children;
	final int height;
	final E entry;
	
	Point3D min, max;
	Node<E> parent;
	
	Node(Node<E> parent, int height) {
		this(Point3D.POSITIVE_INFINITY, Point3D.NEGATIVE_INFINITY, parent, height);
	}
	
	Node(Point3D min, Point3D max, Node<E> parent, int height) {
		children    = new ArrayList<Node<E>>();
		this.height = height;
		this.entry  = null;
		this.min    = min;
		this.max    = max;
		this.parent = parent;
	}
	
	Node(E e, Node<E> parent) {
		children    = Collections.emptyList();
		this.height = 0;
		this.entry  = e;
		this.min    = e.min();
		this.max    = e.max();
		this.parent = parent;
	}
	
	@Override
	public final Point3D min() {
		return min;
	}
	
	@Override
	public final Point3D max() {
		return max;
	}
	
	@Override
	public final Iterable<BoxNode<E>> children() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public final int childrenCount() {
		return children.size();
	}
	
	@Override
	public final int height() {
		return height;
	}
	
	@Override
	public final E entry() {
		return entry;
	}
	
	@Override
	public final String toString() {
		if (entry == null) {
			return "Node[" + min + ", " + max + "]";
		} else {
			return "Node$" + entry;
		}
	}
	
}
