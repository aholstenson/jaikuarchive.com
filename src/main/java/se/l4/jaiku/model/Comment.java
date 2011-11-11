package se.l4.jaiku.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class Comment
{
	private String id;
	
	private String comment_id;
	private String title;
	
	private String content;
	private String pretty_content;
	
	private String url;
	
	private String created_at;
	
	private User user;
	
	public Comment()
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

	public void setContent(String content)
	{
		this.content = content;
	}
	
	public String getPrettyContent()
	{
		return pretty_content;
	}
	
	public void setPrettyContent(String pretty_content)
	{
		this.pretty_content = pretty_content;
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
	public static DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		.appendPattern("yyyy-MM-dd'T'HH-mm-ss'Z'")
		.toFormatter();
}
