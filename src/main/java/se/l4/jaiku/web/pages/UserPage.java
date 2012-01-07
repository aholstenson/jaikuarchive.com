package se.l4.jaiku.web.pages;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.TimeFormatting;
import se.l4.jaiku.model.CachedPresence;
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
	private static final int PAGE_SIZE = 20;

	private final Storage storage;
	
	private User user;
	private List<CachedPresence> presences;
	private CachedPresence presence;

	private int totalPresences;
	private int page;
	private boolean hasPrevious;
	private boolean hasNext;
	
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
		
		UserStream stream = new JaikuUserStream(new Gson(), username, null)
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
	public Object get(@HeaderParam("Host") String host, @QueryParam("page") int page0)
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
		
		List<CachedPresence> presences = storage.getPresencesForUser(username);
		totalPresences = presences.size();
		page = Math.max(page0, 1);
		
		if(page > 1)
		{
			int start = (page-1) * PAGE_SIZE;
			int end = start + PAGE_SIZE;
			
			if(start > presences.size())
			{
				// Nothing left
				presences = Collections.emptyList();
				hasPrevious = false;
				hasNext = false;
			}
			else
			{
				presences = presences.subList(start, Math.min(presences.size(), end));
				hasPrevious = true;
				hasNext = end < totalPresences;
			}
		}
		else if(presences.size() > PAGE_SIZE)
		{
			presences = presences.subList(0, PAGE_SIZE);
			hasNext = true;
			hasPrevious = false;
		}
		else
		{
			hasNext = false;
			hasPrevious = false;
		}
		
		this.presences = presences;
		
		return this;
	}
	
	@GET
	@Path("/all")
	public Object getAll(@HeaderParam("Host") String host)
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
		
		if(username.equalsIgnoreCase("jaiku"))
		{
			// Don't index the Jaiku user, it's full of spam
			return Response.seeOther(URI.create("/")).build();
		}
		
		storage.getUserStream(username.toLowerCase());
		
		return Response.seeOther(URI.create("/")).build();
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String avatar(User user)
	{
		return AvatarsPage.avatar(user);
	}
	
	public List<CachedPresence> getPresences()
	{
		return presences;
	}
	
	public int getTotalPresences()
	{
		return totalPresences;
	}
	
	public CachedPresence getPresence()
	{
		return presence;
	}
	
	public void setPresence(CachedPresence presence)
	{
		this.presence = presence;
	}
	
	public boolean isHasNext()
	{
		return hasNext;
	}
	
	public boolean isHasPrevious()
	{
		return hasPrevious;
	}
	
	public int getPage()
	{
		return page;
	}
	
	public String getRelativeTime(DateTime dt)
	{
		Interval interval = new Interval(dt.getMillis(), System.currentTimeMillis());
		Period period = interval.toPeriod();
		
		return period.toString(TimeFormatting.YEARS_AND_MONTHS);
	}
}
