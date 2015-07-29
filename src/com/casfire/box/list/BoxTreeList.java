package com.casfire.box.list;

import java.util.NoSuchElementException;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;
import com.casfire.box.util.BoxTree;

public final class BoxTreeList<E extends BoundingBox3D> extends BoxTree<E> {
	
	private final BoxTreeListRoot<E> root;
	
	public BoxTreeList() {
		root = new BoxTreeListRoot<E>();
	}
	
	@Override
	public final BoxNode<E> root() {
		return root;
	}
	
	@Override
	public final boolean add(E e) {
		if (root.list.add(new BoxTreeListNode<E>(e))) {
			Box3D bound = Box3D.bound(e, root);
			root.min = bound.min;
			root.max = bound.max;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected final void delete(BoxNode<E> node) {
		if (root.list.remove(node)) {
			Box3D bound = Box3D.bound(root);
			root.min = bound.min;
			root.max = bound.max;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	@Override
	public final void clear() {
		root.list.clear();
		root.min = Point3D.POSITIVE_INFINITY;
		root.max = Point3D.NEGATIVE_INFINITY;
	}
	
	@Override
	public final int size() {
		return root.list.size();
	}
	
}
