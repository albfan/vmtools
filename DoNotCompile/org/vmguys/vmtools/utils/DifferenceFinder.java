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
import java.util.HashMap;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;


/*
 * $Log: DifferenceFinder.java,v $
 * Revision 1.1  2002/01/31 14:22:45  gwheeler
 * No longer used. Replaced by DifferenceFinder2.
 *
 * Revision 1.12  2001/11/30 21:31:05  gwheeler
 * Factored convertCost into several smaller methods. The result is much
 * clearer and easier to track.
 *
 * convertCost now may return null if the one node cannot be converted
 * to the other. This means there may be null entries in the costs table,
 * so other code must be updated to protect itself.
 *
 * Simplified the code that finds matching children. It no longer looks
 * for children in different positions.
 *
 * Added code to add a cost of two children being compared are
 * not the same child number. The cost includes operations to
 * move the child.
 *
 * Revision 1.11  2001/11/16 15:02:23  gwheeler
 * Minor comment changes. No functional changes.
 *
 * Revision 1.10  2001/10/26 21:01:38  gwheeler
 * Removed debug print statements.
 *
 * Revision 1.9  2001/10/26 15:02:23  gwheeler
 * Major changes to convertCost() to attempt to reduce the amount ot
 * work being performed, and to be sensitive to the order of the
 * content of a node.
 * Changed code that instantiates the various operations to match the
 * changes to their classes.
 *
 * Revision 1.8  2001/10/22 19:48:28  gwheeler
 * Changed constructors. Re-arranged the order of the parameters to
 * make the least-used parameter last.
 *
 * Revision 1.7  2001/10/17 18:54:03  gwheeler
 * Added support for namespaces.
 * Added a parameter to the constructor to take the namespace to be used
 * when creating any new Elements.
 * Changed the code that instantiates the operations to pass the
 * namespace.
 * Changed the comparison between two Elements to include their
 * namespaces.
 * Changed the comparison between Attributes to include their
 * namespaces.
 *
 * Revision 1.6  2001/10/16 20:54:21  gwheeler
 * Changed calls to deleteContent to reflect its new operation.
 *
 * Revision 1.5  2001/10/16 14:12:19  gwheeler
 * Added code to discard unnecessary whitespace from text content
 * in the tree. Added a constructor with a second parameter to turn
 * on/off this feature.
 *
 * Revision 1.4  2001/10/15 18:32:08  gwheeler
 * Updated so it compares ALL content of one node to another.
 * Various side-effects include: change declaration of some parameters
 * from Element to Object, declaring exceptions thrown, etc.
 *
 * Revision 1.3  2001/10/11 15:54:00  gwheeler
 * Intermediate version. I want to save this before I make some changes
 * to it for testing.
 *
 * Revision 1.2  2001/10/05 15:27:41  gwheeler
 * Modified comments to clarify the ordering of the operations. No code
 * changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.10  2001/09/24 19:45:50  gwheeler
 * Corrected the use of java.lang.String.trim(). It doesn't trim the string on which
 * it is invoked -- it returns a trimmed version of the string that must be
 * assigned to a new string.
 *
 * Revision 1.9  2001/09/12 14:05:15  gwheeler
 * *** empty log message ***
 *
 * Revision 1.8  2001/07/25 20:06:34  gwheeler
 * Changed name of method from "convertCost" to "findDifferences".
 *
 * Revision 1.7  2001/07/17 15:14:54  gwheeler
 * Made class NodeInfo a private inner class.
 *
 * Revision 1.6  2001/07/13 20:17:10  gwheeler
 * Changed the way node's numbers are saved. Instead of writing an attribute to the node
 * we now maintain a Map that relates a node to an object that contains extra information,
 * such as the nodeNumber, the childNumber, the XPath, and the treeSize.
 *
 * Revision 1.5  2001/07/12 17:15:02  gwheeler
 * Added addComments property, and added constructor to set it. Modified code to check
 * value of addComments when creating operation objects.
 *
 * Revision 1.4  2001/07/10 18:42:00  gwheeler
 * The class methods are no longer static. In order to number the nodes (see below)
 * the class needs to keep an instance variable as a counter, so it must be instantiated
 * in order to work.
 *
 * Added code to assign a node number and child number to each node in the tree
 * for easier processing later.
 *
 * Corrected code with passes the child number to the insertTree method, based on the
 * new child number assigned to the node.
 *
 * Added code to pass a comment to each operation. This helps clarify things during testing,
 * but could be removed for production.
 *
 * Increased the cost of deleting and inserting a subtree.
 *
 * Revision 1.3  2001/07/09 16:13:56  gwheeler
 * Updated javadocs.
 *
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.2  2001/07/05 14:27:46  gwheeler
 * Removed code that expanded the list of operations to XML or a JDOM tree. That
 * code is now located in XmlDifferenceFinder and JdomDifferenceFinder.
 *
 * Revision 1.1  2001/07/03 13:31:04  gwheeler
 * New class name. Previously was ElementUtils.
 *
 * Revision 1.4  2001/07/02 16:00:24  gwheeler
 * Many changes from the previous version. Now uses the CostOps object to hold lists of
 * operations to be performed.
 *
 * Revision 1.3  2001/06/28 19:07:05  gwheeler
 * Numerous changes, fixes, enhancements, etc. This is an intermediate version.
 *
 * Revision 1.2  2001/06/25 18:38:22  gwheeler
 * Removed unused methods and added new ones.
 *
 * Added convertCost, which recursively compares two JDOM trees to determine how to
 * convert one to the other.
 *
 * Updated editCost, which determines the cost to convert a single Element to another.
 *
 * Revision 1.1  2001/06/22 18:11:15  gwheeler
 * Provides static methods that perform useful functions on/with JDOM Element objects.
 *
 */


/**
 * <p>This class contains methods to compare two JDOM trees and find
 * the differences. It computes the "cost" to convert one tree to the
 * other, and a set of operations to perform the conversion.</p>
 *
 * <p>There are some places where we maintain references to Elements
 * contained in one of the trees that is passed in as a parameter.
 * The references are used during the processing, but the referenced nodes
 * are not modified in any way.</p>
 */
public class DifferenceFinder {
	/**
	 * This boolean determines whether any comments will be saved with the
	 * operations.
	 */
	private boolean addComments;


	/**
	 * This number is used when assigning numbers to the nodes in the tree.
	 */
	private int currNodeNumber = 0;


	/**
	 * This map is used to hold extra information about the nodes in the tree.
	 * Each map entry consists of a reference to a node in the tree (the key),
	 * and a NodeInfo object (the value).
	 */
	private Map treeInfo;


	/**
	 * This is used to cache a NodeInfo object. It is shared by the methods
	 * nodeNumber, childNumber, treeSize, and others that need to get information
	 * from the treeInfo map.
	 */
	private NodeInfo cachedNodeInfo;


	/** Holds value of property discardWhitespace. */
	private boolean discardWhitespace;
	
	
	/**
	 * This indicates the namespace to use for Elements that are created.
	 */
	private Namespace namespace = Namespace.NO_NAMESPACE;
	
	
	/**
	 * Constructs a DifferenceFinder using the namespace of NO_NAMESPACE.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public DifferenceFinder() {
		this(Namespace.NO_NAMESPACE, true, false);
	}
	
	
	/**
	 * Constructs a DifferenceFinder with the specified namespace.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public DifferenceFinder(Namespace namespace) {
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
	public DifferenceFinder(Namespace namespace, boolean discardWhitespace) {
		this(namespace, discardWhitespace, false);
	}
	
		
	/**
	 * Constructs a DifferenceFinder with the specified namespace,
	 * setting for discardWhitespace, and setting for addComments. If addComments
	 * is true, comments will be added to the output indicating the reason
	 * for each operation. This is most useful for debugging or tracing the
	 * operations.
	 */
	public DifferenceFinder(Namespace namespace, boolean discardWhitespace, boolean addComments) {
		this.namespace = namespace;
		this.discardWhitespace = discardWhitespace;
		this.addComments = addComments;
		treeInfo = new HashMap();
	}


	/**
	 * <p>Determines what needs to be done to convert the tree rooted
	 * at n1 into the tree rooted at n2. Once the
	 * minimum cost set of operations has been determined, it is
	 * returned to the caller as a CostOps.</p>
	 *
	 * <p>The order of the operations in the CostOps result is such that
	 * one operation will not alter the xpath for any of the subsequent
	 * operations. Operations on children are listed before any operations
	 * on their parents, most operations on siblings are listed in right-to-left
	 * order, except insertions of new children which are listed in left-to-right
	 * order.</p>
	 */
	public CostOps findDifferences(Element n1, Element n2) throws JDOMException {
		//System.out.println("finding differences between " + n1.getName() + " and " + n2.getName());

		if (discardWhitespace) {
			discardWhitespace(n1);
			discardWhitespace(n2);
		}
		
		buildTreeInfo(n1, null, 1);
		buildTreeInfo(n2, null, 1);

		CostOps cost = convertCost(n1, n2, null, "/" + n1.getName(), 0);
		
		if (cost == null)
			cost = new CostOps();
		
		// Sort the operations based on the node numbers. The numbers
		// are assigned in buildTreeInfo, and assure the correct order
		// of operations.
		
		cost.sortOps();
		
		return cost;
	}


	/**
	 * <p>Determines what needs to be done to convert obj1 into obj2. Once the
	 * minimum cost set of operations has been determined, it is
	 * returned to the caller as a CostOps.</p>
	 *
	 * <p>If the cost cannot be determined, the result is null. This may happen
	 * if the computations required to find the cost will take too long. It will
	 * also happen if obj1 cannot be converted to obj2 at any cost.</p>
	 *
	 * <p>The parentXpath parameter is the xpath to n1's parent. It may be
	 * null if n1 is the root node. The xpath parameter is the xpath to
	 * n1.</p>
	 *
	 * <p>The tree walk is performed in post-order, which is to say a node's
	 * children are processed before the node is.</p>
	 */
	private CostOps convertCost(Object obj1, Object obj2, String parentXpath, String xpath, int level) throws JDOMException {
		CostOps rslt;
		
		//System.out.println(indent(level) + "comparing " + getFullXpath(obj1));
		//System.out.println(indent(level) + "      and " + getFullXpath(obj2));
									
		
		if (obj1.getClass().getName().equals(obj2.getClass().getName())) {
			// The two nodes are the same class. Compare them further.
			
			rslt = compareSame(obj1, obj2, parentXpath, xpath, level);
		}
		else {
			// The two nodes are different classes, and one cannot be
			// converted to the other.
			
			rslt = null;
		}

		/*
		if (rslt == null) {
			System.out.println(indent(level) + "cannot convert");
		}
		else {
			System.out.println(indent(level) + "cost is " + rslt.getCost());
		}
		 */

		return rslt;
	}

	

	/**
	 * Determines the cost to convert one object to another when we know they
	 * are the same type.
	 */
	private CostOps compareSame(Object obj1, Object obj2, String parentXpath, String xpath, int level) throws JDOMException {
		CostOps cost = new CostOps();
		
		if (obj1 instanceof Element) {
			// The two objects are both Elements.
			
			cost.add(compareElements((Element)obj1, (Element)obj2, parentXpath, xpath, level));
		}
		else if (obj1 instanceof String || obj1 instanceof Comment || obj1 instanceof EntityRef ||
														obj1 instanceof ProcessingInstruction || obj1 instanceof CDATA) {
			// The two objects are the same type, but they are not Elements. If they're
			// the same, nothing needs to be done. If they're different, we need to delete
			// one and insert the other.
			
			if (!obj1.equals(obj2)) {
				Element parent = parent(obj1);
				int childNum = childNumber(obj1);
				String comment = "modify some content";
				
				cost.add(modifyContent(xpath, obj2, addComments ? comment : null));
			}
		}
		else {
			// The two objects are the same type, but we don't recognize it.
			
			throw new JDOMException("unexpected type: " + obj1.getClass().getName());
		}
		
		// If they're not in the same positions, add the cost to move them.
		
		int childNum1, childNum2;
		
		if ((childNum1 = childNumber(obj1)) != (childNum2 = childNumber(obj2))) {
			Element parent = parent(obj1);
			cost.add(moveChild(obj1, obj2, childNum1, childNum2, parent, parentXpath, xpath, addComments ? "move a child": null));
		}
		
		return cost;
	}
	

	/**
	 * Determines the cost to convert one object to another when we know they
	 * are both Elements.
	 */
	private CostOps compareElements(Element n1, Element n2, String parentXpath, String xpath, int level) throws JDOMException {
		CostOps cost = new CostOps();
		
		if (n1.getName().equals(n2.getName()) && n1.getNamespace().equals(n2.getNamespace())) {
			cost = compareIdenticalElements(n1, n2, parentXpath, xpath, level);
		}
		else {
			// The nodes have different names and/or different namespaces,
			// which means they are different XML tags.
			// The only way to convert n1 into n2 is to delete it
			// and insert a new subtree.
			
			if (parentXpath == null) {
				// There are special operations to deal with the root
				// node.
				
				cost.add(replaceTree(n2, addComments ? ("replacing entire tree " + n1.getName() + " with " + n2.getName()) : null));
			}
			else {
				String comment = "replace subtree " + n1.getName() + " step ";
				cost.add(deleteSubtree(n1, xpath, addComments ? (comment + "1") : null));
				cost.add(insertSubtree(n1.getParent(), parentXpath, n2, childNumber(n1), addComments ? (comment + "2") : null));
			}
			
			// Since we've just specified that the whole subtree is going to be
			// replaced, there's no need to compare the children or perform
			// any recursion or walk the tree any further.
		}
		
		return cost;
	}
	
	
	/**
	 * Determines the cost to convert one object to another when we know they
	 * are both Elements with the same names and namespaces.
	 */
	private CostOps compareIdenticalElements(Element n1, Element n2, String parentXpath, String xpath, int level) throws JDOMException {
		CostOps cost = new CostOps();
		
		// The nodes have the same names (same XML types), so one subtree
		// can be converted into the other. We want to check the children
		// first, then compare the specified nodes.
		
		// Determine the cost to change their content. Process the
		// children before the parent.
		
		List content1 = n1.getContent();
		List content2 = n2.getContent();
		
		if (content1.size() > 0 && content2.size() > 0) { // both nodes have content
			cost = compareContent(n1, content1, n2, content2, parentXpath, xpath, level);
		}
		else if (content1.size() > 0) {	// n1 has children but n2 has no children
			// To convert one subtree into the other, all of n1's children
			// must be deleted.
			
			for (int i = content1.size() - 1; i >= 0; --i) {
				Object child = content1.get(i);
				
				if (child instanceof Element) {
					cost.add(deleteSubtree(n1, xpath, (Element)child, addComments ? "deleting all children" : null));
				}
				else {
					cost.add(deleteContent(child, childXpath(n1, xpath, child), addComments ? "deleting all content" : null));
				}
			}
		}
		else if (content2.size() > 0) {	// n2 has children but n1 has no children
			// To convert one subtree into the other, all of n2's children
			// must be created.
			
			for (int c = 0; c < content2.size(); ++c) {
				Object child = content2.get(c);
				
				if (child instanceof Element) {
					cost.add(insertSubtree(n1, xpath, (Element)child, c + 1, addComments ? "creating all new children" : null));
				}
				else {
					cost.add(addContent(n1, parentXpath, child, c + 1, addComments ? "creating all new content" : null));
				}
			}
		}
		else {	// both have no children
			
			// No changes are needed.
			
		}
		
		// Add the cost of converting the node's attributes.
		
		cost.add(compareNodeAttributes(n1, xpath, n2));
		
		return cost;
	}
	

	/**
	 * Determines the cost to convert one Element's content to match another Element.
	 */
	private CostOps compareContent(Element n1, List content1, Element n2, List content2, String parentXpath, String xpath, int level) throws JDOMException {
		final int rows = content1.size();
		final int cols = content2.size();
		
		// Create arrays to note which inputs and outputs
		// need no further consideration. These will be updated
		// by the submethods we call.
		
		boolean[] final1 = new boolean[rows];	// all false to start
		boolean[] final2 = new boolean[cols];	// all false to start
		
		// Create an array to hold the results. This will be
		// updated by the submethods we call.
		
		CostOps[][] costs = new CostOps[rows][cols];
		
		// Since, in many cases, the documents will be identical or
		// similar, we'll first look for children that are the same.
		// If found, we can eliminate other combinations of input and
		// output involving those children to save time.
		
		findIdenticalContent(n1, content1, final1, n2, content2, final2, costs, xpath, level);
		
		// Now look at the other combinations of children.
		
		compareAllContent(n1, content1, final1, n2, content2, final2, costs, xpath, level);

		// The submethods have entered costs into the costs array. They indicate
		// how to convert various combinations of input content to output content,
		// and at what cost. Array entries that are null indicate that a conversion
		// cannot be made in some combinations.
		//
		// Attempt to find the minimal cost to convert n1's children
		// to n2's children.

		CostOps cost = new CostOps();
		
		if (rows == 1 && cols == 1) {
			// This is a trivial case.

			cost.add(costs[0][0]);
		}
		else {
			MinFinder mf = new MinFinder(costs);
			int minCost = mf.getMinCost();

			if (minCost < Integer.MAX_VALUE) {
				// A minimal solution has been found.

				int[] solution = mf.getSolution();

				// The solution tells how n1's children will be made into
				// n2's children, at minimum cost. We need to build a new
				// CostOps to be returned to our caller that combines all
				// this information.
				//
				// Each column in costs represents one of n2's children,
				// and each row represents one of n1's children. Each element
				// of the array describes how to change from n1 to n2.
				// The solution indicates an entry from each column so we
				// can tell which children should be chosen. If an entry
				// in solution is Integer.MAX_VALUE it means n2's child
				// must be created from scratch.

				boolean[] chosen = new boolean[rows];   // autoinitialized to false

				int numChildren = content1.size();

				for (int c = 0; c < cols; ++c) {
					if (solution[c] == Integer.MAX_VALUE) {
						// We'll need to create a new child.

						Object child = content2.get(c);

						String comment = addComments ? ("adding new child to parent " + n1.getName()) : null;

						if (child instanceof Element) {
							cost.add(insertSubtree(n1, xpath, (Element)child, numChildren + 1, comment));
						}
						else {
							cost.add(addContent(n1, xpath, child, numChildren + 1, comment));
						}

						++numChildren;
					}
					else {
						// Add the cost to convert the one child to the other.

						cost.add(costs[solution[c]][c]);
						chosen[solution[c]] = true;
					}
				}

				// Any of n1's children that were not chosen must be deleted.

				for (int r = 0; r < rows; ++r) {
					if (!chosen[r]) {
						Object child = content1.get(r);

						if (child instanceof Element) {
							cost.add(deleteSubtree(n1, xpath, (Element)child, addComments ? "deleting unused subtree" : null));
						}
						else {
							cost.add(deleteContent(child, childXpath(n1, xpath, child), addComments ? ("deleting unused content " + child.getClass().getName()) : null));
						}
					}
				}
			}
			else {
				// A minimum solution could not be found. This may happen if the number
				// of combinations of children is too great, and the computation would
				// take too long. In that case, generate the brute force solution by
				// deleting n1 and inserting n2.

				if (parentXpath == null) {
					// There are special operations to deal with the root
					// node.

					cost.add(replaceTree(n2, addComments ? ("replacing entire tree " + n1.getName() + " with " + n2.getName()) : null));
				}
				else {
					String comment = "replace subtree " + n1.getName() + " step ";
					cost.add(deleteSubtree(n1, xpath, addComments ? (comment + "1") : null));
					cost.add(insertSubtree(n1.getParent(), parentXpath, n2, childNumber(n1), addComments ? (comment + "2") : null));
				}
			}
		}
		
		return cost;
	}
	
	
	/**
	 * Compares the children of the two nodes, in order. It computes the cost to convert one 
	 * to the other. Hopefully it will be zero.
	 * If it is, the row and column are marked as final because we don't need to do any other
	 * comparisons to convert that input to that output.
	 *
	 * NOTE: This method will call convertCost, which is indirectly recursive because
	 * this method was called indirectly from convertCost.
	 */
	private void findIdenticalContent(Element n1, List content1, boolean[] final1, Element n2, List content2, boolean[] final2, CostOps[][] costs, String xpath, int level) throws JDOMException {
		final int rows = content1.size();
		final int cols = content2.size();
		
		//System.out.println(indent(level) + "-findIdenticalContent");

		for (int i = 0; i < rows && i < cols; ++i) {
			if (costs[i][i] == null) {
				Object x1 = content1.get(i);
				Object x2 = content2.get(i);

				String newXpath = childXpath(n1, xpath, x1);

				costs[i][i] = convertCost(x1, x2, xpath, newXpath, level + 1);

				if (costs[i][i] != null && costs[i][i].getCost() == 0) {
					// The row and column are marked as final because we don't need to do any other
					// comparisons to convert that input to that output.

					//System.out.println(indent(level) + "-final " + i + ", " + i);
					final1[i] = true;
					final2[i] = true;
				}
			}
		}
	}	
	

	/**
	 * Compares one Element's children to another's. Children that have already been used in
	 * a zero-cost operation will not be included. The costs are placed into the
	 * costs array.
	 *
	 * NOTE: In some cases this method will call convertCost, which is indirectly recursive because
	 * this method was called indirectly from convertCost.
	 */
	private void compareAllContent(Element n1, List content1, boolean[] final1, Element n2, List content2, boolean[] final2, CostOps[][] costs, String xpath, int level) throws JDOMException {
		final int rows = content1.size();
		final int cols = content2.size();
		
		//System.out.println(indent(level) + "-compareAllContent");

		for (int r = 0; r < rows; ++r) {
			if (!final1[r]) {
				Object child1 = content1.get(r);
				boolean exactMatch = false;

				for (int c = 0; c < cols && !exactMatch; ++c) {
					if (!final2[c] && costs[r][c] == null) {
						Object child2 = content2.get(c);
						String newXpath = childXpath(n1, xpath, child1);

						costs[r][c] = convertCost(child1, child2, xpath, newXpath, level + 1);

						if (costs[r][c] != null && costs[r][c].getCost() == 0) {
							// The row and column are marked as final because we don't need to do any other
							// comparisons to convert that input to that output.

							//System.out.println(indent(level) + "-final " + r + ", " + c);
							final1[r] = true;
							final2[c] = true;
							exactMatch = true;
						}
					}
				}
			}
		}
	}
	
	

	/**
	 * Compares the attributes of two nodes and returns a CostOps showing how
	 * one set of attributes can be converted to the other.
	 */
	private CostOps compareNodeAttributes(Element n1, String xpath, Element n2) {
		CostOps cost = new CostOps();

		List attribs1 = n1.getAttributes();
		List attribs2 = n2.getAttributes();

		// See if n2 has all the attributes of n1.

		for (int i = 0; i < attribs1.size(); ++i) {
			Attribute attrib1 = (Attribute)attribs1.get(i);

			// See if the other node also has this attribute.
			
			Attribute attrib2 = n2.getAttribute(attrib1.getName(), attrib1.getNamespace());

			if (attrib2 == null) {
				// This attribute will have to be removed.
				cost.add(deleteAttr(n1, xpath, attrib1, addComments ? "removing attribute" : null));
			}
			else {
				if (!attrib1.getNamespace().equals(attrib2.getNamespace())) {
					// They have the same name, but they're in different namespaces.
					cost.add(deleteAttr(n1, xpath, attrib1, addComments ? "replacing attribute" : null));
					cost.add(insertAttr(n1, xpath, attrib2, addComments ? "replacing attribute" : null));
				}
				else if (!attrib1.getValue().equals(attrib2.getValue())) {
					cost.add(modifyAttr(n1, xpath, attrib2, addComments ? "modifying attribute" : null));
				}
			}
		}

		// See if n1 has all the attributes of n2.

		for (int i = 0; i < attribs2.size(); ++i) {
			Attribute attrib2 = (Attribute)attribs2.get(i);

			// See if the other node also has this attribute.
			
			Attribute attrib1 = n1.getAttribute(attrib2.getName(), attrib2.getNamespace());

			if (attrib1 == null) {
				// This attribute will have to be added.
				cost.add(insertAttr(n1, xpath, attrib2, addComments ? "adding new attribute" : null));
			}
		}
		
		return cost;
	}
	
	
	/**
	 * Generates the operation needed to replace an entire tree with another.
	 */
	private CostOps replaceTree(Element newTree) {
		return replaceTree(newTree, null);
	}


	/**
	 * Generates the operation needed to replace an entire tree with another.
	 */
	private CostOps replaceTree(Element newTree, String comment) {
		ReplaceOperation ro = new ReplaceOperation(newTree, namespace, comment);
		return new CostOps(ro);
	}


	/**
	 * Generates the operation needed to insert a subtree.
	 */
	private CostOps insertSubtree(Element parent, String parentXpath, Element child, int childNum, String comment) {
		//System.out.println("insertSubtree: xpath = " + parentXpath);
		
		// If the child being inserted has no children, this can be treated as ElementInsert.
		
		if (child.getChildren().size() == 0) {
			return addContent(parent, parentXpath, child, childNum, comment);
		}
		else {
			SubtreeInsertOperation io = new SubtreeInsertOperation(parent, parentXpath, child, childNum, nodeNumber(parent), namespace, comment);
			return new CostOps(io);
		}
	}


	/**
	 * Creates the operation to delete a subtree.
	 */
	private CostOps deleteSubtree(Element parent, String xpath, Element child, String comment) throws JDOMException {
		//System.out.println("deleteSubtree: xpath = " + xpath);
		String xpath2 = childXpath(parent, xpath, child);
		return deleteSubtree(child, xpath2, comment);
	}


	/**
	 * Creates the operation to delete a subtree.
	 */
	private CostOps deleteSubtree(Element node, String xpath, String comment) throws JDOMException {
		//System.out.println("deleteSubtree: xpath = " + xpath);
		
		// If the child has no children, this can be represented with ElementDelete.
		
		if (node.getChildren().size() == 0) {
			return deleteContent(node, xpath, comment);
		}
		else {
			SubtreeDeleteOperation dop = new SubtreeDeleteOperation(xpath, nodeNumber(node), namespace, comment);
			return new CostOps(dop);
		}
	}


	/**
	 * Creates the operation to insert a new attribute.
	 */
	private CostOps insertAttr(Element node, String xpath, Attribute newAttr, String comment) {
		//System.out.println("insertAttr: xpath = " + xpath);
		AttributeInsertOperation mao = new AttributeInsertOperation(/*node,*/ xpath, newAttr, nodeNumber(node), namespace, comment);
		return new CostOps(mao);
	}


	/**
	 * Creates the operation to delete a node's attribute.
	 */
	private CostOps deleteAttr(Element node, String xpath, Attribute oldAttr, String comment) {
		//System.out.println("deleteAttr: xpath = " + xpath);
		AttributeDeleteOperation mao = new AttributeDeleteOperation(/*node,*/ xpath, oldAttr, nodeNumber(node), namespace, comment);
		return new CostOps(mao);
	}


	/**
	 * <p>Creates the operation to modify a node's attribute.</p>
	 */
	private CostOps modifyAttr(Element node, String xpath, Attribute newAttr, String comment) {
		//System.out.println("modifyAttr: xpath = " + xpath);
		AttributeModifyOperation mao = new AttributeModifyOperation(node, xpath, newAttr, nodeNumber(node), namespace, comment);
		return new CostOps(mao);
	}


	/**
	 * Creates the operation to modify a node's content.
	 */
	private CostOps modifyContent(String xpath, Object newContent, String comment) {
		//System.out.println("modifyContent: xpath = " + xpath);
		ElementModifyOperation mco = new ElementModifyOperation(xpath, newContent, nodeNumber(newContent), namespace, comment);
		return new CostOps(mco);
	}
	
	
	/**
	 * Creates the operation to add content to a node. The xpath should be the path to the parent node.
	 */
	private CostOps addContent(Element node, String xpath, Object content, int childNum, String comment) {
		//System.out.println("addContent: xpath = " + xpath);
		ElementInsertOperation aco = new ElementInsertOperation(node, xpath, content, childNum, nodeNumber(node), namespace, comment);
		return new CostOps(aco);
	}
	
	
	/**
	 * Creates the operation to delete some content from a node.
	 */
	private CostOps deleteContent(Object content, String contentXpath, String comment) throws JDOMException {
		ElementDeleteOperation dco = new ElementDeleteOperation(contentXpath, nodeNumber(content), namespace, comment);
		return new CostOps(dco);
	}
	
	/*
	private CostOps deleteContent(Element parent, String parentXpath, int childNum, String comment) throws JDOMException {
		//System.out.println("deleteContent: xpath = " + xpath);
		CostOps cost = new CostOps();
		cost.cost = 1;
		DeleteContentOperation dco = new DeleteContentOperation(parent, childNum, parentXpath, nodeNumber(parent), namespace, comment);
		cost.ops.add(dco);
		return cost;
	}
	 */
	
	
	/**
	 * Creates the operation to move a child to a new position. This assumes the two nodes
	 * are the same type.
	 */
	private CostOps moveChild(Object n1, Object n2, int childNum1, int childNum2, Element parent, String parentXpath, String xpath, String comment) throws JDOMException {
		CostOps cost = new CostOps();
		
		if (parentXpath != null) {
			if (n1 instanceof Element) {
				cost.add(deleteSubtree((Element)n1, xpath, comment));
				cost.add(insertSubtree(parent, parentXpath, (Element)n2, childNum2, comment));
			}
			else {
				cost.add(deleteContent(n1, xpath, comment));
				cost.add(addContent(parent, parentXpath, n2, childNum2, comment));
			}
		}
		else {
			// ???
			throw new JDOMException("can't move child without parent");
		}
		
		return cost;
	}
	
	
	/**
	 * Returns the number assigned to the given node.
	 *
	 * Updates cachedNodeInfo to hold information about the node.
	 */
	private int nodeNumber(Object node) {
		if (cachedNodeInfo == null || cachedNodeInfo.node != node) {
			cachedNodeInfo = (NodeInfo)treeInfo.get(node);
		}
		return cachedNodeInfo.nodeNumber;
	}


	/**
	 * Returns the child number assigned to the given node.
	 *
	 * Updates cachedNodeInfo to hold information about the node.
	 */
	private int childNumber(Object node) {
		if (cachedNodeInfo == null || cachedNodeInfo.node != node) {
			cachedNodeInfo = (NodeInfo)treeInfo.get(node);
		}
		return cachedNodeInfo.childNumber;
	}
	
	
	/**
	 * Returns the parent of the given node.
	 *
	 * Updates cachedNodeInfo to hold information about the node.
	 */
	private Element parent(Object node) {
		if (cachedNodeInfo == null || cachedNodeInfo.node != node) {
			cachedNodeInfo = (NodeInfo)treeInfo.get(node);
		}
		
		if (cachedNodeInfo.parentInfo == null)
			return null;
		else
			return (Element)cachedNodeInfo.parentInfo.node;
	}


	/**
	 * Returns the size of the tree rooted at the given node.
	 *
	 * Updates cachedNodeInfo to hold information about the node.
	 */
	private int treeSize(Object node) {
		if (cachedNodeInfo == null || cachedNodeInfo.node != node) {
			cachedNodeInfo = (NodeInfo)treeInfo.get(node);
		}
		return cachedNodeInfo.treeSize;
	}
	
	
	/**
	 * Gets the xpath of the node.
	 */
	/*
	private String getFullXpath(Object n) throws JDOMException {
		return ((NodeInfo)treeInfo.get(n)).fullXpath;
	}
	 */


	/**
	 * Determines the XPath of the child. It should be just the parent's
	 * xpath plus the child's name. However, if there is more that one
	 * child with the same name we have to use an index to disambiguate.
	 *
	 * Updates cachedNodeInfo to hold information about the child node.
	 */
	private String childXpath(Element parent, String parentXpath, Object childObj) throws JDOMException {
		if (cachedNodeInfo == null || cachedNodeInfo.node != childObj) {
			cachedNodeInfo = (NodeInfo)treeInfo.get(childObj);
		}

		if (cachedNodeInfo.xpath == null) {
			cachedNodeInfo.xpath = XPath.getChildXPath(parent, childObj);
		}
		
		return parentXpath + "/" + cachedNodeInfo.xpath;
	}


	/**
	 * Walks a JDOM tree assigning a number to each node. The numbers are
	 * assigned to children before parents, and siblings are numbered
	 * from left to right. It also computes the tree size for each node,
	 * which is the size of the subtree rooted at that node (i.e. number
	 * of descendants + 1).
	 *
	 * This is implemented by putting an entry into the treeInfo map for
	 * each node.
	 *
	 * The method returns the NodeInfo structure created for the node, so
	 * it can be used to build information about the parent.
	 */
	private NodeInfo buildTreeInfo(Object obj, NodeInfo parentInfo, int childNumber) throws JDOMException {
		int numDescendants = 0;
		
		// Create info about this node with as much information as we have. This
		// may be needed if we make a recursive call in the following code.
		
		NodeInfo info = new NodeInfo(obj, parentInfo, childNumber);

		/*
		//**************
		// FOR TESTING
		//**************

		{
			String name;
			if (obj instanceof Element)
				name = ((Element)obj).getName();
			else
				name = obj.getClass().getName();

			if (parentInfo == null) {
				info.fullXpath = "/" + name;
			}
			else {
				info.fullXpath = parentInfo.fullXpath + "/" + name + "(" + childNumber + ")";
			}
		}

		//**************
		 */
		
		if (obj instanceof Element) {
			Element node = (Element)obj;
			
			// If it's an Element, it may have content and children.
			
			List content = node.getContent();
			for (int i = content.size() - 1; i >= 0; --i) {
				// Get the object and call this method recursively to
				// process it. The method returns the info about the
				// child (as well as storing it in the info tree).
				
				Object child = content.get(i);
				NodeInfo childInfo = buildTreeInfo(child, info, i + 1);
				
				// Update the info of the node we're processing by
				// including the total number of descendants of the child.
				
				numDescendants += childInfo.treeSize;
			}
		}

		// Fill in the remaining information about this node now that any
		// children have been processed.
		
		info.nodeNumber = currNodeNumber++;
		info.treeSize = numDescendants + 1;

		treeInfo.put(obj, info);

		return info;
	}
	
	
	/**
	 * Walks a JDOM tree removing any text content that contains only whitespace.
	 * NOTE: This modifies the tree. If that's a problem, it may be necessary
	 * to develop an alternative. One possibility would be to enhance nodeInfo
	 * to include a "discard" flag for the node, and update all the code to check
	 * the flag before processing the node.
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
	 * Returns a String containing characters for the requested
	 * indent. Used for debugging.
	 */
	/*
	private static final String dots = "..........................................";

	private String indent(int n) {
		return dots.substring(0, n);
	}
	 */
	
	/**
	 * This private class is used by DifferenceFinder to hold additional
	 * information about the elements in the JDOM tree.
	 */
	private class NodeInfo {
		Object node;
		NodeInfo parentInfo;
		int nodeNumber;
		int childNumber;
		int treeSize;
		String xpath;		// relative xpath from parent to this node
		//String fullXpath;	// used during testing; should be removed

		public NodeInfo(Object n, NodeInfo pi, int cn) {
			node = n;
			parentInfo = pi;
			childNumber = cn;
		}

		public NodeInfo(Object n, NodeInfo pi, int nn, int cn, int ts) {
			node = n;
			parentInfo = pi;
			nodeNumber = nn;
			childNumber = cn;
			treeSize = ts;
		}
	}
}

