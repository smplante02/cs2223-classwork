package algs.hw1;  // DO NOT COPY THIS CLASS INTO YOUR PROJECT AREA. USE AS IS

import java.util.Objects;

/**
 * Class that represents a location in a two-dimensional array, by its
 * row and column values.
 */
public class Coordinate {
	/** Desired row. */
	public final int row;
	
	/** Desired column. */
	public final int column;
	
	public Coordinate(int r, int c) {
		this.row = r;
		this.column = c;
	}
	
	/** If you override equals, you should also have hashCode properly set (nice catch Aidan). */
	public int hashCode() {
	  return Objects.hash(row, column);
	}
	
	/** Reasonable equals() method. */
	public boolean equals(Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Coordinate) {
			Coordinate other = (Coordinate) o;
			return other.row == row && other.column == column;
		}
		
		return false;
	}
	
	public String toString() { return "(" + row + "," + column + ")"; }
}
