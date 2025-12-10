package bp.data.reader;

import java.nio.ByteOrder;

public interface BPBytesReader
{
	byte get();

	byte get(int index);

	char getChar();

	char getChar(int index);

	short getShort();

	short getShort(int index);

	int getInt();

	int getInt(int index);

	long getLong();

	long getLong(int index);

	float getFloat();

	float getFloat(int index);

	double getDouble();

	double getDouble(int index);

	ByteOrder order();

	BPBytesReader order(ByteOrder bo);

	int position();

	long positionLong();

	BPBytesReader position(int index);

	BPBytesReader positionLong(long index);

	void get(byte[] bs);
}
