package com.casfire.box.rtree;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;
import com.casfire.box.util.BoxTree;

public final class BoxRTree<E extends BoundingBox3D> extends BoxTree<E> {
	
	private final int min, max;
	private int size;
	private Node<E> root;
	
	public BoxRTree() {
		this(4, 10);
	}
	
	public BoxRTree(int min, int max) {
		if (min < 1) throw new IllegalArgumentException("Invalid min: " + min);
		if (max < 2) throw new IllegalArgumentException("Invalid max: " + min);
		if (min > (max + 1) / 2) throw new IllegalArgumentException("Invalid min: " + min);
		this.min = min;
		this.max = max;
		clear();
	}
	
	@Override
	public final BoxNode<E> root() {
		return root;
	}
	
	@Override
	public final boolean add(E e) {
		Insert(e);
		size++;
		return true;
	}
	
	@Override
	protected final void delete(BoxNode<E> node) {
		Delete((Node<E>) node);
		size--;
		
	}
	
	@Override
	public final void clear() {
		size = 0;
		root = new Node<E>(null, 1);
	}
	
	@Override
	public final int size() {
		return size;
	}
	
	private final class Pair { Node<E> a, b; }
	
	// Fix bounds for all nodes up to the root - O(log_min(N))
	private final void AdjustTreeAdded(Node<E> N, BoundingBox3D added) {
		while (N != null && !N.contains(added)) {
			Box3D bound = Box3D.bound(N, added);
			N.min = bound.min;
			N.max = bound.max;
			added = bound;
			N = N.parent;
		}
	}
	
	// Fix bounds for all nodes up to the root - O(max * log_min(N))
	private final void AdjustTreeRemoved(Node<E> N) {
		while (N != null) {
			Box3D bound = Box3D.bound(N.children);
			if (bound.contains(N)) break;
			N.min = bound.min;
			N.max = bound.max;
			N = N.parent;
		}
	}
	
	// Return a pair of children with the biggest cost if grouped together in a split - O(max^2)
	private final Pair PickSeeds(Node<E> N) {
		double best = Double.NEGATIVE_INFINITY; Pair P = new Pair();
		for (Node<E> a : N.children) for (Node<E> b : N.children) if (a != b) {
			double curr = Box3D.bound(a, b).volume() - a.volume() - b.volume();
			if (curr > best) {
				P.a = a; P.b = b;
				best = curr;
			}
		}
		return P;
	}
	
	// Move a child from N to a or b based on maximum difference of volume expansion - O(max)
	private final void PickNext(Node<E> N, Node<E> a, Node<E> b) {
		double best = Double.NEGATIVE_INFINITY; Node<E> B = null, G = null;
		for (Node<E> c : N.children) {
			double currA = Box3D.bound(c, a).volume();
			double currB = Box3D.bound(c, b).volume();
			double curr  = Math.abs(currA - currB);
			if (curr > best) {
				G = currA > currB ? b : a;
				best = curr; B = c;
			}
		}
		N.children.remove(B);
		G.children.add(B); B.parent = G;
		G.min = Point3D.min(G.min, B.min);
		G.max = Point3D.max(G.max, B.max);
	}
	
	// Move all children from N to G - O(max)
	private void PickNext(Node<E> N, Node<E> G) {
		for (Node<E> n : N.children) {
			G.children.add(n); n.parent = G;
			G.min = Point3D.min(G.min, n.min);
			G.max = Point3D.max(G.max, n.max);
		}
		N.children.clear();
	}
	
	// Split node N into two and return their unadjusted parent - O(max^2)
	private final Node<E> SplitNode(Node<E> N) {
		if (N == root) {
			root = new Node<E>(N.min, N.max, null, root.height + 1);
			N.parent = root;
		} else {
			N.parent.children.remove(N);
		}
		Pair S = PickSeeds(N);
		N.children.remove(S.a);
		N.children.remove(S.b);
		Node<E> a = new Node<E>(S.a.min, S.a.max, N.parent, N.height);
		Node<E> b = new Node<E>(S.b.min, S.b.max, N.parent, N.height);
		N.parent.children.add(a);
		N.parent.children.add(b);
		a.children.add(S.a); S.a.parent = a;
		b.children.add(S.b); S.b.parent = b;
		while (!N.children.isEmpty()) {
			PickNext(N, a, b);
			if (a.children.size() + N.children.size() == min) PickNext(N, a);
			if (b.children.size() + N.children.size() == min) PickNext(N, b);
		}
		return N.parent;
	}
	
	// Return a leaf that requires minimum volume expansion for insertion of e - O(max * log_min(N))
	private final Node<E> ChooseLeaf(E e) {
		Node<E> N = root;
		while (N.height > 1) {
			double best = Double.POSITIVE_INFINITY; Node<E> F = null;
			for (Node<E> c : N.children) {
				double curr = Box3D.bound(c, e).volume() - c.volume();
				if (curr < best || curr == best && c.volume() < F.volume()) {
					best = curr; F = c;
				}
			}
			N = F;
		}
		return N;
	}
	
	// Insert entry into the tree - O(log_min(N) * max^2)
	private final void Insert(E e) {
		Node<E> L = ChooseLeaf(e);
		L.children.add(new Node<E>(e, L));
		while (L.children.size() > max) L = SplitNode(L);
		AdjustTreeAdded(L, e);
	}
	
	// Add all entries under N to Q - O(N)
	private void CondenseTreeCollect(Node<E> N, List<E> Q) {
		Deque<Node<E>> stack = new LinkedList<Node<E>>();
		stack.push(N);
		while (!stack.isEmpty()) {
			Node<E> node = stack.pop();
			if (node.entry == null) {
				for (Node<E> c : node.children) stack.push(c);
			} else {
				Q.add(node.entry);
			}
		}
	}
	
	// Eliminate N if it has too few entries - O(N)
	private final void CondenseTree(Node<E> N) {
		List<E> Q = new LinkedList<E>();
		while (N != root && N.children.size() < min) {
			N.parent.children.remove(N);
			CondenseTreeCollect(N, Q);
			N = N.parent;
		}
		AdjustTreeRemoved(N);
		for (E e : Q) Insert(e);
	}
	
	// Delete entry from the tree - O(N)
	private final void Delete(Node<E> e) {
		Node<E> L = e.parent;
		L.children.remove(e);
		CondenseTree(L);
	}
	
}
