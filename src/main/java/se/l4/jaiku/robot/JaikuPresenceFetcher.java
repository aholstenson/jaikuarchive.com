package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
	
	private final String user;
	private final String presence;

	public JaikuPresenceFetcher(Gson gson, String user, String presence)
	{
		this.gson = gson;
		this.user = user;
		this.presence = presence;
	}
	
	/**
	 * Fetch this presence and write it to the given stream.
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void fetch(OutputStream out)
		throws IOException
	{
		// Fetch the first item
		Presence presence = fetch();
		
		OutputStreamWriter writer = new OutputStreamWriter(out);
		gson.toJson(presence, writer);
		writer.flush();
		writer.close();
	}
	
	public Presence fetch()
		throws IOException
	{
		String uri = "http://" + user + ".jaiku.com/presence/" + presence + "/json";
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
