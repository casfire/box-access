package com.casfire.box.grid;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.casfire.box.geometry.BoundingBox3D;

final class BoxGridSpliterator<E extends BoundingBox3D> implements Spliterator<E> {
	
	private final Spliterator<E> list[];
	private int c, i, max;
	private long size;
	
	@SuppressWarnings("unchecked")
	BoxGridSpliterator(List<Iterable<E>> iter) {
		list = new Spliterator[iter.size()];
		i    = 0;
		max  = -1;
		size = 0;
		c    = IMMUTABLE | NONNULL | SIZED | SUBSIZED;
		for (Iterable<E> i : iter) {
			list[++max] = i.spliterator();
			if (!list[max].hasCharacteristics(SUBSIZED)) c &= ~SUBSIZED;
			if (!list[max].hasCharacteristics(SIZED))    c &= ~SIZED;
			final long e = list[max].estimateSize() + size;
			size = e < 0 ? Long.MAX_VALUE : e;
		}
	}
	
	private BoxGridSpliterator(Spliterator<E> list[], int i, int max, int c, long size) {
		this.list = list;
		this.c    = c;
		this.i    = i;
		this.max  = max;
		this.size = size;
	}
	
	@Override
	public final int characteristics() {
		return c;
	}
	
	@Override
	public final long estimateSize() {
		return size;
	}
	
	@Override
	public final void forEachRemaining(Consumer<? super E> action) {
		while (i <= max) list[i++].forEachRemaining(action);
		size = 0;
	}
	
	@Override
	public final boolean tryAdvance(Consumer<? super E> action) {
		while (!list[i].tryAdvance(action)) if (++i > max) return false;
		if (size != Long.MAX_VALUE) size--;
		return true;
	}
	
	@Override
	public final Spliterator<E> trySplit() {
		if (max == i) return split(list[i].trySplit());
		final int mid = (max + i) >>> 1;
		long left = 0, right = 0;
		int  l    = i, r     = max;
		while (l <= r) {
			if (left < right || left == right & i <= mid) {
				long e = list[l++].estimateSize() + left;
				left = e < 0 ? Long.MAX_VALUE : e;
			} else {
				long e = list[r--].estimateSize() + right;
				right = e < 0 ? Long.MAX_VALUE : e;
			}
		}
		if (r == i && left > right << 1) {
			return split(list[r].trySplit());
		} else if (l == max && right > left << 1) {
			return split(list[l].trySplit());
		} else {
			r = i;
			return split(new BoxGridSpliterator<E>(list, r, (i = l) - 1, c, left));
		}
	}
	
	private final Spliterator<E> split(Spliterator<E> s) {
		if (s != null) {
			final long e = size - s.estimateSize();
			size = e > 0 ? e : Long.MAX_VALUE;
		}
		return s;
	}
	
}
