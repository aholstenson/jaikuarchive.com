package se.l4.jaiku.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class ChannelStreamEntry
{
	private String id;
	
	private String title;
	
	private String content;
	
	private String url;
	
	private String created_at;
	
	private User user;
	
	public ChannelStreamEntry()
	{
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public String getCreatedAt()
	{
		return created_at;
	}
	
	public DateTime getCreatedAtDate()
	{
		return formatter.parseDateTime(created_at);
	}
	
	public User getUser()
	{
		return user;
	}
	
	// 2010-01-27T09-17-26Z
	static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd'T'HH-mm-ss'Z'")
		.toFormatter();
}
