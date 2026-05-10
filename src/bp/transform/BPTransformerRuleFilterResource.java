package bp.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

import bp.res.BPResource;
import bp.res.BPResourceByteArray;
import bp.res.BPResourceFile;

public abstract class BPTransformerRuleFilterResource extends BPTransformerRuleFilter<BPResource>
{
	public final static String TFFACNAME_FILTERRESNAME = "Resource Filter Name";
	public final static String TFFACNAME_FILTERRESSIZE = "Resource Filter Size";

	public static class BPTransformerRuleFilterResourceName extends BPTransformerRuleFilterResource
	{
		protected Object transform(Collection<BPResource> t)
		{
			List<BPResource> rc = new ArrayList<BPResource>();
			TextPredicate check = new TextPredicate(m_rule);
			for (BPResource res : t)
				if (check.test(res.getName()))
					rc.add(res);
			return rc;
		}

		public String getInfo()
		{
			return "Filter Resource by Name";
		}

		protected static class TextPredicate implements Predicate<String>
		{
			protected String m_substr;

			public TextPredicate(String substr)
			{
				m_substr = substr == null ? "" : substr;
			}

			public boolean test(String t)
			{
				return t.contains(m_substr);
			}
		}
	}

	public static class BPTransformerRuleFilterResourceSize extends BPTransformerRuleFilterResource
	{
		protected Object transform(Collection<BPResource> t)
		{
			List<BPResource> rc = new ArrayList<BPResource>();
			SizePredicate check = new SizePredicate(m_rule);
			for (BPResource res : t)
			{
				long size = -1;
				if (res.isFileSystem())
				{
					if (res.isLeaf())
						size = ((BPResourceFile) res).getSize();
				}
				else if (res instanceof BPResourceByteArray)
				{
					size = ((BPResourceByteArray) res).getSize();
				}
				if (check.test(size))
					rc.add(res);
			}
			return rc;
		}

		public String getInfo()
		{
			return "Filter Resource by Size";
		}

		private static class SizePredicate implements Predicate<Long>
		{
			protected SizeOP m_op; // 0:=,1:>,2:<,3:>=,4:<=
			protected long m_size;

			public SizePredicate(String substr)
			{
				SizeOP op = SizeOP.EQ;
				char c;
				int vi = -1;
				for (int i = 0; i < substr.length(); i++)
				{
					c = substr.charAt(i);
					if (c >= '0' && c <= '9')
					{
						vi = i;
						break;
					}
				}
				if (vi > -1)
				{
					String opstr = substr.substring(0, vi).trim();
					String numstr = substr.substring(vi).trim();
					op = SizeOP.find(opstr);
					m_size = Long.parseLong(numstr);
				}
				m_op = op;
			}

			public boolean test(Long t)
			{
				return m_op.test(t, m_size);
			}
		}

		private static enum SizeOP
		{
			EQ("="), GT(">"), LT("<"), GTEQ(">="), LTEQ("<=");

			private String opstr;

			private SizeOP(String opstr)
			{
				this.opstr = opstr;
			}

			public final static SizeOP find(String opstr)
			{
				for (SizeOP op : values())
				{
					if (op.opstr.equals(opstr))
					{
						return op;
					}
				}
				return null;
			}

			public boolean test(long v, long st)
			{
				switch (this)
				{
					case EQ:
						return v == st;
					case GT:
						return v > st;
					case LT:
						return v < st;
					case GTEQ:
						return v >= st;
					case LTEQ:
						return v <= st;
				}
				return false;
			}
		}
	}

	public static abstract class BPTransformerFactoryRuleFilterResource implements BPTransformerFactory
	{
		public boolean checkData(Object source)
		{
			return source != null && source instanceof BPResource;
		}

		public Collection<String> getFunctionTypes()
		{
			return new CopyOnWriteArrayList<String>(new String[] { TF_TOLIST });
		}

		public boolean isRuleFilter()
		{
			return true;
		}
	}

	public static class BPTransformerFactoryRuleFilterResourceName extends BPTransformerFactoryRuleFilterResource
	{
		public String getName()
		{
			return TFFACNAME_FILTERRESNAME;
		}

		public BPTransformer<?> createTransformer(String func)
		{
			return new BPTransformerRuleFilterResourceName();
		}
	}

	public static class BPTransformerFactoryRuleFilterResourceSize extends BPTransformerFactoryRuleFilterResource
	{
		public String getName()
		{
			return TFFACNAME_FILTERRESSIZE;
		}

		public BPTransformer<?> createTransformer(String func)
		{
			return new BPTransformerRuleFilterResourceSize();
		}
	}
}