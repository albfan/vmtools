package org.vmguys.ota.utils;

import java.io.*;

/*
 * $Log: TestArray.java,v $
 * Revision 1.1  2001/10/04 20:15:15  gwheeler
 * Throw-away test program (that wasn't thrown away).
 *
 */


/**
 * A test to attempt to extract the minimum cost from a array.
 *
 * Given a array of c columns and r rows, attempt to find a
 * value in each column such that a) the total of the values
 * is a minimum, and b) each row can be used for only one
 * selection.
 *
 * If there are more rows than columns, the result will
 * necessarily not include a value from each row.
 *
 * If there are fewer rows than columns, the result will
 * necessarily not provide a value for one or more columns.
 *
 * In the final application, it is quite likely that array
 * elements (n,n) (along the major diagonal) will contain
 * values of 0, and these are preferred selections if possible.
 * This might provide a useful optimization in the code.
 *
 * All legitimate values are small, positive integers.  A
 * value of Integer.MAX_VALUE will be used to represent a
 * non-value. This will be used, for example, in the case
 * where there are fewer rows than columns. It may also be
 * used if the array is only partially populated -- the array
 * positions that are not populated will contain a value of
 * Integer.MAX_VALUE.
 */
public class TestArray {
	// Creates an instance of the class and calls the go method.

	public static void main(String[] args) {
		new TestArray().go();
		//new TestArray().test();
	}
	
	
	// This method just tests the Sequence class.
	
	private void test() {
		Sequence s = new Sequence(4, 2);
		
		int[] d;
		
		while ((d = s.next()) != null) {
			for (int c = d.length - 1; c >= 0; --c) {
				System.out.print(toPaddedString(d[c], 12));
			}
			System.out.println();
		}
	}


	// This is the main code for the class.

	private void go() {
		// Implementation note: Although Java doesn't care which
		// dimension of the array is used for what purpose, I have
		// adopted the convention that the first dimension
		// represents the rows, and the second represents the
		// columns. This makes it easiest to read the array
		// initializers, because each nested set represents a row.
		
		int array[][] = {
			{5, 3, 0, 1},
			{0, 1, 2, 3},
			{4, 1, 2, 0},
			{1, 0, 1, 3},
			{2, 1, 2, 4},
			{2, 1, 1, 3}
		};
		
		System.out.println("array is " + array.length + " x " + array[0].length + ":");
		
		for (int r = 0; r < array.length; ++r) {
			for (int c = 0; c < array[0].length; ++c) {
				System.out.print(toPaddedString(array[r][c], 5));
			}
			System.out.println();
		}
		System.out.println();
		
		MinFinder mf;
		int[] solution;
		
		mf = new MinFinderB(array);
		
		System.out.println("Using MinFinderB, minimum cost is " + mf.getMinCost());
		
		solution = mf.getSolution();
		System.out.print("solution is ");
		for (int c = 0; c < solution.length; ++c)
			System.out.print(toPaddedString(solution[c], 5));
		System.out.println();
		
		mf = new MinFinderX(array);
		
		System.out.println("Using MinFinderX, minimum cost is " + mf.getMinCost());
		
		solution = mf.getSolution();
		System.out.print("solution is ");
		for (int c = 0; c < solution.length; ++c)
			System.out.print(toPaddedString(solution[c], 5));
		System.out.println();
	}


	/**
	 * Converts an int to a string, and pads it as necessary to
	 * get the specified width.
	 */
	private String toPaddedString(int i, int minWidth) {
		String s1 = Integer.toString(i);

		int width = s1.length();
		if (width < minWidth) {
			s1 = spaces(minWidth - width) + s1;
		}
		else {
			// Pad with at least one space.
			s1 = " " + s1;
		}

		return s1;
	}


	private static String blanks = "                    ";

	/**
	 * Returns a String containing n spaces.
	 */
	private String spaces(int n) {
		return blanks.substring(0, n);
	}
}


abstract class MinFinder {
	protected int[][] array;
	
	
	/**
	 * Constructor that takes the int array as a parameter.
	 */
	public MinFinder(int[][] a) {
		array = a;
	}
	
	
	/**
	 * Returns the minimum cost as an integer, which
	 * is the sum of the cost for each column. If the
	 * problem cannot be solved, returns Integer.MAX_VALUE.
	 */
	public abstract int getMinCost();
	
	
	/**
	 * Returns the solution to the minumum cost as
	 * an array of ints whose size is equal to the
	 * number of columns in the array.
	 */
	public abstract int[] getSolution();
	
	
	/**
	 * Returns the minimum of two integers.
	 */
	protected int min(int i, int j) {
		return (i <= j) ? i : j;
	}


	/**
	 * Returns the maximum of two integers.
	 */
	protected int max(int i, int j) {
		return (i >= j) ? i : j;
	}


	/**
	 * Returns the minimum of three integers.
	 */
	protected int min3(int i, int j, int k) {
		return min(i, min(j, k));
	}


}



/**
 * This class can only solve the problem if the array
 * is square, and all the values on the major diagonal
 * are 0. In that case the cost is 0. If the array
 * doesn't meet these criteria, this class returns
 * "no solution".
 */
class MinFinderA extends MinFinder {
	private boolean solved;
	private int minCost;
	private int[] solution;
	
	public MinFinderA(int[][] a) {
		super(a);
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
		solution = new int[array[0].length];
		
		if (array.length == array[0].length) {
			minCost = 0;	// assume this to start

			for (int i = 0; minCost == 0 && i < array.length; ++i) {
				if (array[i][i] == 0) {
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
 * This class solves the problem when the array is not square,
 * but it does contain a diagonal of 0 values. The following
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
 * could replace MinFinderA with only slight additional cost.
 */
class MinFinderB extends MinFinder {
	private boolean solved;
	private int minCost;
	private int[] solution;
	
	public MinFinderB(int[][] a) {
		super(a);
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
		int rows = array.length;
		int cols = array[0].length;
				
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
					if (array[startRow+i][i] == 0) {
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
					if (array[i][startCol+i] == 0) {
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
 * This class finds the minimum cost solution for any array.
 * It takes a brute force approach, and may be an expensive 
 * solution.
 */
class MinFinderX extends MinFinder {
	private boolean solved;
	private int minCost;
	private int[] solution;
	
	public MinFinderX(int[][] a) {
		super(a);
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
		int rows = array.length;
		int cols = array[0].length;
		
		// Create a sequencer to generate all the combinations to be
		// tested. We want the output to have as many digits as there
		// are columns in the array, and we want each digit to be a
		// row index into the array so that is the number base we specify.
				
		Sequence s = new Sequence(cols, rows);
		
		int[] candidate;
		int numCombinations = 0;
		
		minCost = Integer.MAX_VALUE;
		
		do {
			// Get the next combination of digits as a candidate
			// solution. The sequencer will return null when it runs
			// out of combinations.
			
			candidate = s.next();
			
			if (candidate != null) {
				++numCombinations;
				
				// Compute the cost for this candidate.
				
				int cost = 0;
				for (int i = 0; i < candidate.length; ++i) {
					if (candidate[i] < Integer.MAX_VALUE) {
						cost += array[candidate[i]][i];
					}
				}
				
				// If this cost is lower than others we have tested,
				// save it.

				if (cost < minCost) {
					solution = candidate;
					minCost = cost;
				}
			}
		} while (candidate != null);
		
		System.out.println("MinFinderX tried " + numCombinations + " combinations");
		
		solved = true;
	}
}



/**
 * This class generates all the numbers with the specified
 * number of digits and having no digit repeated.
 */
class Sequence {
	private int numDigits;
	private int base;
	private int pseudobase;
	private boolean end;		// true after all combinations have been used
	int[] digits;

	/**
	 * Constructor takes the number of digits and the number base as 
	 * parameters.
	 */	
	public Sequence(int d, int b) {
		numDigits = d;
		base = b;
		pseudobase = (numDigits >= base) ? numDigits : base;
		
		digits = new int[numDigits];	// automatically initialized to 0
	}
	
	
	/**
	 * Computes and returns the next set of digits. It returns
	 * null if there are no more combinations of digits.
	 */
	public int[] next() {
		computeNext();
		
		int[] rslt;
		
		if (end) {
			rslt = null;
		}
		else {
			// Make a copy of the current set of digits to be returned
			// as a result. If any digit is greater than base, replace
			// it with MAX_VALUE.

			rslt = new int[numDigits];
			for (int i = 0; i < numDigits; ++i) {
				if (digits[i] < base) {
					rslt[i] = digits[i];
				}
				else {
					rslt[i] = Integer.MAX_VALUE;
				}
			}
		}
		
		return rslt;
	}
	
	
	/**
	 * Advances the digits to the next valid number.
	 * Sets the property "end" to true if all combinations
	 * have been used.
	 */
	private void computeNext() {
		// Here's the algorithm: 
		//
		// If numDigits <= base then pseudobase will be the same as base,
		// and the numbers are generated without any difficulty.
		//
		// If numDigits > base there will not be enough unique digits to
		// fill all positions. In that case we want some positions to be
		// assigned MAX_VALUE. But, we still need to remember the
		// combination of digits so we can generate the next one. The
		// solution is to build the number using pseudobase (as though
		// numDigits == base). Then, as the digits array is being copied
		// to the result any digits >= base can be replaced with
		// MAX_VALUE.

		do {
			boolean carry = false;

			++digits[0];
			if (digits[0] >= pseudobase) {
				digits[0] = 0;
				carry = true;
			}

			for (int i = 1; carry && i < numDigits; ++i) {
				++digits[i];
				if (digits[i] >= pseudobase) {
					digits[i] = 0;
					carry = true;
				}
				else {
					carry = false;
				}
			}
			
			if (carry) {
				// If there is a carry out of the last digit, we've
				// used all the combinations.
				
				end = true;
			}
		} while (!end && !valid());
	}
	
	
	/**
	 * Checks a set of digits to see if they are all unique.
	 * Returns true if they are, or false if not.
	 */
	private boolean valid() {
		boolean valid = true;
		
		// Prepare a list of flags showing whether each digit is in use.
		// inuse[0] represents the number 0, inuse[1] represents 1, etc.
		
		boolean[] inuse = new boolean[pseudobase];	// will automatically be set to false
		
		for (int i = 0; valid && i < numDigits; ++i) {
			if (inuse[digits[i]]) {
				// That digit is already in use.
				
				valid = false;
			}
			else {
				inuse[digits[i]] = true;
			}
		}
		
		// Unfortunately this will accept some combinations where the
		// digits are unique now, but they will become the same when being
		// replaced with MAX_VALUE. We avoid this by ensuring only one
		// combination of the digits greater than base is used.
		//
		// To avoid duplicates in the output caused where we have more
		// than one number greater than base (only happens when
		// numDigits > base) we'll insist that any digits greater than
		// base are in ascending order. That way we won't get a
		// different number here (with those digits in a different
		// order) that appears the same in the output when those digits
		// are replaced with MAX_VALUE.
		
		if (valid && numDigits > base) {
			int largest = -1;

			for (int i = 0; valid && i < numDigits; ++i) {
				if (digits[i] >= base) {
					// This digit will later be replaced with MAX_VALUE.
					// Make sure that it is in ascending order compared
					// to other such numbers.
					
					if (digits[i] > largest) {
						// It is greater than the largest seen previously.
						// That's good. Now save its value as the largest
						// seen.
						
						largest = digits[i];
					}
					else {
						// It is smaller than the largest seen previously.
						// That's bad. Mark this as an invalid combination.
						
						valid = false;
					}
				}
			}
		}
		
		return valid;
	}
}

