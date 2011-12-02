package se.l4.jaiku.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stream of a channel.
 * 
 * @author Andreas Holstenson
 *
 */
public class ChannelStream
{
	private String title;
	private String url;
	
	private Channel user;
	
	private List<ChannelStreamEntry> stream;
	
	private boolean morePages;
	
	public ChannelStream()
	{
		stream = new ArrayList<ChannelStreamEntry>();
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public Channel getUser()
	{
		return user;
	}
	
	public List<ChannelStreamEntry> getStream()
	{
		return stream;
	}
	
	public boolean isMorePages()
	{
		return morePages;
	}
	
	public void setMorePages(boolean morePages)
	{
		this.morePages = morePages;
	}
}
