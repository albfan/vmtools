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
import org.jdom.Attribute;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/*
 * $Log: AttributeModifyOperation.java,v $
 * Revision 1.4  2002/02/01 20:39:14  gwheeler
 * Added method getOperationNodeNumber.
 *
 * Revision 1.3  2002/01/28 20:54:15  gwheeler
 * Changed the constructors.
 * Added method getOperationXpath.
 * Restored method asElement.
 *
 * Revision 1.2  2002/01/24 15:11:39  gwheeler
 * This operation has been simplified because many of the properties can now
 * be obtained directly from the node in the JDOM tree.
 *
 * Revision 1.1  2001/10/26 15:08:02  gwheeler
 * New class to represent an operation.
 *
 *
 * AttributeModifyOperation was created by making changes to ModifyAttributeOperation.
 * The following log entries are for the original class.
 *
 * Revision 1.4  2001/10/17 18:50:56  gwheeler
 * Modified to work with namespaces. Changed the way the new and
 * old parameters are being passed to the constructor. Also added a
 * parameter to take the namespace to be used for any new Elements.
 *
 * Revision 1.3  2001/10/16 20:56:00  gwheeler
 * Removed all code related to generating XML.
 *
 * Revision 1.2  2001/10/05 15:30:53  gwheeler
 * Updated comments to remove references to OTA. No code changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.5  2001/07/13 20:13:20  gwheeler
 * Modified Javadocs.
 *
 * Changed toString to include the contained comment (if any) in the output.
 *
 * Revision 1.4  2001/07/12 17:11:22  gwheeler
 * Implemented methods asElement and asXml.
 *
 * Revision 1.3  2001/07/10 18:31:27  gwheeler
 * Added code to set the nodeNumber and comment properties.
 *
 * Revision 1.2  2001/07/03 13:33:57  gwheeler
 * Changed name of parent class from Operations to AbstractOperation.
 *
 * Revision 1.1  2001/07/02 15:58:23  gwheeler
 * Classes to represent operations that need to be performed on one tree to make it into
 * a different tree.
 *
 */


/**
 * This class represents a change to an attribute of an Element.
 */
public class AttributeModifyOperation extends AbstractOperation {
	/**
	 * The new attribute.
	 */
	private Attribute newAttr;

	/**
	 * Constructs an operation that will modify an attribute of the given node.
	 * The attribute's new value is given.
	 */
	public AttributeModifyOperation(DiffElement node, Attribute newAttr) {
		this(node, newAttr, null);
	}
	
	
	/**
	 * Constructs an operation that will modify an attribute of the given node.
	 * The attribute's new value is given.
	 * The operation will include the specified comment. The comment may be null if it is not needed.
	 */
	public AttributeModifyOperation(DiffElement node, Attribute newAttr, String comment) {
		this.node = node;
		this.newAttr = newAttr;
		this.comment = comment;
	}


	/**
	 * Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.
	 */
	public Element asElement(Namespace namespace) {
		Element el = new Element("Attribute", namespace);

		el.setAttribute("Name", newAttr.getQualifiedName());
		el.setAttribute("Operation", "modify");
		el.setAttribute("Value", newAttr.getValue());

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
		return "modify attribute " + newAttr.getQualifiedName() + " of node " + node + " to " + newAttr.getValue();
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