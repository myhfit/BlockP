package bp.unit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import bp.util.ObjUtil;

public interface BPUnit<U extends BPUnit<U, C>, C>
{
	String name();

	C castValue(Object v);

	default C convert(U dest, C v)
	{
		return null;
	}

	C getValue();

	int ordinal();

	public static interface BPUnitDouble<U extends BPUnit<U, Double>> extends BPUnit<U, Double>
	{
		default Double castValue(Object v)
		{
			if (v == null)
				return Double.NaN;
			if (v instanceof Number)
				return ((Number) v).doubleValue();
			try
			{
				return Double.valueOf(ObjUtil.toString(v));
			}
			catch (NumberFormatException e)
			{
				return Double.NaN;
			}
		}

		default Double convert(U dest, Double v)
		{
			return v * getValue() / dest.getValue();
		}
	}

	public static interface BPUnitBigDecimal<U extends BPUnit<U, BigDecimal>> extends BPUnit<U, BigDecimal>
	{
		default BigDecimal castValue(Object v)
		{
			if (v == null)
				return null;
			if (v instanceof Long)
				return BigDecimal.valueOf((long) v);
			if (v instanceof BigInteger)
				return new BigDecimal((BigInteger) v);
			try
			{
				return new BigDecimal(ObjUtil.toString(v));
			}
			catch (NumberFormatException e)
			{
				return null;
			}
		}

		default BigDecimal convert(U dest, BigDecimal v)
		{
			return v.multiply(getValue()).divide(dest.getValue(), 24, RoundingMode.HALF_UP);
		}
	}
}
