package com.casfire.box.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxTreeIterator<E extends BoundingBox3D> implements Iterator<E> {
	
	private final BoxTree<E> tree;
	private final Predicate<BoxNode<E>> decend;
	private final Deque<BoxNode<E>> stack;
	private BoxNode<E> next, prev;
	private E entry;
	
	BoxTreeIterator(BoxTree<E> tree, BoxNode<E> root, Predicate<BoxNode<E>> decend) {
		this.tree   = tree;
		this.decend = decend;
		this.stack  = new ArrayDeque<BoxNode<E>>();
		this.prev   = null;
		this.next   = null;
		stack.push(root);
		advance();
	}
	
	private final void advance() {
		entry = null;
		while (entry == null && !stack.isEmpty()) {
			entry = (next = stack.pop()).entry();
			if (!decend.test(next)) continue;
			for (BoxNode<E> c : next.children()) {
				stack.push(c);
			}
		}
	}
	
	@Override
	public final boolean hasNext() {
		return entry != null;
	}
	
	@Override
	public final E next() {
		if (entry == null) throw new NoSuchElementException();
		final E entry = (prev = next).entry();
		advance();
		return entry;
	}
	
	@Override
	public final void remove() {
		if (prev == null) throw new NoSuchElementException();
		tree.delete(prev);
		prev = null;
	}
	
}
