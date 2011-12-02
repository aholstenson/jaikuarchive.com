package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.joda.time.DateTime;

import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.ChannelStreamEntry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Fetcher for channels.
 * 
 * @author Andreas Holstenson
 *
 */
public class JaikuChannelFetcher
{
	public interface Callback
	{
		void save(ChannelStream stream, int number);
	}
	
	private final String channel;
	private final Gson gson;
	private final Callback callback;

	public JaikuChannelFetcher(Gson gson, String channel, Callback callback)
	{
		this.gson = gson;
		this.channel = channel;
		this.callback = callback;
	}
	
	public void fetch()
		throws IOException
	{
		// Fetch the first item
		int number = 1;
		ChannelStream stream = fetch(null, 0);
		
		if(! stream.getStream().isEmpty())
		{
			int pagesFetched = 1;
			while(true)
			{
				ChannelStreamEntry last = stream.getStream().get(stream.getStream().size() - 1);
				DateTime dt = last.getCreatedAtDate();
				
				int count;
				ChannelStream temp = null;
				if(pagesFetched++ % 5 == 0)
				{
					// Fetched the fifth page, save as one
					temp = stream;
					stream = null;
					count = 0;
				}
				else
				{
					count = stream.getStream().size();
				}
				
				stream = fetch(stream, dt.getMillis()/1000-1);
				
				if(stream.getStream().size() == count)
				{
					if(temp != null)
					{
						temp.setMorePages(false);
						stream = temp;
					}
					
					break;
				}
				else if(temp != null)
				{
					temp.setMorePages(true);
					callback.save(temp, number++);
				}
			}
		}
		
		// Save the last segment
		callback.save(stream, number);
	}
	
	public ChannelStream fetch(ChannelStream output, long offset)
		throws IOException
	{
		String uri = "http://jaiku.com/channel/" + channel + "/json" + (offset != 0 ? "?offset=" + offset + ".0" : "");
		URL url = new URL(uri);
		System.out.println("Fetching " + uri);
		
		InputStream stream = url.openStream();
		
		try
		{
			InputStreamReader reader = new InputStreamReader(stream);
			JsonReader jsonReader = new JsonReader(reader);
			jsonReader.setLenient(true);
			
			ChannelStream channelStream = gson.fromJson(jsonReader, ChannelStream.class);
			
			if(output == null)
			{
				output = channelStream;
			}
			else
			{
				output.getStream().addAll(channelStream.getStream());
			}
			
			return output;
		}
		finally
		{
			stream.close();
		}
	}
}
