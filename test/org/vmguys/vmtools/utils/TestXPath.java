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
import junit.framework.*;
import org.jdom.Element;
import org.jdom.JDOMException;


/*
 * $Log: TestXPath.java,v $
 * Revision 1.2  2001/10/15 18:39:22  gwheeler
 * Added tests for new features that were added to XPath.
 * Changed some parameters from Element to Object to match changes
 * in other modules.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.5  2001/07/25 20:11:54  gwheeler
 * Changed names of JUnit methods to latest versions. Previous versions were deprecated.
 *
 * Revision 1.4  2001/07/09 16:18:27  gwheeler
 * Modified method names to match new JDOM version beta 7.
 *
 * Revision 1.3  2001/06/27 18:27:35  gwheeler
 * Added several tests: TestBadElement verifies that an exception is thrown if the child
 * is not found in the tree. TestIndex1 and TestIndex2 verify that the index number is
 * used correctly and generated correctly when there are more than one sibling with the
 * same name.
 *
 */


/**
 * This is a JUnit test suite to exercise the XPath class.
 */
public class TestXPath extends TestCase {
	private Element tree1, tree2, surname, fantasy1;
	private Object surnameObject;
	private String xpathToSurname;
	private String xpathToSurnameString;
	private String xpathToFantasy1;

	public TestXPath(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
		tree1 = createTree1();
		tree2 = createTree2();

		surname = tree1.getChild("Customer").getChild("PersonName").getChild("SurName");
		if (surname == null)
			throw new Exception("can't find SurName element");
		xpathToSurname = "/Profile/Customer/PersonName/SurName";
		
		surnameObject = surname.getContent().get(0);
		xpathToSurnameString = xpathToSurname + "/self::node()[1]";

		fantasy1 = tree2.getChild("Customer").getChild("Fantasy");
		if (fantasy1 == null)
			throw new Exception("can't find Fantasy[1] element");
		xpathToFantasy1 = "/Profile/Customer/Fantasy[1]";
	}


	protected void tearDown() throws Exception {
		tree1 = null;
		tree2 = null;
	}


	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new TestXPath("testNothing"));
		suite.addTest(new TestXPath("testRelative"));
		suite.addTest(new TestXPath("testSimplePath"));
		suite.addTest(new TestXPath("testSimplePath2"));
		suite.addTest(new TestXPath("testBadPath"));
		suite.addTest(new TestXPath("testGetPath"));
		suite.addTest(new TestXPath("testGetPath2"));
		suite.addTest(new TestXPath("testBadElement"));
		suite.addTest(new TestXPath("testIndex1"));
		suite.addTest(new TestXPath("testIndex2"));

		return suite;
	}


	// Verify we can instantiate the objects.

	public void testNothing() {
	}


	// Verify that getElement throws an exception if the
	// path is not absolute.

	public void testRelative() {
		try {
			XPath.getElement(tree1, "badpath");
			fail("getElement didn't throw exception on relative path");
		}
		catch (JDOMException jde) {
			String goodMsg = "not absolute";
			String errMsg = jde.getMessage();

			if (errMsg != null) {
				if (errMsg.indexOf(goodMsg) < 0) {
					fail("expected exception containing \"" + goodMsg + "\"; got \"" + errMsg + "\"");
				}
			}
			else {
				fail("getElement threw exception with null message");
			}
		}
	}


	// Use an xpath to get an element of the profile. Use
	// JDOM to get the same element, then verify they
	// match.

	public void testSimplePath() {
		Object e1 = null;

		try {
			e1 = XPath.getElement(tree1, xpathToSurname);
			assertTrue("getElement returned null", e1 != null);
			assertTrue("getElement returned incorrect element", e1 == surname);
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting element: " + jde.getMessage());
		}
	}


	// Use an xpath to get an element of the profile. Use
	// JDOM to get the same element, then verify they
	// match.

	public void testSimplePath2() {
		Object e1 = null;

		try {
			e1 = XPath.getElement(tree1, xpathToSurnameString);
			assertTrue("getElement returned null", e1 != null);
			assertTrue("getElement returned incorrect element", e1 == surnameObject);
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting element: " + jde.getMessage());
		}
	}


	// Verify that a bad path (where the specified element doesn't
	// exist) returns a null result.

	public void testBadPath() {
		try {
			Object e1 = XPath.getElement(tree1, "/Profile/Customer/Foofoo");
			assertTrue("got unexpected element using bad xpath", e1 == null);
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting element: " + jde.getMessage());
		}
	}


	// Verify it can get the path to a child element.

	public void testGetPath() {
		try {
			String xpath = XPath.getXPath(tree1, surname);
			assertTrue("got incorrect xpath", xpath.equals(xpathToSurname));
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting path: " + jde.getMessage());
		}
	}


	// Verify it can get the path to a child element.

	public void testGetPath2() {
		try {
			String xpath = XPath.getXPath(tree1, surnameObject);
			assertTrue("got incorrect xpath", xpath.equals(xpathToSurnameString));
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting path: " + jde.getMessage());
		}
	}


	// Verify it throws an exception if it can't find the child element.

	public void testBadElement() {
		try {
			Element nonChild = new Element("fake");
			String xpath = XPath.getXPath(tree1, nonChild);
			fail("getXPath didn't throw exception for bad element");
		}
		catch (JDOMException jde) {
		}
	}


	// Verify we can use an index to specify the correct child when
	// there are more than one with the same name.

	public void testIndex1() {
		try {
			// First, get the two children using JDOM techniques.

			Element customer = tree2.getChild("Customer");
			List twins = customer.getChildren("Fantasy");
			assertTrue("can't find the two Fantasy twins", twins.size() == 2);

			Element fantasy1 = (Element)twins.get(0);
			Element fantasy2 = (Element)twins.get(1);

			// Now get the two children using xpath.

			Object xfantasy1 = XPath.getElement(tree2, "/Profile/Customer/Fantasy[1]");
			assertTrue("can't get /Profile/Customer/Fantasy[1]", xfantasy1 != null);

			Object xfantasy2 = XPath.getElement(tree2, "/Profile/Customer/Fantasy[2]");
			assertTrue("can't get /Profile/Customer/Fantasy[2]", xfantasy1 != null);

			assertTrue("/Profile/Customer/Fantasy[1] didn't retrieve the correct element", xfantasy1 == fantasy1);
			assertTrue("/Profile/Customer/Fantasy[2] didn't retrieve the correct element", xfantasy2 == fantasy2);
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting child: " + jde.getMessage());
		}
	}


	// Verify we can get the correct xpath for a child that has siblings with the
	// same name.

	public void testIndex2() {
		try {
			String xpath = XPath.getXPath(tree2, fantasy1);
			assertTrue("got unexpected xpath to fantasy1", xpath.equals(xpathToFantasy1));
		}
		catch (JDOMException jde) {
			fail("caught JDOMException getting xpath: " + jde.getMessage());
		}
	}


	// Create a sample profile.

	private Element createTree1() {
		Element nameTitle = new Element("NameTitle");
		nameTitle.addContent("Mr.");

		Element givenName = new Element("GivenName");
		givenName.addContent("George");

		Element surName = new Element("SurName");
		surName.addContent("Smith");

		Element personName = new Element("PersonName");
		personName.setAttribute("NameType", "Default");
		personName.addContent(nameTitle);
		personName.addContent(givenName);
		personName.addContent(surName);

		Element customer = new Element("Customer");
		customer.addContent(personName);

		Element profile = new Element("Profile");
		profile.addContent(customer);

		return profile;
	}


	// Create a modified version of the other profile.

	private Element createTree2() throws Exception {
		Element newSurName = new Element("SurName");
		newSurName.addContent("Jones");

		Element profile = createTree1();

		Element customer = profile.getChild("Customer");

		Element personName = customer.getChild("PersonName");
		if (personName == null) {
			throw new Exception("can't find PersonName element");
		}
		personName.removeChild("SurName");
		personName.addContent(newSurName);

		Element fantasy1 = new Element("Fantasy");
		fantasy1.addContent("Lamborghini");

		Element fantasy2 = new Element("Fantasy");
		fantasy2.addContent("Money");

		customer.addContent(fantasy1);
		customer.addContent(fantasy2);

		return profile;
	}
}

