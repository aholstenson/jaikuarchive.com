package se.l4.jaiku.web.pages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.joda.time.DateTime;

import se.l4.dust.jaxrs.CacheResponses;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.model.Channel;
import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
@Path("/avatar/{username}")
public class AvatarsPage
{
	private static final DateTime fakeTime = new DateTime().minusMonths(1);
	
	private final Storage storage;

	@Inject
	public AvatarsPage(Storage storage)
	{
		this.storage = storage;
	}
	
	@GET
	public Response getAvatar(@PathParam("username") String username)
		throws IOException
	{
		final InputStream stream = storage.getAvatar(username);
		if(stream == null)
		{
			return Response.status(404).build();
		}
		
		return CacheResponses.longTermCacheResponse(fakeTime.toDate())
			.entity(new StreamingOutput()
			{
				@Override
				public void write(OutputStream output)
					throws IOException, WebApplicationException
				{
					try
					{
						ByteStreams.copy(stream, output);
					}
					finally
					{
						Closeables.closeQuietly(stream);
					}
				}
			})
			.type("image/jpeg")
			.build();
	}

	public static String avatar(User user)
	{
		return "http://" + JaikuConstants.ARCHIVE_URL + "/avatar/" + user.getNick();
	}
	
	public static String avatar(Channel channel)
	{
		return "http://" + JaikuConstants.ARCHIVE_URL + "/avatar/" + channel.getNick().replace("#", "%23");
	}
}
