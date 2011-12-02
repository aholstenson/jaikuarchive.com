package se.l4.jaiku.web.pages;

import java.io.IOException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.JaikuConstants;
import se.l4.jaiku.TimeFormatting;
import se.l4.jaiku.model.Channel;
import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.ChannelStreamEntry;
import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

import com.google.gson.JsonParseException;
import com.google.inject.Inject;

@Template
@Path("/channel/{id}")
public class ChannelPage
{
	private static final Logger logger = LoggerFactory.getLogger(ChannelPage.class);
	
	private final Storage storage;
	private ChannelStream stream;
	private ChannelStreamEntry entry;

	private boolean jsonFailure;
	private boolean failure;

	private String channel;

	private int page;

	@Inject
	public ChannelPage(Storage storage)
	{
		this.storage = storage;
	}
	
	@GET
	public Object channel(@PathParam("id") String id, 
			@QueryParam("page") @DefaultValue("1") int page)
		throws IOException
	{
		this.channel = id;
		this.page = page;
		
		try
		{
			stream = storage.getChannel(id, page);
			if(stream == null)
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
	
	public ChannelStream getStream()
	{
		return stream;
	}
	
	public boolean isJsonFailure()
	{
		return jsonFailure;
	}
	
	public boolean isFailure()
	{
		return failure;
	}
	
	public ChannelStreamEntry getEntry()
	{
		return entry;
	}
	
	public void setEntry(ChannelStreamEntry entry)
	{
		this.entry = entry;
	}
	
	public String getRelativeTime(DateTime dt)
	{
		Interval interval = new Interval(dt.getMillis(), System.currentTimeMillis());
		Period period = interval.toPeriod();
		
		return period.toString(TimeFormatting.YEARS_AND_MONTHS);
	}
	
	public boolean hasPrevious()
	{
		return page > 1;
	}
	
	public boolean hasNext()
	{
		return stream.isMorePages();
	}
	
	public int getPage()
	{
		return page;
	}
	
	public String getChannel()
	{
		return channel;
	}
	
	public String avatar(User user)
	{
		return AvatarsPage.avatar(user);
	}
	
	public String avatar(Channel channel)
	{
		return AvatarsPage.avatar(channel);
	}
	
	public String user(User user)
	{
		return "http://" + user.getNick().toLowerCase() + "." + JaikuConstants.ARCHIVE_URL;
	}
	
	public String url()
	{
		return url(channel);
	}
	
	public static String url(String channel)
	{
		return "http://" + JaikuConstants.ARCHIVE_URL + "/channel/" + channel;
	}
}
