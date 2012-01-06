package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.jaiku.model.CachedPresence;
import se.l4.jaiku.model.Channel;
import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.ChannelStreamEntry;
import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;
import se.l4.jaiku.robot.JaikuAvatarFetcher;
import se.l4.jaiku.robot.JaikuChannelFetcher;
import se.l4.jaiku.robot.JaikuPresenceFetcher;
import se.l4.jaiku.robot.TextUpdater;

import com.google.gson.Gson;

/**
 * Storage that will fetch missing data on the fly.
 * 
 * @author Andreas Holstenson
 *
 */
public class FetchingStorage
	implements Storage
{
	private static final Logger logger = LoggerFactory.getLogger(FetchingStorage.class);
	
	private final long timeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
	
	private final Gson gson;
	private final Storage backend;
	private final FetchQueue queue;

	public FetchingStorage(Gson gson, Storage backend)
	{
		this.gson = gson;
		this.backend = backend;
		
		queue = new FetchQueue(this);
	}
	
	@Override
	public Presence getPresence(String username, String id) throws IOException
	{
		Presence presence = backend.getPresence(username, id);
		if(presence != null)
		{
			return presence;
		}
		
		Future<Presence> future = queue.queuePresence(username, id);
		try
		{
			return future.get(timeout, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e)
		{
			return null;
		}
		catch(ExecutionException e)
		{
			logger.warn("Unable to fetch via queue; " + e.getMessage(), e);
			return null;
		}
		catch(TimeoutException e)
		{
			logger.warn("Time out, trying to cancel");
			future.cancel(true);
			return null;
		}
	}

	public Presence fetchPresence(String username, String id)
		throws IOException
	{
		Presence presence = backend.getPresence(username, id);
		if(presence == null)
		{
			try
			{
				presence = new JaikuPresenceFetcher(gson)
					.fetch( username, id);
				
				backend.savePresence(presence);
				
				JaikuAvatarFetcher avatars = new JaikuAvatarFetcher(backend);
				avatars.fetchAvatar(presence.getUser());
				
				for(Comment c : presence.getComments())
				{
					avatars.fetchAvatar(c.getUser());
					
					for(UserWithId ui : TextUpdater.getPresenceLinks(c.getPrettyContent()))
					{
						queue.queuePresence(ui.getUsername(), ui.getId());
					}
				}
			}
			catch(IOException e)
			{
				// TODO: Request to Jaiku failed, what should we do?
				logger.warn("Unable to fetch " + username + " presence " + id, e);
			}
		}
		
		return presence;
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
	public ChannelStream getChannel(final String channel, int page)
		throws IOException
	{
		if(page > 1) return backend.getChannel(channel, page);
		
		ChannelStream stream = backend.getChannel(channel, page);
		if(stream != null)
		{
			return stream;
		}
		
		new JaikuChannelFetcher(gson, channel, new JaikuChannelFetcher.Callback()
		{
			@Override
			public void save(ChannelStream stream, int number)
			{
				try
				{
					backend.saveChannel(stream, number);
					
					JaikuAvatarFetcher avatars = new JaikuAvatarFetcher(backend);
					Channel ch = stream.getChannel();
					avatars.fetchAvatar(ch.getNick(), ch.getAvatar());
					
					// Queue each presence for fetching
					for(ChannelStreamEntry entry : stream.getStream())
					{
						queue.queueChannelPresence(channel, entry.getId());
					}
				}
				catch(IOException e)
				{
					logger.warn("Unable to save " + stream.getChannel().getNick() + " page " + number, e);
				}
			}
		})
		.fetch();
		
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
		Presence presence = backend.getChannelPresence(channel, id);
		if(presence != null)
		{
			return presence;
		}
		
		Future<Presence> future = queue.queueChannelPresence(channel, id);
		try
		{
			return future.get(timeout, TimeUnit.MILLISECONDS);
		}
		catch(InterruptedException e)
		{
			return null;
		}
		catch(ExecutionException e)
		{
			logger.warn("Unable to fetch via queue; " + e.getMessage(), e);
			return null;
		}
		catch(TimeoutException e)
		{
			logger.warn("Time out, trying to cancel");
			future.cancel(true);
			return null;
		}
	}
	
	public Presence fetchChannelPresence(String channel, String id)
		throws IOException
	{
		Presence presence = backend.getChannelPresence(channel, id);
		if(presence == null)
		{
			try
			{
				presence = new JaikuPresenceFetcher(gson)
					.fetchChannel(channel, id);
				
				backend.saveChannelPresence(channel, presence);
				
				JaikuAvatarFetcher avatars = new JaikuAvatarFetcher(backend);
				avatars.fetchAvatar(presence.getUser());
				
				for(Comment c : presence.getComments())
				{
					avatars.fetchAvatar(c.getUser());
					
					for(UserWithId ui : TextUpdater.getPresenceLinks(c.getPrettyContent()))
					{
						queue.queuePresence(ui.getUsername(), ui.getId());
					}
				}
			}
			catch(IOException e)
			{
				// TODO: Request to Jaiku failed, what should we do?
				logger.warn("Unable to fetch channel " + channel + " presence " + id, e);
			}
		}
		
		return presence;
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
		return backend.getPresencesForUser(user);
	}
}
