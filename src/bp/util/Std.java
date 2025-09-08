package bp.util;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Std
{
	private static boolean s_showdebug = false;
	private static boolean s_showinfo = true;
	private static boolean s_showerror = true;

	public final static int STDMODE_DEBUG = 2;
	public final static int STDMODE_INFO = 1;
	public final static int STDMODE_ERR = 0;

	private static volatile Consumer<String> s_info_u;
	private static volatile Consumer<String> s_err_u;
	private static volatile Function<String, Boolean> s_confirm_u;
	private static volatile Function<String, String> s_prompt_u;
	private static volatile Function<String[], String> s_select_u;
	private static volatile Consumer<Object> s_showdata_u;

	public final static void setStdMode(int stdmode)
	{
		s_showerror = stdmode >= STDMODE_ERR;
		s_showinfo = stdmode >= STDMODE_INFO;
		s_showdebug = stdmode >= STDMODE_DEBUG;
	}

	public final static int getStdMode()
	{
		return s_showdebug ? 2 : (s_showinfo ? 1 : 0);
	}

	public final static void info(String str)
	{
		if (s_showinfo)
			System.out.println(str);
	}

	public final static void debug(String str)
	{
		if (s_showdebug)
			System.out.println(str);
	}

	public final static void err(Throwable err)
	{
		if (s_showerror)
		{
			System.err.println(err);
			StackTraceElement[] stes = err.getStackTrace();
			if (stes != null && stes.length > 0)
			{
				for (StackTraceElement ste : stes)
					System.err.println("\t" + ste);
			}
		}
	}

	public final static void err(String str)
	{
		if (s_showerror)
			System.err.println(str);
	}

	public final static void setupUI(Consumer<String> infou, Consumer<String> erru, Function<String, Boolean> confirmu, Function<String, String> promptu, Function<String[], String> selectu)
	{
		s_info_u = infou;
		s_err_u = erru;
		s_confirm_u = confirmu;
		s_prompt_u = promptu;
		s_select_u = selectu;
	}

	@SuppressWarnings("unchecked")
	public final static void setupAdv(Map<String, Object> m)
	{
		s_showdata_u = (Consumer<Object>) m.get("showdata");
	}

	public final static void info_user(String str)
	{
		Consumer<String> infou = s_info_u;
		if (infou != null)
			infou.accept(str);
		else
			info(str);
	}

	public final static void err_user(String str)
	{
		Consumer<String> erru = s_err_u;
		if (erru != null)
			erru.accept(str);
		else
			err(str);
	}

	public final static boolean confirm(String str)
	{
		Function<String, Boolean> confirmu = s_confirm_u;
		if (confirmu != null)
			return confirmu.apply(str);
		throw new NoSuchMethodError();
	}

	public final static String prompt(String str)
	{
		Function<String, String> promptu = s_prompt_u;
		if (promptu != null)
			return promptu.apply(str);
		throw new NoSuchMethodError();
	}

	public final static String select(String[] strs)
	{
		Function<String[], String> selectu = s_select_u;
		if (selectu != null)
			return selectu.apply(strs);
		throw new NoSuchMethodError();
	}

	public final static void showData(Object data)
	{
		Consumer<Object> showdatau = s_showdata_u;
		if (showdatau != null)
			showdatau.accept(data);
		else
			info(ObjUtil.toString(data));
	}
}
