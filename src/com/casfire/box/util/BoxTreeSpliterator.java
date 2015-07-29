package com.casfire.box.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxTreeSpliterator<E extends BoundingBox3D> implements Spliterator<E> {
	
	private final Predicate<BoxNode<E>> decend;
	private final Deque<BoxNode<E>> stack;
	
	BoxTreeSpliterator(BoxNode<E> root, Predicate<BoxNode<E>> decend) {
		this.decend = decend;
		this.stack  = new ArrayDeque<BoxNode<E>>();
		stack.push(root);
	}
	
	private BoxTreeSpliterator(Predicate<BoxNode<E>> decend, Deque<BoxNode<E>> stack) {
		this.decend = decend;
		this.stack  = stack;
	}
	
	@Override
	public final int characteristics() {
		return IMMUTABLE | NONNULL;
	}
	
	@Override
	public final long estimateSize() {
		return Long.MAX_VALUE;
	}
	
	private final BoxNode<E> remove() {
		final BoxNode<E> node = stack.pop();
		if (decend.test(node)) {
			for (BoxNode<E> c : node.children()) {
				stack.push(c);
			}
		}
		return node;
	}
	
	@Override
	public final void forEachRemaining(Consumer<? super E> action) {
		while (!stack.isEmpty()) {
			final BoxNode<E> node = remove();
			final E entry = node.entry();
			if (entry != null) action.accept(node.entry());
		}
	}
	
	@Override
	public final boolean tryAdvance(Consumer<? super E> action) {
		while (!stack.isEmpty()) {
			final BoxNode<E> node = remove();
			final E entry = node.entry();
			if (entry != null) {
				action.accept(node.entry());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public final Spliterator<E> trySplit() {
		while (stack.size() < 2) {
			if (stack.isEmpty()) return null;
			if (stack.peek().entry() != null) return null;
			remove();
		}
		return new BoxTreeSpliterator<E>(decend, splitDeque());
	}
	
	private final Deque<BoxNode<E>> splitDeque() {
		final Deque<BoxNode<E>> q = new ArrayDeque<BoxNode<E>>();
		final int s = stack.size() >>> 1;
		for (int i = 0; i < s; i++) {
			q.add(stack.pop());
		}
		return q;
	}
	
}
