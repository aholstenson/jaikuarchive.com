package se.l4.jaiku.web;

import se.l4.crayon.Configurator;
import se.l4.dust.jaxrs.AppBootstrap;
import se.l4.dust.jaxrs.resteasy.ResteasyModule;

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
