package algs.hw1; // DO NOT COPY THIS CLASS INTO YOUR PROJECT AREA. USE AS IS

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import algs.hw1.api.ISlicerFinder;

/**
 * Creates an NxN two-dimensional array, A, containing the values from
 * 0 to N*N-1 in some random arrangement.
 * 
 * Consider the following 5x5 Slicer square 
 *    
 *        C0   C1   C2   C3   C4
 * 
 * R0     4,  10,   6,  17,  22 
 * R1    20,   5,  24,  18,  14 
 * R2     9,  11,  16,   3,  21 
 * R3     0,  19,   8,   1,  23 
 * R4    15,  13,   7,   2,  12      
 *
 * inLeft(2, 5) is TRUE because 15 is contained in either column C0, C1 or C2.
 * 
 * inLeft(2, 22) is FALSE because it is not found there (that value is in column C4).
 * 
 * inTop(1, 17) is TRUE because 17 is in either row R0 or R1.
 * 
 * inTop(1, 9) is FALSE because it is not found there (that value is in row R2).
 * 
 * DO NOT MODIFY THIS FILE OR COPY IT INTO YOUR PROJECT AREA
 */
public class Slicer {

	/**
	 * Storage for Slicer. The values from 0 .. N*N-1 appear in 
	 * some order in this 2d array.
	 */
	int A[][];
	
	/** Use this generator for repeatable executions. */
	Random rand = new Random();
	
	/** Each probe attempt is accumulated here. */
	private int numProbes;
	
	/** Size of problem. */
	public final int N;
	
	/** 
	 * Constructs a two-dimensional array of N*N elements containing
	 * the values from 0 .. N*N-1 in random locations.
	 */
	public Slicer (int N) {
		this.N = N;
		initialize(N);
	}
	
	/** 
	 * Create a specific instance for testing.
	 */
	public Slicer (int N, long seed) {
		this.N = N;
		rand = new Random(seed);
		initialize(N);
	}
	
	/** Helper method to randomly place 0 .. N*N-1 into 2D array, based on rand. */
	private void initialize(int N) {
		numProbes = 0;
		A = new int[N][N];
		
		java.util.List<Integer> T = new java.util.ArrayList<>();
		for (int i = 0; i < N*N; i++) {
			T.add(i);
		}
		Collections.shuffle(T, rand);
		int idx = 0;
		for (int r = 0; r < N; r++) {
			for (int c = 0; c < N; c++) {
				A[r][c] = T.get(idx);
				idx++;
			}
		}
	}

	/** Return the number of probes made. */
	public int getNumProbes() {
		return numProbes;
	}
	
	/**
	 * Determines whether target value is found in a column in the
	 * left half of the array, that is, in a column ranked from 0 .. column.
	 */
	public boolean inLeft(int column, int target) {
		numProbes++;
		for (int r = 0; r < A.length; r++) {
			for (int c = 0; c <= column; c++) {
				if (A[r][c] == target) { return true; }
			}
		}
		
		return false;
	}

	/**
	 * Determines whether target value is found in a row in the
	 * top half of the array, that is, in a row ranked from 0 to row.
	 */
	public boolean inTop(int row, int target) {
		numProbes++;
		for (int r = 0; r <= row; r++) {
			for (int c = 0; c < A.length; c++) {
				if (A[r][c] == target) { return true; }
			}
		}
		
		return false;
	}

	/** Generate string representation, but then reset to new configuration. */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int r = 0; r < A.length; r++) {
			sb.append(Arrays.toString(A[r])).append("\n");
		}
		initialize(A.length);
		return sb.toString();
	}
	
	/**
	 * Take a solver and validates it can find all N*N targets, returning the
	 * total number of probes required.
	 * 
	 * This will issue a total of N*N find() requests.
	 * 
	 * If any value is NOT found, that is returned as an exception.
	 */
	public int solver(ISlicerFinder s) { 
		int originalProbes = getNumProbes();
				
		for (int i = 0; i < A.length*A.length; i++) {
			Coordinate coord = s.find(this, i);
			if (coord == null) {
				throw new RuntimeException("Unable to find " + i + " in Slicer.");
			}
			if (coord.row < 0 || coord.row >= A.length || coord.column < 0 || coord.column >= A.length) {
				throw new RuntimeException("Returned coordinate for " + i + " is invalid:" + coord);
			}
			if (A[coord.row][coord.column]!= i) {
				throw new RuntimeException("Slicer[" + coord.row + "][" + coord.column + "] does not contain " + i);
			}
		}
		
		return getNumProbes() - originalProbes;
	}
	
}
