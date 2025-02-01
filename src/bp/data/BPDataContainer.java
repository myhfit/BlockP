package bp.data;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import bp.res.BPResource;

public interface BPDataContainer
{
	void close();
	
	void open();
	
	void bind(BPResource res);
	
	void unbind();
	
	BPResource getResource();
	
	byte[] readAll();
	
	boolean writeAll(byte[] bs);

	<T> T useInputStream(Function<InputStream, T> in);

	<T> T useOutputStream(Function<OutputStream, T> out);
	
	String getTitle();
	
	default boolean canOpen()
	{
		return true;
	}
}
