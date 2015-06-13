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

import java.util.List;
import junit.framework.*;
import org.jdom.*;
import org.vmguys.vmtools.utils.*;


/*
 * $Log: TestOtaUpdate.java,v $
 * Revision 1.4  2002/01/31 15:37:55  gwheeler
 * Had to cast the result of OtaUpdate.generateDiffs to a DiffElement.
 *
 * Revision 1.3  2002/01/31 14:15:19  gwheeler
 * Updated code to use DiffElement and DifferenceFinder2 instead of Element and
 * DifferenceFinder.
 *
 * Revision 1.2  2001/10/26 15:11:49  gwheeler
 * Added namespace support.
 *
 * Revision 1.1  2001/10/08 19:43:04  gwheeler
 * Moved to package org.vmguys.vmtools.ota from package
 * org.vmguys.vmtools.utils.
 *
 * Revision 1.2  2001/10/05 15:35:21  gwheeler
 * Updated name of otaelements package. No code changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.4  2001/07/25 20:09:37  gwheeler
 * Changed the way the test checks the result of GenericUpdate. Instead of looking directly
 * at the result, it uses DifferenceFinder to compare it with the desired result.
 *
 * Changed the names of JUnit methods to the latest version; previous versions were deprecated.
 *
 * Revision 1.3  2001/07/09 16:18:26  gwheeler
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.2  2001/06/27 18:28:31  gwheeler
 * Changed comments.
 *
 */


/**
 * This is a JUnit test suite to test the OtaUpdate class.
 */
public class TestOtaUpdate extends TestCase {
	private DiffElement original, modified;
	private UniqueId id;
	private OtaUpdate otaUpdate;
	private Namespace namespace;


	public TestOtaUpdate(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
		original = createOriginal();
		modified = createModified();
		id = new UniqueId("Profile", "12345");

		// The updater is instantiated with the parameter
		// true. This will force it to generate replacements,
		// rather than minimal diffs. We need to know that
		// in the test code when we examine the result of
		// generateDiff.

		otaUpdate = new OtaUpdate(true);
		namespace = Namespace.getNamespace(OtaUpdate.xmlns);
	}


	protected void tearDown() throws Exception {
		original = null;
		modified = null;
		id = null;
		otaUpdate = null;
	}


	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new TestOtaUpdate("testNothing"));
		suite.addTest(new TestOtaUpdate("testReplace"));
		suite.addTest(new TestOtaUpdate("testGenDiff"));

		return suite;
	}


	// Verify we can instantiate the objects.

	public void testNothing() {
	}


	// Verify we can change the replace property.

	public void testReplace() {
		// Since it was constructed with 'true', it should be
		// true now.

		assertTrue("initial value of 'replace' is incorrect", otaUpdate.replace == true);

		otaUpdate.setReplace(false);
		assertTrue("can't set 'replace' to false", otaUpdate.replace == false);

		otaUpdate.setReplace(true);
		assertTrue("can't set 'replace' to true", otaUpdate.replace == true);
	}


	// Verify the differences between two elements.

	public void testGenDiff() {
		try {
			DiffElement diffs = (DiffElement)otaUpdate.generateDiffs(original, modified, id);

		//	System.out.println(listTree(diffs));

			// Since generateDiffs is running in replace mode, the
			// result will be an OTA update request containing the
			// modified element.

			boolean looksGood = false;

			if (diffs.getName().equals("OTA_UpdateRQ")) {
				// The update request should contain an element called Position.

				Element position = diffs.getChild("Position", namespace);
				if (position != null) {
					// The Position element should contain an element called Root.

					Element root = position.getChild("Root", namespace);
					if (root != null) {
						// The Root element should contain 1 child, which is what we
						// want to find.

						List children = root.getChildren();
						if (children.size() == 1) {
							Element content = (Element)children.get(0);

//							System.out.println();
//							System.out.println("----- wanted -------");
//							System.out.println(listTree(modified));
//							System.out.println("-----  got   -------");
//							System.out.println(listTree(content));

/*
							// Get a list of the elements in the content, and a list of the
							// elements in the modified element (the global one), and compare
							// them.
							// NOTE: This compares only the first-level children. We could
							// add code to walk the whole tree and compare everything.

							List l1 = content.getChildren();
							List l2 = modified.getChildren();
							looksGood = l1.equals(l2);
*/

							// To see if the update request looks right, see if
							// it contains a copy of the modified tree. To do
							// that, just compare the two. Of course, this
							// assumes that DifferenceFinder is working correctly,
							// which may be a little circular. However, it should
							// have been tested separately before this test is run.

							DifferenceFinder2 diff = new DifferenceFinder2();
							CostOps co = diff.findDifferences(modified, content);
							looksGood = co.getCost() == 0;
						}
						else {
							fail("Root contains " + children.size() + " elements instead of 1");
						}
					}
					else {
						fail("can't get Root element");
					}
				}
				else {
					fail("can't get Position element");
				}
			}
			else {
				fail("first element is not OTA_UpdateRQ");
			}

			if (!looksGood) {
				fail("the diffs don't look right");
			}
		}
		catch (JDOMException jde) {
			fail("caught JDOMException generating diffs: " + jde.getMessage());
		}
	}


	private DiffElement createOriginal() {
		DiffElement nameTitle = new DiffElement("NameTitle");
		nameTitle.addContent("Mr.");

		DiffElement givenName = new DiffElement("GivenName");
		givenName.addContent("George");

		DiffElement surName = new DiffElement("SurName");
		surName.addContent("Smith");

		DiffElement personName = new DiffElement("PersonName");
		personName.setAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);

		DiffElement customer = new DiffElement("Customer");
		customer.addContent(personName);

		DiffElement profile = new DiffElement("Profile");
		profile.addContent(customer);

		return profile;
	}


	private DiffElement createModified() {
		DiffElement newSurName = new DiffElement("SurName");
		newSurName.addContent("Jones");

		DiffElement profile = createOriginal();

		DiffElement personName = (DiffElement)(profile.getChild("Customer").getChild("PersonName"));
		personName.removeChild("SurName");
		personName.addContent(newSurName);

		return profile;
	}


	/**
	 * Walk a tree and list all the information as XML. Returns a String.
	 */
	private String listTree(Element root) {
		String nl = System.getProperty("line.separator");
		StringBuffer xml = new StringBuffer();

		listTree(root, xml, nl);

		return xml.toString();
	}


	/**
	 * Walk a tree and list all the information as XML. All the XML is appended
	 * to the StringBuffer. Each line is terminated with the nl string. This is
	 * used to enumerate a subtree to be inserted.
	 */
	private void listTree(Element node, StringBuffer xml, String nl) {
		xml.append("<" + node.getName() + listAttribs(node) + ">" + nl);
		xml.append(listContents(node) + nl);

		List children = node.getChildren();
		for (int i = 0; i < children.size(); ++i) {
			listTree((Element)children.get(i), xml, nl);
		}

		xml.append("</" + node.getName() + ">" + nl);
	}


	/**
	 * Lists the attributes of an Element so they can be included in some XML.
	 */
	private String listAttribs(Element n) {
		StringBuffer rslt = new StringBuffer();

		List attribs = n.getAttributes();

		for (int a = 0; a < attribs.size(); ++a) {
			Attribute attrib = (Attribute)attribs.get(a);

			// Always insert a leading space. This separates the first attribute
			// from the element name, and separates the other attributes from
			// the preceding one.

			rslt.append(" " + attrib.getName() + "=\"" + attrib.getValue() + "\"");
		}

		return rslt.toString();
	}


	/**
	 * Lists the contents of an Element so they can be included in some XML.
	 */
	private String listContents(Element n) {
		StringBuffer rslt = new StringBuffer();

		List cont = n.getContent();

		for (int i = 0; i < cont.size(); ++i) {
			Object x = cont.get(i);
			if (x instanceof String) {
				rslt.append((String)x + " ");
			}
		}

		return rslt.toString();
	}
}

