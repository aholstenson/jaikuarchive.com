package se.l4.jaiku.storage;

public class UserWithId
{
	final String username;
	final String id;

	public UserWithId(String username, String id)
	{
		this.username = username;
		this.id = id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		UserWithId other = (UserWithId) obj;
		if(id == null)
		{
			if(other.id != null)
				return false;
		}
		else if(!id.equals(other.id))
			return false;
		if(username == null)
		{
			if(other.username != null)
				return false;
		}
		else if(!username.equals(other.username))
			return false;
		return true;
	}
}