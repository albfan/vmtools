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


package org.vmguys.vmtools.ota;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;        // only needed for testing
import org.vmguys.vmtools.utils.*;


/**
 * <p>This class generates and applies OTA update requests.
 * If given two pieces of OTA data, it will generate an update request that will
 * show the differences between the data. If given a piece of OTA data and an update
 * request, it will apply the update and return the modified data.</p>
 *
 * <p>This class operates on JDOM trees, rather than XML documents. An application
 * using this class should parse the input XML files to create the trees, or
 * use the result tree to generate an XML document.</p>
 */
public class OtaUpdate {
//	public static final String supportedVersions = "1";
	public static final String generatedVersion = "1";

	/** The element tag name for an OTA_UpdateRQ document. */
	public static final String tagname = "OTA_UpdateRQ";

	/** The default namespace for OTA documents. */
	public static final String xmlns   = "http://www.opentravel.org/OTA";

	/** The default schemaLocation value for OTA_UpdateRQ documents. */
	public static final String schemaLocation = "http://www.opentravel.org/OTA OTA_UpdateRQ.xsd";

	/**
	 * <p>The replace flag indicates the type of difference Element
	 * created by generateDiffs.</p>
	 *
	 * <p>Note: This is declared protected so the junit test
	 * programs can access it.</p>
	 *
	 * @see #generateDiffs(Element, Element, UniqueId)
	 * @see #generateDiffs(Element, Element, UniqueId, boolean)
	 * @see #getReplace()
	 * @see #setReplace(boolean)
	 */
	protected boolean replace;
	
	private ProgressReporter progressReporter;

	/**
	 * Constructs an object with <code>replace</code> set to <code>false</code> so
	 * it will attempt to find the minimal differences between the trees and record
	 * them in the update request.
	 *
	 * @see #replace
	 */
	public OtaUpdate() {
		this.replace = false;		// redundant, but makes it clear
	}

	/**
	 * Constructs an object with <code>replace</code> set to the specified value.
	 * If false, it will attempt to find the minimal differences between the trees and record
	 * them in the update request. If true, it will generate an update request that
	 * simply contains the modified version of the tree and a <code>replace</code> command.
	 *
	 * @see #replace
	 */
	public OtaUpdate(boolean replace) {
		this.replace = replace;
	}

	/**
	 * Sets <code>replace</code> to the specified value.
	 * If false, it will attempt to find the minimal differences between the trees and record
	 * them in the update request. If true, it will generate an update request that
	 * simply contains the modified version of the tree and a <code>replace</code> command.
	 *
	 * @see #replace
	 */
	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	/**
	 * Gets the current value of <code>replace</code>.
	 *
	 * @see #replace
	 */
	public boolean getReplace() {
		return this.replace;
	}

	/**
	 * <p>Returns the differences between the original tree and the
	 * modified tree as an OTA update request using the current
	 * setting of <code>replace</code>.</p>
	 *
	 * <p>The content of the output depends on the setting
	 * of the <code>replace</code> field. If true, the update
	 * message specifies the <code>replace</code> operation
	 * and includes the <code>modified</code> tree as
	 * the replacement. If false, the message specifies
	 * combinations of insert and delete operations and
	 * includes the minimal differences between the original
	 * and modified Elements.
	 *
	 * <p>In either case,
	 * when this update request is applied against the original tree by
	 * applyDiffs, the result will be the modified tree.</p>
	 *
	 * @see #applyDiffs(Element, Element)
	 * @see #replace
	 */
	public Element generateDiffs(Element original, Element modified, UniqueId id) throws JDOMException {
		return generateDiffs(original, modified, id, replace);
	}

	/**
	 * Returns the differences between the original tree and the
	 * modified tree as an OTA update request using the specified
	 * value of <code>replace</code>.
	 *
	 * <p>The content of the output depends on the setting
	 * of the <code>replace</code> parameter. If true, the update
	 * message specifies the <code>replace</code> operation
	 * and includes the <code>modified</code> tree as
	 * the replacement. If false, the message specifies
	 * combinations of insert and delete operations and
	 * includes the minimal differences between the original
	 * and modified Elements.
	 *
	 * <p>In either case,
	 * when this update request is applied against the original tree by
	 * applyDiffs, the result will be the modified tree.
	 *
	 * <p>The setting of the <code>replace</code> parameter to this method does
	 * not change the contents of the <code>replace</code> field in the object.
	 *
	 * @see #applyDiffs(Element, Element)
	 * @see #replace
	 */
	public Element generateDiffs(Element original, Element modified, UniqueId id, boolean replace) throws JDOMException {
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

		if (replace)
			rslt = generateReplace(original, modified, id);
		else
			rslt = generateUpdate(original, modified, id);

		return rslt;
	}

	/**
	 * Applies the update request to the original tree to recreate
	 * the modified tree. The tree rooted at the original element is
	 * cloned, then the updates specified in the update request are
	 * applied. The result is returned to the caller.
	 *
	 * @see #generateDiffs(Element, Element, UniqueId)
	 * @see #generateDiffs(Element, Element, UniqueId, boolean)
	 */
	public Element applyDiffs(Element original, Element updateRequest) throws JDOMException {
		Element rslt = null;
		
		// Make sure this is really an update request.

		if (tagname.equals(updateRequest.getName())) {
			// TODO: check diffs version

			List children = updateRequest.getChildren();

			// The first child of the update request should be the
			// UniqueId.

			Element child = (Element)children.get(0);
			
			if (child.getName().equals("UniqueId")) {
				// Create a List of the update operations, but without the
				// UniqueId element. I was thinking of cloning the children
				// List, but there's no guarantee that it implements
				// cloneable.
				
				ArrayList patches = new ArrayList();
				
				for (int i = 1; i < children.size(); ++i) {
					patches.add(children.get(i));
				}
				
				JdomPatcher patcher = new JdomPatcher();
				patcher.setDiscardWhitespace(true);
				rslt = patcher.patch(original, patches);
			}
			else {
				throw new JDOMException("expecting UniqueId; found \"" + child.getName() + "\"");
			}
		}
		else {
			throw new JDOMException("expecting " + tagname + "; found \"" + updateRequest.getName() + "\"");
		}
		
		return rslt;
	}


	/**
	 * Generates an OTA update request using the replace verb and
	 * containing the modified tree. When this update request is applied
	 * against the original tree by applyDiffs, the result will be
	 * the modified tree.
	 *
	 * @see #applyDiffs(Element, Element)
	 */
	private Element generateReplace(Element original, Element modified, UniqueId id) throws JDOMException {

		Namespace myNamespace = Namespace.getNamespace(xmlns);
		Element root = new Element("Root", myNamespace);
		root.setAttribute("Operation", "replace");
		root.addContent((Element)modified.clone());

		Element position = new Element("Position", myNamespace);
		position.setAttribute("XPath", "/" + original.getName());
		position.addContent(root);

		Element repl = generateMyElement();
		repl.addContent(id.asJdomElement());
		repl.addContent(position);

		return repl;
	}

	
	/**
	 * Generates an OTA update request containing the differences between the two
	 * Elements. When this update request is applied against the original tree
	 * by applyDiffs, the result will be the modified tree.
	 *
	 * @see #applyDiffs(Element, Element)
	 */
	private Element generateUpdate(Element original, Element modified, UniqueId id) throws JDOMException {
		Element update = generateMyElement();
		update.addContent(id.asJdomElement());

		JdomDifferenceFinder diffFinder = new JdomDifferenceFinder(Namespace.getNamespace(xmlns), true);
		if (progressReporter != null)
			diffFinder.setProgressReporter(progressReporter);
		diffFinder.setAllowRename(false);
		diffFinder.findDifferences(original, modified, update);
		
		// The difference finder has a slightly different view of the world than
		// the OTA, and so some of its operations may not be OTA-compliant.
		// Make sure none of them sneaked through. We might remove this in the
		// production version.
		
		checkContent(update);

		return update;
	}
	
	
	/**
	 * Checks the tree for unexpected types of nodes, and substitutes OTA operations
	 * in their place.
	 */
	private void checkContent(Element root) throws JDOMException {
		List children = root.getChildren();
		
		for (int i = 0; i < children.size(); ++i) {
			Element child = (Element)children.get(i);
			String name = child.getName();
			
			if ("Position".equals(name)) {
				checkPosition(child);
			}
			else if ("UniqueId".equals(name)) {
			}
			else {
				throw new JDOMException("the difference tree contains the non-OTA-compliant type \"" + name + "\"");
			//	System.out.println("the difference tree contains the non-OTA-compliant type \"" + name + "\"");
			}
		}
	}
	
	
	private void checkPosition(Element pos) throws JDOMException {
		List children = pos.getChildren();
		
		for (int i = 0; i < children.size(); ++i) {
			Element child = (Element)children.get(i);
			String name = child.getName();

			if ("Content".equals(name)) {
				// The difference finder refers to it as Content, but OTA calls it
				// Element. It's the same thing. Just change the name.
				
				child.setName("Element");
			}
			else if (!("Root".equals(name) || "Subtree".equals(name) || "Element".equals(name) || "Attribute".equals(name))) {
				throw new JDOMException("the difference tree contains the non-OTA-compliant type \"" + name + "\"");
			//	System.out.println("the difference tree contains the non-OTA-compliant type \"" + name + "\"");
			}
		}
	}
	
	
	/* Generate root element with appropriate tag, attributes, namespace declarations etc.
	 */
	private Element generateMyElement()
	{
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3c.org/2001/XMLSchema-instance");

		/* timestamp - ISO8601 format
		 */
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssz");
 		// Date currentTime = new Date();
 		// String timestamp = formatter.format(currentTime);
		
		Element element = new Element(tagname, xmlns);
		element.addNamespaceDeclaration(xsi);
		element.setAttribute("schemaLocation", schemaLocation, xsi);
		element.setAttribute("Version", generatedVersion);
		element.setAttribute("ReqRespVersion", generatedVersion);
		// element.setAttribute("Timestamp", timestamp);

		return element;
	}
	
	
	/**
	 * This method sets the progress reporter. When generateUpdate is called,
	 * it will call the progress reporter with updates on the percentage of
	 * the operation that has been performed. This can be useful during
	 * lengthy operations to provide feedback to the user.
	 */
	public void setProgressReporter(ProgressReporter pr) {
		progressReporter = pr;
	}
}


/*
 * $Log: OtaUpdate.java,v $
 * Revision 1.10  2002/02/01 20:35:14  gwheeler
 * Added a call to setAllowRename so the difference tree does not contain any
 * rename operations. OTA doesn't use rename.
 * Adjusted checkContent to take a better look at the difference tree for
 * non-OTA-compliant elements.
 *
 * Revision 1.9  2002/01/31 22:19:16  gwheeler
 * Updated javadocs.
 *
 * Revision 1.8  2002/01/29 21:21:38  gwheeler
 * Added setProgressReporter method to pass the progress report object
 * to the lower classes.
 *
 * Revision 1.7  2001/12/12 14:49:54  gwheeler
 * Many methods have been removed and placed into a separate
 * package. Only the OTA-specific code remains here.
 *
 * Revision 1.6  2001/11/19 18:39:45  dmarshall
 * Use correct OTA namespace per 2001C specification.
 * Suppress timestamp, echoToken attributes on the root element.
 *
 * Revision 1.5  2001/10/26 14:54:13  gwheeler
 * Added code to check the tree for non-OTA-compliant content.
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
