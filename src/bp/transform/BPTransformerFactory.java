package bp.transform;

import java.util.Collection;

public interface BPTransformerFactory
{
	String getName();

	boolean checkData(Object source);

	Collection<String> getFunctionTypes();

	BPTransformer<?> createTransformer(String func);

	public final static String TF_TOSTRING = "to String";
	public final static String TF_TOBYTEARRAY = "to byte[]";
	public final static String TF_TOMAP = "to Map";
	public final static String TF_TOLIST = "to List";
	public final static String TF_TOOBJ = "to Object";

	public final static String[] TF_ALL = new String[] { TF_TOSTRING, TF_TOBYTEARRAY, TF_TOMAP, TF_TOLIST, TF_TOOBJ };
}
