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

import junit.framework.*;
import org.jdom.Element;
import org.jdom.Namespace;


/*
 * $Log: TestDeleteOperation.java,v $
 * Revision 1.6  2002/01/29 21:14:56  gwheeler
 * Changed references from Element to DiffElement.
 * Modified tests to suit latest version.
 *
 * Revision 1.5  2001/10/26 15:15:18  gwheeler
 * The class being tested has changed to SubtreeDeleteOperation,
 * so all references here were changed to match.
 * I guess I should change the name of this class to match. Later.
 *
 * Revision 1.4  2001/10/17 18:55:19  gwheeler
 * Added support for namespaces. A Namespace needs to be passed
 * to the operation when it is constructed.
 *
 * Revision 1.3  2001/10/16 20:59:06  gwheeler
 * Removed all code related to generating XML.
 *
 * Revision 1.2  2001/10/15 18:37:26  gwheeler
 * Removed extra comments. No functional changes.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.1  2001/07/27 15:17:42  gwheeler
 * Tests the DeleteOperation class.
 *
 */


/**
 * This is a JUnit test suite to test the DeleteOperation class.
 */
public class TestDeleteOperation extends TestCase {
	private DiffElement node1;
	private SubtreeDeleteOperation do1;

	private final String NODENAME = "element";
	//private final String XPATH = "/foo/bar";
	//private final int NODENUM = 7;
	private final String COMMENT = "comment";


	public TestDeleteOperation(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
		node1 = new DiffElement(NODENAME);
		//do1 = new SubtreeDeleteOperation(/*node1,*/ XPATH, NODENUM, Namespace.NO_NAMESPACE, COMMENT);
		do1 = new SubtreeDeleteOperation(node1, COMMENT);
	}


	protected void tearDown() throws Exception {
		do1 = null;
	}


	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new TestDeleteOperation("testNothing"));
		suite.addTest(new TestDeleteOperation("testConstruction"));
		suite.addTest(new TestDeleteOperation("testAsElement"));
		//suite.addTest(new TestDeleteOperation("testAsXml"));

		return suite;
	}


	// Verify we can instantiate the objects.

	public void testNothing() {
	}


	// Verify the objects are constructed correctly.

	public void testConstruction() {
		assertTrue("node is null", do1.node != null);
		assertTrue("node has incorrect value", do1.node == node1);
		//assertTrue("xpath is null", do1.xpath != null);
		//assertTrue("xpath has incorrect value (" + do1.xpath + ")", do1.xpath.equals(XPATH));
		//assertTrue("node number has incorrect value (" + do1.nodeNumber + ")", do1.nodeNumber == NODENUM);
		assertTrue("comment is null", do1.comment != null);
		assertTrue("comment has incorrect value (" + do1.comment + ")", do1.comment.equals(COMMENT));
	}


	// Get the operation as an Element and verify it is correct.

	public void testAsElement() {
		Element el = do1.asElement(Namespace.NO_NAMESPACE);
		assertTrue("returned Element is null", el != null);
		assertTrue("Element has wrong name (" + el.getName() + ")", "Subtree".equals(el.getName()));
		String op = el.getAttributeValue("Operation");
		assertTrue("Element has no Operation attribute", op != null);
		assertTrue("Element has wrong Operation (" + op + ")", "delete".equals(op));
	}


	// Get the operation as XML and verify it is correct.

	/*
	public void testAsXml() {
		String nl = System.getProperty("line.separator");
		String goodXml = "<Subtree Operation=\"delete\">" + nl + "<!--" + COMMENT + "-->" + nl + "</Subtree>" + nl;

		String xml = do1.asXml(nl);
		assertTrue("returned XML is null", xml != null);
		assertTrue("returned XML is wrong", goodXml.equals(xml));
	}
	 */
}

