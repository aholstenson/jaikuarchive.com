package se.l4.jaiku.storage;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.jaiku.model.Presence;

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
	private ExecutorService executor;

	public FetchQueue(FetchingStorage storage)
	{
		this.storage = storage;
		executor = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat("jaiku-fetch-%s")
			.build()
		);
	}
	
	public Future<Presence> queuePresence(final String username, final String id)
	{
		return executor.submit(new Callable<Presence>()
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
			}
		});
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
}
