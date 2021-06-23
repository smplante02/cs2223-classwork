package algs.hw1; // DO NOT COPY THIS CLASS INTO YOUR PROJECT AREA. USE AS IS

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import algs.hw1.api.IManhattanSquareFinder;

/**
 * Creates an NxN two-dimensional array, A, containing the values from 
 * 0 to N*N-1 in some random order. 
 * 
 * You can inspect any (r,c) location to check if it contains a target value.
 * if the value is there, 0 is returned; otherwise the Manhattan distance
 * to the items location is returned. Is the target is not contained within
 * the ManhattanSquare object, then -1 is returned.
 * 
 * The  Manhattan distance to its actual location in A, where you can move 
 * only horizontally or vertically (not diagonally).
 * 
 * DO NOT MODIFY THIS FILE OR COPY IT INTO YOUR PROJECT AREA
 */
public class ManhattanSquare {

	/**
	 * Storage for ManhattanSquare. The values in this NxN array are in ascending
	 * order by row, and then by column.
	 */
	int A[][];
	
	/** Size of the problem instance. */
	public final int N;
	
	/** Use this generator for repeatable executions. */
	Random rand = new Random();
	
	/** Each probe attempt is accumulated here. */
	private int numProbes;
	
	/** 
	 * Constructs a two-dimensional array of N*N elements containing
	 * the values from 0 .. N*N-1 in random locations.
	 */
	public ManhattanSquare (int N) {
		this.N = N;
		initialize(N);
	}
	
	/** 
	 * Create a specific instance for testing.
	 */
	public ManhattanSquare (int N, long seed) {
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
	 * Returns distance to target from (r,c). If target is contained in A[r][c]
	 * then 0 is returned, otherwise a positive integer reflecting the 
	 * Manhattan distance to its actual location in A, where you can move 
	 * only horizontally or vertically (not diagonally).
	 * 
	 * If target does not exist in A, then return -1;
	 */
	public int distance(int r, int c, int target) {
		numProbes++;
		if (A[r][c] == target) { return 0; }
		
		for (int rt = 0; rt < A.length; rt++) {
			for (int ct=  0; ct < A.length; ct++) {
				if (A[rt][ct] == target) { return Math.abs(r-rt) + Math.abs(c-ct); }
			}
		}
		
		return -1;
	}
		
	/**
	 * Take a solver and validates it can find all N*N targets, returning the
	 * total number of probes required.
	 * 
	 * This will issue a total of N*N find() requests.
	 * 
	 * If any value is NOT found, that is returned as an exception.
	 */
	public int solver(IManhattanSquareFinder ms) {
		int originalProbes = getNumProbes();
				
		for (int i = 0; i < A.length*A.length; i++) {
			Coordinate coord = ms.find(this, i);
			if (coord == null) {
				throw new RuntimeException("Unable to find " + i + " in ManhattanSquare.");
			}
			if (coord.row < 0 || coord.row >= A.length || coord.column < 0 || coord.column >= A.length) {
				throw new RuntimeException("Returned coordinate for " + i + " is invalid:" + coord);
			}
			if (A[coord.row][coord.column]!= i) {
				throw new RuntimeException("ManhattanSquare[" + coord.row + "][" + coord.column + "] does not contain " + i);
			}
		}
		
		return getNumProbes() - originalProbes;
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
}
