package se.l4.jaiku.robot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.storage.UserWithId;

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
	
	private static final Pattern PRESENCE_PATTERN =
		Pattern.compile("\"http://(.+?)\\." + JaikuConstants.ARCHIVE_URL + "/presence/(.+?)\"");
	
	private TextUpdater()
	{
	}
	
	public static String updateLinks(String input)
	{
		Matcher matcher = LINK_PATTERN.matcher(input);
		return matcher.replaceAll("http://$1." + JaikuConstants.ARCHIVE_URL);
	}
	
	/**
	 * Extract presence links from the specified text.
	 * 
	 * @param text
	 * @return
	 */
	public static List<UserWithId> getPresenceLinks(String text)
	{
		List<UserWithId> result = new ArrayList<UserWithId>();
		Matcher matcher = PRESENCE_PATTERN.matcher(text);
		while(matcher.find())
		{
			String username = matcher.group(1);
			String id = matcher.group(2);
			
			result.add(new UserWithId(username, id));
		}
		
		return result;
	}
	
	public static void main(String[] args)
	{
		getPresenceLinks("<a href=\"http://jocke.jaikuarchive.com/presence/44981067\" target=\"_new\">http://jocke.jaikuarchive.com/presence/44981067</a></p>");
	}
}
