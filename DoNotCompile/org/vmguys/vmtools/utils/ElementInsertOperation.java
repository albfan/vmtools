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

import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;


/*
 * $Log: ElementInsertOperation.java,v $
 * Revision 1.2  2002/01/31 15:25:40  gwheeler
 * no message
 *
 * Revision 1.4  2002/01/25 19:28:54  gwheeler
 * This operation is no longer needed.
 *
 * Revision 1.3  2002/01/24 15:11:39  gwheeler
 * This operation has been simplified because many of the properties can now
 * be obtained directly from the node in the JDOM tree.
 *
 * Revision 1.2  2001/10/26 20:44:37  gwheeler
 * Added code to wrap Comments and ProcessingInstructions.
 *
 * Revision 1.1  2001/10/26 15:08:03  gwheeler
 * New class to represent an operation.
 *
 *
 * ElementInsertOperation was created by copying AddContentOperation.
 * The following log entries are for the original class.
 *
 * Revision 1.3  2001/10/17 18:49:09  gwheeler
 * Added support for namespaces. Added a parameter to the constructor
 * to take the desired namespace, and added code to use that
 * namespace when creating new Elements.
 *
 * Revision 1.2  2001/10/16 20:49:53  gwheeler
 * Removed all code related to generating XML.
 * Added code to clone objects before adding them to the tree.
 *
 * Revision 1.1  2001/10/15 18:41:19  gwheeler
 * This object represents an operation to modify the content of an Element.
 *
 */


/**
 * This class contains the information needed to insert a child element
 * into the tree. The child can be an Element, or it can be content such
 * as a String, a Comment, etc. However, if the object being inserted
 * is an Element, it will not have children. If it did, the SubtreeInsert
 * operation would be used.
 */
public class ElementInsertOperation extends AbstractOperation {
	/**
	 * Constructs an operation that will insert an Element into the tree.
	 * The operation will include the specified comment. The comment may be null if it is not needed.
	 */
	public ElementInsertOperation(DiffElement node) {
		this(node, null);
	}
	
	
	/**
	 * Constructs an operation that will insert an Element into the tree.
	 * The operation will include the specified comment. The comment may be null if it is not needed.
	 */
	public ElementInsertOperation(DiffElement node, String comment) {
		this.node = node;
		this.comment = comment;
	}


	/**
	 * <p>Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.</p>
	 */
	public Element asElement() throws JDOMException {
		Element el = new Element("Element", namespace);
		el.setAttribute("Operation", "insert");
		el.setAttribute("Child", Integer.toString(childNumber));

		if (content instanceof Element) {
			Element el2 = (Element)((Element)content).clone();
			el.addContent(wrapMetaData(el2));
		}
		else if (content instanceof String) {
			el.addContent((String)content);
		}
		else if (content instanceof Comment) {
			el.addContent(wrapComment((Comment)(((Comment)content).clone())));
		}
		else if (content instanceof CDATA) {
			el.addContent((CDATA)(((CDATA)content).clone()));
		}
		else if (content instanceof ProcessingInstruction) {
			el.addContent(wrapPI((ProcessingInstruction)(((ProcessingInstruction)content).clone())));
		}
		else if (content instanceof EntityRef) {
			el.addContent((EntityRef)(((EntityRef)content).clone()));
		}
		else {
			throw new JDOMException("Can't handle " + content.getClass().getName() + " in AddContentOperation");
		}
		

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
		return node.getTreesize();
	}


	/**
	 * Returns a String with information about the operation.
	 */
	public String toString() {
		//return xpath + ": insert element " + content.getClass().getName() + " to " + node.getName() + " as child " + childNumber + ((comment == null) ? "" : (" (" + comment + ")"));
		return "insert element " + node;
	}
	
	/**
	 * Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.
	 */
	public Element asElement(Namespace namespace) {
		return null;
	}
	
}