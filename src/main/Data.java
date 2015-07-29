package main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Spliterator;

import com.casfire.box.geometry.Box3D;
import com.casfire.box.util.BoxIndex;

public class Data implements Iterable<Entry> {
	
	private final List<Entry> data;
	final int length;
	final Box3D bound;
	
	public Data(Entry[] d) {
		data   = Arrays.asList(d);
		length = d.length;
		bound  = Box3D.bound(d);
	}
	
	@Override
	public Iterator<Entry> iterator() {
		return data.iterator();
	}
	
	@Override
	public Spliterator<Entry> spliterator() {
		return data.spliterator();
	}
	
	public Entry get(int i) {
		return data.get(i);
	}
	
	public void insert(BoxIndex<Entry> index) {
		index.addAll(data);
		valid(index);
	}
	
	public void reinsert(BoxIndex<Entry> index) {
		for (Entry e : data) {
			index.remove(e);
			index.add(e);
		}
		valid(index);
	}
	
	public void reinsert(Random random, BoxIndex<Entry> index, int count, int size) {
		for (int c = 0; c < count; c++) {
			int len = random.nextInt(size);
			Set<Entry> set = new HashSet<Entry>();
			while (set.size() < len) set.add(data.get(random.nextInt(data.size())));
			for (Entry e : set) index.remove(e);
			for (Entry e : set) index.add(e);
		}
		valid(index);
	}
	
	private void valid(BoxIndex<Entry> index) {
		int size = index.size();
		if (length != size) {
			throw new IllegalStateException (
				"Invalid size " + size + " after insertion. Expected " + length
			);
		}
		for (Entry e : index) {
			size--;
			Entry r = data.get(e.ID);
			if (r != e) {
				throw new IllegalStateException (
					"Invalid entry " + e + " after insertion. Expected " + r
				);
			}
		}
		if (size != 0) {
			throw new IllegalStateException (
				"Invalid iterated size " + (length - size) + ". Expected " + length
			);
		}
	}
	
}
