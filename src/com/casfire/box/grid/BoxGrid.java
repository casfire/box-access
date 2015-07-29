package com.casfire.box.grid;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.list.BoxList;
import com.casfire.box.util.BoxIndex;

public final class BoxGrid<E extends BoundingBox3D> implements BoxIndex<E> {
	
	private final Point3D min, cell;
	private final int countX, countY, countZ;
	private final Function<Box3D, BoxIndex<E>> inside;
	private final BoxIndex<E> outside;
	
	private final BoxIndex<E> grid[];
	int size;
	
	public BoxGrid(BoundingBox3D bound, Point3D count) {
		this(bound, count, b -> new BoxList<E>(), new BoxList<E>());
	}
	
	public BoxGrid(BoundingBox3D bound, Point3D count, BoxIndex<E> out) {
		this(bound, count, b -> new BoxList<E>(), out);
	}
	
	public BoxGrid(BoundingBox3D bound, Point3D count, Function<Box3D, BoxIndex<E>> in) {
		this(bound, count, in, new BoxList<E>());
	}
	
	@SuppressWarnings("unchecked")
	public BoxGrid(BoundingBox3D bound, Point3D count, Function<Box3D, BoxIndex<E>> in, BoxIndex<E> out) {
		count = Point3D.floor(count);
		if (in == null) throw new NullPointerException();
		this.min     = bound.min();
		this.cell    = Point3D.div(Point3D.sub(bound.max(), min), count);
		this.countX  = (int) count.x;
		this.countY  = (int) count.y;
		this.countZ  = (int) count.z;
		this.inside  = in;
		this.outside = out;
		this.grid    = new BoxIndex[countX * countY * countZ];
		this.size    = outside.size();
	}
	
	@Override
	public final Iterable<E> contain(Point3D p) {
		BoxGridIterable<E> query = new BoxGridIterable<E>(this);
		index(p, index -> query.add(index.contain(p)));
		return query;
	}
	
	@Override
	public final Iterable<E> contain(BoundingBox3D box) {
		BoxGridIterable<E> query = new BoxGridIterable<E>(this);
		index(box.min(), box.max(), index -> query.add(index.contain(box)));
		return query;
	}
	
	@Override
	public final Iterable<E> overlap(BoundingBox3D box) {
		BoxGridIterable<E> query = new BoxGridIterable<E>(this);
		index(box.min(), box.max(), index -> query.add(index.overlap(box)));
		return query;
	}
	
	@Override
	public final int size() {
		return size;
	}
	
	@Override
	public final void clear() {
		outside.clear();
		Arrays.fill(grid, null);
		size = 0;
	}
	
	public final void clean() {
		if (outside instanceof BoxGrid) ((BoxGrid<E>) outside).clean();
		size = outside.size();
		for (int i = 0; i < grid.length; i++) if (grid[i] != null) {
			if (grid[i] instanceof BoxGrid) {
				((BoxGrid<E>) grid[i]).clean();
			} else if (grid[i].isEmpty()) {
				grid[i] = null;
			} else {
				size += grid[i].size();
			}
		}
	}
	
	@Override
	public final boolean add(E e) {
		if (addGrid(e) || outside.add(e)) {
			size++;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public final Iterator<E> iterator() {
		BoxGridIterable<E> query = new BoxGridIterable<E>(this);
		index(index -> query.add(index));
		return query.iterator();
	}
	
	@Override
	public final Spliterator<E> spliterator() {
		BoxGridIterable<E> query = new BoxGridIterable<E>(this);
		index(index -> query.add(index));
		return query.spliterator();
	}
	
	@Override
	public final Stream<E> stream() {
		return Stream.concat(
				outside.stream(),
				Arrays.asList(grid)
					.stream()
					.filter(Objects::nonNull)
					.flatMap(g -> g.stream())
		);
	}
	
	@Override
	public final Stream<E> parallelStream() {
		return Stream.concat(
				outside.parallelStream(),
				Arrays.asList(grid)
					.parallelStream()
					.filter(Objects::nonNull)
					.flatMap(g -> g.parallelStream())
		);
	}
	
	private final boolean addGrid(E e) {
		final Point3D a = e.min(), b = e.max();
		final int x = indexX(a); if (x < 0 || x >= countX || x != indexX(b)) return false;
		final int y = indexY(a); if (y < 0 || y >= countY || y != indexY(b)) return false;
		final int z = indexZ(a); if (z < 0 || z >= countX || z != indexZ(b)) return false;
		final int i = x + y * countX + z * countX * countY;
		if (grid[i] == null) grid[i] = inside.apply(cell(x, y, z));
		return grid[i].add(e);
	}
	
	private final Box3D cell(int x, int y, int z) {
		return new Box3D(
				new Point3D(
						min.x + x * cell.x,
						min.y + y * cell.y,
						min.z + z * cell.z
				),
				new Point3D(
						min.x + (x + 1) * cell.x,
						min.y + (y + 1) * cell.y,
						min.z + (z + 1) * cell.z
				)
		);
	}
	
	private final int indexX(Point3D p) { return countX == 1 ? 0 : (int) ((p.x - min.x) / cell.x); }
	private final int indexY(Point3D p) { return countY == 1 ? 0 : (int) ((p.y - min.y) / cell.y); }
	private final int indexZ(Point3D p) { return countZ == 1 ? 0 : (int) ((p.z - min.z) / cell.z); }
	
	private final void index(Consumer<BoxIndex<E>> f) {
		if (!outside.isEmpty()) f.accept(outside);
		for (int i = 0; i < grid.length; i++) {
			if (grid[i] == null) continue;
			if (grid[i].isEmpty()) {
				grid[i] = null;
			} else {
				f.accept(grid[i]);
			}
		}
	}
	
	private final void index(Point3D p, Consumer<BoxIndex<E>> f) {
		if (!outside.isEmpty()) f.accept(outside);
		final int x = indexX(p); if (x < 0 || x >= countX) return;
		final int y = indexY(p); if (y < 0 || y >= countY) return;
		final int z = indexZ(p); if (z < 0 || z >= countZ) return;
		final int i = x + y * countX + z * countX * countY;
		if (grid[i] != null) f.accept(grid[i]);
	}
	
	private final void index(Point3D min, Point3D max, Consumer<BoxIndex<E>> f) {
		final int x1 = clamp((int) ((min.x - this.min.x) / cell.x), countX);
		final int y1 = clamp((int) ((min.y - this.min.y) / cell.y), countY);
		final int z1 = clamp((int) ((min.z - this.min.z) / cell.z), countZ);
		final int x2 = clamp((int) ((max.x - this.min.x) / cell.x) + 1, countX);
		final int y2 = clamp((int) ((max.y - this.min.y) / cell.y) + 1, countY);
		final int z2 = clamp((int) ((max.z - this.min.z) / cell.z) + 1, countZ);
		index(x1, y1, z1, x2, y2, z2, f);
	}
	
	private final int clamp(int i, int max) {
		return Math.max(0, Math.min(i, max));
	}
	
	private final void index(int x1, int y1, int z1, int x2, int y2, int z2, Consumer<BoxIndex<E>> f) {
		if (!outside.isEmpty()) f.accept(outside);
		final int xy = countX * countY;
		for (int x = x1; x < x2; x++) 
		for (int y = y1; y < y2; y++) 
		for (int z = z1; z < z2; z++) {
			final int i = x + y * countX + z * xy;
			if (grid[i] == null) continue;
			if (grid[i].isEmpty()) {
				grid[i] = null;
			} else {
				f.accept(grid[i]);
			}
		}
	}
	
}
