package bp.unit;

import java.util.ArrayList;
import java.util.List;

public interface BPUnits<U extends BPUnit<?, ?>>
{
	String getUnitsName();

	@SuppressWarnings("unchecked")
	default List<U> getUnitValues()
	{
		Class<Enum<?>> cls = (Class<Enum<?>>) getUnitClass();
		Enum<?>[] es = cls.getEnumConstants();
		List<U> rc = new ArrayList<U>();
		for (Enum<?> e : es)
		{
			rc.add((U) e);
		}
		return rc;
	}

	Class<U> getUnitClass();

	U getBaseUnit();
}
