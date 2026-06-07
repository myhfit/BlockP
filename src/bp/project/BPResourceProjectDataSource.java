package bp.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.BPCore;
import bp.config.BPSetting;
import bp.data.BPDataSource;
import bp.data.BPDataSourceFactory;
import bp.res.BPResource;
import bp.res.BPResourceDataSource;
import bp.res.BPResourceDir;
import bp.res.BPResourceFile;
import bp.res.BPResourceIO;
import bp.util.ClassUtil;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPResourceProjectDataSource extends BPResourceProjectFile
{
	public final static String PRJTYPE_DS = "datasource";

	public BPResourceProjectDataSource(BPResourceDir dir)
	{
		super(dir, false);
	}

	public String getResType()
	{
		return "datasource project";
	}

	public String getProjectTypeName()
	{
		return PRJTYPE_DS;
	}

	public BPProjectItemFactory[] getItemFactories()
	{
		List<BPProjectItemFactory> facs = new ArrayList<BPProjectItemFactory>();
		for (BPDataSourceFactory dsfac : ClassUtil.getServices(BPDataSourceFactory.class))
		{
			BPProjectItemFactoryDataSource fac = new BPProjectItemFactoryDataSource();
			fac.setup(dsfac);
			facs.add(fac);
		}
		return facs.toArray(new BPProjectItemFactory[facs.size()]);
	}

	public List<BPResource> getProjectFunctionItems()
	{
		List<BPResource> rc = new ArrayList<BPResource>();
		return rc;
	}

	public final static boolean writeDSLink(BPResourceFile res, BPDataSource ds)
	{
		return res.useOutputStream((out) ->
		{
			try
			{
				String str = JSONUtil.encode(ds.getMappedData(), 6);
				IOUtil.write(out, str.getBytes("utf-8"));
				return true;
			}
			catch (Exception e)
			{
				Std.err(e);
			}
			return false;
		});
	}

	public BPResource wrapResource(BPResource res)
	{
		BPResource rc = null;
		if (res.isFileSystem())
		{
			if (res.isLeaf())
			{
				if (".bpds".equals(res.getExt()))
				{
					rc = readDSLink((BPResourceFile) res);
				}
				else if (".bpprj".equalsIgnoreCase(res.getName()))
				{
					return null;
				}
				return rc;
			}
			else
			{
				return res;
			}
		}
		return super.wrapResource(res);
	}

	protected BPResource readDSLink(BPResourceFile res)
	{
		try
		{
			String typename = null;
			Map<String, Object> dsps = JSONUtil.decode(TextUtil.toString(IOUtil.read(res), "utf-8"));
			typename = (String) dsps.get("dstype");
			if (typename == null)
			{
				String mname = res.getName();
				mname = mname.substring(0, mname.length() - res.getExt().length());
				int vi = mname.lastIndexOf(".");
				if (vi > -1 && vi < mname.length() - 1)
				{
					typename = mname.substring(vi + 1);
				}
			}

			if (typename != null)
			{
				final String tname = typename;
				BPDataSourceFactory dsfac = ClassUtil.findService(BPDataSourceFactory.class, s -> tname.equals(s.getName()));
				if (dsfac != null)
				{
					BPResourceDataSource rc = dsfac.create(JSONUtil.decode(TextUtil.toString(IOUtil.read(res), "utf-8"))).getStructureResource();
					rc.setDSLinkFilename(res.getFileFullName());
					return rc;
				}
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return null;
	}

	public void save(BPResource res)
	{
		if (res instanceof BPResourceDataSource)
		{
			String filename = ((BPResourceDataSource) res).getDSLinkFilename();
			BPDataSource ds = ((BPResourceDataSource) res).getDataSource();
			IOUtil.write((BPResourceIO) BPCore.getFileContext().getRes(filename), TextUtil.fromString(JSONUtil.encode(ds), "utf-8"));
		}
	}

	public static class BPProjectItemFactoryDataSource implements BPProjectItemFactory
	{
		protected String m_name;
		protected String m_clsname;
		protected String m_dsfacname;

		public String getName()
		{
			return m_name;
		}

		public void setup(BPDataSourceFactory fac)
		{
			m_name = fac.getName();
			m_dsfacname = fac.getName();
			m_clsname = fac.getProjectItemClassName();
		}

		public void create(Map<String, Object> params, BPResourceProject project, BPResource par)
		{
			BPDataSourceFactory dsfac = ClassUtil.findService(BPDataSourceFactory.class, s -> m_dsfacname.equals(s.getName()));
			if (dsfac != null)
			{
				BPDataSource ds = dsfac.create(params);
				String name = (String) params.get("name");
				BPResourceDir p = (BPResourceDir) par;
				if (p == null)
					p = (BPResourceDir) project;
				BPResourceFile f = (BPResourceFile) p.createChild(name + "." + m_name + ".bpds", true);
				writeDSLink(f, ds);
			}
		}

		public String getItemClassName()
		{
			return m_clsname;
		}

		public BPSetting getSetting()
		{
			BPDataSourceFactory dsfac = ClassUtil.findService(BPDataSourceFactory.class, s -> m_dsfacname.equals(s.getName()));
			return dsfac == null ? null : dsfac.getSetting();
		}
	}

	public static class BPProjectFactoryDataSource implements BPProjectFactory
	{
		public BPResourceProject create(String prjtype, BPResourceDir dir, Map<String, String> prjdata)
		{
			BPResourceProjectDataSource project = new BPResourceProjectDataSource(dir);
			if (prjdata.containsKey("name"))
				project.setName(prjdata.get("name"));
			if (prjdata.containsKey("path"))
				project.setPath(prjdata.get("path"));
			return project;
		}

		public Class<? extends BPResourceProject> getProjectClass()
		{
			return BPResourceProjectDataSource.class;
		}

		public List<String> getProjectTypes()
		{
			List<String> rc = new ArrayList<String>();
			rc.add(PRJTYPE_DS);
			return rc;
		}

		public boolean canHandle(String prjtype)
		{
			return prjtype.equalsIgnoreCase(PRJTYPE_DS);
		}

		public String getName()
		{
			return "DataSource Project";
		}
	}
}
