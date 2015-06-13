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

import java.io.*;
import java.util.Arrays;


/*
 * $Log: MinFinder.java,v $
 * Revision 1.1  2002/01/31 15:18:14  gwheeler
 * no message
 *
 * Revision 1.7  2002/01/22 19:33:37  gwheeler
 * no message
 *
 * Revision 1.6  2001/12/19 15:54:25  gwheeler
 * Reduced the default value of maxOperations by a factor of 10.
 *
 * Fixed a small bug in ExhaustiveMinFinder to do with maxOperations.
 * In the loop that computes numOperations, the code exited if
 * numOperations >= maxOperations. In the following code, the test
 * is to see if numOperations > maxOperations. I changed the first code
 * to make these two tests equivalent.
 *
 * Revision 1.5  2001/11/30 21:36:37  gwheeler
 * Changed the code in ExhaustiveMinFinder that checked the number
 * of computations. If a column contained zero non-null entries it was
 * entering a dummy number just so it could proceed. That has been
 * removed and the code changed so it generates no result in that
 * case.
 *
 * Revision 1.4  2001/11/30 21:14:05  gwheeler
 * Changed all code to watch for null values in the input array.
 *
 * Added maxComputations property to limit the time spent analyzing the
 * array. Most of the subsidiary minfinders ignore it; it is mostly for the
 * benefit of ExhaustiveMinFinder.
 *
 * Changed ExhaustiveMinFinder to move the work out of the
 * constructor.
 *
 * Added ZeroValuesMinFinder.
 *
 * Revision 1.3  2001/11/27 15:47:19  gwheeler
 * Changed names of classes to be more descriptive. They're not known
 * outside this file, so no other code is affected.
 *
 * Corrected tests that check if a result has been found. They were testing
 * for 0, but should be testing for <MAX_INT.
 *
 * Added check in ExhaustiveMinFinder to limit the number of computations
 * that will be performed.
 *
 * Revision 1.2  2001/11/16 15:23:46  gwheeler
 * Modified to use the new getCost() method in CostOps instead of getting
 * the public data directly.
 *
 * Started to add another subsidiary finder (MinFinderG), but it is not
 * finished and so it is commented out.
 *
 * Revision 1.1.1.1  2001/10/04 18:52:54  gwheeler
 *
 *
 * Revision 1.6  2001/09/12 14:38:28  gwheeler
 *
 * Removed print statements.
 * Removed MinFinderX, which has been replaced by ExhaustiveMinFinder.
 * Removed all Sequence classes, which were used only by MinFinderX.
 * Added comments in ExhaustiveMinFinder.
 *
 * Revision 1.5  2001/09/12 13:32:36  gwheeler
 *
 * Numerous changes made while attempting to optimize behaviour.
 * This version contains all the Sequence classes that are used
 * by MinFinderX. This also contains ExhaustiveMinFinder, which will replace
 * MinFinderX.
 *
 * Revision 1.4  2001/07/05 14:29:09  gwheeler
 * Fixed Javadocs.
 *
 * Revision 1.3  2001/06/27 18:31:14  gwheeler
 * Fixed a bug in SingleRowMinFinder. It was setting the cost in the solution instead of setting the
 * row number.
 *
 * Revision 1.2  2001/06/25 18:36:09  gwheeler
 * Updated javadoc comments.
 *
 * Changed to accept array of CostOps instead of ints to allow it to work better with methods
 * in ElementUtils.
 *
 * Revision 1.1  2001/06/22 18:10:27  gwheeler
 * Performs various tests to locate the minimum sums in an array.
 *
 */


/**
 * <p>Given a array of c columns and r rows, attempt to find a
 * value in each column such that a) the total of the values
 * is a minimum, and b) each row is used for only one
 * selection.</p>
 *
 * <p>If there are more rows than columns, the result will
 * necessarily not include a value from each row.</p>
 *
 * <p>If there are fewer rows than columns, the result will
 * necessarily not provide a value for one or more columns.</p>
 *
 * <p>In the application where this will initially be used, it
 * is quite likely that array elements (n,n) (along the major
 * diagonal) will contain values of 0, and these are preferred
 * selections if possible. This might provide a useful
 * optimization in the code.</p>
 *
 * <p>All legitimate values are small, positive integers.  A
 * value of Integer.MAX_VALUE will be used to represent a
 * non-value. This will be used, for example, in the case
 * where there are fewer rows than columns. It may also be
 * used if the array is only partially populated -- the array
 * positions that are not populated will contain a value of
 * Integer.MAX_VALUE.</p>
 *
 * <p>NOTE: MinFinder was originally written to work with an array of ints as
 * input. It was changed to use an array of CostOps to make it more compatible
 * with the application using it. However, this code only reads the .cost
 * property of each element of the array. It does not use the ops property
 * at all. It could easily be changed back to use an array of ints again.</p>
 *
 * <p>NOTE: This should be re-architected. MinFinder should be an interface
 * that is implemented by various classes. The master MinFinder should have
 * an array of Class objects (or something like that) that it iterates
 * through, looking for a MinFinder that can solve the current problem.</p>
 */
public class MinFinder {
	private CostOps[][] costs;
	private long maxComputations;
	private int minCost;
	private int[] solution;
	private boolean solved;


	/**
	 * Constructs the object with an array of costs.
	 */
	public MinFinder(CostOps[][] costs) {
		this(costs, 1000000L);
	}
	
	
	/**
	 * Constructs the object with an array of costs and a maximum number
	 * of computations.
	 */
	public MinFinder(CostOps[][] costs, long maxComputations) {
		this.costs = costs;
		this.maxComputations = maxComputations;
		solved = false;

		/*
		System.out.println("MinFinder: called with " + costs.length + " x " + costs[0].length + " input");
		for (int i = 0; i < costs.length; ++i) {
			for (int j = 0; j < costs[i].length; ++j) {
				if (costs[i][j] == null)
					System.out.print("- ");
				else
					System.out.print(costs[i][j].getCost() + " ");
			}
			System.out.println();
		}
		System.out.println();
		 */
	}


	/**
	 * Returns the minimum cost.
	 */
	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}


	/**
	 * <p>Returns the choices that make up the minimum cost conversion.
	 * The result array has the same number of elements as there are
	 * columns in the input costs array. Each value of this result
	 * indicates the row element of the original array that should
	 * be used for that column.</p>
	 *
	 * <p>In other words, to select the minimal elements from the input
	 * array, use the following pseudocode:</p>
	 *
	 * <p><code>
	 * for (int i = 0; i < solution.length; ++i) {
	 *     choice[i] = array[solution[i]][i];
	 * }</code></p>
	 */
	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}
	
	
	private void findSolution() {
		findSolution2();
		solved = true;
		
		/*
		System.out.print("minimum cost is " + minCost + "; solution is ");
		for (int i = 0; i < solution.length; ++i)
			System.out.print(solution[i] + " ");
		System.out.println();
		 */
		
		System.out.println("--");
	}


	/**
	 * Finds the solution to the problem, and updates the instance
	 * variables minCost and solution.
	 */
	private void findSolution2() {
		// Try each subsidiary MinFinder in turn.
		// Quit when we get a good solution. Most of the minfinders
		// look for a special pattern in the input. If they can't find one, they
		// return MAX_VALUE. Some look for a small cost, and it should
		// be compared to other costs.
		
		// The first MinFinders look for a pattern in the input. If they find
		// it, the solution they return is guaranteed to be a minimum cost.
		// Take the first available solution and quit.
		
		{
			SingleColumnMinFinder mf = new SingleColumnMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using SingleColumnMinFinder");
				return;
			}
		}

		{
			SingleRowMinFinder mf = new SingleRowMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using SingleRowMinFinder");
				return;
			}
		}

		{
			SquareDiagZeroMinFinder mf = new SquareDiagZeroMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using SquareDiagZeroMinFinder");
				return;
			}
		}

		{
			RectDiagZeroMinFinder mf = new RectDiagZeroMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using RectDiagZeroMinFinder");
				return;
			}
		}

		{
			SingleValueRowsMinFinder mf = new SingleValueRowsMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using SingleValueRowsMinFinder");
				return;
			}
		}

		{
			SingleValueColumnsMinFinder mf = new SingleValueColumnsMinFinder(costs, maxComputations);
			minCost = mf.getMinCost();
			solution = mf.getSolution();
			if (minCost < Integer.MAX_VALUE) {
				System.out.println("found minimum cost using SingleValueColumnsMinFinder");
				return;
			}
		}
		
		// The remaining MinFinders look for a good, but not guaranteed, low
		// cost. Get a solution from each one and take the lowest value.

		/*
		{
			ApproximateMinFinder amf = new ApproximateMinFinder(costs, maxComputations);
			int cost = amf.getMinCost();
			//System.out.println("ApproximateMinFinder: minCost = " + approxCost);
			
			if (cost < minCost) {
				minCost = cost;
				solution = amf.getSolution();
				if (minCost == 0) {
					System.out.println("found zero cost using ApproximateMinFinder");
					return;
				}
				System.out.println("found lower cost (" + minCost + ") using ApproximateMinFinder");
			}
		}
		 */

		boolean exhaustFailed = false;
		{
			ExhaustiveMinFinder mfy = new ExhaustiveMinFinder(costs, maxComputations);
			int cost = mfy.getMinCost();
			//System.out.println("ExhaustiveMinFinder: minCost = " + exhaustCost);
			
			if (cost == Integer.MAX_VALUE) {
				exhaustFailed = true;
				System.out.println("ExhaustiveMinFinder gave up");
			}
			else if (cost < minCost) {
				minCost = cost;
				solution = mfy.getSolution();
				if (minCost == 0) {
					System.out.println("found zero cost using ExhaustiveMinFinder");
					return;
				}
				System.out.println("found lower cost (" + minCost + ") using ExhaustiveMinFinder");
			}
		}

		// If ExhaustiveMinFinder could not find an answer, try trimming the array
		// and trying again.
		
		if (exhaustFailed) {
			ZeroValuesMinFinder zmf = new ZeroValuesMinFinder(costs, maxComputations);
			int cost = zmf.getMinCost();
			//System.out.println("ZeroValuesMinFinder: minCost = " + cost);
			
			if (cost == Integer.MAX_VALUE) {
				System.out.println("ZeroValuesMinFinder gave up");
			}
			else if (cost < minCost) {
				minCost = cost;
				solution = zmf.getSolution();
				if (minCost == 0) {
					System.out.println("found zero cost using ZeroValuesMinFinder");
					return;
				}
				System.out.println("found lower cost (" + minCost + ") using ZeroValuesMinFinder");
			}
		}
	}
	
	/**
	 * Make a duplicate of a two-dimensional, rectangular array.
	 */
	public static CostOps[][] dupArray(CostOps[][] in) {
		int rows = in.length;
		int cols = in[0].length;
		
		CostOps[][] out = new CostOps[rows][cols];
		
		for (int col = 0; col < cols; ++col) {
			for (int row = 0; row < rows; ++row) {
				out[row][col] = in[row][col];
			}
		}
		
		return out;
	}
}	



/**
 * This class solves the problem if the array has only one
 * column. In that case we just choose the minimum value
 * in the column.
 */
class SingleColumnMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public SingleColumnMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}

	private void findSolution() {
		int rows = costs.length;
		int cols = costs[0].length;

		if (cols == 1) {
			solution = new int[cols];
			minCost = Integer.MAX_VALUE;

			for (int r = 0; r < rows; ++r) {
				if (costs[r][0] != null && costs[r][0].getCost() < minCost) {
					minCost = costs[r][0].getCost();
					solution[0] = r;
				}
			}
		}
		else {
			// This simple version only works one-column arrays.
			// Since this array is not like that, return a value that
			// indicates no solution.

			minCost = Integer.MAX_VALUE;
		}

		solved = true;
	}
}


/**
 * This class solves the problem if the array has only one
 * row. In that case we just choose the minimum value
 * in the row.
 */
class SingleRowMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public SingleRowMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}

	private void findSolution() {
		int rows = costs.length;
		int cols = costs[0].length;

		if (rows == 1) {
			int minCol = -1;
			minCost = Integer.MAX_VALUE;

			for (int c = 0; c < cols; ++c) {
				if (costs[0][c] != null && costs[0][c].getCost() < minCost) {
					minCost = costs[0][c].getCost();
					minCol = c;
				}
			}

			if (minCost < Integer.MAX_VALUE) {
				solution = new int[cols];
				for (int c = 0; c < cols; ++c) {
					solution[c] = Integer.MAX_VALUE;
				}

				solution[minCol] = 0;   // in this column, use the 0th row
			}
		}
		else {
			// This simple version only works one-column arrays.
			// Since this array is not like that, return a value that
			// indicates no solution.

			minCost = Integer.MAX_VALUE;
		}

		solved = true;
	}
}


/**
 * This class can only solve the problem if the array
 * is square, and all the values on the major diagonal
 * are 0. In that case the cost is 0. If the array
 * doesn't meet these criteria, this class returns
 * "no solution".
 */
class SquareDiagZeroMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public SquareDiagZeroMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}

	private void findSolution() {
		solution = new int[costs[0].length];

		if (costs.length == costs[0].length) {
			minCost = 0;	// assume this to start

			for (int i = 0; minCost == 0 && i < costs.length; ++i) {
				if (costs[i][i] != null && costs[i][i].getCost() == 0) {
					solution[i] = i;
				}
				else {
					minCost = Integer.MAX_VALUE;
				}
			}
		}
		else {
			// This simple version only works on square arrays.
			// Since this array is not square, return a value that
			// indicates no solution.

			minCost = Integer.MAX_VALUE;
		}

		solved = true;
	}
}



/**
 * This class solves the problem when the array is not square
 * but does contain a diagonal of 0 values. The following
 * are example arrays this class can solve:
 *
 * 0XXXX X0XXXX 0XXX XXXX XXXX
 * X0XXX XX0XXX X0XX 0XXX 0XXX
 * XX0XX XXX0XX XX0X X0XX X0XX
 * XXX0X XXXX0X XXX0 XX0X XX0X
 *              XXXX XXX0 XXX0
 *                        XXXX
 *
 * Actually, it can also solve when the array is square, so it
 * could replace SquareDiagZeroMinFinder with only slight additional cost.
 */
class RectDiagZeroMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public RectDiagZeroMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}

	private void findSolution() {
		int rows = costs.length;
		int cols = costs[0].length;

		solution = new int[cols];

		int startRow, startCol;
		boolean goodDiag;

		if (rows >= cols) {
			// The array has more rows than columns, so we need
			// to find the upper left end of the diagonal in the
			// first column.

			startCol = 0;
			goodDiag = false;

			for (startRow = 0; !goodDiag && startRow <= rows - cols; ++startRow) {
				// Look along the diagonal starting at (startRow, startCol)
				// to see if all the values are 0.

				goodDiag = true;

				for (int i = 0; goodDiag && i < cols; ++i) {
					if (costs[startRow+i][i] != null && costs[startRow+i][i].getCost() == 0) {
						solution[i] = startRow + i;
					}
					else {
						goodDiag = false;
					}
				}
			}
		}
		else {
			// The array has more columns than rows, so we need
			// to find the upper left end of the diagonal in the
			// first row.

			startRow = 0;
			goodDiag = false;

			for (startCol = 0; !goodDiag && startCol <= cols - rows; ++startCol) {
				// Some elements of the solution will have a non-value. Fill
				// it in now, then overwrite with the correct values as we
				// find them.

				for (int i = 0; i < cols; ++i)
					solution[i] = Integer.MAX_VALUE;

				// Look along the diagonal starting at (startRow, startCol)
				// to see if all the values are 0.

				goodDiag = true;

				for (int i = 0; goodDiag && i < rows; ++i) {
					if (costs[i][startCol+i] != null && costs[i][startCol+i].getCost() == 0) {
						solution[startCol+i] = startRow+i;
					}
					else {
						goodDiag = false;
					}
				}
			}
		}

		minCost = goodDiag ? 0 : Integer.MAX_VALUE;

		solved = true;
	}
}


/**
 * This class finds the minimum cost when all the values in
 * each row are the same. In that case it doesn't matter
 * which entry in the row is assigned to the solution. If the
 * array doesn't meet this criterion, this class returns
 * "no solution".
 */
class SingleValueRowsMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public SingleValueRowsMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}
	
	private void findSolution() {
		boolean canDo = true;
		
		int rows = costs.length;
		int cols = costs[0].length;
		
		solution = new int[cols];
		
		// NOTE: This algorithm bails out if any null entries are found
		// in the array. It might be possible to enhance it to work around
		// null entries, as long as the major diagonal is non-null. It might
		// also be possible to return a solution other than the major diagonal
		// to work around null entries.

		for (int row = 0; canDo && row < rows; ++row) {
			int firstVal = 0;
			
			if (costs[row][0] == null)
				canDo = false;
			else
				firstVal = costs[row][0].getCost();
			
			for (int col = 1; canDo && col < cols; ++col) {
				if (costs[row][col] == null || costs[row][col].getCost() != firstVal) {
					canDo = false;
				}
			}
		}
		
		if (canDo) {
			// Since all the values in row 0 are the same, and all values in row 1
			// are the same, and so on, just pick the costs on the major diagonal
			// to pass to the output. We know that canDo will be true only if there
			// are no null entries in the array.
			
			minCost = 0;
			for (int col = 0; col < cols; ++col) {
				if (col < rows) {
					solution[col] = col;
					minCost += costs[col][col].getCost();
				}
				else {
					solution[col] = Integer.MAX_VALUE;
				}
			}
		}
		else {
			minCost = Integer.MAX_VALUE;
		}
		
		solved = true;
	}
}


/**
 * This class finds the minimum cost when all the values in
 * each column are the same. In that case it doesn't matter
 * which entry in the column is assigned to the solution. We
 * just need to pick the columns with the minimum values.
 * If the array doesn't meet this criterion, this class returns
 * "no solution".
 */
class SingleValueColumnsMinFinder {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public SingleValueColumnsMinFinder(CostOps[][] c, long maxComputations) {
		costs = c;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}
	
	private void findSolution() {
		boolean canDo = true;
		
		int rows = costs.length;
		int cols = costs[0].length;
		
		solution = new int[cols];

		// NOTE: This algorithm bails out if any null entries are found
		// in the array. It might be possible to enhance it to work around
		// null entries, as long as the major diagonal is non-null. It might
		// also be possible to return a solution other than the major diagonal
		// to work around null entries.

		for (int col = 0; canDo && col < cols; ++col) {
			int firstVal = 0;
			
			if (costs[0][col] == null)
				canDo = false;
			else
				firstVal = costs[0][col].getCost();
			
			for (int row = 1; canDo && row < rows; ++row) {
				if (costs[row][col] == null || costs[row][col].getCost() != firstVal) {
					canDo = false;
				}
			}
		}
		
		if (canDo) {
			// We know that canDo will be true only if there are no null entries in the array.
			// The code doesn't need to test for null entries within this block.
			
			if (rows >= cols) {
				// Since all the values in col 0 are the same, and all values in col 1
				// are the same, and so on, just pick the costs on the major diagonal
				// to pass to the output. 

				minCost = 0;
				for (int col = 0; col < cols; ++col) {
					solution[col] = col;
					minCost += costs[col][col].getCost();
				}
			}
			else {
				// There are more columns than rows, so not all columns will be used
				// in the solution. Choose the columns so that we minimize the
				// cost.
				
				// Allocate an array to store the numbers of the lowest columns.
				// This doesn't hold the values in the columns, but the indexes
				// of the columns.
				
				int[] lowCols = new int[rows];
				boolean[] usedCols = new boolean[cols];	// all values will be false to start
				
				// Loop to fill in each entry of lowCols.
				
				for (int lowCol = 0; lowCol < rows; ++ lowCol) {
					// Find the first unused column.
					
					int candidate = 0;
					while (usedCols[candidate])
						++candidate;
					
					// Look at the rest of the columns to see if there's one with
					// a lower value.
					
					for (int testCol = candidate + 1; testCol < cols; ++testCol) {
						if (!usedCols[testCol] && costs[0][testCol].getCost() < costs[0][candidate].getCost())
							candidate = testCol;
					}
					
					// Save the index of the lowest-valued column. Also update
					// usedCols to show we've already used this column.
					
					lowCols[lowCol] = candidate;
					usedCols[lowCol] = true;
				}
				
				// Fill in the solutions array and minimum cost based on what we've found.
				
				for (int i = 0; i < cols; ++i)
					solution[i] = Integer.MAX_VALUE;
				
				minCost = 0;
				for (int i = 0; i < rows; ++i) {
					solution[lowCols[i]] = i;
					minCost += costs[0][lowCols[i]].getCost();
				}
			}
		}
		else {
			minCost = Integer.MAX_VALUE;
		}
		
		solved = true;
	}
}


/**
 * This class looks for a quick low cost by looking for the minimum
 * cost in each column.
 */
/*
class MinFinderG {
	private CostOps[][] costs;
	private boolean solved;
	private int minCost;
	private int[] solution;
	int rows;
	int cols;
	boolean[] rowsChosen;

	public MinFinderG(CostOps[][] c) {
		costs = c;
		solved = false;
		rows = costs.length;
		cols = costs[0].length;
		rowsChosen = new boolean[rows];
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}

	private void findSolution() {
		// Look for the minimum value in each column, keeping in mind
		// that a row can only be used once.
		
		for (int c = 0; c < cols; ++c) {
			int minCost = Integer.MAX_VALUE;
			int minRow = -1;
			
			for (int r = 0; r < rows; ++r) {
				if (!rowsChosen[r]) {
					if (costs[r][c].getCost() < minCost) {
						minCost = costs[r][c].getCost();
						minRow = r;
					}
				}
			}
			
			solution[c] = minRow;
			rowsChosen[minRow] = true;
		}
	}
}
 */


/**
 * This class finds the minimum cost solution for any array.
 * It takes a brute force approach, and may be an expensive
 * solution.
 */
class ExhaustiveMinFinder {
	private CostOps[][] costs;
	private long maxComputations;
	private boolean solved;
	private int minCost;
	private int[] minSolution;
	private int[] possibleSolution;
	int rows;
	int cols;
	boolean[] rowsChosen;
		

	public ExhaustiveMinFinder(CostOps[][] c, long maxComputations) {
		costs = MinFinder.dupArray(c);
		this.maxComputations = maxComputations;
		solved = false;
		rows = costs.length;
		cols = costs[0].length;
		
		minSolution = new int[cols];
		possibleSolution = new int[cols];
		rowsChosen = new boolean[rows];

		/*
		{
			System.out.println("creating ExhaustiveMinFinder with " + rows + " x " + cols + " input");
			for (int row = 0; row < rows; ++row) {
				for (int col = 0; col < cols; ++col) {
					if (costs[row][col] == null)
						System.out.print("-- ");
					else
						System.out.print(costs[row][col].getCost() + " ");
				}
				System.out.println();
			}
		}
		 */
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return minSolution;
	}

	
	/**
	 * Initializes things and starts the recursive algorithm.
	 */
	private void findSolution() {
		minCost = Integer.MAX_VALUE;

		// Determine approximately how many computations will be needed
		// to find the minimum cost.

		long numComputations = 1L;		// must be initialized to 1

		//System.out.print("ExhaustiveMinFinder: r=" + rows + ", c=" + cols + ", n=(");

		for (int col = 0; numComputations <= maxComputations && numComputations > 0 && col < cols; ++col) {
			// Find the number of non-null values in this column of the costs array.

			int valuesInColumn = 0;

			for (int row = 0; row < rows; ++row) {
				if (costs[row][col] != null)
					++valuesInColumn;
			}

			//System.out.print(((col == 0) ? "" : "*") + valuesInColumn);

			numComputations *= valuesInColumn;
		}
		//System.out.println(")=" + numComputations);

		if (numComputations > maxComputations || numComputations <= 0) {
			// The number of computations is too great. Shortcut the
			// process and return an "I dunno" answer.

			minCost = Integer.MAX_VALUE;

			//System.out.println("    too many computations; not processing");
		}
		else {
			// The number of computations seems reasonable. Proceed with
			// the work.
			
			find2(0);
		}

		/*
		if (minCost == Integer.MAX_VALUE) {
			for (int row = 0; row < rows; ++row) {
				for (int col = 0; col < cols; ++col) {
					if (costs[row][col] == null)
						System.out.print("-- ");
					else
						System.out.print(costs[row][col].getCost() + " ");
				}
				System.out.println();
			}
		}
		 */
		
		solved = true;
	}

	
	/**
	 * <p>Runs a recursive algorithm to choose combinations of values
	 * from the input array. Each level of recursion works with one
	 * column of the input.</p>
	 *
	 * <p>The object is to choose a set of numbers from the costs array
	 * to generate a minimum cost. The set of numbers chosen is saved
	 * in the solutions array. The following constraints must be met:</p>
	 *
	 * <p>1) Each number chosen must be from a different row/column of the
	 * costs array.</p>
	 *
	 * <p>The solution array holds the row indexes of the chosen numbers
	 * for each column. For example, solution[0] holds the index of the
	 * number chosen from costs[][0], solution[1] holds the index of 
	 * the number chosen from costs[][1], etc.</p>
	 *
	 * <p>If there are more columns than rows in the input, it will not
	 * be possible to choose a number from every column and still meet
	 * constraint 1. Therefore, a placeholder may be used in the
	 * solution array to indicate that no number is selected from a
	 * particular column. (The placeholder is Integer.MAX_VALUE.)</p>
	 */
	private void find2(int column) {
		int numValuesTested = 0;
		
		if (column < cols) {
			// Loop through each row in the specified column.
			// NOTE: If a solution is found with a minimum cost
			// of 0 we can stop, because we won't find any better
			// one.
			
			for (int row = 0; minCost > 0 && row < rows; ++row) {
				// Check to see if this row has already been used
				// in the current solution.
				
				if (!rowsChosen[row] && costs[row][column] != null) {
					// If not, put this row index in the possible
					// solution, and mark it in the rowsChosen array.
					
					possibleSolution[column] = row;
					rowsChosen[row] = true;
					++numValuesTested;
					
					// Make a recursive call to choose rows in the
					// columns to the right.

					find2(column + 1);
					
					// We're finished with this row, so
					// unmark it.

					rowsChosen[row] = false;
				}
			}
	
			if (cols > rows) {
				// In the case where there are more columns than rows, the
				// solution will necessarily not include an choice in every
				// column.
				// After trying each row, offer a solution where we deliberately
				// choose nothing for this column.

				possibleSolution[column] = Integer.MAX_VALUE;
				
				// Make a recursive call to choose rows in the
				// columns to the right.

				find2(column + 1);
			}
		}
		else {
			// If the column index is larger than allowed, it indicates
			// this is the depest level of the recursion. In that case
			// we can compute the cost of the current solution.
			
			// If any null array entry is found, the recursive call is
			// not made. Therefore, if this code is run we know that the
			// proposed solution does not include any null array entries.
			// The code in this block does not need to check for nulls.
			
			// If the number of columns is greater than the number of rows,
			// some of the solutions offered will omit choices for some
			// of the columns. However, some will omit too many choices
			// and not all rows will be included in the proposed solution.
			// Check for that and ignore such invalid solutions.
			
			boolean goodSolution = true;
			if (cols > rows) {
				for (int r = 0; r < rows && goodSolution; ++r) {
					if (!rowsChosen[r])
						goodSolution = false;
				}
			}
			
			if (goodSolution) {
				// Compute the cost of the proposed solution.
				
				int cost = 0;

				for (int c = 0; c < cols; ++c) {
					if (possibleSolution[c] < Integer.MAX_VALUE)
						cost += costs[possibleSolution[c]][c].getCost();
				}

				/*
				System.out.print("test: cost " + cost + "; solution ");
				for (int c = 0; c < cols; ++c)
					System.out.print(possibleSolution[c] + " ");
				System.out.println();
				 */

				if (cost < minCost) {
					//System.out.println("*** This is a new minimum. ***");

					// This is a new minimum solution. Save it.

					minCost = cost;
					for (int c = 0; c < cols; ++c)
						minSolution[c] = possibleSolution[c];
				}
			}
		}
	}
}


/**
 * This class looks for as many zero values as possible to try to
 * find a minimum cost.
 */
class ZeroValuesMinFinder {
	private CostOps[][] costs;
	private long maxComputations;
	private boolean solved;
	private int minCost;
	private int[] solution;

	public ZeroValuesMinFinder(CostOps[][] c, long maxComputations) {
		costs = MinFinder.dupArray(c);
		this.maxComputations = maxComputations;
		solved = false;
	}

	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}

	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}
	
	private void findSolution() {
		int rows = costs.length;
		int cols = costs[0].length;
		
		// Try to find a solution by looking for zero values
		// in the columns. If found, remove all the other values
		// from the column. Then pass the array to ExhaustiveMinFinder
		// to see if there is a solution.

		boolean madeChanges = false;
		
		for (int col = 0; col < cols; ++col) {
			boolean foundZero = false;
			
			for (int row = 0; row < rows; ++row) {
				if (costs[row][col] != null && costs[row][col].getCost() == 0) {
					foundZero = true;
				}
			}
			
			if (foundZero) {
				for (int row = 0; row < rows; ++row) {
					if (costs[row][col] != null && costs[row][col].getCost() > 0) {
						costs[row][col] = null;
					}
				}
				
				madeChanges = true;
			}
		}

		if (madeChanges) {
			//System.out.println("ZeroValuesMinFinder: made changes; trying again");
			ExhaustiveMinFinder emf = new ExhaustiveMinFinder(costs, maxComputations);
			minCost = emf.getMinCost();
			solution = emf.getSolution();
		}
		else {
			minCost = Integer.MAX_VALUE;
		}
		
		solved = true;
	}
}



/**
 * <p>This MinFinder was contributed by Richard Titze
 * (richard.titze@tin.it).</p>
 *
 * <p>It places all the costs from the input array into a
 * one-dimensional array, and sorts it by cost. It then looks for
 * the lowest costs that are from discrete rows and columns. This
 * generates a quich answer, but it is not guaranteed to be the
 * minimal answer.</p>
 */
class ApproximateMinFinder {
	CostOps[][] costs;
	private int minCost;
	private int[] solution;
	private boolean solved;


	/**
	 * Constructs the object with an array of costs and a maximum number
	 * of computations. The maxComputations parameter is not used by
	 * this code.
	 */
	public ApproximateMinFinder(CostOps[][] costs, long maxComputations) {
		this.costs = costs;
		solved = false;

		/*
		System.out.println("MinFinder: called with " + costs.length + " x " + costs[0].length + " input");
		for (r = 0; r < rows; r++) {
			for ( c = 0; c < cols; c++ ) {
				if (costs[r][c] == null)
					System.out.print("- ");
				else
					System.out.print(costs[r][c].getCost() + " ");
			}
			System.out.println();
		}
		System.out.println();
		 */
	}


	/**
	 * Returns the minimum cost.
	 */
	public int getMinCost() {
		if (!solved) {
			findSolution();
		}

		return minCost;
	}


	/**
	 * <p>Returns the choices that make up the minimum cost conversion.
	 * The result array has the same number of elements as there are
	 * columns in the input costs array. Each value of this result
	 * indicates the row element of the original array that should
	 * be used for that column.</p>
	 *
	 * <p>In other words, to select the minimal elements from the input
	 * array, use the following pseudocode:</p>
	 *
	 * <p><code>
	 * for (int i = 0; i < solution.length; ++i) {
	 *     choice[i] = array[solution[i]][i];
	 * }</code></p>
	 */
	public int[] getSolution() {
		if (!solved) {
			findSolution();
		}

		return solution;
	}
	
	
	private void findSolution() {
		int rows = costs.length;
		int cols = costs[0].length;
		
		// Create a one-dimensional array of the costs. Null entries in the
		// input are replaced by a value of Integer.MAX_VALUE. Each cost
		// is accompanied by the row and column number where it was located
		// in the input.

		CostMatrixEntry[] sortedCost = new CostMatrixEntry[rows * cols ];
		{
			int i = 0;
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					sortedCost[i++] = new CostMatrixEntry(r, c, costs[r][c] == null ? Integer.MAX_VALUE : costs[r][c].getCost());
				}
			}
		}
		
		// Sort the array of costs.
		
		Arrays.sort(sortedCost);

		// Initialize the solution array.
		
		solution = new int[cols];
		for (int c = 0; c < cols; c++) {
			solution[c] = Integer.MAX_VALUE;
		}
		
		// Initialize boolean arrays that will indicate which rows and columns of
		// the input costs have been selected. Each row and column will contribute
		// only one value to the solution.
            
		boolean usedRows[] = new boolean[rows];		// all values will initially be false
		boolean usedCols[] = new boolean[cols];
		
		// Determine how many values there will be in the solution. If the
		// input cost array has more rows than columns, all the columns will
		// be used in the solution. If there are more columns than rows,
		// some entries in the solution will be unused.
		
		int solutionEntries = (cols > rows) ? rows : cols;
		
		int columnsSolved = 0;

		for (int i = 0; i < sortedCost.length && columnsSolved < solutionEntries; ++i) {
			CostMatrixEntry current = sortedCost[i];
			
			if (current.cost != Integer.MAX_VALUE ) {
				if (!usedCols[current.col] && !usedRows[current.row]) {
					// This value is from a row and column we have not used yet.
					// Update the row/column info, and save the value in the solution.
					
					usedCols[current.col] = true;
					usedRows[current.row] = true;
					
					solution[current.col] = current.row;
					
					minCost += current.cost;
					++columnsSolved;
				}
			}
			else {
				// We have come to the part of the array with the highest costs.
				// There's no point searching further.
				
				break;
			}
		}
		
		// We need to verify that we have the correct number of values in the
		// solution. If not, we need to return an "I dunno" answer.
		
		if (columnsSolved < solutionEntries) {
			minCost = Integer.MAX_VALUE;
		}

		solved = true;
		
		/*
		for ( c = 0; c < cols; c++ )
			System.out.println("solution[" + c + "]=" + solution[c]);
		 */
		
	}
}

