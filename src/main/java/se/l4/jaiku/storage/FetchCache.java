package se.l4.jaiku.storage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Simple in memory fetch queue.
 * 
 * @author Andreas Holstenson
 *
 */
public class FetchCache
{
	private static Logger logger = LoggerFactory.getLogger(FetchCache.class);
	
	private final Storage storage;
	private ExecutorService executor;

	public FetchCache(Storage storage)
	{
		this.storage = storage;
		executor = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder()
			.setDaemon(true)
			.setNameFormat("jaiku-fetch-%s")
			.build()
		);
	}
	
	public void queuePresence(final String username, final String id)
	{
		executor.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					storage.getPresence(username, id);
				}
				catch(IOException e)
				{
					logger.warn("Could not fetch " + username + " presence " + id, e);
				}
			}
		});
	}
	
	public void queueChannelPresence(final String channel, final String id)
	{
		executor.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					storage.getChannelPresence(channel, id);
				}
				catch(IOException e)
				{
					logger.warn("Could not fetch " + channel + " presence " + id, e);
				}
			}
		});
	}
}
