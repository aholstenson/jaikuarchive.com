package se.l4.jaiku.web.pages;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.TimeFormatting;
import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

import com.google.inject.Inject;

/**
 * Page that handles a Jaiku presence. This will attempt to fetch presences
 * on demand.
 * 
 * @author Andreas Holstenson
 *
 */
@Template
@Path("/presence/{id}")
public class PresencePage
{
	private final Storage storage;
	private Presence presence;
	private Comment comment;

	@Inject
	public PresencePage(Storage storage)
	{
		this.storage = storage;
	}
	
	@GET
	public Object presence(@HeaderParam("Host") String host, @PathParam("id") String id)
		throws IOException
	{
		int idx = host.indexOf('.');
		String username = host.substring(0, idx);
		
		presence = storage.getPresence(username, id);
		if(presence == null)
		{
			return Response.status(404).build();
		}
		
		return this;
	}

	public Presence getPresence()
	{
		return presence;
	}
	
	public Comment getComment()
	{
		return comment;
	}
	
	public void setComment(Comment comment)
	{
		this.comment = comment;
	}
	
	public String getRelativeTime(DateTime dt)
	{
		Interval interval = new Interval(dt.getMillis(), System.currentTimeMillis());
		Period period = interval.toPeriod();
		
		return period.toString(TimeFormatting.YEARS_AND_MONTHS);
	}
	
	public String avatar(User user)
	{
		return AvatarsPage.avatar(user);
	}
	
	public String user(User user)
	{
		return "http://" + user.getNick() + "." + JaikuConstants.ARCHIVE_URL;
	}
}
