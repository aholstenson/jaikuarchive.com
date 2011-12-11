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
	
	private static final Pattern HTTP_PATTERN = Pattern.compile(
		"(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?"
	);
	
	private TextUpdater()
	{
	}
	
	public static String hyperlink(String input)
	{
		Matcher matcher = HTTP_PATTERN.matcher(input);
		
		StringBuilder builder = new StringBuilder();
		int start = 0;
		while(matcher.find())
		{
			int end = matcher.start();
			for(int i=start; i<end; i++)
			{
				escape(builder, input.charAt(i));
			}
			
			builder.append("<a href=\"");
			String href = matcher.group(0);
			escape(builder, href);
			builder.append("\" rel=\"nofollow\">");
			escape(builder, href);
			builder.append("</a>");
			
			start = matcher.end();
		}
		
		for(int i=start, n=input.length(); i<n; i++)
		{
			escape(builder, input.charAt(i));
		}
		
		return builder.toString();
	}
	
	private static void escape(StringBuilder builder, String s)
	{
		for(int i=0, n=s.length(); i<n; i++) escape(builder, s.charAt(i));
	}

	private static void escape(StringBuilder builder, char c)
	{
		// TODO: Escaping of named characters
		switch(c)
		{
			case '<':
				builder.append("&lt;");
				break;
			case '>':
				builder.append("&gt;");
				break;
			case '&':
				builder.append("&amp;");
				break;
			default:
				if(c > 0x7F)
				{
					escapeForce(builder, c);
				}
				else
				{
					builder.append(c);
				}
		}
	}

	private static void escapeForce(StringBuilder builder, char c)
	{
		builder.append("&#");
		builder.append(Integer.toString(c, 10));
		builder.append(';');
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
