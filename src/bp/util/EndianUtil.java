package bp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EndianUtil
{
	public final static int intB2L(int value)
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(value);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt(0);
	}

	public final static long longB2L(long value)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(value);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong(0);
	}

	public final static double doubleB2L(double value)
	{
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putDouble(value);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getDouble(0);
	}
}
