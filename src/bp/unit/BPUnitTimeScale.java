package bp.unit;

import java.math.BigDecimal;

import bp.unit.BPUnit.BPUnitBigDecimal;

public enum BPUnitTimeScale implements BPUnitBigDecimal<BPUnitTimeScale>
{
	Second(1), Minute(60), Hour(3600), Day(86400), MilliSecond("0.001"), MicroSecond("0.000001"), NanoSecond("0.000000001"), PicoSecond("0.000000000001"), FemtoSecond("0.000000000000001"), Week(604800), JulianYear(31557600);

	private BigDecimal m_secs;

	private BPUnitTimeScale(long x)
	{
		m_secs = new BigDecimal(x);
	}

	private BPUnitTimeScale(String x)
	{
		m_secs = new BigDecimal(x);
	}

	public BigDecimal getSeconds()
	{
		return m_secs;
	}

	public BigDecimal getValue()
	{
		return (BigDecimal) m_secs;
	}

	public static class BPUnitTimeScales implements BPUnits<BPUnitTimeScale>
	{
		public Class<BPUnitTimeScale> getUnitClass()
		{
			return BPUnitTimeScale.class;
		}

		public String getUnitsName()
		{
			return "Time Scale";
		}

		public BPUnitTimeScale getBaseUnit()
		{
			return Second;
		}
	}
}
