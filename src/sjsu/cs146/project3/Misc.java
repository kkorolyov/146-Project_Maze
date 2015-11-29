package sjsu.cs146.project3;

import java.util.Map;

/**
 * Provides miscellaneous helper methods for {@link Maze}.
 */
public class Misc {
	
	/**
	 * Repeats a {@code char} to fill a specified length.
	 * @param source character to expand
	 * @param length length to expand to
	 * @return expanded character
	 */
	public static String repeat(char source, int length) {
		String expanded = "";
		for (int i = 0; i < length; i++)
			expanded += source;
		return expanded;
	}
	
	/**
	 * Centers a String by adding filler spaces around it
	 * @param source source String
	 * @param length length to expand to
	 * @return source value with appropriate amount of whitespace surrounding it
	 */
	public static String center(String source, int length) {
		String expanded = source;
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
	 * @param source source String
	 * @return last character of source String
	 */
	public static String chop(String source) {
		return source.substring(source.length() - 1);
	}
	
	/**
	 * @param values map of keys and their values
	 * @return maximum value
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
