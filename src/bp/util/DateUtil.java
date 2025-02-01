package bp.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DateUtil
{
	public final static String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public final static long ceilDate(long starttime, long ct, int counter, int unit)
	{
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(ct);
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(starttime);
		if (c.compareTo(cd) >= 0)
			return c.getTimeInMillis();
		int[] interval = getCalendarInterval(unit);
		if (interval[1] == Calendar.MILLISECOND)
		{
			return (long) (Math.ceil((double) (ct - starttime) / (double) (interval[0] * counter))) * counter * interval[0] + starttime;
		}
		else
		{
			c.add(counter * interval[0], interval[1]);
			while (c.compareTo(cd) < 0)
			{
				c.add(1, unit);
			}
			return c.getTimeInMillis();
		}
	}

	public final static int getCalendarType(TimeUnit unit)
	{
		switch (unit)
		{
			case DAYS:
				return Calendar.DATE;
			case HOURS:
				return Calendar.HOUR;
			case MILLISECONDS:
				return Calendar.MILLISECOND;
			case MINUTES:
				return Calendar.MINUTE;
			case SECONDS:
				return Calendar.SECOND;
			default:
				return 0;
		}
	}

	public final static int[] getCalendarInterval(int unit)
	{
		switch (unit)
		{
			case Calendar.MILLISECOND:
				return new int[] { 1, Calendar.MILLISECOND };
			case Calendar.SECOND:
				return new int[] { 1000, Calendar.MILLISECOND };
			case Calendar.MINUTE:
				return new int[] { 60000, Calendar.MILLISECOND };
			case Calendar.HOUR:
				return new int[] { 3600000, Calendar.MILLISECOND };
			case Calendar.DATE:
				return new int[] { 1, Calendar.DATE };
			case Calendar.WEEK_OF_MONTH:
				return new int[] { 1, Calendar.WEEK_OF_MONTH };
			case Calendar.MONTH:
				return new int[] { 1, Calendar.MONTH };
			case Calendar.YEAR:
				return new int[] { 1, Calendar.YEAR };
			default:
				return null;
		}
	}

	public final static Map<Integer, String> getCalendarUnits()
	{
		Map<Integer, String> rc = new HashMap<Integer, String>();
		rc.put(Calendar.MILLISECOND, "MILLISECOND");
		rc.put(Calendar.SECOND, "SECOND");
		rc.put(Calendar.MINUTE, "MINUTE");
		rc.put(Calendar.HOUR, "HOUR");
		rc.put(Calendar.DATE, "DAY");
		rc.put(Calendar.WEEK_OF_MONTH, "WEEK");
		rc.put(Calendar.MONTH, "MONTH");
		rc.put(Calendar.YEAR, "YEAR");
		return rc;
	}

	public final static String formatTime(long time)
	{
		return formatTime(time, DEFAULT_FORMAT);
	}

	public final static String formatTime(long time, String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		return df.format(new Date(time));
	}

	public final static long parseTime(String text)
	{
		return parseTime(text, DEFAULT_FORMAT);
	}

	public final static long parseTime(String text, String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		try
		{
			return df.parse(text).getTime();
		}
		catch (ParseException e)
		{
			Std.err(e);
		}
		return -1;
	}
}