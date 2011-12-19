package se.l4.jaiku.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.l4.jaiku.model.CachedPresence;
import se.l4.jaiku.model.ChannelStream;
import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;
import se.l4.jaiku.model.User;

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
	
	private File getPresencePathForUser(String username)
	{
		username = username.toLowerCase();
		String u1 = username.length() > 2 ? username.substring(0, 2) : username;
		
		File root = new File(directory, "presences");
		return new File(new File(root, u1), username);
	}
	
	private File getPresencePath(String username, String id)
	{
		File user = getPresencePathForUser(username);
		
		String p1 = id.length() > 2 ? id.substring(0, 2) : id;
		String p2 = id.length() > 4 ? id.substring(2, 4) : id;
		
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
			
			saveUser(presence.getUser(), true);
			for(Comment c : presence.getComments())
			{
				saveUser(c.getUser(), true);
			}
		}
		finally
		{
			Closeables.closeQuietly(writer);
		}
	}
	
	private File getAvatarPath(String username)
	{
		username = username.toLowerCase();
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
	
	private File getUserPath(String username)
	{
		username = username.toLowerCase();
		String u1 = username.length() > 2 ? username.substring(0, 2) : username;
		
		File root = new File(directory, "users");
		File folder = new File(root, u1);
		folder.mkdirs();
		
		File user = new File(folder, username + ".json");
		
		return user;
	}
	
	@Override
	public User getUser(String username)
		throws IOException
	{
		File path = getUserPath(username);
		if(path.exists())
		{
			FileReader reader = new FileReader(path);
			try
			{
				return gson.fromJson(reader, User.class);
			}
			finally
			{
				Closeables.closeQuietly(reader);
			}
		}
		return null;
	}
	
	public void saveUser(User user)
		throws IOException
	{
		saveUser(user, false);
	}
	
	public void saveUser(User user, boolean mustNotExist)
		throws IOException
	{
		File path = getUserPath(user.getNick());
		if(mustNotExist && path.exists())
		{
			System.out.println("Abort");
			return;
		}
		
		FileWriter writer = new FileWriter(path);
		try
		{
			gson.toJson(user, writer);
		}
		finally
		{
			Closeables.closeQuietly(writer);
		}
	}
	
	private File getChannelPath(String channel, int page)
	{
		channel = channel.toLowerCase();
		if(channel.startsWith("#"))
		{
			channel = channel.substring(1);
		}
		String u1 = channel.length() > 2 ? channel.substring(0, 2) : channel;
		
		File root = new File(directory, "channels");
		File folder = new File(root, u1);
		folder = new File(folder, channel);
		folder.mkdirs();
		
		File user = new File(folder, "page-" + page + ".json");
		
		return user;
	}
	
	@Override
	public ChannelStream getChannel(String channel, int page)
		throws IOException
	{
		File path = getChannelPath(channel, page);
		if(path.exists())
		{
			FileReader reader = new FileReader(path);
			try
			{
				return gson.fromJson(reader, ChannelStream.class);
			}
			finally
			{
				Closeables.closeQuietly(reader);
			}
		}
		return null;
	}
	
	@Override
	public void saveChannel(ChannelStream channel, int page)
		throws IOException
	{
		File path = getChannelPath(channel.getChannel().getNick(), page);
		if(path.exists())
		{
			return;
		}
		
		FileWriter writer = new FileWriter(path);
		try
		{
			gson.toJson(channel, writer);
		}
		finally
		{
			Closeables.closeQuietly(writer);
		}
	}
	
	private File getChannelPresencePath(String channel, String id)
	{
		channel = channel.toLowerCase();
		if(channel.startsWith("#"))
		{
			channel = channel.substring(1);
		}
		String u1 = channel.length() > 2 ? channel.substring(0, 2) : channel;
		
		File root = new File(directory, "channels");
		File folder = new File(root, u1);
		folder = new File(folder, channel);
		folder = new File(folder, "presences");
		
		String p1 = id.length() > 2 ? id.substring(0, 2) : id;
		String p2 = id.length() > 4 ? id.substring(2, 4) : id;
		folder = new File(
			new File(folder, p1),
			p2
		);
		
		folder.mkdirs();
		
		File user = new File(folder, id + ".json");
		
		return user;
	}
	
	@Override
	public void saveChannelPresence(String channel, Presence presence)
		throws IOException
	{
		File path = getChannelPresencePath(channel, presence.getId());
		if(path.exists())
		{
			return;
		}
		
		FileWriter writer = new FileWriter(path);
		try
		{
			gson.toJson(presence, writer);
			
			saveUser(presence.getUser(), true);
			for(Comment c : presence.getComments())
			{
				saveUser(c.getUser(), true);
			}
		}
		finally
		{
			Closeables.closeQuietly(writer);
		}
	}
	
	@Override
	public Presence getChannelPresence(String channel, String id)
		throws IOException
	{
		File path = getChannelPresencePath(channel, id);
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
	public List<CachedPresence> getPresencesForUser(String user)
		throws IOException
	{
		File file = getPresencePathForUser(user);
		if(! file.exists())
		{
			return Collections.emptyList();
		}
		
		List<CachedPresence> result = new ArrayList<CachedPresence>();
		
		recurse(file, result);
		
		return result;
	}
	
	private void recurse(File file, List<CachedPresence> result)
		throws IOException
	{
		for(File f : file.listFiles())
		{
			if(f.isDirectory())
			{
				recurse(f, result);
			}
			else if(f.isFile() && f.getName().endsWith(".json"))
			{
				FileReader reader = new FileReader(f);
				try
				{
					Presence p = gson.fromJson(reader, Presence.class);
					result.add(
						new CachedPresence(p.getId(), p.getTitle(), p.getCreatedAtDate(), p.getComments().size())
					);
				}
				finally
				{
					Closeables.closeQuietly(reader);
				}
			}
		}
	}
}
