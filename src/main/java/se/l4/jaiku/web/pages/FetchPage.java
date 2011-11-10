package se.l4.jaiku.web.pages;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import se.l4.jaiku.model.Presence;
import se.l4.jaiku.robot.JaikuPresenceFetcher;
import se.l4.jaiku.storage.Storage;

import com.google.gson.Gson;
import com.google.inject.Inject;

@Path("/")
public class FetchPage
{
	private final Storage storage;

	@Inject
	public FetchPage(Storage storage)
	{
		this.storage = storage;
	}
	
	@Path("/presence/{id}")
	@GET
	public Object presence(@HeaderParam("Host") String host, @PathParam("id") String id)
		throws IOException
	{
		int idx = host.indexOf('.');
		String username = host.substring(0, idx);
		
		Presence presence = storage.getPresence(username, id);
		if(presence == null)
		{
			try
			{
				presence = new JaikuPresenceFetcher(new Gson(), username, id)
					.fetch();
				
				storage.savePresence(presence);
			}
			catch(IOException e)
			{
				// TODO: Request to Jaiku failed, what should we do?
			}
		}
		
		if(presence == null)
		{
			return Response.status(404).build();
		}
		
		return new PresencePage(presence);
	}
}
