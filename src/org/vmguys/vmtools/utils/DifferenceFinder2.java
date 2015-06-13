/* ====================================================================
 * The VM Systems, Inc. Software License, Version 1.0
 *
 * Copyright (c) 2001 VM Systems, Inc.  All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED PURSUANT TO THE TERMS OF THIS LICENSE.
 * ANY USE, REPRODUCTION, OR DISTRIBUTION OF THE SOFTWARE OR ANY PART
 * THEREOF CONSTITUTES ACCEPTANCE OF THE TERMS AND CONDITIONS HEREOF.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by 
 *        VM Systems, Inc. (http://www.vmguys.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "VM Systems" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For written
 *    permission, please contact info@vmguys.com.
 *
 * 5. VM Systems, Inc. and any other person or entity that creates or
 *    contributes to the creation of any modifications to the original
 *    software specifically disclaims any liability to any person or
 *    entity for claims brought based on infringement of intellectual
 *    property rights or otherwise. No assurances are provided that the
 *    software does not infringe on the property rights of others.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE TITLE
 * AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT SHALL VM SYSTEMS, INC.,
 * ITS SHAREHOLDERS, DIRECTORS OR EMPLOYEES BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE. EACH RECIPIENT OR USER IS SOLELY RESPONSIBLE
 * FOR DETERMINING THE APPROPRIATENESS OF USING AND DISTRIBUTING THE SOFTWARE
 * AND ASSUMES ALL RISKS ASSOCIATED WITH ITS EXERCISE OF RIGHTS HEREUNDER,
 * INCLUDING BUT NOT LIMITED TO THE RISKS (INCLUDING COSTS) OF ERRORS,
 * COMPLIANCE WITH APPLICABLE LAWS OR INTERRUPTION OF OPERATIONS.
 * ====================================================================
 */


package org.vmguys.vmtools.utils;

import java.util.List;
import java.util.Iterator;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/**
 * <p>This class contains methods to compare two JDOM trees and find
 * the differences. It computes the "cost" to convert one tree to the
 * other, and a set of operations to perform the conversion.</p>
 *
 * <p>The
 * algorithm used is described in Section 3.1 of <b>Tree-to-tree
 * Correction for Document Trees, Technical Report 95-327</b>. The
 * report can be found at <pre>ftp://ftp.qucis.queensu.ca/pub/reports/1995-372.ps</pre></p>
 */
public class DifferenceFinder2 {
	private final static boolean DEBUG = false;
	
	/**
	 * This boolean determines whether any comments will be saved with the
	 * operations.
	 */
	private boolean addComments;
	
	
	/**
	 * This boolean determines whether the result will include any rename
	 * operations.
	 */
	private boolean allowRename = true;


	/** Holds value of property discardWhitespace. */
	private boolean discardWhitespace;
	
	
	/**
	 * This indicates the namespace to use for Elements that are created.
	 */
	private Namespace namespace = Namespace.NO_NAMESPACE;
	
	/** 
	 * Specifies the callback to show progress during the
	 * computation. If null, no callback is needed.
	 */
	private ProgressReporter progressReporter;	
	
	/**
	 * Constructs a DifferenceFinder2 using the namespace of NO_NAMESPACE.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public DifferenceFinder2() {
		this(Namespace.NO_NAMESPACE, true, false);
	}
	
	
	/**
	 * Constructs a DifferenceFinder2 with the specified namespace.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public DifferenceFinder2(Namespace namespace) {
		this(namespace, true, false);
	}


	/**
	 * Constructs a Differencefinder with the specified namespace
	 * and setting for discardWhitespace. The addComments property
	 * will be set false.
	 * If discardWhitespace is true, any text content that contains only
	 * whitespace will be discarded, it will not be considered during the
	 * comparisons, and it will not appear in the output.
	 */
	public DifferenceFinder2(Namespace namespace, boolean discardWhitespace) {
		this(namespace, discardWhitespace, false);
	}
	
		
	/**
	 * Constructs a DifferenceFinder2 with the specified namespace,
	 * setting for discardWhitespace, and setting for addComments. If addComments
	 * is true, comments will be added to the output indicating the reason
	 * for each operation. This is most useful for debugging or tracing the
	 * operations.
	 */
	public DifferenceFinder2(Namespace namespace, boolean discardWhitespace, boolean addComments) {
		this.namespace = namespace;
		this.discardWhitespace = discardWhitespace;
		this.addComments = addComments;
		this.addComments = true;		// FOR TESTING!!
	}
	
	
	/**
	 * Same as the other version of findDifferences except for the types of
	 * the parameters. This just casts the Elements to DiffElements and calls
	 * the other version. It will throw an exception if the Elements can't
	 * be recast.
	 *
	 * @see #findDifferences(DiffElement, DiffElement)
	 */
	public CostOps findDifferences(Element root1, Element root2) throws JDOMException {
		try {
			// Assume we can cast the arguments to the desired type.
			
			return findDifferences((DiffElement)root1, (DiffElement)root2);
		}
		catch (ClassCastException e) {
			throw new JDOMException("can't cast argument to DiffElement", e);
		}
	}
	
	
	/**
	 * <p>Determines what needs to be done to convert the tree rooted
	 * at n1 into the tree rooted at n2. Once the
	 * minimum cost set of operations has been determined, it is
	 * returned to the caller as a CostOps.</p>
	 */
	public CostOps findDifferences(DiffElement root1, DiffElement root2) throws JDOMException {
		if (discardWhitespace) {
			discardWhitespace(root1);
			discardWhitespace(root2);
		}
		
		// The reference papers for the algorithms used here number the nodes
		// starting at 1. In order to avoid confusion, this code will do the same.
		// In some cases we want the node number to match an array index. Since
		// Java normally numbers array indexes starting at 0, we will implement
		// this by making the array larger than normal and ignoring the 0th
		// element. This will give the code more clarity and speed (no need to
		// add and subtract 1 everywhere) at the expense of some storage space.
		
		// Get lists of the Element nodes in each tree. This allows quick access to
		// any node using its node number as an index into the array.
		// Node #1 is in position 1, node #2 is in position 2, etc. The root 
		// node, #n, is in position n, which is the last element of the array.
		//
		// NOTE: These arrays do *not* include JDOM tree nodes that are not
		// Elements (various types of content, such as Comments, Strings, etc.).
		
		DiffElement[] tree1 = getNodesAsArray(root1);
		DiffElement[] tree2 = getNodesAsArray(root2);
		
		if (DEBUG) {
			for (int i = 1; i < tree1.length; ++i) {
				DiffElement n = tree1[i];

				DiffElement parent = (DiffElement)n.getParent();
				System.out.print(i + " (" + n.getName() + "): parent=");
				if (parent == null)
					System.out.print("none");
				else
					System.out.print(parent.getNodeNumber() + " (" + parent.getName() + ")");
				System.out.println();
				
				Object[] content = n.getContentAsArray();
				for (int j = 0; j < content.length; ++j) {
					System.out.println("    " + content[j]);
				}
			}
		}
		
		// Allocate the distance array, which will record the distance between
		// each pair of nodes in the two trees. It is named "d" to match the
		// name used in the papers.
		
		CostOps[][] d = new CostOps[tree1.length][tree2.length];
		
		// Initialize a counter of the number of entries that have been solved.
		
		int solvedEntries = 0;
		int totalEntries = tree1.length * tree2.length;
		if (DEBUG) { System.out.println("difference array contains " + totalEntries + " entries (" + tree1.length + " x " + tree2.length + ")"); }

		// Look at each node in tree 1.
		
		for (int i1 = 1; i1 < tree1.length; ++i1) {
			// We only need to process key root nodes.
			
			if (tree1[i1].hasLeftSibling() || tree1[i1] == root1) {
				// Get a reference to the node being processed, and call it "x"
				// to match the papers.
				
				DiffElement x = tree1[i1];
				
				// Get the number of x's leftmost leaf.
				
				int xLeftLeafNum = x.getLeftmostLeafNum();
				
				// For each node in tree 1, look at each node in tree 2.
				
				for (int i2 = 1; i2 < tree2.length; ++i2) {
					// We only need to process key root nodes.

					if (tree2[i2].hasLeftSibling() || tree2[i2] == root2) {
						// Get a reference to the node being processed, and call it "y"
						// to match the papers.
				
						DiffElement y = tree2[i2];
						
						// Get the number of y's leftmost leaf.
						
						int yLeftLeafNum = y.getLeftmostLeafNum();
						
						// Create a temporary forest distance table for this comparison.
						// As with the tree arrays, we want to use the node numbers as
						// indexes into this array, so the numbers of the two nodes
						// will determine its dimensions. Due to the postorder numbering
						// of the tree, we know that all descendants will have numbers that
						// are less than the root nodes.
						
						CostOps[][] fd = new CostOps[x.getNodeNumber() + 1][y.getNodeNumber() + 1];
						
						if (DEBUG) { System.out.println("comparing " + x.getName() + " to " + y.getName()); }
						
						fd[xLeftLeafNum - 1][yLeftLeafNum - 1] = new CostOps();
						if (DEBUG) { System.out.println("a) fd[" + (xLeftLeafNum - 1) + "][" + (yLeftLeafNum - 1) + "] = 0"); }
						
						for (int i = xLeftLeafNum; i <= x.getNodeNumber(); ++i) {
							fd[i][yLeftLeafNum - 1] = fd[tree1[i].getLeftmostLeafNum() - 1][yLeftLeafNum - 1].combine(deleteSubtree(tree1[i]));
							if (DEBUG) { System.out.println("b) fd[" + (i) + "][" + (yLeftLeafNum - 1) + "] = " + fd[i][yLeftLeafNum - 1]); }
						}
						
						for (int j = yLeftLeafNum; j <= y.getNodeNumber(); ++j) {
							fd[xLeftLeafNum - 1][j] = fd[xLeftLeafNum - 1][tree2[j].getLeftmostLeafNum() - 1].combine(insertSubtree(tree2[j]));
							if (DEBUG) { System.out.println("c) fd[" + (xLeftLeafNum - 1) + "][" + (j) + "] = " + fd[xLeftLeafNum - 1][j]); }
						}
						
						for (int i = xLeftLeafNum; i <= x.getNodeNumber(); ++i) {
							for (int j = yLeftLeafNum; j <= y.getNodeNumber(); ++j) {
								// NOTE: This code generates a lot of objects that are simply
								// discarded. An alternative would be to compute just the cost
								// (without the operations) first, and then generate the operations
								// for the minimum cost. One method might be to pass a boolean to
								// the methods to indicate whether they should create the various
								// operation objects or not. If not, we would obtain a CostOps
								// object that has the cost, but no list of operations. After the
								// minimum cost is chosen, the appropriate method could be called
								// again to generate a full CostOps object.
								
								
								// Select the minimum of three different values. For clarity we'll
								// compute the values first and then take the minimum.
								//
								// NOTE: The published algorithm computes four different values,
								// one being the cost to create a new node within the tree. However,
								// that operation is complicated by the need to determine which other
								// nodes will become its children and which will not. We have decided
								// to forego that operation for the moment.
								
								int iLeftLeafNum = tree1[i].getLeftmostLeafNum();
								int jLeftLeafNum = tree2[j].getLeftmostLeafNum();

								CostOps deleteCost = fd[i - 1][j].combine(deleteNode(tree1[i]));
								//CostOps createCost = fd[i][j - 1].add(costToCreate(tree2[j]));
								CostOps deleteSubtreeCost = fd[iLeftLeafNum - 1][j].combine(deleteSubtree(tree1[i]));
								CostOps insertSubtreeCost = fd[i][jLeftLeafNum - 1].combine(insertSubtree(tree2[j]));
								
								CostOps m = min(deleteCost, deleteSubtreeCost, insertSubtreeCost);

								// See if the node pairs (i and x, and j and y) have common leftmost
								// leaves.
								
								if (iLeftLeafNum == xLeftLeafNum && jLeftLeafNum == yLeftLeafNum) {
									// Yes. We can add another entry to the tree distance table.
									
									fd[i][j] = min(m, fd[i-1][j-1].combine(editNode(tree1[i], tree2[j])));
									d[i][j] = fd[i][j];
									++solvedEntries;
									
									if (DEBUG) { System.out.println("d) d[" + i + "][" + j + "] = fd[" + i + "][" + j + "] = " + fd[i][j]); }
								}
								else {
									// No. We just update the forest distance table.
									
									fd[i][j] = min(m, fd[iLeftLeafNum - 1][jLeftLeafNum - 1].combine(d[i][j]));
									if (DEBUG) { System.out.println("e) fd[" + i + "][" + j + "] = " + fd[i][j]); }
								}
							}
						}
						
						if (DEBUG) {
							System.out.println("forest distance table:");
							for (int i = 0; i < fd.length; ++i) {
								System.out.print(i + ":  ");
								for (int j = 0; j < fd[0].length; ++j) {
									if (fd[i][j] != null) {
										System.out.print(fd[i][j].getCost() + "  ");
									}
									else {
										System.out.print("-  ");
									}
								}
								System.out.println();
							}
							System.out.println();
							
							System.out.println("tree distance table:");
							for (int i = 1; i < d.length; ++i) {
								System.out.print(i + ":  ");
								for (int j = 1; j < d[0].length; ++j) {
									System.out.print(d[i][j] + "  ");
								}
								System.out.println();
							}
							System.out.println();
						}
					}
					
					if (progressReporter != null) {
						progressReporter.showProgress(solvedEntries, totalEntries);
					}
					
					if (DEBUG) { 
						System.out.println();
						System.out.println("solved " + solvedEntries + " of " + totalEntries);
					}
				}
			}
		}
		
		if (progressReporter != null) {
			progressReporter.showProgress(totalEntries, totalEntries);
		}
		
		CostOps s = d[tree1.length-1][tree2.length-1];

		if (DEBUG) {
			System.out.println("Solution: " + s);
		}
		
		return s;
	}
    
    
	/**
	 * Walks a tree and places references to the nodes into an array. This
	 * allows quick access to any node by its node number. This only
	 * includes Element nodes, not the various other types of content.
	 */
	private DiffElement[] getNodesAsArray(DiffElement root) {
		// We know that the nodes are numbered postorder, so all descendants
		// of the root will have node numbers less than the root. Therefore
		// we know how big to make the array simply by looking at the root's
		// node number.
		
		DiffElement[] a = new DiffElement[root.getNodeNumber() + 1];
		
		a[root.getNodeNumber()] = root;
		
		getNodesAsArray2(a, root);
		
		return a;
	}
    
    
	private void getNodesAsArray2(DiffElement[] a, DiffElement n) {
		List children = n.getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			DiffElement child = (DiffElement)it.next();
			a[child.getNodeNumber()] = child;
			getNodesAsArray2(a, child);
		}
	}
	
	
	/**
	 * Returns the cost to delete the specified node.
	 */
	private CostOps deleteNode(DiffElement n) {
		return new CostOps(new ElementDeleteOperation(n));
	}
	
	
	/**
	 * Returns the cost to delete a subtree.
	 */
	private CostOps deleteSubtree(DiffElement n) {
		return new CostOps(new SubtreeDeleteOperation(n));
	}
	
	
	/**
	 * Returns the cost to insert a subtree.
	 */
	private CostOps insertSubtree(DiffElement n) {
		return new CostOps(new SubtreeInsertOperation(n));
	}
	
	
	/**
	 * Returns the cost to edit a node. This includes changing the node's
	 * name, changing the node's attributes, and changing any of the node's
	 * non-Element content (i.e. data, but not children).
	 */
	private CostOps editNode(DiffElement n1, DiffElement n2) {
		CostOps rslt = new CostOps();
		
		if (!n1.getName().equals(n2.getName())) {
			if (allowRename) {
				rslt.add(new ElementRenameOperation(n1, n2.getName()));
			}
			else {
				// If the node names are different, but the rename operation
				// is not allowed, make this operation so expensive that it
				// won't be selected. This is not foolproof; we should probably
				// find some better way to indicate a prohibited operation.
				// Maybe the returned CostOps could be null.
				
				rslt.add(10000);
			}
		}

		editContents(n1, n2, rslt);
		editAttributes(n1, n2, rslt);
		
		return rslt;
	}
	
	
	/**
	 * <p>Adds the cost to modify one node's contents to make it the
	 * same as another node. The cost and operations are added to
	 * the CostOps object passed in by the caller.</p>
	 *
	 * <p>This method really needs a makeover. When comparing the
	 * nodes' contents, we should be doing something similar to diff,
	 * where we look for the longest matching groups of content. Then
	 * the non-matching parts could be change using modify, insert, and
	 * delete operations.</p>
	 */
	private void editContents(DiffElement n1, DiffElement n2, CostOps rslt) {
		// Get an array of each node's contents.
		
		Object[] content1 = n1.getContentAsArray();
		Object[] content2 = n2.getContentAsArray();
		
		if (content1.length == 1 && content2.length == 1) {
			// Handle this as a special case. If the contents are different
			// it is more efficient to use a change operation than a
			// delete and an insert.
			
			if (!content1[0].equals(content2[0])) {
				rslt.add(new ContentModifyOperation(n1, content1[0], content2[0]));
			}
		}
		else {
			// Compare the two arrays. Since order is important, we just
			// have to walk each array looking for matches or mismatches.

			int i1 = 0, i2 = 0;

			while (i1 < content1.length && i2 < content2.length) {
				if (content1[i1].equals(content2[i2])) {
					++i1;
					++i2;
				}
				else {
					rslt.add(new ContentDeleteOperation(n1, content1[i1]));
					++i1;
				}
			}

			while (i1 < content1.length) {
				rslt.add(new ContentDeleteOperation(n1, content1[i1]));
				++i1;
			}


			while (i2 < content2.length) {
				rslt.add(new ContentAddOperation(n2, content2[i2]));
				++i2;
			}
		}
	}
	
	
	/**
	 * Adds the cost to modify one node's attributes to make it the
	 * same as another node. The cost and operations are added to
	 * the CostOps object passed in by the caller.
	 */
	private void editAttributes(DiffElement n1, DiffElement n2, CostOps rslt) {
		// Get an array of each node's attributes. The attributes
		// are sorted by name for easy comparison.
		
		Attribute[] attr1 = n1.getAttributesAsArray();
		Attribute[] attr2 = n2.getAttributesAsArray();
		
		// Compare the two lists.

		int i1 = 0, i2 = 0;
		
		while (i1 < attr1.length && i2 < attr2.length) {
			int nameCompare = attr1[i1].getName().compareTo(attr2[i2].getName());
			
			if (nameCompare == 0) {
				// We have attributes with the same name.
				// See if they have the same value.
				
				if (!attr1[i1].getValue().equals(attr2[i2].getValue())) {
					rslt.add(new AttributeModifyOperation(n1, attr2[i2], null));
				}

				++i1;
				++i2;
			}
			else if (nameCompare < 0) {
				// The name of attr1 is less than the name of attr2, which
				// indicates attr1 is not an attribute of n2.
				// Add an operation to delete the attribute and
				// then advance in list 1 only.
				
				rslt.add(new AttributeDeleteOperation(n1, attr1[i1]));
				++i1;
			}
			else {
				// The name of attr1 is greater than the name of attr2, which
				// indicates attr2 is not an attribute of n1.
				// Add an operation to add the attribute and
				// then advance in list 2 only.
				
				rslt.add(new AttributeInsertOperation(n1, attr2[i2], null));
				++i2;
			}
		}
		
		// If the lists are different lengths, we need to process
		// the items remaining in the longer list.
		
		while (i1 < attr1.length) {
			rslt.add(new AttributeDeleteOperation(n1, attr1[i1]));
			++i1;
		}
		
		while (i2 < attr2.length) {
			rslt.add(new AttributeInsertOperation(n1, attr2[i2], null));
			++i2;
		}
	}
	
	
	/**
	 * Finds the minimum of two ints.
	 */
	private int min(int a, int b) {
		return (a < b) ? a : b;
	}
	
	
	/**
	 * Finds the minimum of four ints.
	 */
	private int min(int a, int b, int c, int d) {
		return min(min(a, b), min(c, d));
	}
	
	
	/**
	 * Finds the minimum of two CostOps. If the costs are the same,
	 * preference is given to the first one.
	 */
	private CostOps min(CostOps a, CostOps b) {
		return (a.getCost() <= b.getCost()) ? a : b;
	}
	
	
	/**
	 * Finds the minimum of three CostOps. If the costs of two are
	 * the same, preference is given to the one earlier in the list.
	 */
	private CostOps min(CostOps a, CostOps b, CostOps c) {
		return min(min(a, b), c);
	}
	
    
	/**
	 * Finds the minimum of four CostOps. If the costs of two are
	 * the same, preference is given to the one earlier in the list.
	 */
	private CostOps min(CostOps a, CostOps b, CostOps c, CostOps d) {
		return min(min(a, b), min(c, d));
	}
	
	
	/**
	 * Walks a JDOM tree removing any text content that contains only whitespace.
	 * NOTE: This modifies the tree.
	 */
	private void discardWhitespace(Element node) {
		// Get all the content of this node.
		
		List contents = node.getContent();
		
		for (int i = contents.size() - 1; i >= 0; --i) {
			Object x = contents.get(i);
			
			if (x instanceof String) {
				// Remove any unnecessary whitespace from the string.
				// If the string contains only whitespace, remove it
				// entirely from the parent's content.
				
				String s = ((String)x).trim();
				
				if (s.length() < ((String)x).length()) {
					contents.remove(i);
					if (s.length() > 0) {
						contents.add(i, s);
					}
				}
			}
			else if (x instanceof Element) {
				// Process this child just like its parent.
				
				discardWhitespace((Element)x);
			}
		}
	}

	
	/** Getter for property discardWhitespace.
	 * @return Value of property discardWhitespace.
	 */
	public boolean isDiscardWhitespace() {
		return discardWhitespace;
	}
	
	/** Setter for property discardWhitespace.
	 * @param discardWhitespace New value of property discardWhitespace.
	 */
	public void setDiscardWhitespace(boolean discardWhitespace) {
		this.discardWhitespace = discardWhitespace;
	}
	
	
	/** Getter for property addComments.
	 * @return Value of property addComments.
	 */
	public boolean isAddComments() {
		return addComments;
	}
	
	/** Setter for property addComments.
	 * @param addComments New value of property addComments.
	 */
	public void setAddComments(boolean addComments) {
		this.addComments = addComments;
	}
	
	/**
	 * Returns the current ProgressReporter, or null if there
	 * isn't one.
	 */
	public ProgressReporter getProgressReporter() {
		return this.progressReporter;
	}
	
	/**
	 * This method sets the progress reporter. When generateUpdate is called,
	 * it will call the progress reporter with updates on the percentage of
	 * the operation that has been performed. This can be useful during
	 * lengthy operations to provide feedback to the user.
	 */
	public void setProgressReporter(ProgressReporter progressReporter) {
		this.progressReporter = progressReporter;
	}
	
	/** Getter for property allowRename.
	 * @return Value of property allowRename.
	 */
	public boolean isAllowRename() {
		return this.allowRename;
	}
	
	/** Setter for property allowRename.
	 * @param allowRename New value of property allowRename.
	 */
	public void setAllowRename(boolean allowRename) {
		this.allowRename = allowRename;
	}
	
}


/*
 * $Log: DifferenceFinder2.java,v $
 * Revision 1.8  2002/02/06 16:06:40  gwheeler
 * Modified the parameters of showProgress to match the changes to the interface.
 * Added an optimisation to editContents.
 *
 * Revision 1.7  2002/02/01 20:43:38  gwheeler
 * Added code to set/get the allowRename property, and added code to check
 * it when comparing nodes.
 *
 * Revision 1.6  2002/01/31 22:26:01  gwheeler
 * Updated javadocs.
 *
 * Revision 1.5  2002/01/29 16:25:52  gwheeler
 * Added code to support ProgressReporter.
 *
 * Revision 1.4  2002/01/28 21:07:40  gwheeler
 * Finished findDifferences so it returns the desired result.
 * Some minor changes where the operations objects are instantiated to take
 * advantage of their new contstructors.
 * Some minor debug code changes.
 *
 * Revision 1.3  2002/01/24 18:04:32  gwheeler
 * Numerous changes. This version works quite well.
 *
 * Revision 1.2  2002/01/22 19:25:01  gwheeler
 * This version uses SimpleCostOps to track the operations needed. It works
 * fine but isn't complete. It's being checked in for posterity.
 *
 * Revision 1.1  2002/01/18 16:30:34  gwheeler
 * no message
 *
 */
