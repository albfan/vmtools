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
import org.jdom.Namespace;


/*
 * $Log: TestCostOps.java,v $
 * Revision 1.4  2002/01/29 21:18:00  gwheeler
 * Modified slightly to match constructor of operation object.
 *
 * Revision 1.3  2001/10/26 15:12:42  gwheeler
 * Changed to use the new getters and setters in CostOps.
 *
 * Revision 1.2  2001/10/17 18:55:19  gwheeler
 * Added support for namespaces. A Namespace needs to be passed
 * to the operation when it is constructed.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.1  2001/07/27 15:17:03  gwheeler
 * Tests the CostOps class.
 *
 */


/**
 * This is a JUnit test suite to test the CostOps class.
 */
public class TestCostOps extends TestCase {
	private CostOps cost1, cost2;


	public TestCostOps(String name) {
		super(name);
	}


	protected void setUp() throws Exception {
		cost1 = new CostOps();
		cost2 = new CostOps();
	}


	protected void tearDown() throws Exception {
		cost1 = null;
		cost2 = null;
	}


	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new TestCostOps("testNothing"));
		suite.addTest(new TestCostOps("testConstruction"));
		suite.addTest(new TestCostOps("testAdd"));

		return suite;
	}


	// Verify we can instantiate the objects.

	public void testNothing() {
	}


	// Verify the state of a newly-constructed CostOps object.

	public void testConstruction() {
		assertTrue("initial cost should be 0", cost1.getCost() == 0);
		assertTrue("initial ops should not be null", cost1.getOps() != null);
		assertTrue("initial ops size should be 0", cost1.getOps().size() == 0);
	}


	// Verify that two CostOps can be added.

	public void testAdd() {
		addOps(cost1);
		addOps(cost2);

		cost1.add(cost2);

		assertTrue(("cost should be " + (cost2.getCost() * 2)), cost1.getCost() == (cost2.getCost() * 2));
		assertTrue(("number of operations should be " + (cost2.getOps().size() * 2)), cost1.getOps().size() == (cost2.getOps().size() * 2));
	}


	// Add some information to a CostOps object.

	private void addOps(CostOps c) {
		DiffElement el = new DiffElement("test");
		c.add(new ContentDeleteOperation(el, "foo"));
		c.setCost(3);
	}
}

