package se.l4.jaiku.robot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import se.l4.jaiku.model.User;
import se.l4.jaiku.storage.Storage;

import com.google.common.io.Closeables;

/**
 * Fetcher for a Jaiku avatar.
 * 
 * @author Andreas Holstenson
 *
 */
public class JaikuAvatarFetcher
{
	private final Storage storage;

	public JaikuAvatarFetcher(Storage storage)
	{
		this.storage = storage;
	}
	
	public void fetchAvatar(User user)
		throws IOException
	{
		fetchAvatar(user.getNick(), user.getAvatar());
	}
	
	public void fetchAvatar(String username, String avatarUrl)
		throws IOException
	{
		InputStream stream = storage.getAvatar(username);
		if(stream != null)
		{
			Closeables.closeQuietly(stream);
			return;
		}
		
		URL url = new URL(avatarUrl);
		stream = url.openStream();
		try
		{
			storage.saveAvatar(username, stream);
		}
		finally
		{
			Closeables.closeQuietly(stream);
		}
	}
}
