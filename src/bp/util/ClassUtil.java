package bp.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtil
{
	// adv method makes some langserver high cpu usage
	// @SuppressWarnings("unchecked")
	// public final static <T, R> R useMethod(Class<?> cls, String methodname,
	// Function<CBFunction<T, R>, R> cb, Class<?>... pcls)
	// {
	// try
	// {
	// Method m = cls.getMethod(methodname, pcls);
	// if (m != null)
	// {
	// return cb.apply((obj, params) ->
	// {
	// try
	// {
	// return (R) m.invoke(obj, params);
	// }
	// catch (IllegalAccessException | IllegalArgumentException |
	// InvocationTargetException e)
	// {
	// throw new RuntimeException(e);
	// }
	// });
	// }
	// }
	// catch (NoSuchMethodException | SecurityException e)
	// {
	// Std.err(e);
	// }
	// return null;
	// }
	//
	// public static interface CBFunction<T, R>
	// {
	// R apply(T obj, Object... params);
	// }
	private final static BPExtClassLoader S_CL = new BPExtClassLoader();

	@SuppressWarnings("unchecked")
	public final static <T> T tryCallSimpleMethod(String classname, String methodname, Object obj, Object... params)
	{
		T rc = null;
		try
		{
			Class<?> cls = Class.forName(classname, true, ClassUtil.getExtensionClassLoader());
			if (cls != null)
			{
				Method[] ms = cls.getMethods();
				for (Method m : ms)
				{
					if (methodname.equals(m.getName()))
					{
						rc = (T) m.invoke(obj, params);
						break;
					}
				}
			}
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}

		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T> T callMethod(Class<?> cls, String methodname, Class<?>[] paramcls, Object obj, boolean ignoreNoMethod, Object... params)
	{
		try
		{
			Method m = cls.getMethod(methodname, paramcls);
			if (m != null)
			{
				try
				{
					return (T) m.invoke(obj, params);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		catch (NoSuchMethodException e)
		{
			if (!ignoreNoMethod)
				Std.err(e);
		}
		catch (SecurityException e)
		{
			Std.err(e);
		}
		return null;
	}

	public final static Map<String, Object> getMappedDataReflect(Object obj)
	{
		Map<String, Object> rc = new LinkedHashMap<String, Object>();
		List<Field> fs = ClassUtil.getFields(obj.getClass());
		for (Field f : fs)
		{
			if (Modifier.isPublic(f.getModifiers()))
			{
				try
				{
					rc.put(f.getName(), cloneDataReflect(f.get(obj)));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					Std.err(e);
				}
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	protected final static Object cloneDataReflect(Object obj)
	{
		Object rc = obj;
		if (obj != null)
		{
			if (obj instanceof List)
			{
				List<Object> r = new ArrayList<Object>();
				List<?> src = (List<?>) obj;
				for (Object s : src)
				{
					r.add(cloneDataReflect(s));
				}
				rc = r;
			}
			else if (obj instanceof Map)
			{
				Map<String, ?> src = (Map<String, ?>) obj;
				Map<String, Object> r = new LinkedHashMap<String, Object>();
				for (String k : src.keySet())
				{
					r.put(k, cloneDataReflect(src.get(k)));
				}
				rc = r;
			}
			else
			{
				Class<?> c = obj.getClass();
				if (c.isArray())
				{
					int l = Array.getLength(obj);
					List<Object> r = new ArrayList<Object>();
					for (int i = 0; i < l; i++)
						r.add(cloneDataReflect(Array.get(obj, i)));
					rc = r;
				}
				else if (!(c.getName().startsWith("java.")))
				{
					rc = getMappedDataReflect(obj);
				}
			}
		}
		return rc;
	}

	public final static <T> ServiceLoader<T> getServices(Class<T> ifcclass)
	{
		ServiceLoader<T> facs = ServiceLoader.load(ifcclass, S_CL);
		return facs;
	}

	public final static <T> ServiceLoader<T> getServices(Class<T> ifcclass, ClassLoader loader)
	{
		ServiceLoader<T> facs = ServiceLoader.load(ifcclass, loader);
		return facs;
	}

	public final static <T> ServiceLoader<T> getExtensionServices(Class<T> ifcclass)
	{
		ServiceLoader<T> facs = ServiceLoader.load(ifcclass, S_CL);
		return facs;
	}

	public final static <T> List<T> filterServices(Class<T> ifcclass, Predicate<T> check)
	{
		ServiceLoader<T> facs = getExtensionServices(ifcclass);
		List<T> rc = new ArrayList<T>();
		for (T t : facs)
		{
			if (check.test(t))
			{
				rc.add(t);
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public final static <T, R> R findService(Class<T> ifcclass, Predicate<T> check)
	{
		ServiceLoader<T> facs = getExtensionServices(ifcclass);
		for (T t : facs)
		{
			if (check.test(t))
			{
				return (R) t;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public final static <T, R> R findServiceByMaxScore(Class<T> ifcclass, Function<T, Integer> scorefunc, Predicate<T> check)
	{
		ServiceLoader<T> facs = getExtensionServices(ifcclass);
		int max = Integer.MIN_VALUE;
		R rc = null;
		for (T t : facs)
		{
			if (check != null && !check.test(t))
				continue;
			int score = scorefunc.apply(t);
			if (score > max)
			{
				max = score;
				rc = (R) t;
			}
		}
		return rc;
	}

	public final static List<Field> getFields(Class<?> c)
	{
		List<Field> rc = new ArrayList<Field>();
		List<Class<?>> cc = getClassChain(c);
		for (Class<?> cls : cc)
		{
			Field[] fs = cls.getFields();
			for (Field f : fs)
				if (!rc.contains(f))
					rc.add(f);
		}
		return rc;
	}

	public final static List<Class<?>> getClassChain(Class<?> c)
	{
		List<Class<?>> rc = new LinkedList<>();
		rc.add(c);
		c = c.getSuperclass();
		while (c != Object.class)
		{
			rc.add(0, c);
			c = c.getSuperclass();
		}
		return new ArrayList<Class<?>>(rc);
	}

	@SuppressWarnings("unchecked")
	public final static <T> T createObject(String classname)
	{
		T rc = null;
		try
		{
			Class<?> cls = Class.forName(classname, true, S_CL);
			Constructor<?> cons = cls.getConstructor();
			rc = (T) cons.newInstance();
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e)
		{
			Std.err(e);
		}
		return rc;
	}

	public final static <T> T createObject(Class<?> clz, Class<T> castClass, Class<?>[] paramtypes, Object[] params)
	{
		T rc = null;
		Object obj = null;
		try
		{
			if (paramtypes == null || paramtypes.length == 0 || params == null || params.length == 0 || paramtypes.length != params.length)
			{
				Constructor<?> cons = clz.getConstructor();
				obj = cons.newInstance();
			}
			else
			{
				Constructor<?> cons = clz.getConstructor(paramtypes);
				obj = cons.newInstance(params);
			}
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		if (obj != null)
			rc = castClass.cast(obj);
		return rc;
	}

	public final static <T> List<T> createObjects(List<Class<?>> classes, Class<T> castClass, Class<?>[] paramtypes, Object[] params)
	{
		List<T> objs = new ArrayList<T>();
		for (Class<?> clz : classes)
		{
			objs.add(createObject(clz, castClass, paramtypes, params));
		}
		return objs;
	}

	public final static List<Class<?>> getClasses(List<String> classnames)
	{
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String cls : classnames)
		{
			if (cls == null || cls.trim().length() == 0)
				continue;
			try
			{
				Class<?> c = Class.forName(cls.trim());
				classes.add(c);
			}
			catch (Exception e)
			{
				Std.err(e);
			}
		}
		return classes;
	}

	public final static List<String> getClassNames(String packname)
	{
		return getClassNames(packname, false);
	}

	public final static void addClassLoaderURLs(ClassLoader cl, List<URL> urls, boolean issystem)
	{
		if (cl instanceof URLClassLoader)
		{
			URLClassLoader ucl = (URLClassLoader) cl;
			URL[] _urls = ucl.getURLs();
			for (URL url : _urls)
			{
				if (!urls.contains(url))
					urls.add(url);
			}
		}
		else if (issystem)
		{
			String jcpstr = System.getProperty("java.class.path");
			String[] jcps = jcpstr.split(File.pathSeparator);
			for (String jcp : jcps)
			{
				URL url = null;
				if (jcp.endsWith(".jar"))
				{
					try
					{
						File f = new File(jcp);
						url = new URL("jar", null, f.toURI().toURL().toString() + "!/");
					}
					catch (MalformedURLException e)
					{
						Std.err(e);
					}
				}
				else
				{
					try
					{
						url = new URL("file", null, jcp);
					}
					catch (MalformedURLException e)
					{
						Std.err(e);
					}
				}
				if (!urls.contains(url))
					urls.add(url);
			}
		}
	}

	public final static List<URL> getClassPaths()
	{
		List<URL> urls = new ArrayList<URL>();
		List<ClassLoader> cls = new LinkedList<ClassLoader>();
		ClassLoader cl = S_CL;
		cls.add(cl);
		ClassLoader clp = cl.getParent();
		while (clp != null && clp != cl)
		{
			cl = clp;
			if (cls.contains(cl))
				break;
			cls.add(0, cl);
			clp = cl.getParent();
		}
		for (ClassLoader cl2 : cls)
		{
			addClassLoaderURLs(cl2, urls, !(cl2 instanceof URLClassLoader));
		}
		return urls;
	}

	public final static List<String> getClassNames(String packname, boolean recursive)
	{
		List<String> classnames = new ArrayList<String>(20);
		Set<String> cnset = new HashSet<String>();
		String pack = packname;
		String packageDirName = packname.replace('.', '/');
		List<URL> dirs = new ArrayList<URL>(20);
		Enumeration<URL> direnum;
		try
		{
			if (packname.length() == 0)
			{
				addClassLoaderURLs(ClassLoader.getSystemClassLoader(), dirs, true);
				addClassLoaderURLs(Thread.currentThread().getContextClassLoader(), dirs, false);
			}
			else
			{
				try
				{
					direnum = ClassLoader.getSystemResources(packageDirName);
					while (direnum.hasMoreElements())
					{
						URL url = direnum.nextElement();
						if (!dirs.contains(url))
							dirs.add(url);
					}
				}
				catch (Exception e)
				{
					Std.err(e);
				}
				try
				{
					direnum = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
					while (direnum.hasMoreElements())
					{
						URL url = direnum.nextElement();
						if (!dirs.contains(url))
							dirs.add(url);
					}
				}
				catch (Exception e)
				{
					Std.err(e);
				}
			}

			for (URL url : dirs)
			{
				String protocol = url.getProtocol();
				if ("file".equals(protocol))
				{
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findClassNameInPackage(pack, filePath, recursive, classnames, cnset);
				}
				else if ("jar".equals(protocol))
				{
					JarFile jar;
					try
					{
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements())
						{
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/')
							{
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName))
							{
								int idx = name.lastIndexOf('/');
								if ((idx != -1) || recursive)
								{
									if (name.endsWith(".class") && !entry.isDirectory())
									{
										String fullname = name.replace('/', '.');
										fullname = fullname.substring(0, fullname.length() - 6);
										if (!cnset.contains(fullname))
										{
											cnset.add(fullname);
											classnames.add(fullname);
										}
									}
								}
							}
						}
					}
					catch (IOException e)
					{
						Std.err(e);
					}
				}
			}
		}
		catch (IOException e)
		{
			Std.err(e);
		}

		return classnames;
	}

	public final static void findClassNameInPackage(String packname, String packnamepath, boolean recursive, List<String> classnames, Set<String> cnset)
	{
		File dir = new File(packnamepath);
		if (!dir.exists() || !dir.isDirectory())
		{
			return;
		}
		File[] dirfiles = dir.listFiles();
		for (File file : dirfiles)
		{
			if (recursive && file.isDirectory())
			{
				findClassNameInPackage((packname.length() == 0 ? "" : packname + ".") + file.getName(), file.getAbsolutePath(), recursive, classnames, cnset);
			}
			else if (file.getName().endsWith(".class"))
			{
				String className = file.getName().substring(0, file.getName().length() - 6);
				String fullname = packname + '.' + className;
				if (!cnset.contains(fullname))
				{
					cnset.add(fullname);
					classnames.add(fullname);
				}
			}
		}
	}

	public final static Class<?> getTClass(String classname, ClassLoader cl)
	{
		Class<?> cls = null;
		try
		{
			cls = cl.loadClass(classname);
		}
		catch (ClassNotFoundException | NoClassDefFoundError | IllegalAccessError e)
		{
		}
		return cls;
	}

	public final static Class<?> getTClass(String classname)
	{
		return getTClass(classname, Thread.currentThread().getContextClassLoader());
	}

	public final static boolean checkChildClass(Class<?> parent, Class<?> childclass)
	{
		if (childclass == null)
			return false;
		if (childclass == parent)
			return false;
		return parent.isAssignableFrom(childclass);
	}

	public final static boolean checkChildClass(Class<?> parent, String childclassname, ClassLoader cl)
	{
		return checkChildClass(parent, getTClass(childclassname, cl));
	}

	public final static <T> T tryLoopSuperClass(Function<Class<?>, T> testfunc, Class<?> cls, Class<?> root)
	{
		Class<?> cur = cls;
		T rc = null;
		while (cur != null)
		{
			rc = testfunc.apply(cur);
			if (rc != null)
				break;
			cur = cur.getSuperclass();
			if (cur != null && !root.isAssignableFrom(cur))
			{
				cur = null;
			}
		}
		return rc;
	}

	public final static BPExtClassLoader getExtensionClassLoader()
	{
		return S_CL;
	}

	public static class BPExtClassLoader extends URLClassLoader
	{
		public BPExtClassLoader()
		{
			super(new URL[0]);
		}

		public BPExtClassLoader(ClassLoader parent)
		{
			super(new URL[0], parent);
		}

		public void addExtURL(String filename)
		{
			try
			{
				URL url = new URL("file:" + filename);
				addURL(url);
			}
			catch (MalformedURLException e)
			{
				Std.err(e);
			}
		}

		public List<URL> getAllURLs()
		{
			List<URL> rc = new ArrayList<URL>();
			LinkedList<URLClassLoader> cls = new LinkedList<URLClassLoader>();
			cls.add(this);
			ClassLoader cl = this.getParent();
			while (cl != null && cl instanceof URLClassLoader)
			{
				cls.add((URLClassLoader) cl);
				ClassLoader clp = cl.getParent();
				if (cl == clp)
					break;
				cl = clp;
			}
			while (cls.size() > 0)
			{
				URLClassLoader cl0 = cls.pop();
				URL[] urls = cl0.getURLs();
				for (URL url : urls)
				{
					if (!rc.contains(url))
						rc.add(url);
				}
			}

			return rc;
		}
	}
}
