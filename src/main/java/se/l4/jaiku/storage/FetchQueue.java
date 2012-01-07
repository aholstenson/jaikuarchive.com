package se.l4.jaiku.storage;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.UserStream;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Simple in memory fetch queue.
 * 
 * @author Andreas Holstenson
 *
 */
public class FetchQueue
{
	private static Logger logger = LoggerFactory.getLogger(FetchQueue.class);
	
	private final FetchingStorage storage;
	private final ExecutorService executor;
	private final ExecutorService userExecutor;
	private final Set<String> userQueue;
	private final Map<String, Future<Presence>> futures;

	public FetchQueue(FetchingStorage storage)
	{
		this.storage = storage;
		executor = Executors.newFixedThreadPool(16, new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat("jaiku-fetch-%s")
			.build()
		);
		userExecutor = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat("jaiku-user-fetch-%s")
			.build()
		);
		
		userQueue = new CopyOnWriteArraySet<String>();
		futures = new ConcurrentHashMap<String, Future<Presence>>();
	}
	
	public Future<Presence> queuePresence(final String username, final String id)
	{
		if(futures.containsKey(id))
		{
			return futures.get(id);
		}
		
		Future<Presence> f = executor.submit(new Callable<Presence>()
		{
			@Override
			public Presence call()
			{
				try
				{
					return storage.fetchPresence(username, id);
				}
				catch(IOException e)
				{
					logger.warn("Could not fetch " + username + " presence " + id, e);
					return null;
				}
				finally
				{
					futures.remove(id);
				}
			}
		});
		futures.put(id, f);
		return f;
	}
	
	public Future<Presence> queueChannelPresence(final String channel, final String id)
	{
		return executor.submit(new Callable<Presence>()
		{
			@Override
			public Presence call()
			{
				try
				{
					return storage.fetchChannelPresence(channel, id);
				}
				catch(IOException e)
				{
					logger.warn("Could not fetch channel " + channel + " presence " + id, e);
					return null;
				}
			}
		});
	}
	
	public Future<UserStream> queueUserStream(final String user)
	{
		if(userQueue.contains(user))
		{
			return null;
		}
		
		userQueue.add(user);
		return userExecutor.submit(new Callable<UserStream>()
		{
			@Override
			public UserStream call()
			{
				try
				{
					return storage.fetchUserStream(user);
				}
				catch(IOException e)
				{
					logger.warn("Could not fetch user " + user, e);
					return null;
				}
				finally
				{
					userQueue.remove(user);
				}
			}
		});
	}
}
