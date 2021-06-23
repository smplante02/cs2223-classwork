package algs.hw1; // DO NOT COPY THIS CLASS INTO YOUR PROJECT AREA. USE AS IS

import java.util.Arrays;
import java.util.Random;

import algs.hw1.api.IHeisenbergFinder;

/**
 * Heisenberg(N) contains an array of integer values in ascending order, ranging  
 * from 0 (inclusively) to 10*N (inclusively).
 * 
 *     "What we observe is not nature itself, but nature exposed to our method of questioning."
 *     Werner Heisenberg
 * 
 * Whenever you inspect an element, A[i], you increment all values from A[i+1 .. N-1]
 * and decrement all values from A[0 .. i-1]. if A[i] is ODD then it increases by one,
 * otherwise it decreases by one.
 * 
 * For example, if A is the following:
 * 
 *      0   1   2   3
 *     [4, 10, 17, 24]
 *     
 *     
 * Then inspect(1) will return the value 17, but the resulting array will be:
 * 
 *     [3,  9, 18, 25]     A[1] is decremented
 * 
 * As you can see, the resulting perturbed array still contains values in 
 * ascending order.
 * 
 * If you now call inspect(0), the returned value is 3, but you know this means that
 * the original value had been 4, so you use this to assert that 4 had originally been
 * present. Since 3 is odd, then A[0] is incremented.
 * 
 * Note after this inspection, the perturbed array will either be:
 * 
 *     [4,  10, 19, 26]     A[0] is incremented
 * 
 * Armed with this information, you can still determine whether the original
 * array as instantiated had originally contained a specific value in time 
 * proportional to log(N).
 * 
 * DO NOT MODIFY THIS FILE OR COPY IT INTO YOUR PROJECT AREA
 */
public class Heisenberg {
	/**
	 * Storage for Heisenberg. The values in this array are in ascending order.
	 */
	int A[];
	
	/** Problem instance size. */
	public final int N;
	
	/** Use this generator for repeatable executions. */
	Random rand = new Random();
	
	/** Each probe attempt is accumulated here. */
	private int numProbes;
	
	/** 
	 * Create a specific instance for testing.
	 */
	public Heisenberg(int N, long seed) {
		this.N = N;
		rand = new Random(seed);
		initialize(N);
	}
	
	/** Return the number of probes made. */
	public int getNumProbes() {
		return numProbes;
	}
	
	/** 
	 * Constructs an array of N elements in sorted order. All values are guaranteed
	 * to be in the range 0 .. 10*N
	 */
	public Heisenberg(int N) {
		this.N = N;
		initialize(N);
	}
	
	/** Helper method to construct A once random generator is set. */
	private void initialize(int N) {
		numProbes = 0;
		A = new int[N];
		
		A[0] = rand.nextInt(9);
		for (int i = 1; i < N; i++) {
			A[i] = A[i-1] + 1 + rand.nextInt(9);
		}
	}
	
	/** Helper method to perturb values up/down in given range. */
	private void perturb(int lo, int hi, int delta) {
		for (int i = lo; i <= hi; i++) {
			A[i] += delta;
		}
	}
	
	/**
	 * Return value at A[idx].
	 * 
	 * Every inspection has the following effect:
	 * 
	 *   + A[idx] is increased by 1 if ODD, decreased by 1 if EVEN.
	 *   + A[0 .. idx-1] are reduced by 1
	 *   + A[idx+1 .. N-1] are increased by 1
	 *   
	 * Increases the number of probes.
	 */
	public int inspect(int idx) { 
		numProbes += 1;
		int reading = A[idx];
		if (reading % 2 == 0) {
			A[idx] -= 1;
		} else {
			A[idx] += 1;
		}
		
		perturb(0, idx-1, -1);
		perturb(idx+1, A.length-1, +1);
		return reading;
	}
	
	/** All values are reinitialized after global snapshot taken. */
	public String toString() {
		String str = Arrays.toString(A);
		initialize(A.length);
		return str;
	}
	
	/**
	 * Take a solver and validates it successfully determines whether v is a member
	 * of the Heisenberg object, for v in the range 0 to 10*N inclusively.
	 * 
	 * This means there will be 10*N + 1 search() requests.
	 * 
	 * If a value SHOULD have been found and wasn't an exception is thrown.
	 * 
	 * If a value WAS found and shouldn't have, then an exception is thrown.
	 * 
	 * All solutions are generated against a private copy because, as you see,
	 * inspecting an Heisenberg object through its interface perturbs it in some way.
	 */
	public int solver(IHeisenbergFinder h) {
		int numProbes = 0;
				
		for (int i = 0; i <= 10*A.length; i++) {
			// make a safe copy
			Heisenberg copy = new Heisenberg(A.length);
			for (int j = 0; j < A.length; j++) { copy.A[j] = A[j]; }
			
			int idx = h.find(copy, i);
			if (idx >= 0) {
				if (A[idx] != i) {
					throw new RuntimeException("Heisenberg finder said it found " + i + " but it was not originally present.");
				}
			} else {
				boolean found = false;
				for (int j = 0; j < A.length; j++) { if (A[j] == i) { found = true; break; }}
				if (found) {
					throw new RuntimeException("Heisenberg finder said it didn't found " + i + " but it was originally present.");
				}
			}
			
			// accumulate probe count
			numProbes += copy.getNumProbes();
		}
		
		return numProbes;
	}
}
