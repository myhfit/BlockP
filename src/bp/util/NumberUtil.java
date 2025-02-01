package bp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtil
{
	private final static NumberFormat S_NF_PERCENT()
	{
		return new DecimalFormat("0.00%");
	}

	private final static NumberFormat S_NF_BYTES()
	{
		return new DecimalFormat("0.0");
	}

	private final static String[] S_BYTEUNITS = new String[] { "B", "KiB", "MiB", "GiB" };

	public final static String formatCurrency(long v)
	{
		return NumberFormat.getNumberInstance(Locale.US).format(v);
	}

	public final static String formatPercent(Number v)
	{
		return S_NF_PERCENT().format(v.doubleValue());
	}

	public final static String formatByteCount(Number v)
	{
		double num = v.doubleValue();
		int c = 0;
		if (num > 10000)
		{
			num /= 1024d;
			c++;
		}
		if (num > 10000)
		{
			num /= 1024d;
			c++;
		}
		if (num > 10000)
		{
			num /= 1024d;
			c++;
		}
		return S_NF_BYTES().format(num) + S_BYTEUNITS[c];
	}
}
