/*
 * MinFinderMunkres.java
 *
 * Created on January 7, 2002, 1:32 PM
 */

package org.vmguys.vmtools.utils;

import java.util.ArrayList;

/**
 *
 * @author  gwheeler
 * @version 
 */
public class MinFinderMunkres {
	private CostOps[][] originalCosts;	// the original matrix of costs
	private int[][] costs;				// the matrix of costs to be manipulated
	private int rows, cols;				// dimensions of costs[][]
	private boolean[][] stars;			// true indicates a starred zero in costs
	private boolean[][] primes;			// true indicates a primed zero in costs
	private boolean[] coveredRows;		// true indicates a covered row
	private boolean[] coveredColumns;	// true indicates a covered column
	private RowCol z0;					// carries results from step4 to step5; yuck

	private int minCost;
	private int[] solution;
	private boolean solved;
	
	static final boolean TEST = false;
	

	/** Creates new MinFinderMunkres */
    public MinFinderMunkres(CostOps[][] costs) {
		this.originalCosts = costs;
		solved = false;
    }
	
	
	/**
	 * Main function for testing.
	 */
	public static void main(String args[]) {
		CostOps[][] co = new CostOps[3][4];
		co[0][0] = new CostOps(1);
		co[0][1] = new CostOps(2);
		co[0][2] = new CostOps(3);
		co[0][3] = new CostOps(4);
		co[1][0] = new CostOps(2);
		co[1][1] = new CostOps(4);
		co[1][2] = new CostOps(6);
		co[1][3] = new CostOps(8);
		co[2][0] = new CostOps(3);
		co[2][1] = new CostOps(6);
		co[2][2] = new CostOps(9);
		co[2][3] = new CostOps(12);
		
		/*
		CostOps[][] co = new CostOps[4][3];
		co[0][0] = new CostOps(1);
		co[0][1] = new CostOps(2);
		co[0][2] = new CostOps(3);
		co[1][0] = new CostOps(2);
		co[1][1] = new CostOps(4);
		co[1][2] = new CostOps(6);
		co[2][0] = new CostOps(3);
		co[2][1] = new CostOps(6);
		co[2][2] = new CostOps(9);
		co[3][0] = new CostOps(4);
		co[3][1] = new CostOps(8);
		co[3][2] = new CostOps(12);
		 */

		int rows = co.length;
		int cols = co[0].length;
		
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				System.out.print(co[r][c].getCost() + "  ");
			}
			System.out.println();
		}
		System.out.println();
		
		MinFinderMunkres mf = new MinFinderMunkres(co);
		int minCost = mf.getMinCost();
		System.out.println("minimum cost: " + minCost);
		
		int[] solution = mf.getSolution();
		System.out.print("solution: ");
		for (int i = 0; i < solution.length; ++i) {
			System.out.print(solution[i] + ", ");
		}
		System.out.println();
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

	
	/**
	 * <p>This method uses the Munkres algorithm to find the minimum cost
	 * set. For more information, see the following URLs:</p>
	 *
	 *<pre>
	 * http://www.npac.syr.edu/copywrite/pcw/node220.html
	 * http://campus.murraystate.edu/academic/faculty/bob.pilgrim/445/algorithms_7.html
	 *</pre>
	 *
	 * <p>Thanks to Richard Titze (richard.titze@tin.it) for pointing
	 * this out to me.</p>
	 *
	 * <p>Most of the code was translated into Java from the Pascal sample
	 * provided on the web pages of murraystate.edu.</p>
	 *
	 * <p></p>
	 */
	private void findSolution() {
		rows = originalCosts.length;
		cols = originalCosts[0].length;
		boolean inputRotated = false;
		
		// There are two (or three) reasons for making a copy of the input matrix.
		// 1) This algorithm modifies the matrix, and we don't want to
		//    mess up the caller's copy.
		// 2) By building a matrix of int's instead of CostOp's we can
		//    run faster.
		// 3) We might have to rotate the matrix, which means we need to copy
		//    it anyway.
		
		if (cols > rows) {
			// The Munkres algorithm requires a matrix where the number
			// of rows >= the number of columns. Rotate the matrix to suit
			// while we copy it.

			costs = new int[cols][rows];

			for (int r = 0; r < rows; ++r) {
				for (int c = 0; c < cols; ++c) {
					if (originalCosts[r][c] != null) {
						costs[c][r] = originalCosts[r][c].getCost();
					}
					else {
						costs[c][r] = Integer.MAX_VALUE;
					}
				}
			}
			
			inputRotated = true;
		
			// Reset the rows and columns to match the new dimensions.

			rows = costs.length;
			cols = costs[0].length;
			
			if (TEST) {
				System.out.println("rotated costs matrix");
			}
		}
		else {
			costs = new int[rows][cols];

			for (int r = 0; r < rows; ++r) {
				for (int c = 0; c < cols; ++c) {
					if (originalCosts[r][c] != null) {
						costs[r][c] = originalCosts[r][c].getCost();
					}
					else {
						costs[r][c] = Integer.MAX_VALUE;
					}
				}
			}
		}
		
		// Allocate the other data structures needed by the algorithm.
		// Note that booleans will be created with an initial value
		// of false.
		
		stars = new boolean[rows][cols];
		primes = new boolean[rows][cols];
		coveredRows = new boolean[rows];
		coveredColumns = new boolean[cols];

		// The algorithm is a step-by-step process, where the result
		// of one step determines which step will be executed next.
		// To implement this we use a state machine of sorts, where
		// each method returns an int that specifies the next step.
		
		int nextStep = 1;
		int operation = 2;
		boolean done = false;
		
		while (!done) {
			if (TEST) {
				System.out.println("------ Operation " + operation + ": step " + nextStep + " --------");
			}
			
			switch (nextStep) {
				case 1:
					nextStep = step1();
					break;
					
				case 2:
					nextStep = step2();
					break;
					
				case 3:
					nextStep = step3();
					break;
					
				case 4:
					nextStep = step4();
					break;
					
				case 5:
					nextStep = step5();
					break;
					
				case 6:
					nextStep = step6();
					break;
					
				default:
					done = true;
					break;
			}
			
			if (TEST) {
				for (int r = 0; r < rows; ++r) {
					for (int c = 0; c < cols; ++c) {
						System.out.print(costs[r][c]);
						if (stars[r][c])
							System.out.print("*");
						if (primes[r][c])
							System.out.print("'");
						System.out.print("  ");
					}
					System.out.println();
				}
				System.out.println();
				
				System.out.print("covered rows: ");
				for (int r = 0; r < rows; ++r) {
					if (coveredRows[r]) {
						System.out.print(r + "  ");
					}
				}
				System.out.println();
				System.out.println();
				
				System.out.print("covered cols: ");
				for (int c = 0; c < cols; ++c) {
					if (coveredColumns[c]) {
						System.out.print(c + "  ");
					}
				}
				System.out.println();
				System.out.println();

				++operation;
			}
		}
		
		// The starred zeros represent the solution.
		
		minCost = 0;
		
		if (inputRotated) {
			// Due to the rotation, we need to reverse the rows and
			// columns when computing the result. Because there are more
			// columns than rows in the input, some columns of the output
			// will contain non-values (represented by Integer.MAX_VALUE).
			
			solution = new int[rows];	// same as number of columns in input
			for (int r = 0; r < rows; ++r) {
				solution[r] = Integer.MAX_VALUE;
				
				for (int c = 0; c < cols; ++c) {
					if (stars[r][c]) {
						// Swap use of rows and columns in this section.
						
						solution[r] = c;
						minCost += originalCosts[c][r].getCost();
						break;
					}
				}
			}
		}
		else {
			solution = new int[cols];
			for (int c = 0; c < cols; ++c) {
				for (int r = 0; r < rows; ++r) {
					if (stars[r][c]) {
						solution[c] = r;
						minCost += originalCosts[r][c].getCost();
						break;
					}
				}
			}
		}
		
		if (TEST) {
			System.out.println("------- Done --------");
		}
		
		// Deallocate the structures we used.

		costs = null;
		stars = null;
		primes = null;
		coveredRows = null;
		coveredColumns = null;
		
		solved = true;
	}
	
	
	/**
	 * Performs step 1 of the Munkres algorithm.
	 *
	 * For each row of the matrix, find the smallest value and subtract
	 * that value from all the entries in the row. Go to step 2.
	 */
	private int step1() {
		for (int r = 0; r < rows; ++r) {
			int minVal = Integer.MAX_VALUE;
			
			for (int c = 0; c < cols; ++c) {
				if (costs[r][c] < minVal) {
					minVal = costs[r][c];
				}
			}
			
			for (int c = 0; c < cols; ++c) {
				if (costs[r][c] != Integer.MAX_VALUE)
					costs[r][c] -= minVal;
			}
		}
		
		// Next step will be step 2.
		
		return 2;
	}
	
	
	/**
	 * Performs step 2 of the Munkres algorithm.
	 *
	 * Find a zero in the matrix. If there is no starred zero in its
	 * row or column, star the zero. Repeat for each element in the
	 * matrix. Go to step 3.
	 */
	private int step2() {
		for (int r = 0; r < rows; ++r) {
			if (!coveredRows[r]) {
				for (int c = 0; c < cols; ++c) {
					if (!coveredColumns[c] && costs[r][c] == 0) {
						stars[r][c] = true;
						coveredRows[r] = true;
						coveredColumns[c] = true;
					}
				}
			}
		}
		
		// Reset the covered rows and columns arrays.
		
		clearCovers();
		
		// Next step will be step 3.
		
		return 3;
	}
	
	
	/**
	 * Performs step 3 of the Munkres algorithm.
	 *
	 * Cover each column containing a starred zero. If all columns are covered
	 * the starred zeros describe a complete set of unique assignments. 
	 * In this case the algorithm is done. Otherwise go to step 4.
	 */
	private int step3() {
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {	// optimization: stop if coveredColumns[c]
				if (stars[r][c]) {
					coveredColumns[c] = true;
				}
			}
		}
		
		int count = 0;
		
		for (int c = 0; c < cols; ++c) {
			if (coveredColumns[c]) {
				++count;
			}
		}
		
		// If all columns are not covered the next step will be 4.
		
		return (count >= cols) ? 99 : 4;
	}
	
	
	/**
	 * Performs step 4 of the Munkres algorithm.
	 *
	 * Find a noncovered zero and prime it. If there is no starred zero
	 * in the row containing this primed zero, go to step 5. Otherwise
	 * cover this row and uncover the column containing the starred zero.
	 * Continue in this manner until there are no uncovered zeros left.
	 * Save the smallest uncovered value and go to step 6.
	 */
	private int step4() {
		int nextStep = -1;
		
		boolean done = false;
		
		while (!done) {
			RowCol rc = findAZero();
			if (rc.row < 0) {
				nextStep = 6;
				done = true;
			}
			else {
				primes[rc.row][rc.col] = true;
				stars[rc.row][rc.col] = false;
				
				int starCol = findStarInRow(rc.row);
				if (starCol >= 0) {
					coveredRows[rc.row] = true;
					coveredColumns[starCol] = false;
				}
				else {
					z0 = rc;		// z0 is used in step5
					nextStep = 5;
					done = true;
				}
			}
		}
		
		return nextStep;
	}
	
	
	/**
	 * Performs step 5 of the Munkres algorithm.
	 *
	 * Construct a series of alternating primed and starred zeros.
	 */
	private int step5() {
		//RowCol[] path = new RowCol[rows * cols];
		//int count = 0;
		ArrayList path = new ArrayList(rows * cols);	// contains RowCol objects
		RowCol mostRecent;

		//path[count] = z0;
		path.add(z0);
		mostRecent = z0;
		
		boolean done = false;
		
		while (!done) {
			//int row = findStarInCol(path[count].col);
			int row = findStarInCol(mostRecent.col);
			if (row >= 0) {
				//++count;
				//path[count] = new RowCol(row, path[count-1].col);
				mostRecent = new RowCol(row, mostRecent.col);
				path.add(mostRecent);
			}
			else {
				done = true;
			}
			
			if (!done) {
				//int col = findPrimeInRow(path[count].row);
				//++count;
				//path[count] = new RowCol(path[count-1].row, col);
				
				int col = findPrimeInRow(mostRecent.row);
				mostRecent = new RowCol(mostRecent.row, col);
				path.add(mostRecent);
			}
		}

		// This next bit was in convert_path in the original Pascal
		// code. I have chosen to place it inline here so the path
		// can be kept local rather than making it a global
		// variable.
		
		for (int i = 0; i < path.size(); ++i) {
			/*
			int r = path[i].row;
			int c = path[i].col;
			
			stars[r][c] = false;		// this is not needed
			stars[r][c] = primes[r][c];
			 */
			
			RowCol rc = (RowCol)path.get(i);
			
			// If the zero was primed, make it starred instead.
			// If it was starred, unstar it.
			
			stars[rc.row][rc.col] = primes[rc.row][rc.col];
		}

		clearCovers();
		erasePrimes();
		
		return 3;
	}
	
	
	/**
	 * Performs step 6 of the Munkres algorithm.
	 *
	 * Add the value found in step 4 to every element of each covered
	 * row, and subtract it from every element of each uncovered column.
	 * Return to step 4.
	 *
	 * NOTE: Rather than carrying a value from step4(), it is easier to
	 * determine the minimum value here, in findSmallest().
	 */
	private int step6() {
		int minVal = findSmallest();
		
		for (int r = 0; r < rows; ++r) {
			if (coveredRows[r]) {
				if (TEST) {
					System.out.println("adding " + minVal + " to all elements of row " + r);
				}

				for (int c = 0; c < cols; ++c) {
					if (costs[r][c] != Integer.MAX_VALUE)
						costs[r][c] += minVal;
				}
			}
		}
		
		for (int c = 0; c < cols; ++c) {
			if (!coveredColumns[c]) {
				if (TEST) {
					System.out.println("subtracting " + minVal + " from all elements of column " + c);
				}
				
				for (int r = 0; r < rows; ++r) {
					if (costs[r][c] != Integer.MAX_VALUE)
						costs[r][c] -= minVal;
				}
			}
		}
		
		return 4;
	}
	
	
	/**
	 * Searches for a 0 element in the costs matrix.
	 * Returns the row and column where it is found. The coordinates
	 * will be (-1, -1) if one is not found.
	 */
	private RowCol findAZero() {
		RowCol rc = new RowCol(-1, -1);
		boolean found = false;
		
		for (int r = 0; !found && r < rows; ++r) {
			if (!coveredRows[r]) {
				for (int c = 0; !found && c < cols; ++c) {
					if (!coveredColumns[c] && costs[r][c] == 0) {
						rc.row = r;
						rc.col = c;
						found = true;
					}
				}
			}
		}
		
		return rc;
	}
	
	
	/**
	 * Searches for a starred 0 in a specified row of the matrix.
	 * Returns the column where it is found. The column
	 * will be -1 if one is not found.
	 */
	private int findStarInRow(int row) {
		int col = -1;
		
		for (int c = 0; col < 0 && c < cols; ++c) {
			if (stars[row][c]) {
				col = c;
			}
		}
		
		return col;
	}
	
	
	/**
	 * Searches for a starred 0 in a specified column of the matrix.
	 * Returns the row where it is found. The row
	 * will be -1 if one is not found.
	 */
	private int findStarInCol(int col) {
		int row = -1;
		
		for (int r = 0; row < 0 && r < rows; ++r) {
			if (stars[r][col]) {
				row = r;
			}
		}
		
		return row;
	}

	
	/**
	 * Searches for a primed 0 in a specified row of the matrix.
	 * Returns the column where it is found. The column
	 * will be -1 if one is not found.
	 */
	private int findPrimeInRow(int row) {
		int col = -1;
		
		for (int c = 0; col < 0 && c < cols; ++c) {
			if (primes[row][c]) {
				col = c;
			}
		}
		
		return col;
	}
	
	
	/**
	 * Resets the covered rows and columns.
	 */
	private void clearCovers() {
		for (int r = 0; r < rows; ++r) {
			coveredRows[r] = false;
		}
		
		for (int c = 0; c < cols; ++c) {
			coveredColumns[c] = false;
		}
	}
	
	
	/**
	 * Resets the primed zeros array.
	 */
	private void erasePrimes() {
		for (int r = 0; r < rows; ++r) {
			for (int c = 0; c < cols; ++c) {
				primes[r][c] = false;
			}
		}
	}
	
	
	/**
	 * Finds the smallest value in the matrix and returns it.
	 */
	private int findSmallest() {
		int min = Integer.MAX_VALUE;

		for (int r = 0; r < rows; ++r) {
			if (!coveredRows[r]) {
				for (int c = 0; c < cols; ++c) {
					if (!coveredColumns[c] && costs[r][c] < min) {
						min = costs[r][c];
					}
				}
			}
		}

		return min;
	}
}


/**
 * This class represents a row and column index in a matrix.
 */
class RowCol {
	public int row;
	public int col;
	
	public RowCol() {
		this(0, 0);
	}
	
	public RowCol(int row, int col) {
		this.row = row;
		this.col = col;
	}
}

