package se.l4.jaiku.model;

/**
 * Basic information about a channel.
 * 
 * @author Andreas Holstenson
 *
 */
public class Channel
{
	private String avatar;
	private String nick;
	
	public Channel()
	{
	}
	
	public String getAvatar()
	{
		return avatar;
	}
	
	public void setAvatar(String avatar)
	{
		this.avatar = avatar;
	}
	
	public String getNick()
	{
		return nick;
	}
}
