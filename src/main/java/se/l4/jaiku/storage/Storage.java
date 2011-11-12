package se.l4.jaiku.storage;

import java.io.IOException;
import java.io.InputStream;

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
}
