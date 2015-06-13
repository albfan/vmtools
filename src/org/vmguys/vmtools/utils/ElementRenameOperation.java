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

import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/**
 * This class represents a change to the name of a node in the
 * tree.
 */
public class ElementRenameOperation extends AbstractOperation {
	private String newName;


	/**
	 * Constructs an object that will rename a node. The node and
	 * its new name are given.
	 */
	public ElementRenameOperation(DiffElement node, String newName) {
		this(node, newName, null);
	}
	
	
	/**
	 * Constructs an object that will rename a node. The node and
	 * its new name are given.
	 * The operation will include the specified comment. The comment may be null if it is not needed.
	 */
	public ElementRenameOperation(DiffElement node, String newName, String comment) {
		this.node = node;
		this.newName = newName;
		this.comment = comment;
	}


	/**
	 * Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.
	 */
	public Element asElement(Namespace namespace) {
		Element el = new Element("ElementName", namespace);

		el.setAttribute("Operation", "modify");
		el.setAttribute("Value", newName);

		if (comment != null) {
			Comment c = new Comment(comment);
			el.addContent(c);
		}

		return el;
	}
	

	/**
	 * Returns the cost of this operation.
	 */
	public int cost() {
		return 1;
	}
	
	
	/**
	 * Returns a String with information about the operation.
	 */
	public String toString() {
		String cmt = (comment == null) ? "" : " (" + comment + ")";

		return "modify the name from " + node.getName() + " to " + newName + cmt;
	}


	/**
	 * Returns the xpath of the node this operation is being applied to.
	 */
	public String getOperationXpath() throws JDOMException {
		return node.getXpath();
	}
	

	/**
	 * Returns the number of the node this operation is being applied to.
	 */
	public int getOperationNodeNumber() {
		return node.getNodeNumber();
	}
	
}


/*
 * $Log: ElementRenameOperation.java,v $
 * Revision 1.5  2002/02/01 20:45:01  gwheeler
 * Added method getOperationNodeNumber.
 *
 * Revision 1.4  2002/01/31 22:40:24  gwheeler
 * Updated javadocs.
 *
 * Revision 1.3  2002/01/28 21:33:13  gwheeler
 * Added import statement for org.jdom.Comment.
 *
 * Revision 1.2  2002/01/28 21:31:36  gwheeler
 * Completed the asElement method.
 * Added the getOperationXpath method.
 *
 * Revision 1.1  2002/01/24 15:10:18  gwheeler
 * This class represents an operation to rename
 * a node.
 *
 */
