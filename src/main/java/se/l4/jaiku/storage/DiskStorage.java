package se.l4.jaiku.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import se.l4.jaiku.model.Presence;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.gson.Gson;

/**
 * Disk based {@link Storage}.
 * 
 * @author Andreas Holstenson
 *
 */
public class DiskStorage
	implements Storage
{
	private final File directory;
	private final Gson gson;

	public DiskStorage(File directory, Gson gson)
	{
		this.directory = directory;
		this.gson = gson;
		
		directory.mkdirs();
	}
	
	private File getPresencePath(String username, String id)
	{
		String u1 = username.length() > 2 ? username.substring(0, 2) : username;
		
		String p1 = id.length() > 2 ? id.substring(0, 2) : id;
		String p2 = id.length() > 4 ? id.substring(2, 4) : id;
		
		File root = new File(directory, "presences");
		File user = new File(new File(root, u1), username);
		File folder = new File(
			new File(user, p1),
			p2
		);
		
		folder.mkdirs();
		
		return new File(folder, id + ".json");
	}
	
	@Override
	public Presence getPresence(String username, String id) throws IOException
	{
		File path = getPresencePath(username, id);
		
		if(path.exists())
		{
			FileReader reader = new FileReader(path);
			try
			{
				return gson.fromJson(reader, Presence.class);
			}
			finally
			{
				Closeables.closeQuietly(reader);
			}
		}
		
		return null;
	}
	
	@Override
	public void savePresence(Presence presence) throws IOException
	{
		String username = presence.getUser().getNick();
		File path = getPresencePath(username, presence.getId());
		
		FileWriter writer = new FileWriter(path);
		try
		{
			gson.toJson(presence, writer);
		}
		finally
		{
			Closeables.closeQuietly(writer);
		}
	}
	
	private File getAvatarPath(String username)
	{
		String u1 = username.length() > 2 ? username.substring(0, 2) : username;
		
		File root = new File(directory, "avatars");
		File folder = new File(root, u1);
		folder.mkdirs();
		
		File user = new File(folder, username + ".jpg");
		
		return user;
	}
	
	@Override
	public InputStream getAvatar(String username) throws IOException
	{
		File path = getAvatarPath(username);
		if(path.exists())
		{
			return new FileInputStream(path);
		}
		
		return null;
	}
	
	@Override
	public void saveAvatar(String username, InputStream stream)
		throws IOException
	{
		File path = getAvatarPath(username);
		FileOutputStream out = new FileOutputStream(path);
		try
		{
			ByteStreams.copy(stream, out);
		}
		finally
		{
			Closeables.closeQuietly(out);
		}
	}
}
