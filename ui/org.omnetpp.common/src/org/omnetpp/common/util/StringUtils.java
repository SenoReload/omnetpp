package org.omnetpp.common.util;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
	
	/**
	 * Converts a java id string (camel case and '_' used as word separators)
	 * to a display string (' ' used as word separator).
	 */
	public static String toDisplayString(String javaIdString) {
		return join(splitCamelCaseString(capitalize(javaIdString), '_').iterator(), ' ');
	}
	
	public static List<String> splitCamelCaseString(String str, char separator)
	{
		List<String> result = new ArrayList<String>();
		StringBuffer currentWord = new StringBuffer();

		int length = str.length();
		boolean lastIsLower = false;

		for (int index = 0; index < length; index++)
		{
			char curChar = str.charAt(index);
			if (Character.isUpperCase(curChar) || (!lastIsLower && Character.isDigit(curChar)) || curChar == separator)
			{
				if (lastIsLower || curChar == separator)
				{
					result.add(currentWord.toString());
					currentWord = new StringBuffer();
				}
				lastIsLower = false;
			}
			else
			{
				if (!lastIsLower)
				{
					int currentWordLength = currentWord.length();
					if (currentWordLength > 1)
					{
						char lastChar = currentWord.charAt(--currentWordLength);
						currentWord.setLength(currentWordLength);
						result.add(currentWord.toString());
						currentWord = new StringBuffer();
						currentWord.append(lastChar);
					}
				}
				lastIsLower = true;
			}
			if (curChar != separator)
			{
				currentWord.append(curChar);
			}
		}

		result.add(currentWord.toString());
		return result;
	}
}
