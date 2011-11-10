package se.l4.jaiku;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import se.l4.dust.Dust;
import se.l4.dust.jaxrs.DustFilter;
import se.l4.jaiku.web.JaikuBootstrap;

public class JettyServer
{
	public static void main(String[] args)
		throws Exception
	{
		Server server = new Server(8888);

		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
 
        context.addFilter(DustFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        context.addServlet(DefaultServlet.class, "/*");
        context.addEventListener(new JaikuBootstrap());

		server.setHandler(context);

		server.start();
		
		if("false".equals(System.getProperty(Dust.DUST_PRODUCTION)))
		{
			System.out.println("You are running in development mode, press enter to exit.");  
			System.in.read();
			
			server.stop();
			System.exit(0);
		}
	}
}