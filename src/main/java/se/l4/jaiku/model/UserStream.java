package se.l4.jaiku.model;

import java.util.ArrayList;
import java.util.List;

public class UserStream
{
	private String title;
	private String url;
	private String contacts;
	
	private User user;
	
	private List<UserStreamEntry> stream;
	
	public UserStream()
	{
		stream = new ArrayList<UserStreamEntry>();
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public String getContacts()
	{
		return contacts;
	}
	
	public List<UserStreamEntry> getStream()
	{
		return stream;
	}
}
