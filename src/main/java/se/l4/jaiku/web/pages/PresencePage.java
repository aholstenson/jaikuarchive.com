package se.l4.jaiku.web.pages;

import se.l4.dust.api.annotation.Template;
import se.l4.jaiku.model.Comment;
import se.l4.jaiku.model.Presence;

@Template
public class PresencePage
{
	private final Presence presence;
	private Comment comment;

	public PresencePage(Presence presence)
	{
		this.presence = presence;
	}

	public Presence getPresence()
	{
		return presence;
	}
	
	public Comment getComment()
	{
		return comment;
	}
	
	public void setComment(Comment comment)
	{
		this.comment = comment;
	}
}
