package bp.data;

import java.util.Map;

public interface BPInstanceFactory<T>
{
	String getName();

	T create(Map<String, Object> params);

	Class<? extends T> getInstanceClass();
}
