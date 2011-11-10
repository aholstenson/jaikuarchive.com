package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import org.joda.time.DateTime;

import se.l4.jaiku.model.UserStream;
import se.l4.jaiku.model.UserStreamEntry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JaikuUserStream
{
	private final String user;
	private final Gson gson;

	public JaikuUserStream(Gson gson, String user)
	{
		this.gson = gson;
		this.user = user;
	}
	
	public void fetch(OutputStream out)
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
		
		OutputStreamWriter writer = new OutputStreamWriter(out);
		gson.toJson(stream, writer);
		writer.flush();
		writer.close();
	}
	
	public UserStream fetch(UserStream output, long offset)
		throws IOException
	{
		System.out.println("Fetching with " + offset);
		String uri = "http://" + user + ".jaiku.com/json" + (offset != 0 ? "?offset=" + offset + ".0" : "");
		URL url = new URL(uri);
		
		InputStream stream = url.openStream();
		
		try
		{
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
			
			return output;
		}
		finally
		{
			stream.close();
		}
	}
}
