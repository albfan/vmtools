package org.vmguys.ota.utils;

import java.io.*;


public class Ta3 {
	public static int MAXNODES = 10;
	public static int MAXDEPTH = 10;

	// The original Turing code sets up these arrays using origin 1.
	// We can't do that in Java, so we'll allocate an extra element in each
	// dimension and just ignore the 0th element.
	
	int[][] d = new int[MAXNODES+1][MAXNODES+1];
	int[][] cc = new int[MAXNODES+1][MAXNODES+1];
	int[][][][][][] e = new int[MAXNODES+1][MAXNODES+1][MAXDEPTH+1][MAXDEPTH+1][MAXDEPTH+1][MAXDEPTH+1];
	
	
	// Creates an instance of the class and calls the go method.

	public static void main(String[] args) {
		new Ta3().go();
	}


	// This is the main code for the class.

	private void go() {
		try {
			String s1, s2;
			Node t1, t2;
			Node[] nl1, nl2;
			TreeMaker maker;

			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Examples of legal trees are (a(b(c))) and (a(b)(c)).");
			System.out.println("Node labels are single characters. No blanks are allowed.");

			System.out.print("Enter source tree: ");
			s1 = input.readLine();
			maker = new TreeMaker(s1, MAXNODES);
			t1 = maker.makeTree();
			nl1 = maker.getList();

			System.out.print("Enter target tree: ");
			s2 = input.readLine();
			maker = new TreeMaker(s2, MAXNODES);
			t2 = maker.makeTree();
			nl2 = maker.getList();
			
			maker = null;			// don't need it any more

			System.out.println("Source is size " + t1.treeSize());
			t1.printTree();
			System.out.println("Target is size " + t2.treeSize());
			t2.printTree();

			step1(t1, t2, nl1, nl2);
			step2(t1, t2, nl1, nl2);
			step3(t1, t2, nl1, nl2);
			System.out.println("Distance is " + d[t1.treeSize()][t2.treeSize()]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Sets the value of one element in the array e.

	private void setValue(int s, int u, int i, int t, int v, int j, int x, int y, Node t1, Node t2, Node[] nl1, Node[] nl2) {
		if ((s == u && u == i) && (t == v && v == j)) {
			e[s][u][i][t][v][j] = gamma(nl1[i].name, nl2[j].name);
		}
		else if ((s == u && u == i) || (t < v && v == j)) {
			e[s][u][i][t][v][j] = e[s][u][i][t][parentOf(j, nl2)][j-1] + gamma(' ', nl2[j].name);
		}
		else if ((s < u && u == i) || (t == v && v == j)) {
			e[s][u][i][t][v][j] = e[s][parentOf(i, nl1)][i-1][t][v][j] + gamma(nl1[i].name, ' ');
		}
		else {
			e[s][u][i][t][v][j] = min(e[s][x][i][t][v][j], min(e[s][u][i][t][y][j], e[s][u][x-1][t][v][y-1] + e[x][x][i][y][y][j]));
		}
	}


	// Performs the first step of the computations. Sets the values in the
	// array e.

	private void step1(Node t1, Node t2, Node[] nl1, Node[] nl2) {
		int x = -1;
		int y = -1;

		for (int i = 1; i <= t1.treeSize(); ++i) {
			for (int j = 1; j <= t2.treeSize(); ++j) {
				int u = i;
				while (u != 0) {
					int s = u;
					while (s != 0) {
						int v = j;
						while (v != 0) {
							int t = v;
							while (t != 0) {
								setValue(s, u, i, t, v, j, x, y, t1, t2, nl1, nl2);
								t = parentOf(t, nl2);
							}
							y = v;
							v = parentOf(v, nl2);
						}
						s = parentOf(s, nl1);
					}
					x = u;
					u = parentOf(u, nl1);
				}
			}
		}
	}


	// Performs the second step of the computations. Sets the values in the
	// array cc.

	private void step2(Node t1, Node t2, Node[] nl1, Node[] nl2) {
		cc[1][1] = 0;

		for (int i = 2; i <= t1.treeSize(); ++i) {
			cc[i][1] = i;
		}

		for (int j = 2; j <= t2.treeSize(); ++j) {
			cc[1][j] = j;
		}

		for (int i = 2; i <= t1.treeSize(); ++i) {
			for (int j = 2; j <= t2.treeSize(); ++j) {
				cc[i][j] = 99999;
				int s = parentOf(i, nl1);
				while (true) {
					int t = parentOf(j, nl2);
					while (true) {
						cc[i][j] = min(cc[i][j], cc[s][t] + e[s][parentOf(i, nl1)][i-1][t][parentOf(j, nl2)][j-1] -
								gamma(nl1[s].name, nl2[t].name));
						if (t == 1)
							break;
						t = parentOf(t, nl2);
					}
					if (s == 1)
						break;
					s = parentOf(s, nl1);
				}
				cc[i][j] = cc[i][j] + gamma(nl1[i].name, nl2[j].name);
			}
		}
	}


	// Performs the third step of the computations. Sets the values in the
	// array d.

	private void step3(Node t1, Node t2, Node[] nl1, Node[] nl2) {
		d[1][1] = 0;
		for (int i = 2; i <= t1.treeSize(); ++i) {
			d[i][1] = d[i-1][1] + gamma(nl1[i].name, ' ');
		}
		for (int j = 2; j <= t2.treeSize(); ++j) {
			d[1][j] = d[1][j-1] + gamma(' ', nl2[j].name);
		}
		for (int i = 2; i <= t1.treeSize(); ++i) {
			for (int j = 2; j <= t2.treeSize(); ++j) {
				d[i][j] = min(cc[i][j], min(d[i-1][j] + gamma(nl1[i].name, ' '), d[i][j-1] + gamma(' ', nl2[j].name)));
			}
		}
	}


	// Return the node number of the parent of the given node.

	private int parentOf(int nodeNum, Node[] list) {
		int parentNum = 0;

		if (nodeNum > 1)
			parentNum = list[nodeNum].parentNumber;

		//System.out.println("parent of " + nodeNum + " is " + parentNum);
		return parentNum;
	}


	public static int gamma(char p, char q) {
		//System.out.println("comparing '" + p + "' to '" + q + "'");
		return (p == q) ? 0 : 1;
	}


	public static int min(int a, int b) {
		return (a < b) ? a : b;
	}
}




/**
 * This class represents one node in the tree. Each node can have
 * a maximum of MAXCHILDREN children.
 *
 * Many of the procedures of the original Turing code have been placed
 * in this class. For example, given a Node, it will return the size
 * of its subtree, and it will display itself.
 */
class Node {
	public static int MAXCHILDREN = 10;

	private static String blanks = "                    ";

	public char name;
	public int number;		// a unique number for each node; also the index into node list
	public int parentNumber;
	
	private Node[] children;
	private int nextChild;

	public Node(char name, int number) {
		this.name = name;
		this.number = number;
		this.parentNumber = 0;		// no parent yet
		nextChild = 0;				// no children yet
		children = new Node[MAXCHILDREN];
	}

	public void addChild(Node child) {
		if (nextChild < MAXCHILDREN) {
			children[nextChild++] = child;
			child.parentNumber = number;	// this node is the child's parent
		}
	}

	public int treeSize() {
		int size = 1;		// automatically count this node

		for (int i = 0; i < nextChild; ++i) {
			size += children[i].treeSize();
		}

		return size;
	}

	public void printTree() {
		printTree(0);
	}

	private void printTree(int indent) {
		//System.out.println(blanks.substring(0, indent) + number + ":" + name);
		System.out.println(blanks.substring(0, indent) + name + "[" + number + ">" + ((parentNumber > 0) ? Integer.toString(parentNumber) : "*") + "]");
		for (int i = 0; i < nextChild; ++i) {
			children[i].printTree(indent + 1);
		}
	}
}


/**
 * The original Turing code used procedure parameters that were passed
 * by reference, so the recursive function always had access to the
 * latest value of the parameters, and so the higher level calls
 * could "see" the updated value when the lower level calls returned.
 * Java cannot pass an int by reference, so we substitute this class.
 * the "parameters" are given to the constructor, where they are saved
 * as instance variables. The instances of the recursive method all
 * use and update the instance variables.
 *
 * The description string consists of single-character node names
 * nested within parentheses. The nesting indicates the parent-child
 * relationships. For example: "(a(b(c)))", or "(a(b)(c))".
 *
 * In this code a Node can be indicated by a reference to the Node
 * object, or by using the node's number. This version of TreeMaker
 * builds two structures from the description string. There is a tree of
 * Nodes, and there is also a list of Nodes. The list is used to find a
 * Node by its number, since the nth item in the list is a reference to
 * the Node with number n.
 *
 * The makeTree method returns the root node of the tree directly. After
 * that, the application may call the getList method to get the list of
 * nodes that matches the tree.
 *
 * This code contains only minimal error checking. Invalid input will
 * definitely cause erroneous output.
 */
class TreeMaker {
	private String s;	// string defining the tree to build
	private int i;		// count of number of characters parsed to date
	private int nextNodeNumber;
	private Node[] list;

	public TreeMaker(String s, int maxNodes) throws Exception {
		this.s = s;
		list = new Node[maxNodes + 1];	// list[0] will be unused; indexes start at 1
		nextNodeNumber = 1;	// node numbers start at 1 to match Turing code

		if (s.charAt(0) == '(') {
			i = 1;		// the 0th char has been parsed
		}
		else {
			throw new Exception ("invalid input string -- needs leading '('");
		}
	}


	// Make a subtree starting with the next part of the string
	// to be parsed.
	// The next character to be parsed should be the node's name.

	public Node makeTree() {
		Node newNode = new Node(s.charAt(i++), nextNodeNumber++);
		list[newNode.number] = newNode;

		while (s.charAt(i++) == '(') {
			newNode.addChild(makeTree());
		}

		// If the last char read wasn't '(', it must have been ')'
		// (assuming valid input). It indicates the end of the list
		// of children for the subtree being constructed, and the
		// subtree's root can be returned to the calling code.

		return newNode;
	}


	// After the tree has been constructed, the list of nodes can
	// be retrieved.

	public Node[] getList() {
		return list;
	}
}
