package se.l4.jaiku.robot;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ManualPresenceFetcher
{
	public static void main(String[] args)
		throws IOException
	{
		Gson gson = new GsonBuilder()
			.setPrettyPrinting()
			.create();
		
		new JaikuPresenceFetcher(gson, "jocke", "35672489").fetch(System.out);
	}
}
