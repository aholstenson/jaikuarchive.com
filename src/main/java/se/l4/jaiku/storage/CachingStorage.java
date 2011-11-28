package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;

import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Storage that will cache data in memory.
 * 
 * @author Andreas Holstenson
 *
 */
public class CachingStorage
	implements Storage
{
	private static final Presence EMPTY_PRESENCE = new Presence();
	
	private final Cache<UserWithId, Presence> presences;
	private final Storage backend;
	
	public CachingStorage(Storage backend0)
	{
		this.backend = backend0;
		presences = CacheBuilder.newBuilder()
			.softValues()
			.expireAfterAccess(1, TimeUnit.HOURS)
			.build(new CacheLoader<UserWithId, Presence>()
			{
				@Override
				public Presence load(UserWithId key)
					throws Exception
				{
					Presence result = backend.getPresence(key.username, key.id);
					return result == null ? EMPTY_PRESENCE : result;
				}
			});
	}
	
	@Override
	public Presence getPresence(String username, String id)
		throws IOException
	{
		try
		{
			Presence presence = presences.get(new UserWithId(username, id));
			return presence == EMPTY_PRESENCE ? null : presence;
		}
		catch(ExecutionException e)
		{
			Throwables.propagateIfPossible(e.getCause());
			Throwables.propagateIfInstanceOf(e.getCause(), IOException.class);
			
			throw new IOException("Unable to fetch presence; " + e.getMessage(), e.getCause());
		}
		catch(UncheckedExecutionException e)
		{
			Throwables.propagateIfPossible(e.getCause());
			Throwables.propagateIfInstanceOf(e.getCause(), IOException.class);
			
			throw new IOException("Unable to fetch presence; " + e.getMessage(), e.getCause());
		}
	}
	
	@Override
	public void savePresence(Presence presence)
		throws IOException
	{
		backend.savePresence(presence);
	}
	
	@Override
	public InputStream getAvatar(String username)
		throws IOException
	{
		return backend.getAvatar(username);
	}
	
	@Override
	public void saveAvatar(String username, InputStream stream)
		throws IOException
	{
		backend.saveAvatar(username, stream);
	}
	
	@Override
	public User getUser(String username)
		throws IOException
	{
		return backend.getUser(username);
	}
	
	@Override
	public void saveUser(User user)
		throws IOException
	{
		backend.saveUser(user);
	}
	
	private static class UserWithId
	{
		private final String username;
		private final String id;

		public UserWithId(String username, String id)
		{
			this.username = username;
			this.id = id;
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
}
