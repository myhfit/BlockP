package bp.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import bp.BPCore;
import bp.cache.BPTreeCacheNode.BPTreeCacheNodeRoot;
import bp.context.BPProjectsContext;
import bp.project.BPResourceProject;
import bp.res.BPResourceFileSystem;
import bp.util.FileUtil;
import bp.util.Std;
import bp.util.ThreadUtil;

public class BPCacheFileSystemBase extends BPCacheBase implements BPCacheFileSystem
{
	protected List<String> m_cachedroots = new ArrayList<String>();

	protected Queue<String> m_cacheq = new ConcurrentLinkedQueue<String>();

	protected List<String> m_taskroots = new ArrayList<String>();

	protected Map<String, BPTreeCacheNodeRoot<BPCacheDataFileSystem>> m_data = new ConcurrentHashMap<String, BPTreeCacheNodeRoot<BPCacheDataFileSystem>>();

	protected ExecutorService m_execs = Executors.newScheduledThreadPool(2, (r) ->
	{
		Thread t = new Thread();
		t.setDaemon(true);
		return t;
	});

	public void clear()
	{
		m_cacheq.clear();
		m_cacheq.addAll(m_taskroots);
		m_data.clear();
	}

	public void clearTasks()
	{
		m_taskroots.clear();
	}

	public void addCacheTask(String task)
	{
		if (!m_taskroots.contains(task))
			m_taskroots.add(task);
		if (!m_cacheq.contains(task))
			m_cacheq.add(task);
	}

	protected boolean doCache()
	{
		Queue<String> ts = m_cacheq;
		String t = ts.poll();
		if (t != null)
		{
			File f = new File(t);
			f = f.getAbsoluteFile();
			BPTreeCacheNodeRoot<BPCacheDataFileSystem> root = new BPTreeCacheNodeRoot<BPCacheDataFileSystem>();
			root.setKey(f.getName());
			root.setValue(createCacheData(null, f));
			long ct = System.currentTimeMillis();
			makeCache(f, root);
			long ct2 = System.currentTimeMillis() - ct;
			Std.debug("File Cache:" + f.getName() + "=" + root.count() + " Loaded in " + ct2 + "ms");
			m_data.put(f.getAbsolutePath(), root);
			refreshProject(root);
			BPCore.EVENTS_CACHE.trigger(BPCore.getFileSystemCacheChannelID(), new BPEventCache(f.getAbsolutePath()));
		}
		return ts.isEmpty();
	}

	protected void makeCache(File par, BPTreeCacheNode<BPCacheDataFileSystem> parnode)
	{
		Map<File, BPTreeCacheNode<BPCacheDataFileSystem>> dirmap = new HashMap<File, BPTreeCacheNode<BPCacheDataFileSystem>>();
		FileUtil.forEachFile(par, false, (p, f) ->
		{
			boolean flag = true;
			boolean isdir = f.isDirectory();
			boolean isfile = f.isFile();
			if (!isfile && !isdir)
			{
				flag = false;
			}
			else if (f.isHidden())
			{
				flag = false;
			}
			else if (isdir && f.getName().startsWith("."))
			{
				flag = false;
			}

			if (flag)
			{
				BPTreeCacheNode<BPCacheDataFileSystem> node = new BPTreeCacheNode<BPCacheDataFileSystem>();
				node.setKey(f.getName());
				BPCacheDataFileSystem data = createCacheData(parnode.getValue(), f);
				node.setValue(data);
				parnode.addChild(node);
				if (isdir)
				{
					dirmap.put(f, node);
				}
			}
			return flag;
		});
		if (dirmap.size() > 0)
		{
			for (Entry<File, BPTreeCacheNode<BPCacheDataFileSystem>> entry : dirmap.entrySet())
			{
				makeCache(entry.getKey(), entry.getValue());
			}
		}
	}

	protected BPCacheDataFileSystem createCacheData(BPCacheDataFileSystem par, File f)
	{
		return new BPCacheDataFileSystem(par, f);
	}

	public CompletableFuture<List<BPTreeCacheNode<BPCacheDataFileSystem>>> searchFileByNameAsync(String filename, String ext, int limit)
	{
		CompletableFuture<List<BPTreeCacheNode<BPCacheDataFileSystem>>> rc = CompletableFuture.supplyAsync(() -> searchFileByName(filename, ext, limit), ThreadUtil.getCacheAsyncPool());
		return rc;
	}

	public List<BPTreeCacheNode<BPCacheDataFileSystem>> searchFileByName(String filename, String ext, int limit)
	{
		return searchFileByName(filename, ext, limit, null);
	}

	public List<BPTreeCacheNode<BPCacheDataFileSystem>> searchFileByName(String filename, String ext, int limit, Predicate<String> cachekeyfilter)
	{
		List<BPTreeCacheNode<BPCacheDataFileSystem>> rc = new ArrayList<BPTreeCacheNode<BPCacheDataFileSystem>>();
		int count = 0;
		Map<String, BPTreeCacheNodeRoot<BPCacheDataFileSystem>> data = m_data;
		for (Entry<String, BPTreeCacheNodeRoot<BPCacheDataFileSystem>> entry : data.entrySet())
		{
			String key = entry.getKey();
			if (cachekeyfilter != null && !cachekeyfilter.test(key))
				continue;
			BPTreeCacheNode<BPCacheDataFileSystem> root = entry.getValue();
			if (searchCache(cpKey(filename), splitKeyExts(ext), root, limit, rc, count))
			{
				break;
			}
			count = rc.size();
		}
		return rc;
	}

	protected String[] splitKeyExts(String ext)
	{
		if (ext == null || ext.length() == 0)
			return null;
		String[] exts = ext.split(";");
		List<String> rc = new ArrayList<String>();
		for (int i = 0; i < exts.length; i++)
		{
			String e = exts[i].trim();
			if (e.length() > 0)
				rc.add(cpKey(e));
		}
		return rc.toArray(new String[rc.size()]);
	}

	protected String cpKey(String key)
	{
		return key.toLowerCase();
	}

	protected boolean searchCache(String key, String[] exts, BPTreeCacheNode<BPCacheDataFileSystem> node, int limit, List<BPTreeCacheNode<BPCacheDataFileSystem>> results, int count)
	{
		List<BPTreeCacheNode<BPCacheDataFileSystem>> fs = node.getChildren();
		if (fs == null)
			return false;
		int c2 = count;
		for (BPTreeCacheNode<BPCacheDataFileSystem> f : fs)
		{
			if (f.getValue().isDirectory())
			{
				int rs = results.size();
				boolean schd = searchCache(key, exts, f, limit, results, c2);
				if (schd)
					return true;
				c2 += (results.size() - rs);
				if (limit > 0 && c2 >= limit)
					return true;
			}
			else
			{
				if (cpKey(f.getKey()).indexOf(key) > -1 && checkExt(f, exts))
				{
					results.add(f);
					c2++;
					if (limit > 0 && c2 >= limit)
						return true;
				}
			}
		}
		return false;
	}

	protected boolean checkExt(BPTreeCacheNode<BPCacheDataFileSystem> f, String[] exts)
	{
		if (exts == null)
			return true;
		else
		{
			for (String ext : exts)
			{
				String key = f.getKey();
				if (cpKey(key).endsWith(ext))
					return true;
			}
			return false;
		}
	}

	public void invalidate(String filename)
	{
		runCacheSegment(() ->
		{
			for (BPTreeCacheNodeRoot<BPCacheDataFileSystem> root : m_data.values())
			{
				if (!invalidate(root, filename))
					root.setRootValid(false);
			}
		}, false);
	}

	protected boolean invalidate(BPTreeCacheNode<?> node, String filename)
	{
		boolean rc = true;
		BPCacheDataFileSystem data = (BPCacheDataFileSystem) node.getValue();
		if (data != null)
		{
			if (data.getFullName().equals(filename))
			{
				node.setValid(false);
				rc = false;
			}
		}
		List<BPTreeCacheNode<Object>> children = node.getChildren();
		if (children != null)
		{
			for (BPTreeCacheNode<?> child : children)
			{
				rc = rc && invalidate(child, filename);
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public void refresh()
	{
		runCacheSegment(() ->
		{
			for (BPTreeCacheNodeRoot<BPCacheDataFileSystem> root : m_data.values())
			{
				if (root.isRootValid())
					continue;
				root.setRootValid(true);
				if (root.filter((node) ->
				{
					if (!node.isValid())
					{
						node.clear();
						BPCacheDataFileSystem v = (BPCacheDataFileSystem) node.getValue();
						makeCache(new File(v.getFullName()), (BPTreeCacheNode<BPCacheDataFileSystem>) node);
						return true;
					}
					return false;
				}, null).size() > 0)
				{
					refreshProject(root);
					BPCore.EVENTS_CACHE.trigger(BPCore.getFileSystemCacheChannelID(), new BPEventCache(root.getValue().getFullName()));
				}
			}
		}, true);
	}

	protected void refreshProject(BPTreeCacheNode<BPCacheDataFileSystem> root)
	{
		String rootpath = root.getValue().getFullName();
		BPProjectsContext context = BPCore.getProjectsContext();
		for (BPResourceProject prj : context.listProject())
		{
			if (prj.isFileSystem())
			{
				if (rootpath.equals(((BPResourceFileSystem) prj).getFileFullName()))
				{
					prj.refreshByCache(root);
				}
			}
		}
		for (BPResourceProject prj : context.listProject())
		{
			if (prj.isFileSystem())
			{
				if (rootpath.equals(((BPResourceFileSystem) prj).getFileFullName()))
				{
					prj.refreshStatistics();
				}
			}
		}
	}
}
