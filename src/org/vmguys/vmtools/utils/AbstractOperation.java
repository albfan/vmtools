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
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;


/**
 * This is the parent class of the various operations that can be put
 * into a CostOps object.
 */
public abstract class AbstractOperation {
	/**
	 * This is a reference to the Element being operated upon.
	 * For the operations that operate upon an Element's content
	 * (AddContentOperation, DeleteContentOperation, etc.) this
	 * is the Element that contains the content.
	 */
	protected DiffElement node;

	/**
	 * This is a comment associated with the operation.
	 */
	protected String comment;
	

	public AbstractOperation() {
	}

	public String toString() {
		return "undetermined operation";
	}


	/**
	 * Returns the node number this operation is being applied to.
	 */
	public int getNodeNumber() {
		return node.getNodeNumber();
	}
	
	
	/**
	 * Returns the xpath of the node saved in this operation.
	 */
	public String getXpath() throws JDOMException {
		return node.getXpath();
	}
	
	
	/**
	 * <p>Returns the xpath of the node this operation is being applied to.
	 * This may not be the same as the xpath of the node. In some cases
	 * the xpath needed for the operation is the node's parent.</p>
	 *
	 * <p>NOTE: I think this is too specific to the upper levels. Not all
	 * difference representations are going to need the same value here.
	 * This should be removed and replaced with some other technique.</p>
	 */
	public abstract String getOperationXpath() throws JDOMException;	
	
	
	/**
	 * <p>Returns the number of the node this operation is being applied to.
	 * This may not be the same as the number of the node. In some cases
	 * the number needed for the operation is the node's parent.</p>
	 *
	 * <p>NOTE: I think this is too specific to the upper levels. Not all
	 * difference representations are going to need the same value here.
	 * This should be removed and replaced with some other technique.</p>
	 */
	public abstract int getOperationNodeNumber();
	
	
	/**
	 * Returns a cost associated with this operation. Generally, the more
	 * complex the tree that will be returned by asElement, the higher
	 * the cost.
	 */
	public abstract int cost();
	
	
	/**
	 * Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.
	 */
	public abstract Element asElement(Namespace namespace);


	/**
	 * Looks for any Comments or ProcessingInstruction elements in the
	 * tree and replaces them with an Element containing the same
	 * information. This ensures they will not be confused with comments
	 * or processing instructions.
	 */
	protected Element wrapMetaData(Element node) {
		List content = node.getContent();
		
		for (int i = 0; i < content.size(); ++i) {
			Object x = content.get(i);
			
			if (x instanceof Element) {
				wrapMetaData((Element)x);
			}
			else if (x instanceof Comment) {
				content.remove(i);
				content.add(i, wrapComment((Comment)x));
			}
			else if (x instanceof ProcessingInstruction) {
				content.remove(i);
				content.add(i, wrapPI((ProcessingInstruction)x));
			}
		}
		
		return node;
	}
	
	
	/**
	 * Returns an Element containing all the pertinent information about a
	 * Comment.
	 */
	protected Element wrapComment(Comment comment) {
		Element c = new Element("Comment");
		c.setAttribute("Value", (comment.getText()));
		return c;
	}


	/**
	 * Returns an Element containing all the pertinent information about a
	 * ProcessingInstruction.
	 */
	protected Element wrapPI(ProcessingInstruction procInst) {
		Element pi = new Element("ProcessingInstruction");
		pi.setAttribute("Target", procInst.getTarget());
		pi.setAttribute("Data", procInst.getData());
		return pi;
	}
}




/*
 * $Log: AbstractOperation.java,v $
 * Revision 1.12  2002/02/01 20:36:47  gwheeler
 * Added abstract method getOperationNodeNumber.
 *
 * Revision 1.11  2002/01/31 22:19:59  gwheeler
 * Updated javadocs.
 *
 * Revision 1.10  2002/01/28 20:49:41  gwheeler
 * Removed code that is no longer used.
 * Added getOperationXpath method.
 *
 * Revision 1.9  2002/01/24 15:05:19  gwheeler
 * Many of the properties have been removed from AbstractOperation (and its
 * subclasses) because they are now being contained within the nodes of
 * the JDOM tree. The operation just needs to hold a reference to the node
 * in order to have access to the information.
 *
 * Revision 1.8  2001/10/26 20:41:50  gwheeler
 * Changed sort order of children in compareTo.
 * Added methods to wrap Comments and ProcessingInstructions.
 *
 * Revision 1.7  2001/10/26 14:55:19  gwheeler
 * Added abstract method cost() so each operation can compute its
 * own cost.
 *
 * Added concrete operation treeSize() to compute the size of a subtree.
 *
 * Revision 1.6  2001/10/17 18:46:01  gwheeler
 * Added support for namespaces.
 *
 * Revision 1.5  2001/10/16 20:47:53  gwheeler
 * Removed all code related to generating XML.
 * Added child number and updated the compare method to use it.
 *
 * Revision 1.4  2001/10/15 18:30:06  gwheeler
 * Updated javadocs.
 *
 * Revision 1.3  2001/10/11 15:46:20  gwheeler
 * Modified declarations of abstract methods asElement and asXml so
 * they may throw a JDOMException.
 *
 * Revision 1.2  2001/10/05 15:26:28  gwheeler
 * Changed comments to remove references to OTA. No code changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.4  2001/07/13 20:13:19  gwheeler
 * Modified Javadocs.
 *
 * Changed toString to include the contained comment (if any) in the output.
 *
 * Revision 1.3  2001/07/12 17:09:44  gwheeler
 * Added abstract methods asXml and asElement for subclasses to implement.
 *
 * Added methods listTree, listAttr, and listContent for subclasses to use.
 *
 * Revision 1.2  2001/07/10 18:29:12  gwheeler
 * Now implements Comparable interface.
 * Added comment property.
 * Made properties protected.
 *
 * Revision 1.1  2001/07/03 13:32:49  gwheeler
 * New class name. Previously was Operations.
 *
 * Revision 1.1  2001/07/02 15:58:23  gwheeler
 * Classes to represent operations that need to be performed on one tree to make it into
 * a different tree.
 *
 */
