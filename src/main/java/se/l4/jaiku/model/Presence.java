package se.l4.jaiku.model;

import java.util.List;

import org.joda.time.DateTime;

/**
 * Presence feed.
 * 
 * @author Andreas Holstenson
 *
 */
public class Presence
{
	private String id;
	private String title;
	private String content;
	private String icon;
	private String url;
	private String created_at;
	
	private User user;
	
	private List<Comment> comments;
	
	public Presence()
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
	
	public String getIcon()
	{
		return icon;
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
		return Comment.formatter.parseDateTime(created_at);
	}
	
	public User getUser()
	{
		return user;
	}
	
	public List<Comment> getComments()
	{
		return comments;
	}
}
