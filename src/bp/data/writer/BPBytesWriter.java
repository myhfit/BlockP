package bp.data.writer;

import java.nio.ByteOrder;

public interface BPBytesWriter
{
	BPBytesWriter put(byte v);

	BPBytesWriter putShort(short v);

	BPBytesWriter putInt(int v);

	BPBytesWriter putLong(long v);

	BPBytesWriter putFloat(float v);

	BPBytesWriter putDouble(double v);

	BPBytesWriter put(byte[] bs);

	ByteOrder order();

	BPBytesWriter order(ByteOrder bo);

	int position();

	long positionLong();

	BPBytesWriter position(int index);

	BPBytesWriter positionLong(long index);
}
