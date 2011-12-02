package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;

import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;

/**
 * Simple abstraction for the Jaiku storage.
 * 
 * @author Andreas Holstenson
 *
 */
public interface Storage
{
	/**
	 * Attempt to get a presence from the storage with the specified 
	 * identifier.
	 * 
	 * @param username
	 * 		username, without the {@code jaiku.com} part
	 * @param id
	 * 		identifier of presence
	 * @return
	 * 		presence if found, or {@code null}
	 * @throws IOException
	 */
	Presence getPresence(String username, String id)
		throws IOException;
	
	/**
	 * Save a presence to the storage.
	 * 
	 * @param presence
	 * @throws IOException
	 */
	void savePresence(Presence presence)
		throws IOException;
	
	/**
	 * Get a stored avatar.
	 * 
	 * @param username
	 * 		username, without the {@code jaiku.com} part
	 * @return
	 * 		avatar if found, or {@code null}
	 * @throws IOException
	 */
	InputStream getAvatar(String username)
		throws IOException;
	
	/**
	 * Save an avatar for the given user.
	 * 
	 * @param username
	 * @param stream
	 * @throws IOException
	 */
	void saveAvatar(String username, InputStream stream)
		throws IOException;
	
	/**
	 * Get information about a specific user.
	 * 
	 * @param username
	 * @return
	 * @throws IOException
	 */
	User getUser(String username)
		throws IOException;
	
	/**
	 * Save information about a user (if it does not exist).
	 * 
	 * @param user
	 * @throws IOException
	 */
	void saveUser(User user)
		throws IOException;
	
	/**
	 * Attempt to get the specified channel and page.
	 * 
	 * @param channel
	 * @param page
	 * @return
	 * @throws IOException
	 */
	ChannelStream getChannel(String channel, int page)
		throws IOException;
	
	/**
	 * Save the channel.
	 * 
	 * @param channel
	 * @param page
	 * @throws IOException
	 */
	void saveChannel(ChannelStream channel, int page)
		throws IOException;
	
	/**
	 * Get presence associated with a channel.
	 * 
	 * @param channel
	 * @param id
	 * @return
	 * @throws IOException
	 */
	Presence getChannelPresence(String channel, String id)
		throws IOException;
	
	/**
	 * Save presence for the specified channel.
	 * 
	 * @param presence
	 * @param id
	 * @throws IOException
	 */
	void saveChannelPresence(String channel, Presence presence)
		throws IOException;
}
