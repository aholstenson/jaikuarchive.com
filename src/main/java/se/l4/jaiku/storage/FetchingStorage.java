package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;

import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;
import se.l4.jaiku.robot.JaikuAvatarFetcher;
import se.l4.jaiku.robot.JaikuPresenceFetcher;

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
	private final Gson gson;
	private final Storage backend;

	public FetchingStorage(Gson gson, Storage backend)
	{
		this.gson = gson;
		this.backend = backend;
	}

	@Override
	public Presence getPresence(String username, String id)
		throws IOException
	{
		Presence presence = backend.getPresence(username, id);
		if(presence == null)
		{
			try
			{
				presence = new JaikuPresenceFetcher(gson, username, id)
					.fetch();
				
				backend.savePresence(presence);
				
				JaikuAvatarFetcher avatars = new JaikuAvatarFetcher(backend);
				avatars.fetchAvatar(presence.getUser());
				
				for(Comment c : presence.getComments())
				{
					avatars.fetchAvatar(c.getUser());
				}
			}
			catch(IOException e)
			{
				// TODO: Request to Jaiku failed, what should we do?
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
}
