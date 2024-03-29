package se.l4.jaiku.web;

import java.io.File;

import org.eclipse.jetty.servlets.GzipFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.l4.crayon.CrayonModule;
import se.l4.crayon.annotation.Contribution;
import se.l4.dust.api.NamespaceManager;
import se.l4.dust.api.annotation.NamespaceBinding;
import se.l4.dust.jaxrs.ServletBinder;
import se.l4.jaiku.storage.CachingStorage;
import se.l4.jaiku.storage.DiskStorage;
import se.l4.jaiku.storage.FetchingStorage;
import se.l4.jaiku.storage.Storage;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Stage;

/**
 * Module that binds up the web based user interface.
 * 
 * @author Andreas Holstenson
 *
 */
public class WebModule
	extends CrayonModule
{
	private static final Logger logger = LoggerFactory.getLogger(WebModule.class);
	
	public static final String NAMESPACE = "jaikuarchive";
	
	@Override
	protected void configure()
	{
	}
	
	@Provides
	@Singleton
	public Gson provideGson()
	{
		return new Gson();
	}
	
	@Provides
	@Singleton
	public DiskStorage provideDiskStorage(Gson gson)
	{
		File file = new File("/var/jaikuarchive");
		if(! file.exists() || ! file.canWrite())
		{
			logger.warn("Can not write to " + file.getAbsolutePath() + ", using current folder");
			file = new File("jaikuarchive");
		}
		
		return new DiskStorage(file, gson);
	}
	
	@Provides
	@Singleton
	public FetchingStorage provideFetchingStorage(Gson gson, DiskStorage storage)
	{
		return new FetchingStorage(gson, storage);
	}
	
	@Provides
	@Singleton
	public Storage provideStorage(FetchingStorage storage)
	{
		return new CachingStorage(storage);
	}
	
	@NamespaceBinding
	public void contributeNamespace(NamespaceManager manager)
	{
		manager.bind(NAMESPACE)
			.setPrefix("jaiku")
			.setPackageFromClass(getClass())
			.setVersion("4") // XXX: Update if assets have changed
			.add();
	}
	
	@Contribution
	public void bindGzip(ServletBinder binder, Stage stage)
	{
		if(stage == Stage.PRODUCTION)
		{
			binder.filter("/*").with(GzipFilter.class);
		}
	}
}
