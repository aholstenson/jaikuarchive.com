package se.l4.jaiku.web.pages;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

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
	public Object get(@HeaderParam("Host") String host)
		throws IOException
	{
		int idx = host.indexOf('.');
		String username = host.substring(0, idx);
		
		if(username.equals("www") || username.equals(JaikuConstants.ARCHIVE_URL_NO_COM))
		{
			// TODO: Main site
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
