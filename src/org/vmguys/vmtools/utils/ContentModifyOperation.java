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


/**
 * This class represents a modification of some of the content of an Element.
 * The content being changed might be a Comment, a CDATA, a String,
 * an EntityRef, or a ProcessingInstruction.
 */
public class ContentModifyOperation extends AbstractOperation {
	private Object oldContent;
	private Object newContent;


	/** 
	 * Constructs an operation that will delete content from the given node.
	 * The old content is given.
	 */
    public ContentModifyOperation(DiffElement node, Object oldContent, Object newContent) {
		this(node, oldContent, newContent, null);
	}
	
	
	/** 
	 * Constructs an operation that will delete content from the given node.
	 * The old content is given.
	 * The operation will include the specified comment. The comment may be null if it is not needed.
	 */
    public ContentModifyOperation(DiffElement node, Object oldContent, Object newContent, String comment) {
		this.node = node;
		this.oldContent = oldContent;
		this.newContent = newContent;
		this.comment = comment;
    }

	
	/**
	 * Returns a cost associated with this operation.
	 */
	public int cost() {
		return 1;
	}
	

	/**
	 * Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.
	 */
	public Element asElement(Namespace namespace) {
		Element el = new Element("Content", namespace);

		el.setAttribute("Operation", "modify");
		el.setAttribute("Child", childNumber(oldContent));
		
		if (newContent instanceof String) {
			el.addContent((String)newContent);
		}
		else if (newContent instanceof Comment) {
			el.addContent((Comment)newContent);
		}
		else if (newContent instanceof EntityRef) {
			el.addContent((EntityRef)newContent);
		}
		else if (newContent instanceof ProcessingInstruction) {
			el.addContent((ProcessingInstruction)newContent);
		}
		else if (newContent instanceof CDATA) {
			el.addContent((CDATA)newContent);
		}
		else if (newContent instanceof Element) {
			el.addContent((Element)((Element)newContent).clone());
		}
		else {
			el.addContent(newContent.toString());
		}

		if (comment != null) {
			Comment c = new Comment(comment);
			el.addContent(c);
		}

		return el;
	 }
	

	/**
	 * Returns the child number of the content as a String.
	 */
	private String childNumber(Object child) {
		int n = -1;
		
		Object[] c = node.getContentAsArray();
		for (int i = 0; n < 0 && i < c.length; ++i) {
			if (c[i] == child)
				n = i + 1;		// adjust to origin-1
		}
		
		return Integer.toString(n);
	}
	

	/**
	 * Returns a String with information about the operation.
	 */
	public String toString() {
		return "modify content " + oldContent + " to " + newContent + " in " + node;
	}
	

	/**
	 * Returns the xpath of the node this operation is being applied to.
	 * In this case it's the xpath of the content.
	 */
	public String getOperationXpath() throws JDOMException {
		return XPath.getXPath(node.getRoot(), node, oldContent);
	}
	

	/**
	 * Returns the number of the node this operation is being applied to.
	 */
	public int getOperationNodeNumber() {
		return node.getNodeNumber();
	}
	
}


/*
 * $Log: ContentModifyOperation.java,v $
 * Revision 1.1  2002/02/11 14:32:58  gwheeler
 * Initial version.
 *
 * Revision 1.4  2002/02/01 20:41:10  gwheeler
 * Added method getOperationNodeNumber.
 *
 * Revision 1.3  2002/01/31 22:22:50  gwheeler
 * Updated javadocs.
 *
 * Revision 1.2  2002/01/28 20:55:08  gwheeler
 * Initial version.
 *
 */
