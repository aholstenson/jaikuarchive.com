package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Fetcher for a users presence with comments.
 * 
 * @author Andreas Holstenson
 *
 */
public class JaikuPresenceFetcher
{
	private final Gson gson;
	
	public JaikuPresenceFetcher(Gson gson)
	{
		this.gson = gson;
	}
	
	public Presence fetchChannel(String channel, String id)
		throws IOException
	{
		return fetch("http://jaiku.com/channel/" + channel + "/presence/" + id + "/json");
	}
	
	public Presence fetch(String user, String presence)
		throws IOException
	{
		return fetch("http://" + user + ".jaiku.com/presence/" + presence + "/json");
	}
	
	public Presence fetch(String uri)
		throws IOException
	{
		URL url = new URL(uri);
		
		InputStream stream = url.openStream();
		
		try
		{
			InputStreamReader reader = new InputStreamReader(stream);
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			
			Presence presence = gson.fromJson(jsonReader, Presence.class);
			for(Comment c : presence.getComments())
			{
				if(c.getContent() != null)
				{
					c.setContent(TextUpdater.updateLinks(c.getContent()));
				}
				
				if(c.getPrettyContent() != null)
				{
					c.setPrettyContent(TextUpdater.updateLinks(c.getPrettyContent()));
				}
			}
			
			return presence;
		}
		finally
		{
			stream.close();
		}
	}
}
