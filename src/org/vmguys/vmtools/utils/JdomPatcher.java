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
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/**
 * This class takes a set of patch instructions that are in a
 * JDOM tree or a List and applies them to a JDOM tree.
 */
public class JdomPatcher {
	/**
	 * This is the namespace to use when creating new elements.
	 */
	private Namespace namespace;
	
	
	/**
	 * This flag indicates whether whitespace is significant.
	 */
	private boolean discardWhitespace;
	

	/**
	 * Constructs a JdomPatcher using the namespace of NO_NAMESPACE.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public JdomPatcher() {
		this(Namespace.NO_NAMESPACE);
	}


	/**
	 * Constructs a JdomPatcher with the specified namespace.
	 * The discardWhitespace property will be set true, and the
	 * addComments property will be set false.
	 */
	public JdomPatcher(Namespace namespace) {
		this(namespace, true);
	}
	
	
	/**
	 * Constructs a JdomPatcher with the specified namespace
	 * and setting for discardWhitespace. The addComments property
	 * will be set false.
	 */
	public JdomPatcher(Namespace namespace, boolean discardWhitespace) {
		this(namespace, discardWhitespace, false);
	}
	
	
	/**
	 * Constructs a JdomPatcher with the specified namespace,
	 * setting for discardWhitespace, and setting for addComments. If addComments
	 * is true, comments will be added to the output indicating the reason
	 * for each operation. This is most useful for debugging or tracing the
	 * operations.
	 */
	public JdomPatcher(Namespace namespace, boolean discardWhitespace, boolean addComments) {
		this.discardWhitespace = discardWhitespace;
		this.namespace = namespace;
	}


	/**
	 * Applies patches to a JDOM tree and returns the modified tree. The original
	 * tree is cloned before the patches are applied, so it is unchanged.
	 */
	public Element patch(Element originalTree, Element patchTree) throws JDOMException {
		return patch(originalTree, patchTree.getChildren());
	}
	
	
	/**
	 * Applies patches to a JDOM tree and returns the modified tree. The original
	 * tree is cloned before the patches are applied, so it is unchanged. The
	 * patchList should be a List of Elements. This is available in case there
	 * is a need to detach the children from the root of the patch tree.
	 */
	public Element patch(Element originalTree, List patchList) throws JDOMException {
		// Start by making a clone of the original tree.
		
		Element rslt = (Element)originalTree.clone();
		
		// If whitespace is not important, discard any elements that
		// contain only whitespace from the original tree and from
		// the list of patches.
		
		if (discardWhitespace) {
			discardWhitespace(rslt);
			
			for (int i = 0; i < patchList.size(); ++i) {
				discardWhitespace((Element)patchList.get(i));
			}
		}

		for (int i = 0; i < patchList.size(); ++i) {
			Element child = (Element)patchList.get(i);

			// All the children of the update request should be
			// Position elements containing update information.

			if (child.getName().equals("Position")) {
				rslt = processPosition(rslt, child);
			}
			else {
				throw new JDOMException("expecting \"Position\"; found \"" + child.getName() + "\"");
			}
		}
		
		return rslt;
	}
	
	
	/**
	 * Processes a Position subtree to update the tree. Takes the original tree
	 * and the position element as input, and returns the updated tree.
	 */
	private Element processPosition(Element tree, Element position) throws JDOMException {
		Element rslt = null;

		// Get the XPath specified in the Position element.

		String xpath = position.getAttributeValue("XPath");
		if (xpath == null) {
			throw new JDOMException("Position element has no XPath attribute");
		}

		// Get the Element or content specified by the XPath.

		Object currNode = XPath.getElement(tree, xpath);
		
		if (currNode == null) {
			throw new JDOMException("there is no element for xpath \"" + xpath + "\"");
		}

		// If it is an Element, instantiate a copy in case we need it.
		
		Element currElement = null;
		if (currNode instanceof Element)
			currElement = (Element)currNode;

		// A Position element may have a single child of type Root, or it may
		// have several children with types Subtree, Attribute, or Element,
		// and they must appear in that order.

		String[] opOrder = new String[] {"Subtree", "Attribute", "Element"};
		int curOp = 0;

		List operands = position.getChildren();
		for (int i = 0; i < operands.size(); ++i) {
			Element operand = (Element)operands.get(i);

			if (i == 0 && operands.size() == 1 && operand.getName().equals("Root")) {
				rslt = processRoot(tree, currElement, operand);
			}
			else {
				// Make sure this operand's name follows the correct sequence.

				while (curOp < opOrder.length - 1 && !operand.getName().equals(opOrder[curOp])) {
					++curOp;
				}
				if (!operand.getName().equals(opOrder[curOp])) {
					throw new JDOMException("expecting operand type \"" + opOrder[curOp] + "\"; found \"" + operand.getName() + "\"");
				}

				switch (curOp) {
					case 0:
						rslt = processSubtree(tree, currElement, operand);
						break;

					case 1:
						rslt = processAttribute(tree, currElement, operand);
						break;

					case 2:
						rslt = processElement(tree, currNode, operand, xpath);
						break;

					default:
						// Should never happen, since this code is controlling the value
						// of curOp.
						throw new Error("internal error in processPosition; curOp=" + curOp);
				}
			}
		}

		return rslt;
	}

	
	/**
	 * Processes a request to replace subtree, with the replacement being
	 * included in the subtree of the root element.
	 */
	private Element processRoot(Element tree, Element currElement, Element rootRequest) throws JDOMException {
		Element rslt = null;

		String operation = rootRequest.getAttributeValue("Operation");

		if (operation.equals("replace")) {
			// The node we were given is the one being replaced. Its name must
			// be the same as the name of the (only) child of the root element.
			// We can't replace apples with oranges.

			String elementName = currElement.getName();

			// Get the replacement element which will be used for the update.
			// Make a clone of it (and its subtree) to be inserted into the
			// tree.

			Element replacement = (Element)rootRequest.getChild(elementName).clone();
			if (replacement == null) {
				throw new JDOMException("expected replacement element to have name \"" + elementName + "\"");
			}

			Element parent = currElement.getParent();
			if (parent != null) {
				// We'll just modify the current tree and return the modified
				// version as the result.

				rslt = tree;

				// Get the list of siblings of the element being replaced. Then find the old
				// child in the list and replace it with the replacement child. This list is
				// "live", so the change will be reflected in the tree.

				List siblings = parent.getChildren();
				int i = siblings.indexOf(currElement);
				siblings.remove(i);
				siblings.add(i, replacement);
			}
			else {
				// The node has no parent. Just replace the entire tree with the
				// replacement.

				rslt = replacement;
			}

		}
		else {
			throw new JDOMException("Root operation should be \"replace\"; found \"" + operation + "\"");
		}

		return rslt;
	}

	
	/**
	 * Processes a request to insert or delete a subtree.
	 */
	private Element processSubtree(Element tree, Element currElement, Element subtreeRequest) throws JDOMException {
		String operation = subtreeRequest.getAttributeValue("Operation");

		if (operation.equals("insert")) {
			int childNum = -1;

			String childNumAttr = subtreeRequest.getAttributeValue("Child");
			if (childNumAttr != null) {
				// The numbering used in XPath and OTA is origin-1. The numbering in the
				// List is origin-0. Make an adjustment.

				childNum = Integer.parseInt(childNumAttr) - 1;
				if (childNum < 0) {
					throw new JDOMException("Subtree insert request has invalid child number (" + (childNum + 1) + ")");
				}
			}

			// The subtree request should have a single child, which is the new
			// subtree to be inserted. Make a clone of it to be inserted into
			// the tree.

			Element newSubtree = (Element)((Element)subtreeRequest.getChildren().get(0)).clone();

			List children = currElement.getChildren();

			if (childNum < 0 || childNum >= children.size()) {
				// Just insert the new subtree after the last child. Make a clone of
				// the subtree included in the request.

				children.add(newSubtree);
			}
			else {
				children.add(childNum, newSubtree);
			}
		}
		else if (operation.equals("delete")) {
			Element parent = currElement.getParent();
			if (parent != null) {
				// Get the list of siblings of the element, then remove the element from the list.

				List siblings = parent.getChildren();
				siblings.remove(currElement);
			}
			else {
				// The subtree operation cannot be used with the tree's root element.

				throw new JDOMException("Subtree delete request cannot delete root element \"" + currElement.getName() + "\"");
			}
		}
		else {
			throw new JDOMException("Subtree operation should be \"insert\" or \"delete\"; found \"" + operation + "\"");
		}
		// Just return the modified tree.

		return tree;
	}


	/**
	 * Processes a request to modify a node's attribute.
	 */
	private Element processAttribute(Element tree, Element currElement, Element attrRequest) throws JDOMException {
		String operation = attrRequest.getAttributeValue("Operation");
		String attrName = attrRequest.getAttributeValue("Name");

		if (attrName == null) {
			throw new JDOMException("Attribute " + operation + " request contains null attribute name");
		}

		if (operation.equals("insert") || operation.equals("modify")) {
			String attrValue = attrRequest.getAttributeValue("Value");
			currElement.setAttribute(attrName, attrValue);
		}
		else if (operation.equals("delete")) {
			currElement.removeAttribute(attrName);
		}
		else {
			throw new JDOMException("Attribute operation should be \"insert\", \"modify\" or \"delete\"; found \"" + operation + "\"");
		}

		return tree;
	}

	
	/**
	 * Processes a request to insert, modify, or delete an element. The xpath parameter is
	 * the path that was used to retrieve currElement from the tree.
	 */
	private Element processElement(Element tree, Object currElement, Element elementRequest, String xpath) throws JDOMException {
		String operation = elementRequest.getAttributeValue("Operation");

		if (operation.equals("insert")) {
			int childNum = -1;

			String childNumAttr = elementRequest.getAttributeValue("Child");
			if (childNumAttr != null) {
				// The numbering used in XPath and OTA is origin-1. The numbering in the
				// List is origin-0. Make an adjustment.

				childNum = Integer.parseInt(childNumAttr) - 1;
				if (childNum < 0) {
					throw new JDOMException("Element insert request has invalid child number (" + (childNum + 1) + ")");
				}
			}

			// The element request should have a single child, which is the new
			// element or content to be inserted.

			//Object newObject = elementRequest.getChildren().get(0);
			Object newObject = elementRequest.getContent().get(0);
			
			if (newObject instanceof Element) {
				insertElement((Element)currElement, (Element)newObject, childNum);
			}
			else {
				Element parent = XPath.getParentElement(tree, xpath);
				insertContent(parent, newObject, childNum);
			}
		}
		else if (operation.equals("modify")) {
			// The element request should have a single child, which is the new
			// value after modification.

			//Object newObject = elementRequest.getChildren().get(0);
			Object newObject = elementRequest.getContent().get(0);
			
			if (newObject instanceof Element) {
				modifyElement((Element)currElement, (Element)newObject);
			}
			else {
				Element parent = XPath.getParentElement(tree, xpath);
				modifyContent(parent, newObject, xpath);
			}
		}
		else if (operation.equals("delete")) {
			if (currElement instanceof Element) {
				deleteElement((Element)currElement);
			}
			else {
				Element parent = XPath.getParentElement(tree, xpath);
				deleteContent(parent, xpath);
			}
		}
		else {
			throw new JDOMException("Element operation should be \"insert\", \"modify\" or \"delete\"; found \"" + operation + "\"");
		}

		return tree;
	}
	
	
	/**
	 * Inserts a new Element into the tree.
	 */
	private void insertElement(Element currElement, Element newElement, int childNum) throws JDOMException {
		// The element request should have a single child, which is the new
		// element to be inserted. Make a clone of it to be inserted into
		// the tree.

		if (newElement.hasChildren()) {
			throw new JDOMException("Element insert request cannot be used to insert a subtree; element \"" + newElement.getName() + "\" has children");
		}
		
		// Clone the element to be inserted. We know it doesn't have children, so this
		// should not be very hard.
		
		insertContent(currElement, (Element)newElement.clone(), childNum);
	}

	
	/**
	 * Inserts new content into the tree.
	 */
	private void insertContent(Element parent, Object newContent, int childNum) throws JDOMException {
		List children = parent.getChildren();

		if (childNum < 0 || childNum >= children.size()) {
			// Just insert the new subtree after the last child.

			children.add(newContent);
		}
		else {
			children.add(childNum, newContent);
		}
		
		int a = 0;	// for testing; breakpoint here
	}
	

	/**
	 * Modifies an Element in the tree. This only modifies the content of
	 * the Element, and doesn't change anything about its children.
	 */
	private void modifyElement(Element currElement, Element newElement) throws JDOMException {
		// The current element is the one being modified. Its name must
		// be the same as the name of the new element.
		// We can't replace apples with oranges.

		String elementName = currElement.getName();

		if (newElement.getName().equals(currElement.getName())) {
			List content = currElement.getContent();

			// Remove any existing text content from the element.

			for (int i = content.size() - 1; i >= 0; --i) {
				Object x = content.get(i);
				if (x instanceof String) {
					content.remove(i);
				}
			}

			// Then add the text content of the new element. The new element
			// should not have any other type of content.

			content.addAll(newElement.getContent());
		}
		else {
			throw new JDOMException("Element modify request must contain element with same name as existing element \"" + currElement.getName() + "\"");
		}
	}
	
	
	/**
	 * Modifies some content of a node in a tree.
	 */
	private void modifyContent(Element parent, Object newContent, String xpath) throws JDOMException {
		// The xpath to specify content must be of the form "..../foo/self::node()[3]". We can
		// use the index to determine which part of the content is to be modified.
		
		int childNum = XPath.getIndex(xpath);
		
		List content = parent.getContent();
		content.set(childNum, newContent);
	}
	
	
	/**
	 * Deletes an Element node from the tree.
	 */
	private void deleteElement(Element currElement) throws JDOMException {
		// When deleting an element, its children are moved up in the tree. From
		// the point of view of the element's parent, they are moved from grandchildren
		// to children. Therefore this can only be performed if the element being
		// deleted has a parent.
		//
		// NOTE: For clarity, all the names in the following code are from the point
		// of view of the node being deleted (its parent, its siblings, its children,
		// etc.).

		Element parent = currElement.getParent();
		if (parent != null) {
			List siblings = parent.getChildren();

			// Determine the position of the element being deleted.

			int childNum = siblings.indexOf(currElement);

			// Get a list of its children.

			List children = currElement.getChildren();

			// The list of siblings is "live". Any changes made to that list
			// are reflected in the tree. Just delete the desired element and
			// insert its children.

			siblings.remove(childNum);
			siblings.addAll(childNum, children);
		}
		else {
			throw new JDOMException("Element delete request cannot delete root node of tree (" + currElement.getName() + ")");
		}
	}
	
	
	/**
	 * Deletes content from an Element node.
	 */
	private void deleteContent(Element parent, String xpath) throws JDOMException {
		// The xpath to specify content must be of the form "..../foo/self::node()[3]". We can
		// use the index to determine which part of the content is to be modified.
		
		int childNum = XPath.getIndex(xpath);
		
		List content = parent.getContent();
		content.remove(childNum);
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
	

}


/*
 * $Log: JdomPatcher.java,v $
 * Revision 1.2  2002/01/31 22:42:20  gwheeler
 * Updated javadocs.
 *
 * Revision 1.1  2001/12/12 14:52:39  gwheeler
 * This module contains code to patch an XML document that has been
 * parsed into a JDOM tree. This code was factored out of
 * org.vmguys.vmtools.ota.OtaUpdate because it is not OTA-specific.
 *
 */
