package se.l4.jaiku.web;

import se.l4.crayon.Configurator;
import se.l4.dust.jaxrs.AppBootstrap;
import se.l4.dust.jaxrs.resteasy.ResteasyModule;

/**
 * Boostrap (servlet listener) that makes sure Dust can find our web parts.
 * 
 * @author Andreas Holstenson
 *
 */
public class JaikuBootstrap
	extends AppBootstrap
{

	@Override
	protected void initialize(Configurator configurator)
	{
		configurator
			.add(WebModule.class)
			.add(ResteasyModule.class);
	}

}
