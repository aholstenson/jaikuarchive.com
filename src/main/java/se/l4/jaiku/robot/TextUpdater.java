package se.l4.jaiku.robot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.l4.jaiku.JaikuConstants;

/**
 * Textual updater, used to update any Jaiku links with archive based links.
 * 
 * @author Andreas Holstenson
 *
 */
public class TextUpdater
{
	private static final Pattern LINK_PATTERN =
		Pattern.compile("http://(.+?)\\.(" + JaikuConstants.ORIGINAL_URL + ")");
		
	private TextUpdater()
	{
	}
	
	public static String updateLinks(String input)
	{
		Matcher matcher = LINK_PATTERN.matcher(input);
		return matcher.replaceAll("http://$1." + JaikuConstants.ARCHIVE_URL);
	}
	
}
