package com.casfire.box.rtree;

import java.util.Comparator;
import java.util.Iterator;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.geometry.Dimension;
import com.casfire.box.geometry.Point3D;
import com.casfire.box.util.BoxNode;
import com.casfire.box.util.BoxTree;

public class BoxRStar<E extends BoundingBox3D> extends BoxTree<E> {
	
	private final int min, max, ref;
	private int size;
	private Node<E> root;
	
	public BoxRStar() {
		this(4, 10, 3);
	}
	
	public BoxRStar(int min, int max, int ref) {
		if (min < 1) throw new IllegalArgumentException("Invalid min: " + min);
		if (max < 2) throw new IllegalArgumentException("Invalid max: " + min);
		if (min > (max + 1) / 2) throw new IllegalArgumentException("Invalid min: " + min);
		if (ref > max - min) throw new IllegalArgumentException("Invalid ref: " + ref);
		this.min = min;
		this.max = max;
		this.ref = ref;
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
	
	// Return a child of N with minimum volume expansion needed to insert e - O(max)
	private Node<E> ChoseSubtreeMinVolume(Node<E> N, BoundingBox3D e) {
		double best = Double.POSITIVE_INFINITY; Node<E> C = null;
		for (Node<E> c : N.children) {
			double curr = Box3D.bound(c, e).volume() - c.volume();
			if (curr < best || curr == best && c.volume() < C.volume()) {
				C = c; best = curr;
			}
		}
		return C;
	}
	
	// Return a child of N with minimum overlap expansion needed to insert e - O(max)
	private Node<E> ChoseSubtreeMinOverlap(Node<E> N, BoundingBox3D e) {
		double best = Double.POSITIVE_INFINITY; Node<E> C = null;
		for (Node<E> c : N.children) {
			Box3D bound = Box3D.bound(c, e); double curr = 0;
			for (Node<E> f : N.children) if (f != c && bound.overlaps(f)) {
				curr += Box3D.overlap(bound, f).volume();
			}
			
			if (curr < best) {
				C = c; best = curr;
			} else if (curr == best) {
				double bestVolume = Box3D.bound(C, e).volume() - C.volume();
				double currVolume = Box3D.bound(c, e).volume() - c.volume();
				if (currVolume < bestVolume || currVolume == bestVolume && c.volume() < C.volume()) {
					C = c; best = curr;
				}
			}
		}
		return C;
	}
	
	// Find the best subtree to insert e into - O(max * log_min(N))
	private Node<E> ChooseSubtree(BoundingBox3D e, int height) {
		Node<E> node = root;
		while (node.height > height) {
			if (node.height <= 2) {
				node = ChoseSubtreeMinOverlap(node, e);
			} else {
				node = ChoseSubtreeMinVolume(node, e);
			}
		}
		return node;
	}
	
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
	
	// Return margin-value sum for all group distributions of the children - O(max^2)
	private double ChooseSplitAxisMarginSum(Node<E> N) {
		int maxK = N.children.size() - min; double sum = 0;
		for (int k = min; k <= maxK; k++) {
			Iterator<Node<E>> iter = N.children.iterator();
			sum += Box3D.bound(iter, k).margin();
			sum += Box3D.bound(iter, N.children.size() - k).margin();
		}
		return sum;
	}
	
	// Sort children of N by the best axis for the split - O(max^2)
	private void ChooseSplitAxis(Node<E> N) {
		Comparator<BoundingBox3D> compare = null; double best = Double.POSITIVE_INFINITY;
		for (Dimension dim : Dimension.values()) {
			N.children.sort(dim.compare_min); double min = ChooseSplitAxisMarginSum(N);
			N.children.sort(dim.compare_max); double max = ChooseSplitAxisMarginSum(N);
			if (min < best) { compare = dim.compare_min; best = min; }
			if (max < best) { compare = dim.compare_max; best = max; }
		}
		N.children.sort(compare);
	}
	
	// Return the best distribution split of the children by overlap-value - O(max^2)
	private int ChooseSplitIndex(Node<E> N) {
		int maxK = N.children.size() - min, K = Integer.MAX_VALUE;
		double bestO = Double.POSITIVE_INFINITY, bestV = Double.POSITIVE_INFINITY;
		for (int k = min; k <= maxK; k++) {
			Iterator<Node<E>> iter = N.children.iterator();
			Box3D a = Box3D.bound(iter, k);
			Box3D b = Box3D.bound(iter, N.children.size() - k);
			double currO = a.overlaps(b) ? Box3D.overlap(a, b).volume() : 0;
			double currV = a.volume() + b.volume();
			if (currO < bestO || currO == bestO && currV < bestV) {
				K = k; bestO = currO; bestV = currV;
			}
		}
		return K;
	}
	
	// Add k children of N to A and the rest to B - O(max)
	private void SplitDistribute(Node<E> N, Node<E> A, Node<E> B, int k) {
		for (Node<E> c : N.children) {
			Node<E> P = k-- > 0 ? A : B;
			P.children.add(c); c.parent = P;
			P.min = Point3D.min(P.min, c.min);
			P.max = Point3D.max(P.max, c.max);
		}
	}
	
	// Split N into two groups, add both to parent and return the parent - O(max^2)
	private Node<E> Split(Node<E> N) {
		if (N == root) {
			root = new Node<E>(N.min, N.max, null, N.height + 1);
			N.parent = root;
		} else {
			N.parent.children.remove(N);
		}
		ChooseSplitAxis(N);
		int k = ChooseSplitIndex(N);
		Node<E> a = new Node<E>(N.parent, N.height);
		Node<E> b = new Node<E>(N.parent, N.height);
		N.parent.children.add(a);
		N.parent.children.add(b);
		SplitDistribute(N, a, b, k);
		return N.parent;
	}
	
	// Reinsert children - O(ref * max^2 * log_min(N))
	private final void ReInsert(Node<E> N) {
		Point3D center = N.center();
		N.children.sort((a, b) -> {
			return Double.compare(
					Point3D.distanceSqr(center, b.center()),
					Point3D.distanceSqr(center, a.center())
			);
		});
		@SuppressWarnings("unchecked")
		Node<E> refs[] = new Node[ref];
		Iterator<Node<E>> iter = N.children.iterator();
		for (int i = 0; i < ref; i++) {
			refs[i] = iter.next();
			iter.remove();
		}
		AdjustTreeRemoved(N);
		for (int i = 0; i < ref; i++) Insert(refs[i]);
	}
	
	// Reinsert children or split - O(ref * max^2 * log_min(N))
	private void OverflowTreatment(Node<E> N) {
		if (N == root || ref <= 0) {
			while (N.children.size() > max) N = Split(N);
		} else {
			ReInsert(N);
		}
	}
	
	// Reinsert node - O(max^2 * log_min(N))
	private final void Insert(Node<E> N) {
		Node<E> L = ChooseSubtree(N, N.height + 1);
		L.children.add(N); N.parent = L;
		while (L.children.size() > max) L = Split(L);
		AdjustTreeAdded(L, N);
	}
	
	// Insert entry - O(ref * max^2 * log_min(N))
	private final void Insert(E e) {
		Node<E> L = ChooseSubtree(e, 1);
		L.children.add(new Node<E>(e, L));
		AdjustTreeAdded(L, e);
		if (L.children.size() > max) {
			OverflowTreatment(L);
		}
	}
	
	// Borrow and insert a sibling child, or merge with a sibling, return parent - O(max^3)
	private Node<E> CondenseTree(Node<E> N) {
		Node<E> BG = null; double bgBest = Double.MAX_VALUE; // Sibling child to borrow
		Node<E> BS = null; double bsBest = Double.MAX_VALUE; // Sibling to merge with
		for (Node<E> S : N.parent.children) if (S != N) {
			if (S.children.size() > min) {
				for (int i = 0; i < S.children.size(); i++) {
					Iterator<Node<E>> iter = S.children.iterator();
					Box3D A = Box3D.bound(iter, i);
					Node<E> G = iter.next();
					Box3D B = Box3D.bound(iter, S.children.size() - i - 1);
					Box3D boundS = Box3D.bound(A, B);
					Box3D boundN = Box3D.bound(N, G);
					double curr = 0; for (Node<E> T : N.parent.children) if (T != N) {
						BoundingBox3D boundT = T == S ? boundS : T;
						curr += boundN.overlaps(boundT) ? Box3D.overlap(boundN, boundT).volume() : 0;
					}
					if (curr < bgBest || curr == bgBest && boundN.volume() < Box3D.bound(N, BG).volume()) {
						BG = G; bgBest = curr;
					}
				}
			} else {
				Box3D bound = Box3D.bound(S, N);
				double curr = 0; for (Node<E> T : N.parent.children) if (T != S && T != N) {
					curr += T.overlaps(bound) ? Box3D.overlap(T, bound).volume() : 0;
				}
				if (curr < bsBest || curr == bsBest && Box3D.bound(N, S).volume() < Box3D.bound(N, BS).volume()) {
					BS = S; bsBest = curr;
				}
			}
		}
		if (BG != null) {
			BG.parent.children.remove(BG);
			N.children.add(BG);
			BG.parent = N;
			AdjustTreeAdded(N, BG);
		} else {
			N.parent.children.remove(BS);
			for (Node<E> C : BS.children) {
				N.children.add(C); C.parent = N;
				N.min = Point3D.min(N.min, C.min);
				N.max = Point3D.max(N.max, C.max);
			}
		}
		return N.parent;
	}
	
	// Delete entry - O(max^3 * log_min(N))
	private final void Delete(Node<E> e) {
		Node<E> L = e.parent;
		L.children.remove(e);
		AdjustTreeRemoved(L);
		while (L != root && L.children.size() < min) {
			L = CondenseTree(L);
		}
	}
	
}
