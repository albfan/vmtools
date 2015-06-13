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
import org.jdom.*;
import java.io.*;
import java.util.*;
import java.security.*;


/**
 * <p>This class adds properties to Element that are needed by the
 * algorithm used.</p>
 *
 * <p>Thanks to Richard Titze (richard.titze@tin.it) for the idea and
 * the original implementation.</p>
 */
public class DiffElement extends org.jdom.Element {
	private int nodeNumber = -1;
	private int childNumber = -1;
	private DiffElement root;	// the root of the tree containing this node
	private int treesize = -1;
	private int contentsize = -1;
	private int leftmostLeafNum = -1;
	private List children;		// cache of result of getChildren()
	private Object[] content;		// cache of non-child content
	private Attribute[] attributes;	// cache of sorted attributes
	private boolean treeInfoOk;
	private String xpath;		// xpath to this node

	public DiffElement(String name) {
		super(name);
	}

	public DiffElement(String name, Namespace namespace) {
		super(name, namespace);
	}

	public DiffElement(String name, String uri) {
		super(name, uri);
	}

	public DiffElement(String name, String prefix, String uri) {
		super(name, prefix, uri);
	}


	/**
	 * Returns the size of (the number of nodes in) the
	 * subtree rooted at this node. The number is cached for
	 * future use.
	 */
	public int getTreesize() {
		if (!treeInfoOk) {
			buildTreeInfo();
		}
        
		return treesize;
	}
	
	
	/**
	 * Returns the content size of the subtree rooted at this
	 * node. This is similar to treesize, but it includes all
	 * the content of all the nodes as well as the children.
	 * The number is cached for future use.
	 */
	public int getContentsize() {
		if (!treeInfoOk) {
			buildTreeInfo();
		}
		
		return contentsize;
	}
	

	/**
	 * Returns the number of this child. The leftmost
	 * sibling is child number 1, the next is child
	 * number 2, etc.
	 */
	public int getChildNumber() {
		if (!treeInfoOk) {
			buildTreeInfo();
		}
		
		return childNumber;
	}

	
	/**
	 * Returns the number of this node within the tree.
	 * Each node is assigned a number using a postorder
	 * algorithm during a tree walk. The result is that
	 * all of the nodes in a subtree have numbers lower
	 * than the root of that subtree. This is used by
	 * the difference finding algorithm.
	 */
	public int getNodeNumber() {
		if (!treeInfoOk) {
			buildTreeInfo();
		}
		
		return nodeNumber;
	}

	
	/**
	 * Indicates whether this node is a keyroot or not. By definition,
	 * a keyroot is the root of the (sub)tree or any node that has a
	 * left sibling. In this case we don't know if this node is the root
	 * of the caller's subtree, so we can only indicate if there is a
	 * left sibling. The caller will have to check for rootness
	 * separately.
	 */
	/*
	public boolean isKeyRoot() {
		return getChildNumber() > 1;
	}
	 */
	
	
	/**
	 * Indicates whether this node has a sibling to its left. This is
	 * used in determining if this node is a keyRoot, which is needed
	 * by the difference finding algorithm.
	 */
	public boolean hasLeftSibling() {
		return getChildNumber() > 1;
	}
	
	
	/**
	 * Returns the number of the leftmost leaf. Returns this node's number if it
	 * has no children.
	 */
	public int getLeftmostLeafNum() {
		if (leftmostLeafNum < 0) {
			DiffElement leftmostLeaf = this;

			List children;
			while (true) {
				children = leftmostLeaf.getChildren();
				if (children.size() > 0) {
					leftmostLeaf = (DiffElement)children.get(0);
				}
				else {
					break;
				}
			}
			
			leftmostLeafNum = leftmostLeaf.getNodeNumber();
		}
		
		return leftmostLeafNum;
	}
	
	
	/**
	 * Similar to Element.getParent(). Same functionality, but
	 * the result is cast to a DiffElement object.
	 */
	public DiffElement getDiffParent() {
		return (DiffElement)getParent();
	}
	
	
	/**
	 * Overrides Element.getChildren(). Same functionality, but
	 * the results are cached.
	 */
	public List getChildren() {
		if (children == null) {
			children = super.getChildren();
		}
		
		return children;
	}
	
	
	/**
	 * Gets an array of the node's content that excludes the
	 * children. The array is cached so subsequent calls can
	 * be handled quickly.
	 */
	public Object[] getContentAsArray() {
		if (content == null) {
			List fullContent = getContent();
			List xContent = new ArrayList(fullContent.size());
			
			Iterator it = fullContent.iterator();
			while (it.hasNext()) {
				Object c = it.next();
				if (!(c instanceof DiffElement)) {
					xContent.add(c);
				}
			}
			
			content = xContent.toArray();
		}
		
		return content;
	}
	
	
	/**
	 * Gets an array of the node's attributes. The attributes
	 * are sorted by name. The array is cached for future use.
	 */
	public Attribute[] getAttributesAsArray() {
		if (attributes == null) {
			List attribList = getAttributes();
			attributes = new Attribute[attribList.size()];
			attribList.toArray(attributes);
			Arrays.sort(attributes, new AttributeNameComparator());
		}
		
		return attributes;
	}
	
	
	/**
	 * Returns a String containing the XPath of this node.
	 */
	public String getXpath() throws JDOMException {
		if (xpath == null) {
			//xpath = XPath.getXPath(root, this);
			DiffElement parent = getDiffParent();
			if (parent != null) {
				xpath = parent.getXpath() + XPath.getChildXPath(parent, this);
			}
			else {
				xpath = XPath.getXPath(getRoot(), this);
			}
		}
		
		return xpath;
	}
	
	
	/**
	 * Returns a String containing information about this node.
	 */
	public String toString() {
		try {
			return getXpath();
		}
		catch (JDOMException e) {
			return getNodeNumber() + ":" + getName();
		}
	}
	

	/**
	 * Walks the tree, computing information about each node.
	 * Because the nodes must be numbered, the walk must start
	 * at the root of the tree. This method will find the root
	 * no matter which node instance it is invoked on.
	 */
	private void buildTreeInfo() {
		// This can be called on any node within the tree. It will
		// locate the root node of the tree and proceed from there.
		
		if (!treeInfoOk) {
			DiffElement root = getRoot();
			root.buildTreeInfo(1, new NodeNumberSingleton());
		}
	}

	
	/**
	 * <p>Walks the tree computing information about each node.
	 * This recursive method is called by buildTreeInfo.</p>
	 *
	 * <p>One of the things computed here is the node number.
	 * This class would be more general if that were done outside
	 * this class, so any numbering scheme could be used without
	 * changing this class. If a future version calls for different
	 * number, I would recommend having setNodeNumber and getNodeNumber, and
	 * using another class to walk the tree assigning numbers.</p>
	 */
	private void buildTreeInfo(int childNumber, NodeNumberSingleton nodeNumber) {
		treesize = 0;
		contentsize = 0;
		
		// Initialize the counter that will assign child numbers
		// to this node's children.
		
		int childNum = 1;
		
		List children = getChildren();
		Iterator it = children.iterator();
		while (it.hasNext()) {
			DiffElement child = (DiffElement)it.next();
			child.buildTreeInfo(childNum++, nodeNumber);
			
			// After each child is processed, update the counts
			// that depend on it.
			
			treesize += child.getTreesize();
			contentsize += child.getContentsize();
		}
		
		// After the children are processed, assign information to
		// this node.
		
		this.childNumber = childNumber;
		this.nodeNumber = nodeNumber.getNodeNumber();
		
		// Remember to count this node in the tree size.
		
		treesize += 1;
		
		// Remember to add this node and its content to the content size.
		// As a side effect this will fill the content and attribute
		// arrays.
		
		contentsize += 1 + getContentAsArray().length + getAttributesAsArray().length;
		
		treeInfoOk = true;
	}

	/**
	 * Returns the root element of the tree containing this node.
	 * The result is cached for future use.
	 */
	public DiffElement getRoot() {
		if (root == null) {
			/*
			root = this;
			while (!root.isRootElement())
				root = (DiffElement)root.getParent();
			 */
		
			DiffElement next;
			
			root = this;
			next = (DiffElement)root.getParent();
			
			while (next != null) {
				root = next;
				next = (DiffElement)root.getParent();
			}
		}
			
		return root;
	}	
	
	/**
	 * This class is used to number the nodes in a tree. A single
	 * instance of this class is used to dispense a number for
	 * each node.
	 */
	private class NodeNumberSingleton {
		private int nextNodeNumber;
		
		public NodeNumberSingleton() {
			nextNodeNumber = 1;
		}

		public int getNodeNumber() {
			return nextNodeNumber++;
		}
	}
}


/*
 * $Log: DiffElement.java,v $
 * Revision 1.6  2002/02/01 20:42:13  gwheeler
 * Added VM copyright.
 *
 * Revision 1.5  2002/01/31 22:25:01  gwheeler
 * Updated javadocs.
 * Changed buildTreeInfo to call the getRoot method. Moved the improved
 * get root code into that method.
 *
 */
