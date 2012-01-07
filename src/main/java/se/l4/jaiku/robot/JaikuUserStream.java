package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.joda.time.DateTime;

import se.l4.jaiku.model.UserStream;
import se.l4.jaiku.model.UserStreamEntry;
import se.l4.jaiku.storage.FetchQueue;
import se.l4.jaiku.storage.UserWithId;

import com.google.common.io.Closeables;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JaikuUserStream
{
	private final String user;
	private final Gson gson;
	private final FetchQueue queue; 

	public JaikuUserStream(Gson gson, String user, FetchQueue queue)
	{
		this.gson = gson;
		this.user = user;
		this.queue = queue;
	}
	
	public UserStream fetch()
		throws IOException
	{
		// Fetch the first item
		UserStream stream = fetch(null, 0);
		
		if(! stream.getStream().isEmpty())
		{
			while(true)
			{
				UserStreamEntry last = stream.getStream().get(stream.getStream().size() - 1);
				DateTime dt = last.getCreatedAtDate();
				
				int count = stream.getStream().size();
				fetch(stream, dt.getMillis()/1000-1);
				
				if(stream.getStream().size() == count)
				{
					break;
				}
			}
		}
		
		return stream;
	}
	
	public UserStream fetch(UserStream output, long offset)
		throws IOException
	{
		return fetch(output, offset, 0);
	}
	
	public UserStream fetch(UserStream output, long offset, int count)
		throws IOException
	{
		String uri = "http://" + user + ".jaiku.com/json" + (offset != 0 ? "?offset=" + offset + ".0" : "");
		URL url = new URL(uri);
		System.out.println(uri);
		
		InputStream stream = null;
		try
		{
			stream = url.openStream();
			
			InputStreamReader reader = new InputStreamReader(stream);
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			
			UserStream userStream = gson.fromJson(jsonReader, UserStream.class);
			
			if(output == null)
			{
				output = userStream;
			}
			else
			{
				output.getStream().addAll(userStream.getStream());
			}
			
			if(queue != null)
			{
				for(UserStreamEntry se : userStream.getStream())
				{
					if(se.getCommentId() == null)
					{
						queue.queuePresence(user, se.getId());
					}
					else
					{
						UserWithId id = TextUpdater.getPresenceFromUrl(se.getUrl());
						if(id != null)
						{
							queue.queuePresence(id.getUsername(), id.getId());
						}
					}
				}
			}
			
			return output;
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch(IOException e)
		{
			if(e.getMessage().contains("500 for URL") && count < 4)
			{
				// Fetch again
				try
				{
					Thread.sleep(2000);
				}
				catch(InterruptedException e1)
				{
				}
				
				return fetch(output, offset, count + 1);
			}
			else
			{
				throw e;
			}
		}
		finally
		{
			Closeables.closeQuietly(stream);
		}
	}
}
