package com.mobiquityinc.util;

/**
 * @author Dewald Pieters
 *
 */
/*
 * String Utility class to keep string transformations and manipulation in once
 * place
 */

public final class StringUtil {

	// Make private constructor to avoid instantiation of class
	private StringUtil() {
	}

	// Returns string from index 0 up to the first occurrence of the specified
	// string
	public static String getStringUptoString(String inputString, String beforeString) {
		int beforeStringPosition = inputString.indexOf(beforeString);
		if (beforeStringPosition == -1) {
			return "";
		}
		return removeNonDigitCharacters(inputString.substring(0, beforeStringPosition));
	}

	// Returns string between two strings
	public static String getStringBetweenTwoStrings(String inputString, String fromString, String toString) {
		int fromStringPosition = inputString.indexOf(fromString);
		int toStringPosition = inputString.lastIndexOf(toString);
		int adjustedFromStringPosition = fromStringPosition + fromString.length();
		if (fromStringPosition == -1 || toStringPosition == -1 || adjustedFromStringPosition >= toStringPosition)
			return "";
		return removeNonDigitCharacters(inputString.substring(adjustedFromStringPosition, toStringPosition));
	}

	// Returns string after provided string up to end of string
	public static String getStringAfterString(String inputString, String fromString) {
		int fromStringPosition = inputString.lastIndexOf(fromString);
		int adjustedFromStringPosition = fromStringPosition + fromString.length();
		if (fromStringPosition == -1 || adjustedFromStringPosition >= inputString.length())
			return "";
		return removeNonDigitCharacters(inputString.substring(adjustedFromStringPosition));
	}

	// Removes any alpha numeric characters from string
	private static String removeNonDigitCharacters(String inputString) {
		return inputString.replaceAll("[^\\d.]", "");
	}

}
