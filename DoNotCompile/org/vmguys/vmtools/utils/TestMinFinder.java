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


/*
 * TestMinFinder.java
 *
 * Created on September 11, 2001, 9:30 AM
 */

package org.vmguys.vmtools.utils;

/**
 *
 * @author  gwheeler
 * @version 
 */
public class TestMinFinder {

	/** Creates new TestMinFinder */
    public TestMinFinder() {
    }

    /**
    * @param args the command line arguments
    */
    public static void main (String args[]) {
		new TestMinFinder().go();
    }

	private void go() {
		CostOps[][] co = new CostOps[5][2];
		co[0][0] = new CostOps(0);
		co[0][1] = new CostOps(11);
		co[1][0] = new CostOps(5);
		co[1][1] = new CostOps(2);
		co[2][0] = new CostOps(2);
		co[2][1] = new CostOps(3);
		co[3][0] = new CostOps(3);
		co[3][1] = new CostOps(1);
		co[4][0] = new CostOps(8);
		co[4][1] = new CostOps(9);

		MinFinder mf = new MinFinder(co);
		
		int minCost = mf.getMinCost();
		int[] solution = mf.getSolution();
		
		System.out.print("minimum cost is " + minCost + "; solution is ");
		for (int i = 0; i < solution.length; ++i)
			System.out.print(solution[i] + " ");
		System.out.println();


		co = new CostOps[2][5];
		co[0][0] = new CostOps(0);
		co[0][1] = new CostOps(11);
		co[0][2] = new CostOps(5);
		co[0][3] = new CostOps(2);
		co[0][4] = new CostOps(2);
		co[1][0] = new CostOps(3);
		co[1][1] = new CostOps(3);
		co[1][2] = new CostOps(1);
		co[1][3] = new CostOps(8);
		co[1][4] = new CostOps(9);

		mf = new MinFinder(co);
		
		minCost = mf.getMinCost();
		solution = mf.getSolution();
		
		System.out.print("minimum cost is " + minCost + "; solution is ");
		for (int i = 0; i < solution.length; ++i)
			System.out.print(solution[i] + " ");
		System.out.println();
	}
}
