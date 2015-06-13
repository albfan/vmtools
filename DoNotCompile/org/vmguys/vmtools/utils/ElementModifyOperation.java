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

import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;


/*
 * $Log: ElementModifyOperation.java,v $
 * Revision 1.1  2002/01/31 15:17:57  gwheeler
 * no message
 *
 * Revision 1.2  2001/10/26 21:02:03  gwheeler
 * Added code to wrap Comments and ProcessingInstructions.
 *
 * Revision 1.1  2001/10/26 15:08:03  gwheeler
 * New class to represent an operation.
 *
 *
 * ElementModifyOperation was created by copying ModifyContentOperation.
 * The following log entries are for the original class.
 *
 * Revision 1.5  2001/10/17 18:49:11  gwheeler
 * Added support for namespaces. Added a parameter to the constructor
 * to take the desired namespace, and added code to use that
 * namespace when creating new Elements.
 *
 * Revision 1.4  2001/10/16 20:56:29  gwheeler
 * Removed all code related to generating XML.
 *
 * Revision 1.3  2001/10/11 15:49:29  gwheeler
 * Added a missing newline in the output of asXml.
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
 * This class represents a change to the content of an Element.
 */
public class ElementModifyOperation extends AbstractOperation {
	/**
	 * This element contains the content we want to have in the
	 * modified element. If this is null, the existing
	 * content should be deleted.
	 */
	private Object newContent;


	/**
	 * Constructs an operation that will modify the content of a node. The
	 * xpath and noteNumber refer to the content being modified.
	 * The operation will include the
	 * specified comment. The comment may be null if it is not needed.
	 */
	public ElementModifyOperation(String xpath, Object newContent, int nodeNumber, Namespace namespace, String comment) {
		this.xpath = xpath;
		this.newContent = newContent;
		this.nodeNumber = nodeNumber;
		this.namespace = namespace;
		this.comment = comment;
	}


	/**
	 * <p>Converts the operation to a JDOM Element that
	 * represents the operation.</p>
	 */
	public Element asElement() throws JDOMException {
		Element el = new Element("Element", namespace);
		el.setAttribute("Operation", "modify");

		if (newContent instanceof Element) {
			Element el2 = (Element)((Element)newContent).clone();
			el.addContent(wrapMetaData(el2));
		}
		else if (newContent instanceof String) {
			el.addContent((String)newContent);
		}
		else if (newContent instanceof Comment) {
			el.addContent(wrapComment((Comment)(((Comment)newContent).clone())));
		}
		else if (newContent instanceof CDATA) {
			el.addContent((CDATA)(((CDATA)newContent).clone()));
		}
		else if (newContent instanceof ProcessingInstruction) {
			el.addContent(wrapPI((ProcessingInstruction)(((ProcessingInstruction)newContent).clone())));
		}
		else if (newContent instanceof EntityRef) {
			el.addContent((EntityRef)(((EntityRef)newContent).clone()));
		}
		else {
			throw new JDOMException("Can't handle " + newContent.getClass().getName() + " in AddContentOperation");
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
		return 2;
	}
	
	
	/**
	 * Returns a String with information about the operation.
	 */
	public String toString() {
		String cmt = (comment == null) ? "" : " (" + comment + ")";

		return xpath + ": modify the content" + cmt;
	}
}