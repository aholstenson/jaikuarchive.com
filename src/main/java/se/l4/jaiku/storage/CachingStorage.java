package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import se.l4.jaiku.model.CachedPresence;
import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;
import se.l4.jaiku.model.UserStream;

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
	
	private final Storage backend;
	
	private final Cache<UserWithId, Presence> presences;
	private final Cache<String, List<CachedPresence>> presencesForUser;
	
	public CachingStorage(Storage backend0)
	{
		this.backend = backend0;
		presences = CacheBuilder.newBuilder()
			.softValues()
			.expireAfterAccess(20, TimeUnit.MINUTES)
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
		
		presencesForUser = CacheBuilder.newBuilder()
			.softValues()
			.maximumSize(10)
			.expireAfterAccess(60, TimeUnit.MINUTES)
			.build(new CacheLoader<String, List<CachedPresence>>()
			{
				@Override
				public List<CachedPresence> load(String key)
					throws Exception
				{
					return backend.getPresencesForUser(key);
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
	
	@Override
	public ChannelStream getChannel(String channel, int page)
		throws IOException
	{
		return backend.getChannel(channel, page);
	}
	
	@Override
	public void saveChannel(ChannelStream channel, int page)
		throws IOException
	{
		backend.saveChannel(channel, page);
	}
	
	@Override
	public Presence getChannelPresence(String channel, String id)
		throws IOException
	{
		return backend.getChannelPresence(channel, id);
	}
	
	@Override
	public void saveChannelPresence(String channel, Presence presence)
		throws IOException
	{
		backend.saveChannelPresence(channel, presence);
	}
	
	@Override
	public List<CachedPresence> getPresencesForUser(String user)
		throws IOException
	{
		try
		{
			return presencesForUser.get(user);
		}
		catch(ExecutionException e)
		{
			Throwables.propagateIfPossible(e.getCause());
			Throwables.propagateIfInstanceOf(e.getCause(), IOException.class);
			
			throw new IOException("Unable to fetch presences for " + user + "; " + e.getMessage(), e.getCause());
		}
		catch(UncheckedExecutionException e)
		{
			Throwables.propagateIfPossible(e.getCause());
			Throwables.propagateIfInstanceOf(e.getCause(), IOException.class);
			
			throw new IOException("Unable to fetch presence for " + user + "; " + e.getMessage(), e.getCause());
		}
	}
	
	@Override
	public UserStream getUserStream(String user)
		throws IOException
	{
		return backend.getUserStream(user);
	}
	
	@Override
	public void saveUserStream(UserStream stream)
		throws IOException
	{
		backend.saveUserStream(stream);
	}
}
