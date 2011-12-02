package se.l4.jaiku.web.pages;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.jaiku.model.Presence;
import se.l4.jaiku.storage.Storage;

import com.google.gson.JsonParseException;
import com.google.inject.Inject;

@Path("/channel/{channel}/presence/{id}")
public class ChannelPresencePage
{
	private static final Logger logger = LoggerFactory.getLogger(ChannelPresencePage.class);
	
	private final Storage storage;

	@Inject
	public ChannelPresencePage(Storage storage)
	{
		this.storage = storage;
	}
	
	@GET
	public Object channel(@PathParam("channel") String channel, @PathParam("id") String id)
		throws IOException
	{
		try
		{
			Presence presence = storage.getChannelPresence(channel, id);
			if(presence == null)
			{
				return Response.status(404).build();
			}
			
			return new PresencePage(storage, presence);
		}
		catch(JsonParseException e)
		{
			logger.error("Unable to parse JSON; " + e.getMessage(), e);
//			jsonFailure = true;
			
			return this;
		}
		catch(Exception e)
		{
			logger.error("Unable to parse JSON; " + e.getMessage(), e);
//			failure = true;
			
			return this;
		}
	}
}
