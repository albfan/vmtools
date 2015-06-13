package org.vmguys.ota.utils;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.jdom.*;
import org.vmguys.ota.elements.*;


/**
 * A test to compare two JDOM trees and find minimal changes.
 */
public class TestTree {
	// Creates an instance of the class and calls the go method.

	public static void main(String[] args) {
		new TestTree().go();
	}


	// This is the main code for the class.

	private void go() {
		try {
			Node t1, t2;		// trees of Nodes
			Node[] nl1, nl2;	// lists of Nodes

			{
				TreeMaker maker;

				Element original = createElement1();
				maker = new TreeMaker(original);
				t1 = maker.getRoot();
				nl1 = maker.getList();

				Element modified = createElement2();
				maker = new TreeMaker(modified);
				t2 = maker.getRoot();
				nl2 = maker.getList();
			}

			System.out.println("Source is size " + t1.treeSize());
			t1.printTree();
			System.out.println("Target is size " + t2.treeSize());
			t2.printTree();

			CostOps cost = t1.treeConvert(t2);
			System.out.println("Cost is " + cost.cost);
			List ops = cost.ops;
			for (int i = 0; i < ops.size(); ++i) {
				System.out.println((String)ops.get(i));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private Element createElement1() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mr.");

		Element givenName = new Element("GivenName");
		givenName.addContent("George");

		Element surName = new Element("SurName");
		surName.addContent("Smith");

		Element personName = new Element("PersonName");
		personName.addAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);

		Element customer = new Element("Customer");
		customer.addContent(personName);

		Element profile = new Element("Profile");
		profile.addContent(customer);

		return profile;
	}


	private Element createElement2() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mrs.");

		Element givenName = new Element("GivenName");
		givenName.addContent("Edith");

		Element surName = new Element("SurName");
		surName.addContent("Schwartz");

		Element degrees = new Element("Degrees");
		degrees.addContent("Phd");


		Element personName = new Element("PersonName");
		personName.addAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);
		personName.addContent(degrees);

		Element customer = new Element("Customer");
		customer.addContent(personName);

		Element profile = new Element("Profile");
		profile.addContent(customer);

		/*
		Element profile = createElement1();

		// Change the surname.

		Element newSurName = new Element("SurName");
		newSurName.addContent("Jones");

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("SurName");
		personName.addContent(newSurName);
		*/

		/*
		// Delete the title.

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("NameTitle");
		*/

		return profile;
	}


	/**
	 * Returns the minimum of two numbers.
	 */
	private int min(int i, int j) {
		return (i <= j) ? i : j;
	}


	/**
	 * Returns the maximum of two numbers.
	 */
	private int max(int i, int j) {
		return (i >= j) ? i : j;
	}


	/**
	 * Returns the minimum of three numbers.
	 */
	private int min3(int i, int j, int k) {
		return min(i, min(j, k));
	}


	/**
	 * Converts an int to a string, and pads it as necessary to
	 * get the specified width.
	 */
	private String toPaddedString(int i, int minWidth) {
		String s1 = Integer.toString(i);

		int width = s1.length();
		if (width < minWidth) {
			s1 = spaces(minWidth - width) + s1;
		}

		return s1;
	}


	private static String blanks = "                    ";

	/**
	 * Returns a String containing n spaces.
	 */
	private String spaces(int n) {
		return blanks.substring(0, n);
	}
}




/**
 * This class represents one node in the tree. An object of this class
 * is instantiated with a JDOM Element as a parameter. Each Node holds a
 * reference to its peer Element, and provides additional methods and
 * properties.
 *
 * Many of the procedures of the original Turing code have been placed
 * in this class. For example, given a Node, it will return the size
 * of its subtree, and it will display itself.
 */
class Node {
	public Element el;		// the JDOM Element containing other information
	public int number;		// a unique number for each node 1..n; also the index into node list
	public Node parent;
	private int saveTreeSize = 0;		// init to 0; filled in later
	private List children = new ArrayList();	// contains Node objects


	/**
	 * Gets the node's name.
	 */
	public String getName() {
		return el.getName();
	}


	/**
	 * Adds a child.
	 */
	public void addChild(Node child) {
		children.add(child);
	}


	/**
	 * Gets the nth child, where n is 0 .. children.size()-1.
	 */
	public Node getChild(int n) {
		if (n >= 0 && n < children.size()) {
			return (Node)children.get(n);
		}
		else {
			return null;
		}
	}


	/**
	 * Returns the number of children this node has.
	 */
	public int getNumChildren() {
		return children.size();
	}


	/**
	 * Returns true if this node is the same as that node.
	 * It does NOT check to see if they have the same number of
	 * children. That can be checked separately with getNumChildren
	 * if necessary.
	 */
	public boolean equals(Node that) {
		boolean equal = true;

		if (that == null) {
			equal = false;
		}

		if (equal && !this.getName().equals(that.getName())) {
			equal = false;
		}

		if (!ElementUtils.same(this.el, that.el)) {
			equal = false;
		}

		// Add other tests here.

		return equal;
	}


	/**
	 * Returns true if the tree rooted at this node is the same
	 * as the tree rooted at the other node.
	 */
	public boolean treeEquals(Node that) {
		// Compare this Node to that Node. This verifies they have the
		// same name, the same attributes, etc., and the same number
		// of children. See the method Node.equals for details.

		boolean equal = this.equals(that);

		// The first test verifies that the two nodes have the same
		// number of children, so we can perform a 1-to-1 comparison
		// of them (the children).

		for (int i = 0; equal && i < children.size(); ++i) {
			equal = this.getChild(i).treeEquals(that.getChild(i));
		}

		return equal;
	}


	/**
	 * Determines what needs to be done to convert the tree rooted
	 * at this node into the tree rooted at that node. Once the
	 * minimum cost set of operations has been determined, it is
	 * returned to the caller as a CostOps.
	 */
	public CostOps treeConvert(Node that) {
		CostOps cost = new CostOps();

		if (this.getNumChildren() > 0 && that.getNumChildren() > 0) {
			// We need to compare all of this's children to all of that's children.
			// Create an array to hold the results.

			final int rows = this.getNumChildren();
			final int cols = that.getNumChildren();

			CostOps[][] costs = new CostOps[rows][cols];

			for (int r = 0; r < rows; ++r) {
				for (int c = 0; c < cols; ++c) {
					costs[r][c] = this.getChild(r).treeConvert(that.getChild(c));
				}
			}

			System.out.println("comparing children: " + this.getName() + " (" + rows + ") to " + that.getName() + " (" + cols + ")");

			for (int r = 0; r < rows; ++r) {
				for (int c = 0; c < cols; ++c) {
					CostOps x = costs[r][c];
					System.out.println("we could convert child " + r + " into child " + c + " at cost " + x.cost + ":");
					for (int i = 0; i < x.ops.size(); ++i) {
						System.out.println("    " + (String)x.ops.get(i));
					}
				}
			}

			// Attempt to find the minimal cost to convert this's children
			// to that's children.

			// MinFinder works with an array of ints, not CostOps. Build a parallel
			// array containing just the cost from each element of costs.
			// Think about how to simplify this later.

			int[][] costs2 = new int[rows][cols];
			for (int r = 0; r < rows; ++r) {
				for (int c = 0; c < cols; ++c) {
					costs2[r][c] = costs[r][c].cost;
				}
			}

			MinFinder mf = new MinFinder(costs2);
			int minCost = mf.getMinCost();
			int[] solution = mf.getSolution();

			System.out.println("cost is " + minCost);
			System.out.print("solution is ");
			for (int c = 0; c < solution.length; ++c) {
				System.out.print((c > 0 ? ", " : "") + solution[c]);
			}
			System.out.println();

			// The solution tells how this's children will be made into
			// that's children, at minimum cost. We need to build a new
			// CostOps to be returned to our caller than combines all
			// this information.
			//
			// Each column in costs represents one of that's children,
			// and each row represents one of this's children. Each element
			// of the array describes how to change from this to that.
			// The solution indicates an entry from each column so we
			// can tell which children should be chosen. If an entry
			// in solution is Integer.MAX_VALUE it means that's child
			// must be created from scratch.

			boolean[] chosen = new boolean[rows];   // autoinitialized to false

			for (int c = 0; c < cols; ++c) {
				if (solution[c] == Integer.MAX_VALUE) {
					cost.cost += that.getChild(c).treeSize();
					cost.ops.add(new String("create " + that.getChild(c).getName() + " and its children"));
				}
				else {
					cost.cost += costs[solution[c]][c].cost;
					cost.ops.addAll(costs[solution[c]][c].ops);
					chosen[solution[c]] = true;
				}
			}

			// Any of this's children that were not chosen must be deleted.

			for (int r = 0; r < rows; ++r) {
				if (!chosen[r]) {
					cost.cost += 1;     // assume it is inexpensive to delete a tree
					cost.ops.add(new String("delete " + this.getChild(r).getName()));
				}
			}

			// We may also need to modify this to match that, at slight
			// additional cost.

			if (!this.equals(that)) {
				cost.cost += 1;
				cost.ops.add(new String("edit " + this.getName() + " to " + that.getName()));
			}
		}
		else if (this.getNumChildren() > 0) {	// that has no children
			cost.cost = this.getNumChildren();	// just have to delete children
			cost.ops.add(new String("edit " + this.getName() + " to " + that.getName()));
			cost.ops.add(new String("delete all children of " + this.getName()));
		}
		else if (that.getNumChildren() > 0) {	// this has no children
			cost.cost = that.treeSize();	// have to create all that's children
			cost.ops.add(new String("edit " + this.getName() + " to " + that.getName()));
			cost.ops.add(new String("create all children of " + that.getName()));
		}
		else {	// both have no children
			cost.cost = ElementUtils.editCost(this.el, that.el);
			cost.ops.add(new String("edit " + this.getName() + " to " + that.getName()));
		}

		return cost;
	}


	/**
	 * Returns the number of nodes in this subtree, including this
	 * node. Since the tree should not be changed after its initial
	 * construction, it is safe to save this result so subsequent
	 * calls will be faster.
	 */
	public int treeSize() {
		if (saveTreeSize <= 0) {
			saveTreeSize = 1;		// automatically count this node

			for (int i = 0; i < children.size(); ++i) {
				saveTreeSize += ((Node)children.get(i)).treeSize();
			}
		}

		return saveTreeSize;
	}


	/**
	 * Prints the tree.
	 */
	public void printTree() {
		printTree(0);
	}


	private static String blanks = "                    ";

	/**
	 * Returns a String containing n spaces.
	 */
	private String spaces(int n) {
		return blanks.substring(0, n);
	}


	/*
	private void printTree(int indent) {
	//	System.out.println(blanks.substring(0, indent) + el.getName() + "[" + number + ">" + ((parent != null) ? Integer.toString(parent.number) : "*") + "]" + (keyroot ? "K" : "-") + leftmost);
		System.out.println(blanks.substring(0, indent) + el.getName() + "[" + number + "]" + (keyroot ? "K" : "-") + leftmost);
		for (int i = 0; i < children.size(); ++i) {
			((Node)children.get(i)).printTree(indent + 1);
		}
	}
	*/


	private void printTree(int level) {
		System.out.print(spaces(level) + "<" + el.getName());

		List attributes = el.getAttributes();
		for (int i = 0; i < attributes.size(); ++i) {
			Attribute a = (Attribute)attributes.get(i);
			System.out.print(" " + a.getName() + "=\"" + a.getValue() + "\"");
		}


		List content = el.getMixedContent();
		if (content.size() == 0) {
			// There's no content. Just terminate the element now.

		//	System.out.println("/>  (n=" + number + ", k=" + (keyroot ? "t" : "f") + ", t=" + treeSize() + ")");
			System.out.println("/>  (n=" + number + ", t=" + treeSize() + ")");
		}
		else {
			// Write the final piece of the element name.

		//	System.out.println(">  (n=" + number + ", k=" + (keyroot ? "t" : "f") + ", t=" + treeSize() + ")");
			System.out.println(">  (n=" + number + ", t=" + treeSize() + ")");

			// Write the contents.

			for (int i = 0; i < content.size(); ++i) {
				Object o = content.get(i);
				if (o instanceof org.jdom.Element) {
					// skip it
				}
				else if (o instanceof java.lang.String) {
					String cs = o.toString().trim();
					if (cs.length() > 0) {
						System.out.println(spaces(level+1) + cs);
					}
				}
				else {
					System.out.println(spaces(level+1) + o.toString());
				}
			}

			// Do the children.

			for (int i = 0; i < children.size(); ++i) {
				((Node)children.get(i)).printTree(level + 1);
			}

			// Write the closing element name.

			System.out.println(spaces(level) + "</" + el.getName() + ">");
		}
	}
}


/**
 * Contains a series of operations and a cost.
 */
class CostOps {
	public int cost;
	public List ops = new ArrayList();
}


/**
 * Given a tree of JDOM Elements, this builds a parallel tree of Nodes.
 */
class TreeMaker {
	private int nodes;	// used to number the nodes in postorder()
	private Node root;
	private Node[] list;


	/**
	 * Constructs a TreeMaker that will build a tree of Nodes
	 * for the given tree of Elements.
	 */
	public TreeMaker(Element rootElement) {
		root = makeTree(rootElement);
		list = new Node[root.treeSize()];
		nodes = 0;
		postorder(root);
	}


	/**
	 * Builds the tree of Nodes. This instantiates the Node
	 * objects and sets their child and parent references.
	 * Other information must be filled in separately.
	 */
	private Node makeTree(Element el) {
		Node n = new Node();

		n.el = el;

		List children = el.getChildren();
		for (int i = 0; i < children.size(); ++i) {
			Node child = makeTree((Element)children.get(i));
			n.addChild(child);
			child.parent = n;
		}

		return n;
	}


	/**
	 * Traverse the tree in postorder and fill in the missing values.
	 */
	private void postorder(Node n) {
		if (n != null) {
			for (int i = 1; i <= n.getNumChildren(); ++i) {
				postorder(n.getChild(i));
			}

			n.number = nodes;
			list[n.number] = n;
			++nodes;
		}
	}


	/**
	 * After the tree has been constructed, the root node of the
	 * tree can be retrieved.
	 */
	public Node getRoot() {
		return root;
	}


	/**
	 * After the tree has been constructed, the list of nodes can
	 * be retrieved.
	 */
	public Node[] getList() {
		return list;
	}
}





