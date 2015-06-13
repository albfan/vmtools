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
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;


/*
 * $Log: ReplaceOperation.java,v $
 * Revision 1.1  2002/01/31 15:18:14  gwheeler
 * no message
 *
 * Revision 1.5  2001/10/26 15:06:07  gwheeler
 * Added the cost() method.
 *
 * Revision 1.4  2001/10/17 18:49:11  gwheeler
 * Added support for namespaces. Added a parameter to the constructor
 * to take the desired namespace, and added code to use that
 * namespace when creating new Elements.
 *
 * Revision 1.3  2001/10/16 20:56:51  gwheeler
 * Removed all code related to generating XML.
 *
 * Revision 1.2  2001/10/05 15:30:53  gwheeler
 * Updated comments to remove references to OTA. No code changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.6  2001/07/13 20:13:20  gwheeler
 * Modified Javadocs.
 *
 * Changed toString to include the contained comment (if any) in the output.
 *
 * Revision 1.5  2001/07/12 17:11:22  gwheeler
 * Implemented methods asElement and asXml.
 *
 * Revision 1.4  2001/07/10 18:31:28  gwheeler
 * Added code to set the nodeNumber and comment properties.
 *
 * Revision 1.3  2001/07/05 14:29:38  gwheeler
 * Changed instance variable from public to private.
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
 * This class contains the information needed to replace an entire tree.
 * This is only used when the replace operation starts at the root of the
 * tree. If a part of the tree is going to be replaced, DeleteOperation
 * and InsertOperation are used instead.
 */
public class ReplaceOperation extends AbstractOperation {
	/**
	 * This is a reference to the new tree to be used to replace the
	 * old one.
	 */
	private Element tree;


	/**
	 * Constructs an operation containing the replacement tree.
	 * The operation will include the
	 * specified comment. The comment may be null if it is not needed.
	 */
	public ReplaceOperation(Element replacementTree, Namespace namespace, String comment) {
		this.node = null;    // there is no node to reference
		this.xpath = "/";
		this.tree = replacementTree;
		this.namespace = namespace;
		this.comment = comment;
	}


	/**
	 * <p>Converts the operation to a JDOM Element, possibly with children, that
	 * represents the operation.</p>
	 *
	 * <p>The subtree to be inserted will be cloned before it is used.
	 * The clone operation is done here, instead of in the constructor,
	 * because this method is not likely to be called in all instances of
	 * this class. The clone operation is deferred to this point so the overhead
	 * is only incurred when needed.</p>
	 */
	public Element asElement() {
		Element el = new Element("Root", namespace);
		el.setAttribute("Operation", "replace");

		// We need to clone the subtree before adding it to our tree.

		el.addContent((Element)tree.clone());

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
		return treesize(tree);
	}
	
	
	/**
	 * Converts the operation to XML that represents the operation.
	 * The parameter should contain a string that can be used as a newline
	 * in the generated XML.
	 */
	/*
	public String asXml(String nl) throws JDOMException {
		StringBuffer xml = new StringBuffer();

		xml.append("<Root Operation=\"replace\">" + nl);
		if (comment != null) {
			xml.append("<!--" + comment + "-->" + nl);
		}

		// Walk the subtree and write the XML for it.

		listTree(tree, xml, nl);

		xml.append("</Root>" + nl);

		return xml.toString();
	}
	 */


	/**
	 * Returns a String with information about the operation.
	 */
	public String toString() {
		return xpath + ": replace entire tree with " + tree.getName() + ((comment == null) ? "" : (" (" + comment + ")"));
	}
}