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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.TimeFormatting;
import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

import com.google.gson.JsonParseException;
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
	private static final Logger logger = LoggerFactory.getLogger(PresencePage.class);
	
	private final Storage storage;
	private Presence presence;
	private Comment comment;
	private String url;

	private boolean jsonFailure;

	private boolean failure;


	@Inject
	public PresencePage(Storage storage)
	{
		this.storage = storage;
	}
	
	public PresencePage(Storage storage, Presence presence, String url)
	{
		this(storage);
		this.presence = presence;
		this.url = url;
	}
	
	@GET
	public Object presence(@HeaderParam("Host") String host, @PathParam("id") String id)
		throws IOException
	{
		int idx = host.indexOf('.');
		String username = host.substring(0, idx);
		
		try
		{
			presence = storage.getPresence(username, id);
			if(presence == null)
			{
				return Response.status(404).build();
			}
			
//			return CacheResponses.longTermCacheResponse(presence.getCreatedAtDate().toDate())
//				.entity(this)
//				.build();
			return this;
		}
		catch(JsonParseException e)
		{
			logger.error("Unable to parse JSON; " + e.getMessage(), e);
			jsonFailure = true;
			
			return this;
		}
		catch(Exception e)
		{
			logger.error("Unable to parse JSON; " + e.getMessage(), e);
			failure = true;
			
			return this;
		}
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
		return "http://" + user.getNick().toLowerCase() + "." + JaikuConstants.ARCHIVE_URL;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public boolean isFailure()
	{
		return failure;
	}
	
	public boolean isJsonFailure()
	{
		return jsonFailure;
	}
}
