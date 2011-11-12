package se.l4.jaiku;

import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Formatting utilities for times.
 * 
 * @author Andreas Holstenson
 *
 */
public class TimeFormatting
{
	private TimeFormatting()
	{
	}
	
	public static final PeriodFormatter YEARS_AND_MONTHS = 
		new PeriodFormatterBuilder()
		.appendYears()
		.appendSuffix(" year", " years")
		.appendSeparator(", ")
		.appendMonths()
		.appendSuffix(" month", " months")
		.toFormatter();
}
