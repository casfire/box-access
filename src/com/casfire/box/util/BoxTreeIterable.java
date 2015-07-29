package com.casfire.box.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Predicate;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxTreeIterable<E extends BoundingBox3D> implements Iterable<E> {
	
	private final BoxTree<E> tree;
	private final BoxNode<E> root;
	private final Predicate<BoxNode<E>> decend;
	
	BoxTreeIterable(BoxTree<E> tree, BoxNode<E> root, Predicate<BoxNode<E>> decend) {
		this.tree   = tree;
		this.root   = root;
		this.decend = decend;
	}
	
	@Override
	public final Iterator<E> iterator() {
		return new BoxTreeIterator<E>(tree, root, decend);
	}
	
	@Override
	public final Spliterator<E> spliterator() {
		return new BoxTreeSpliterator<E>(root, decend);
	}
	
}
