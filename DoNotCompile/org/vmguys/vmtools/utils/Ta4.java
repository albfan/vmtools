package org.vmguys.ota.utils;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import org.jdom.*;
import org.vmguys.ota.elements.*;


/**
 * This class was originally created by using the Turing source code provided
 * with the paper "Tree-to-tree Correction for Document Trees", by Barnard,
 * Clarke, and Duncan. It was then modified to handle JDOM trees as used in the
 * OTA spec.
 *
 * This implements Zhang and Shasha's algorithm.
 */
public class Ta4 {
	// The original Turing code sets up the d array using origin 1.
	// We can't do that in Java, so we'll allocate an extra element in each
	// dimension and just ignore the 0th element.
	//
	// The d array measures the tree distance between the two trees. The fd array
	// measures the forest distance between the two trees.

	int[][] d;
	int[][][][] fd;


	// Creates an instance of the class and calls the go method.

	public static void main(String[] args) {
		new Ta4().go();
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

			{			
				int maxNodes = max(t1.treeSize(), t2.treeSize()) + 1;
				d = new int[maxNodes][maxNodes];
				fd = new int[maxNodes+1][maxNodes+1][maxNodes+1][maxNodes+1];
			}

			// Do the computation.
			// In the original Turing code, this code is in Ta4edit.t,
			// and is included in the main code with an "%include"
			// directive. In Java, we have to place the code here
			// completely.
			
			for (int x = 1; x <= t1.treeSize(); ++x) {
				if (nl1[x].keyroot) {
					final int lx = nl1[x].leftmost;
					
					for (int y = 1; y <= t2.treeSize(); ++y) {
						if (nl2[y].keyroot) {
							final int ly = nl2[y].leftmost;
							fd[0][0][0][0] = 0;
							
							for (int i = lx; i <= x; ++i) {
								fd[lx][i][0][0] = access(lx, i-1, 0, 0) + ElementUtils.deleteCost(nl1[i].el);
							}
							
							for (int j = ly; j <= y; ++j) {
								fd[0][0][ly][j] = access(0, 0, ly, j-1) + ElementUtils.insertCost(nl2[j].el);
							}
							
							for (int i = lx; i <= x; ++i) {
								for (int j = ly; j <= y; ++j) {
									int part = min(access(lx, i-1, ly, j) + ElementUtils.deleteCost(nl1[i].el), access(lx, i, ly, j-1) + ElementUtils.insertCost(nl2[j].el));
									
									if (nl1[i].leftmost == lx && nl2[j].leftmost == ly) {
										fd[lx][i][ly][j] = min(part, access(lx, i-1, ly, j-1) + ElementUtils.editCost(nl1[i].el, nl2[j].el));
										d[i][j] = fd[lx][i][ly][j];
									}
									else {
										fd[lx][i][ly][j] = min(part, access(lx, nl1[i].leftmost-1, ly, nl2[j].leftmost-1) + d[i][j]);
									}
								}
							}
						}
					}
				}
			}
							
			// End of computation.
			
			for (int y = 1; y <= t2.treeSize(); ++y) {
				for (int x = 1; x <= t1.treeSize(); ++x) {
					System.out.print(toPaddedString(d[x][y], 5));
				}
				System.out.println();
			}
			System.out.println();
			
			System.out.println("Distance is " + d[t1.treeSize()][t2.treeSize()]);
			
			String ops = "";
			int x = t1.treeSize();
			int y = t2.treeSize();
			
			while (x > 0 && y > 0) {
				//System.out.println("x=" + x + ", y=" + y);
				
				if (d[x][y] == d[x-1][y-1]) {
					// There is no cost, so nothing to do.
					ops = "(x=" + x + ", y=" + y + "); " + ops;
					--x;
					--y;
				}
				else {
					int next = min3(d[x-1][y-1], d[x-1][y], d[x][y-1]);

					if (next == d[x-1][y-1]) {
						ops = "(x=" + x + ", y=" + y + ") " + "edit " + nl1[x].getName() + " to " + nl2[y].getName() + "; " + ops;
						--x;
						--y;
					}
					else if (next == d[x-1][y]) {
						ops = "(x=" + x + ", y=" + y + ") " + "delete " + nl1[x].getName() + "; " + ops;
						--x;
					}
					else {	// next == d[x][y-1]
						ops = "(x=" + x + ", y=" + y + ") " + "insert " + nl2[y].getName() + "; " + ops;
						--y;
					}
				}				

			}
			
			System.out.println(ops);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private int access(int l, int m, int n, int o) {
		int lprime = l;
		int mprime = m;
		int nprime = n;
		int oprime = o;
		
		if (l > m) {
			lprime = 0;
			mprime = 0;
		}
		
		if (n > o) {
			nprime = 0;
			oprime = 0;
		}
		
		return fd[lprime][mprime][nprime][oprime];
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
		Element profile = createElement1();

		/*
		// Change the surname.
		
		Element newSurName = new Element("SurName");
		newSurName.addContent("Jones");

		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("SurName");
		personName.addContent(newSurName);
		*/
				
		// Delete the title.
				
		Element personName = profile.getChild("Customer").getChild("PersonName");
		personName.removeChild("NameTitle");

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
	public int leftmost;
	public boolean keyroot;
//	public Node parent;
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
	 * Gets the nth child. The outside code assumes the children
	 * are numbered from 1 to n, so make an adjustment here.
	 */
	public Node getChild(int i) {
		if (i >= 1 && i <= children.size()) {
			return (Node)children.get(i-1);
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

			System.out.println("/>  (n=" + number + ", k=" + (keyroot ? "t" : "f") + ", t=" + treeSize() + ")");
		}
		else {
			// Write the final piece of the element name.

			System.out.println(">  (n=" + number + ", k=" + (keyroot ? "t" : "f") + ", t=" + treeSize() + ")");

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
		list = new Node[root.treeSize() + 1];	// list[0] will be unused; indexes start at 1
		nodes = 1;
		postorder(root, true);
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
		//	child.parent = n;
		}
		
		return n;
	}
	

	/**	
	 * Traverse the tree in postorder and fill in the missing values.
	 * The Turing version uses more parameters, but we use instance
	 * variables of "list" and "nodes" instead to get the same behaviour.
	 */
	private void postorder(Node n, boolean makeKeyroot) {
		if (n != null) {
			n.leftmost = nodes;
			n.keyroot = makeKeyroot;
			
			for (int i = 1; i <= n.getNumChildren(); ++i) {
				postorder(n.getChild(i), i != 1);
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


		
		
		
