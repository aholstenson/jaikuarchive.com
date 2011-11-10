package se.l4.jaiku.web;

import java.io.File;

import org.eclipse.jetty.servlets.GzipFilter;

import se.l4.crayon.CrayonModule;
import se.l4.crayon.annotation.Contribution;
import se.l4.dust.api.NamespaceManager;
import se.l4.dust.jaxrs.ServletBinder;
import se.l4.jaiku.storage.DiskStorage;
import se.l4.jaiku.storage.Storage;

import com.google.gson.Gson;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Stage;

/**
 * Module that binds up the web based user interface.
 * 
 * @author andreas
 *
 */
public class WebModule
	extends CrayonModule
{
	public static final String NAMESPACE = "jaiku";
	
	@Override
	protected void configure()
	{
	}
	
	@Provides
	@Singleton
	public Storage provideStorage()
	{
		return new DiskStorage(new File("jaiku-archive"), new Gson());
	}
	
	@Contribution(name="tinder-ns")
	public void contributeNamespace(NamespaceManager manager)
	{
		manager.bind(NAMESPACE)
			.setPrefix("jaiku")
			.setPackageFromClass(getClass())
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
