package se.l4.jaiku.model;

public class User
{
	private String avatar;
	private String first_name;
	private String last_name;
	
	private String nick;
	
	private String full_nick;
	private String url;
	
	public User()
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
	
	public String getFirstName()
	{
		return first_name;
	}
	
	public String getLastName()
	{
		return last_name;
	}
	
	public String getNick()
	{
		return nick;
	}
	
	public String getFullNick()
	{
		return full_nick;
	}
	
	public String getUrl()
	{
		return url;
	}
}
