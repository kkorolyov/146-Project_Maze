package sjsu.cs146.project3;

import java.util.Map;

/**
 * Provides miscellaneous helper methods for {@link Maze}.
 */
public class Misc {
	
	/**
	 * Expands a {@code char} to fill a specified length.
	 * @param source character to expand
	 * @param length length to expand to
	 * @return expanded character
	 */
	public static String expand(char source, int length) {	// Expands a single character
		String expanded = "";
		for (int i = 0; i < length; i++)
			expanded += source;
		return expanded;
	}
	/**
	 * Expands an {@code int} by adding filler spaces around it
	 * @param source source value
	 * @param length length to expand to
	 * @return source value with appropriate amount of whitespace surrounding it
	 */
	public static String expand(int source, int length) {	// Adds filler on either side of a number
		String expanded = String.valueOf(source);
		boolean addToEnd = true;
		if (expanded.length() < length) {
			for (int i = expanded.length(); i < length; i++) {
				if (addToEnd)	// Add filler to end
					expanded += " ";	// Fill with spaces
				else	// Add filler to beginning
					expanded = " " + expanded;
				addToEnd = !addToEnd;	// Alternate adding to end and start
			}
		}
		return expanded;
	}
	
	/**
	 * @param values map of values to located maximum value of
	 * @return maximum value of map
	 */
	public static int max(Map<Integer, Integer> values) {	// Calculates max value in map
		int max = 0;
		for (int key : values.keySet()) {
			int currentValue;
			if ((currentValue = values.get(key)) > max)
				max = currentValue;
		}
		return max;
	}
}
