package se.l4.jaiku.web.pages;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.model.User;
import se.l4.jaiku.model.UserStream;
import se.l4.jaiku.model.UserStreamEntry;
import se.l4.jaiku.robot.JaikuUserStream;
import se.l4.jaiku.robot.TextUpdater;
import se.l4.jaiku.storage.Storage;

import com.google.gson.Gson;
import com.google.inject.Inject;

@Path("/")
@Template
public class UserPage
{
	private final Storage storage;
	private User user;

	@Inject
	public UserPage(Storage storage)
	{
		this.storage = storage;
	}
	
	@GET
	@Path("goodbye")
	public Object goodbye(@HeaderParam("Host") String host)
		throws IOException
	{
		int idx = host.indexOf('.');
		if(idx == -1)
		{
			// No dots, use index page
			return Response.status(404).build();
		}
		
		String username = host.substring(0, idx);
		if(username.equals("www") || username.equals(JaikuConstants.ARCHIVE_URL_NO_COM))
		{
			return Response.status(404).build();
		}
		
		user = storage.getUser(username);
		
		if(user == null)
		{
			return Response.status(404).build();
		}
		
		UserStream stream = new JaikuUserStream(new Gson(), username)
			.fetch(null, 0);
		
		for(UserStreamEntry entry : stream.getStream())
		{
			if(entry.getCommentId() == null && entry.getTitle().contains("#goodbye"))
			{
				// Found the message
				user.setGoodbye(TextUpdater.hyperlink(entry.getTitle()));
				storage.saveUser(user);
				break;
			}
		}
		
		return Response.seeOther(URI.create("/")).build();
	}
	
	@GET
	public Object get(@HeaderParam("Host") String host)
		throws IOException
	{
		int idx = host.indexOf('.');
		if(idx == -1)
		{
			// No dots, use index page
			return new IndexPage();
		}
		
		String username = host.substring(0, idx);
		if(username.equals("www") || username.equals(JaikuConstants.ARCHIVE_URL_NO_COM))
		{
			return new IndexPage();
		}
		
		user = storage.getUser(username);
		
		if(user == null)
		{
			return Response.status(404).build();
		}
		
		return this;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String avatar(User user)
	{
		return AvatarsPage.avatar(user);
	}
}
