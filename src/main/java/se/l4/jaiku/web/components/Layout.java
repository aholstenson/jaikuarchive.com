package se.l4.jaiku.web.components;

import se.l4.dust.api.annotation.Component;
import se.l4.dust.api.annotation.PrepareRender;
import se.l4.dust.api.annotation.TemplateParam;

/**
 * Component that contains the layout.
 * 
 * @author Andreas Holstenson
 *
 */
@Component
public class Layout
{
	private String title;

	public Layout()
	{
	}
	
	@PrepareRender
	public void prepare(@TemplateParam("title") String title)
	{
		this.title = title;
	}
	
	public String getTitle()
	{
		return title;
	}
}
