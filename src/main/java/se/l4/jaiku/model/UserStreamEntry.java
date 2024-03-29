package se.l4.jaiku.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class UserStreamEntry
{
	private String id;
	
	private String comment_id;
	private String title;
	
	private String content;
	private String pretty_content;
	
	private String url;
	
	private String created_at;
	
	public UserStreamEntry()
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
	
	public String getCommentId()
	{
		return comment_id;
	}
	
	public DateTime getCreatedAtDate()
	{
		return formatter.parseDateTime(created_at);
	}
	
	// 2010-01-27T09-17-26Z
	static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd'T'HH-mm-ss'Z'")
		.toFormatter();
}
