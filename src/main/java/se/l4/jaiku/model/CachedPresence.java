package se.l4.jaiku.model;

import org.joda.time.DateTime;

/**
 * Presence information that has been cached. To make sure things run a bit
 * more smooth.
 * 
 * @author Andreas Holstenson
 *
 */
public class CachedPresence
{
	private final String id;
	private final String title;
	private final int comments;
	private final DateTime createdAt;
	
	public CachedPresence(String id, String title, DateTime createdAt, int comments)
	{
		this.id = id;
		this.title = title;
		this.createdAt = createdAt;
		this.comments = comments;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public int getComments()
	{
		return comments;
	}
	
	public DateTime getCreatedAt()
	{
		return createdAt;
	}
}
