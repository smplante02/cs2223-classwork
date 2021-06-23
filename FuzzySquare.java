package algs.hw1; // DO NOT COPY THIS CLASS INTO YOUR PROJECT AREA. USE AS IS

import java.util.Arrays;
import java.util.Random;

import algs.hw1.api.IFuzzySquareFinder;

/**
 * Creates an NxN two-dimensional array, A, containing a collection 
 * of integer values in ascending order, ranging  
 * from 0 (inclusively) to 10*N*N (inclusively).
 * 
 * You can inspect any 3x3 region, centered at (r,c) to detect if it 
 * contains a target value. The region can extend beyond the boundaries
 * of the square, but of course the target value can only exist within the 
 * square.
 * 
 * Consider the following 5x5 fuzzy square and a probe3x3(2, 2, TGT) request is made
 * 
 *   4,   8,  11,  17,  24         AB   AB   AB   AB  AB
 *  28,  35,  40,  47,  48         AB   35   40   47  M1
 *  51,  57,  60,  65,  71         M1   57   60   65  M2
 *  74,  77,  80,  85,  91         M2   77   80   85  BL
 *  99, 103, 111, 116, 118         BL   BL   BL   BL  BL
 * 
 * If TGT is either {35, 40, 47, 57, 60, 65, 77, 80, 85} then FOUND is returned.
 * 
 * If TGT is either {36-39, 41-46, 58, 59, 61-64, 78, 79, 81-84} then NOT_PRESENT is returned.
 * 
 * If TGT is a number between 0 and 34, then ABOVE is returned (noted as AB above).
 * 
 * If TGT is a number larger than 86, then BELOW is returned (noted as BL above).
 * 
 * If TGT is a number between 48 and 56, then M1 is returned (stands for middle-1).
 * 
 * If TGT is a number between 66 and 76, then M2 is returned (stands for middle-2).
 * 
 * If probe coordinates are wholly outside the range of the FuzzySquare, then OUT_OF_BOUNDS
 * is returned.
 * 
 * DO NOT MODIFY THIS FILE OR COPY IT INTO YOUR PROJECT AREA
 */
public class FuzzySquare {

	/** Probe coordinates are wholly outside of the bounds of the Fuzzy Square. */
	public static final int OUT_OF_BOUNDS = -1;
	
	/** Values to be returned. */
	public static final int FOUND = 0;
	public static final int ABOVE = 1;
	public static final int M1    = 2;
	public static final int M2    = 3;
	public static final int BELOW = 4;
	
	/** 
	 * Special return value when the target is confirmed NOT to be present.
	 * 
	 * This can happen in many different ways. For example, if the 3x3
	 * area being probed contains the following:
	 * 
	 *     12 19 55
	 *     76 77 80
	 *     83 87 98
	 *     
	 * And a request is to probe (centered on the 77 value) for 40, this probe
	 * can return NOT_PRESENT because the values are known to be in ascending 
	 * order, and there is a gap between 19 and 55. In fact, probing this location
	 * for any value in [20,54] and [13,18] and so on would result in NOT_PRESENT.
	 * 
	 * This can only be returned when there are at least TWO valid columns present
	 * in the 3x3 range.
	 */
	public static final int NOT_PRESENT = 5;
	
	/**
	 * Storage for FuzzySquare. The values in this NxN array are in ascending
	 * order by row, and then by column.
	 */
	int A[][];
	
	/** Can always find N. */
	public final int N;
	
	/** Use this generator for repeatable executions. */
	Random rand = new Random();
	
	/** Each probe attempt is accumulated here. */
	private long numProbes;

	/** 
	 * Constructs a two-dimensional array of N*N elements containing
	 * the values from 0 .. 10*N*N in ascending order by row, and then 
	 * by column.
	 */
	public FuzzySquare (int N) {
		this.N = N;
		initialize(N);
	}
	
	/** 
	 * Create a specific instance for testing.
	 */
	public FuzzySquare (int N, long seed) {
		this.N = N;
		rand = new Random(seed);
		initialize(N);
	}
	
	/** 
	 * Helper method to randomly place 0 .. 10*N*N into 2D array in ascending order, 
	 * based on rand. 
	 */
	private void initialize(int N) {
		numProbes = 0;
		A = new int[N][N];
		
		A[0][0] = rand.nextInt(9);
		int last = 0;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				if (r == 0 && c == 0) {
					last = A[0][0];
				} else {
					A[r][c] = last + 1 + rand.nextInt(8);
					last = A[r][c];
				}
			}
		}
	}

	/** Return the number of probes made. */
	public int getNumProbes() {
		return (int) numProbes;
	}
	
	/** Return the number of probes made. Used to show larger problems and remain backwards compatible. */
	public long getLongNumProbes() {
		return numProbes;
	}
	
	/** Helper method to determine if row is valid. */
	public boolean validRow(int r) {
		return r >= 0 && r < A.length;
	}

	/** Helper method to determine if column is valid. */
	public boolean validColumn(int c) {
		return c >= 0 && c < A.length;
	}
	
	/**
	 * Determines whether target value is found within the 3x3 region
	 * centered at (r, c). Note that index positions "outside the square"
	 * cause no problems.
	 * 
	 * Target must be within range 0 .. 10*N*N otherwise NOT_PRESENT is returned
	 * 
	 * If entire 3x3 is outside the range, OUT_OF_BOUNDS is returned.
	 * 
	 * If the target is found within the 3x3 region, then a Found object is returned.
	 * 
	 * if the target is NOT found, then a NotFound object is returned, which records
	 * the total number of values in the region that were smaller than the target,
	 * and the total number of valid cells that were inspected in the 3x3 region.
	 * 
	 * Every probe attempt increases the number of probe attempts.
	 */
	public int probe3x3(int r, int c, int target) {
		numProbes += 1;
		int N = A.length;

		// Must be a valid target, in a square contained in FuzzySquare.
		if (target < 0 || target > 10*N*N) {
			return NOT_PRESENT;
		}
		if (r <= -2 || r >= N+1 || c <= -2 || c >= N+1) {
			return OUT_OF_BOUNDS;
		}

		// Determine the subset that is wholly contained [b_r : e_r][b_c : e_c].
		int b_r = r-1;
		if (!validRow(b_r)) {
			for (int dr = -2; dr <= 2; dr++) {
				if (validRow(r+dr)) {
					b_r = r+dr;
					break;
				}
			}
		}
		int e_r = r+1;
		for (int dr = r+1; dr >= r-1; dr--) {
			if (validRow(dr)) {
				e_r = dr;
				break;
			}
		}
		int b_c = c;
		for (int dc = -1; dc <= 1; dc++) {
			if (validColumn(c+dc)) {
				b_c = c+dc;
				break;
			}
		}
		int e_c = c;
		for (int dc = c+1; dc >= c-1; dc--) {
			if (validColumn(dc)) {
				e_c = dc;
				break;
			}
		}
		
		// know rows are valid from [b_r .. e_r] and cols from [b_c .. e_c]. 
		if (target < A[b_r][b_c]) { 
			if (b_r == r-1) { return ABOVE; }
			if (b_r == r) { return M1; }
			if (b_r == r+1) { return M2; }
		}
		if (target > A[e_r][e_c]) { 
			if (e_r == r+1) { return BELOW; }
			if (e_r == r) { return M2; }
			if (e_r == r-1) { return M1; }
		}
		
		// check if contained!
		for (int _r = b_r; _r <= e_r; _r++) {
			for (int _c = b_c; _c <= e_c; _c++) {
				if (A[_r][_c] == target) { return FOUND; }
			}
		}
		
		// Find two neighboring cells IN TRAVERSAL ORDER such that ci < target < ci+1
		Coordinate last = null;
		int _r = r-1;
		while (_r <= r+1) {
			int _c = c-1;
			while (_c <= c+1) {
				if (validRow(_r) && validColumn(_c)) {
					if (last == null) {
						last = new Coordinate(_r, _c);
					} else {
						Coordinate next = new Coordinate(_r, _c);
						if (A[last.row][last.column] < target && target < A[next.row][next.column]) {
							if (last.row != next.row) {
								if (last.row == r-1) { return M1; }
								if (last.row == r) { return M2; }
							}
						}
						last = next;
					}
				}
				_c++;
			}
			_r++;
		}
		
		// if you get here, it MUST BE because we have enough information
		// to know that the value IS NOT present. This likely means we 
		// have two consecutive row values within the region, and the 
		// target could have been contained within, but it was not.
		return NOT_PRESENT;
	}
	
	/**
	 * Take a solver and validates it can find all N*N targets, returning the
	 * total number of probes required.
	 * 
	 * This will issue a total of 10*N*N find() requests.
	 * 
	 * If any value is NOT found, that is returned as an exception. If a value
	 * is mistakenly considered to be found, that is returned as an exception.
	 */
	public long solver(IFuzzySquareFinder fsf) {
		long originalProbes = getNumProbes();
		int out = 0;
		for (int i = 0; i < 10*A.length*A.length; i++) {
			if (i > A[A.length-1][A.length-1]) {
				out++;
			}
			Coordinate coord = fsf.find(this, i);
			if (coord == null) {
				for (int r = 0; r < A.length; r++) {
					for (int c = 0; c < A.length; c++) {
						if (A[r][c] == i) { 
							throw new RuntimeException("Unable to find " + i + " in FuzzySquare even though it is there.");
						}
					}
				}
				continue;
			}
			
			if (coord.row < 0 || coord.row >= A.length || coord.column < 0 || coord.column >= A.length) {
				throw new RuntimeException("Returned coordinate for " + i + " is invalid:" + coord);
			}

			if (A[coord.row][coord.column] != i) {
				throw new RuntimeException("FuzzySquare[" + coord.row + "][" + coord.column + "] does not contain " + i);
			}
		}
		//System.out.println("OUT\t" +A.length + "\t" + out);
		return getNumProbes() - originalProbes;
	}
	
//	/**
//	 * Take a solver and validates it can find all N*N targets, returning the
//	 * total number of probes required.
//	 * 
//	 * This will issue a total of 10*N*N find() requests.
//	 * 
//	 * If any value is NOT found, that is returned as an exception. If a value
//	 * is mistakenly considered to be found, that is returned as an exception.
//	 */
//	public int best_solver(IFuzzySquareFinder fsf) {
//		int originalProbes = getNumProbes();
//				
//		for (int i = 0; i < 10*A.length*A.length; i++) {
//			Coordinate coord = fsf.find(this, A[0][0]);
//			if (coord == null) {
//				for (int r = 0; r < A.length; r++) {
//					for (int c = 0; c < A.length; c++) {
//						if (A[r][c] == i) { 
//							throw new RuntimeException("Unable to find " + i + " in FuzzySquare even though it is there.");
//						}
//					}
//				}
//				continue;
//			}
//		}
//		
//		return getNumProbes() - originalProbes;
//	}
	
//	/**
//	 * Take a solver and validates it can find all N*N targets, returning the
//	 * total number of probes required.
//	 * 
//	 * This will issue a total of 10*N*N find() requests.
//	 * 
//	 * If any value is NOT found, that is returned as an exception. If a value
//	 * is mistakenly considered to be found, that is returned as an exception.
//	 */
//	public int worst_solver(IFuzzySquareFinder fsf) {
//		int originalProbes = getNumProbes();
//				
//		for (int i = 0; i < 10*A.length*A.length; i++) {
//			Coordinate coord = fsf.find(this, i+100000);
//			if (coord == null) {
//				for (int r = 0; r < A.length; r++) {
//					for (int c = 0; c < A.length; c++) {
//						if (A[r][c] == i+100000) { 
//							throw new RuntimeException("Unable to find " + i + " in FuzzySquare even though it is there.");
//						}
//					}
//				}
//				continue;
//			}
//			
//			if (coord.row < 0 || coord.row >= A.length || coord.column < 0 || coord.column >= A.length) {
//				throw new RuntimeException("Returned coordinate for " + i + " is invalid:" + coord);
//			}
//
//			if (A[coord.row][coord.column] != i) {
//				throw new RuntimeException("FuzzySquare[" + coord.row + "][" + coord.column + "] does not contain " + i);
//			}
//		}
//		
//		return getNumProbes() - originalProbes;
//	}
	
	/** Generate string representation, but then reset to new configuration. */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < A.length; r++) {
			sb.append(Arrays.toString(A[r])).append("\n");
		}
		initialize(A.length);
		return sb.toString();
	}
}
