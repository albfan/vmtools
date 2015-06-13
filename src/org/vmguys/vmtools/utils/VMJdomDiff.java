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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
//import org.jdom.output.XMLOutputter;        // only needed for testing
import org.vmguys.vmtools.utils.*;


/**
 * <p>This class generates and applies difference documents.
 * If given two XML documents, it will generate an difference document that will
 * show the differences between the data. If given an XML document and a difference
 * document, it will apply the differences and return the updated data.</p>
 *
 * <p>This class operates on JDOM trees, rather than XML documents. An application
 * using this class should parse the input XML files to create the trees, or
 * use the result tree to generate an XML document.</p>
 *
 * <p>This is similar to OtaUpdate except that the result is not OTA compliant.</p>
 */
public class VMJdomDiff {
	/** The element tag name for a difference document. */
	public static final String tagname = "VMXMLDiff";

	/** The default namespace for VMXML documents. */
	public static final String xmlns   = "http://www.vmguys.org/VMXMLDiff";

	/** The default schemaLocation value for VMXML documents. */
	public static final String schemaLocation = "http://www.vmguys.org/VMXMLDiff/xxxx";

	/**
	 * No-args constructor.
	 */
	public VMJdomDiff() {
	}


	/**
	 * <p>Returns the differences between the original tree and the
	 * modified tree as a VMXMLDiff tree. The result tree can then be converted
	 * to an XML document.</p>
	 *
	 * <p>When the difference tree is applied against the original tree by
	 * applyDiffs, the result will be the modified tree.</p>
	 *
	 * @see #applyDiffs(Element, Element)
	 */
	public Element generateDiffs(Element original, Element modified) throws JDOMException {
		Element rslt = null;

		// Make sure the two elements are the same type. We can't
		// compare apples to oranges.

		{
			String origName = original.getName();
			String modName = modified.getName();

			if (!origName.equals(modName)) {
				throw new JDOMException("original and modified elements are not the same type: \"" + origName + "\" vs. \"" + modName + "\"");
			}
		}

		rslt = generateUpdate(original, modified);

		return rslt;
	}


	/**
	 * Applies the difference tree to the original tree to recreate
	 * the modified tree. The tree rooted at the original element is
	 * cloned, then the updates specified in the update request are
	 * applied. The result is returned to the caller.
	 *
	 * @see #generateDiffs(Element, Element)
	 */
	public Element applyDiffs(Element original, Element differences) throws JDOMException {
		// Make a clone of the original to use as a starting point to apply the
		// differences.

		Element rslt = (Element)original.clone();

		// Make sure this is really an update request.

		if (tagname.equals(differences.getName())) {

			// TODO: check diffs version

			List children1 = differences.getChildren();
			for (int i = 0; i < children1.size(); ++i) {
				Element child = (Element)children1.get(i);

				// All the children in the differences tree should be
				// Position elements containing update information.

				if (child.getName().equals("Position")) {
					rslt = processPosition(rslt, child);
				}
				else {
					throw new JDOMException("expecting \"Position\"; found \"" + child.getName() + "\"");
				}
			}
		}
		else {
			throw new JDOMException("expecting " + tagname + "; found \"" + differences.getName() + "\"");
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

		// Get the Element specified by the XPath.
		//
		// TODO: Change this to process differences that involve contents of Elements.

		Object x = XPath.getElement(tree, xpath);
		if (x == null) {
			/*
			XMLOutputter xmlo = new XMLOutputter("  ", true);
			xmlo.setTextNormalize(true);

			try {
				xmlo.output(tree, System.out);
			}
			catch (IOException e) {
			}
			 */

			throw new JDOMException("there is no element for xpath \"" + xpath + "\"");
		}

		if (!(x instanceof Element)) {
			throw new JDOMException("Can't handle " + x.getClass().getName() + " in VMJdomDiff");
		}

		Element currElement = (Element)x;

		// A Position element may have a single child of type Root, or it may
		// have several children with types Subtree, Attribute, or Element,
		// and they must appear in that order.
		//
		// TODO: Add additional types here to handle content.

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
						rslt = processElement(tree, currElement, operand);
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
	 * Processes a request to insert, modify, or delete an element.
	 */
	private Element processElement(Element tree, Element currElement, Element elementRequest) throws JDOMException {
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
			// element to be inserted. Make a clone of it to be inserted into
			// the tree.

			Element newElement = (Element)((Element)elementRequest.getChildren().get(0)).clone();

			if (newElement.hasChildren()) {
				throw new JDOMException("Element insert request cannot be used to insert a subtree; element \"" + newElement.getName() + "\" has children");
			}

			List children = currElement.getChildren();

			if (childNum < 0 || childNum >= children.size()) {
				// Just insert the new subtree after the last child. Make a clone of
				// the subtree included in the request.

				children.add(newElement);
			}
			else {
				children.add(childNum, newElement);
			}
		}
		else if (operation.equals("modify")) {
			// The current element is the one being modified. Its name must
			// be the same as the name of the (only) child of the elementRequest element.
			// We can't replace apples with oranges.
			//
			// TODO: change this to allow other types of content.

			String elementName = currElement.getName();

			Element newElement = (Element)elementRequest.getChildren().get(0);

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
		else if (operation.equals("delete")) {
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
		else {
			throw new JDOMException("Element operation should be \"insert\", \"modify\" or \"delete\"; found \"" + operation + "\"");
		}

		return tree;
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
	 * Generates a difference tree containing the differences between the two
	 * input trees. When this difference tree is applied against the original tree
	 * by applyDiffs, the result will be the modified tree.
	 *
	 * @see #applyDiffs(Element, Element)
	 */
	private Element generateUpdate(Element original, Element modified) throws JDOMException {
		Element update = generateMyElement();

		JdomDifferenceFinder diffFinder = new JdomDifferenceFinder(Namespace.getNamespace(xmlns));
		diffFinder.findDifferences(original, modified, update);

		return update;
	}


	/**
	 * Generate root element with appropriate tag, attributes, namespace declarations etc.
	 */
	private Element generateMyElement()
	{
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3c.org/2001/XMLSchema-instance");

		/* timestamp - ISO8601 format
		 */
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssz");
 		Date currentTime = new Date();
 		String timestamp = formatter.format(currentTime);
		
		Element element = new Element(tagname, xmlns);
		element.addNamespaceDeclaration(xsi);
		element.setAttribute("schemaLocation", schemaLocation, xsi);
		element.setAttribute("ReqRespVersion", "2");
		element.setAttribute("Timestamp", timestamp);

		return element;
	}
}


/*
 * $Log: VMJdomDiff.java,v $
 * Revision 1.2  2002/01/31 22:42:20  gwheeler
 * Updated javadocs.
 *
 * Revision 1.1  2001/10/26 15:10:32  gwheeler
 * This class generates a JDOM tree that represents the differences
 * between two documents. It is similar to OtaUpdate (from which it
 * was derived) but it is not OTA specific.
 *
 *
 * 2001/10/23 17:54 gwheeler
 * The class VMJdomDiff was created by making a copy of OtaUpdate.
 * All of the earlier log entries (below) refer to OtaUpdate.
 *
 * Revision 1.4  2001/10/23 14:22:32  dmarshall
 * Call JdomDifferenceFinder constructor with Namespace arg to ensure elements
 * generated belong to the OTA namespace.
 * Ensure elements created during a simple 'replace' operation also belong
 * to the OTA namespace.
 *
 * Revision 1.3  2001/10/11 20:46:00  dmarshall
 * Add support for the OTA namespace and schemaLocation
 *
 * Revision 1.2  2001/10/11 15:47:30  gwheeler
 * Modified code around call to xpath.getElement. Since it now returns
 * an Object, I had to check the type and cast it. This might be
 * revisited later.
 *
 * Revision 1.1  2001/10/08 19:15:18  gwheeler
 * Moved to package org.vmguys.vmtools.ota from package
 * org.vmguys.vmtools.utils.
 *
 * Revision 1.1  2001/10/05 19:22:28  gwheeler
 * Changed class name. Formerly was GenericUpdate.
 *
 * Revision 1.2  2001/10/05 15:28:24  gwheeler
 * Updated name of otaelements package.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.12  2001/07/27 15:12:21  gwheeler
 * Updated Javadocs.
 *
 * Changed methods from protected to private.
 *
 * Revision 1.11  2001/07/17 15:15:32  gwheeler
 * Added code to implement the "element delete" operation.
 *
 * Revision 1.10  2001/07/13 20:14:31  gwheeler
 * Updated Javadocs.
 *
 * Revision 1.9  2001/07/11 15:50:23  gwheeler
 * Changed so Child attribute is optional in Subtree Insert and Element Insert operations.
 * If not specified, the new child is added after the rightmost existing child.
 *
 * Revision 1.8  2001/07/10 18:46:23  gwheeler
 * Changed the expected order of operations within a position. This no longer complies with
 * the OTA spec., but I believe it is necessary. Need more thinking on this.
 *
 * Changed the way the Modify Element operation is output. It used to have the new content
 * contained within the Element tag; it is now contained within a child of Element.
 *
 * Added a couple of checks for erroneous parameters which will throw exceptions.
 *
 * Revision 1.7  2001/07/09 16:15:06  gwheeler
 * More code completed.
 *
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.6  2001/07/05 14:28:27  gwheeler
 * Changed a method call so this now calls JdomDifferenceFinder.
 *
 * Revision 1.5  2001/07/03 13:33:57  gwheeler
 * Changed name of parent class from Operations to AbstractOperation.
 *
 * Revision 1.4  2001/06/25 18:31:51  gwheeler
 * Updated javadoc comments.
 *
 * Revision 1.3  2001/06/22 18:12:46  gwheeler
 * Changes to comments only.
 *
 */
