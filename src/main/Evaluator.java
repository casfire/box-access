package main;

import java.io.PrintStream;
import java.util.Deque;
import java.util.LinkedList;

import com.casfire.box.geometry.BoundingBox3D;
import com.casfire.box.geometry.Box3D;
import com.casfire.box.util.BoxNode;
import com.casfire.box.util.BoxTree;

public class Evaluator {
	
	private final BoxTree<Entry> tree;
	
	public Evaluator(BoxTree<Entry> tree) {
		this.tree = tree;
	}
	
	public void exportHTML(PrintStream html) {
		html.println("<html><head><style>");
		html.println("  div { position:fixed; display:block; box-sizing:border-box; }");
		html.println("  div:hover{ background:rgba(0, 0, 0, 0.2); }");
		html.println("</style></head><body>");
		htmlNode(tree.root(), html, "  ");
		html.println("</body></html>");
	}
	
	private void htmlNode(BoxNode<Entry> N, PrintStream html, String indent) {
		html.println(indent + "<!-- " + N.toString() + "-->");
		if (N.entry() != null) html.println(indent + "<div " + htmlStyle(N.entry(), 0) + "></div>");
		if (N.childrenCount() == 0) return;
		html.println(indent + "<div " + htmlStyle(N, N.height()) + ">");
		for (BoxNode<Entry> c : N.children()) htmlNode(c, html, indent + "  ");
		html.println(indent + "</div>");
	}
	
	private String htmlStyle(BoundingBox3D N, int h) {
		StringBuilder str = new StringBuilder();
		str.append("style=\"");
		str.append("left: "  + N.min().x + "; top: "    + N.min().z + "; ");
		str.append("width: " + N.sizeX() + "; height: " + N.sizeZ() + "; ");
		if (h <= 0) {
			str.append("border: 1px solid black;");
		} else {
			int b = Math.max(1, (h + 2) / 2);
			String s = h % 2 == 0 ? "blue" : "red";
			str.append("border: " + b + "px dashed " + s + ";");
		}
		return str.toString() + "\"";
	}
	
	public double overlap() {
		Deque<BoxNode<Entry>> stack = new LinkedList<BoxNode<Entry>>();
		double volume = 0, overlap = 0;
		stack.push(tree.root());
		while (!stack.isEmpty()) {
			BoxNode<Entry> N = stack.pop();
			for (BoxNode<Entry> a : N.children()) {
				stack.push(a); volume += a.volume();
				for (BoxNode<Entry> b : N.children()) {
					if (a == b || !a.overlaps(b)) continue;
					overlap += Box3D.overlap(a, b).volume();
				}
			}
		}
		return overlap / volume;
	}
	
}
