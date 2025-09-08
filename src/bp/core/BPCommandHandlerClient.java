package bp.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.client.BPClient;
import bp.client.BPClientFactory;
import bp.client.BPClientManager;
import bp.data.BPCommand;
import bp.data.BPCommandResult;
import bp.util.JSONUtil;
import bp.util.ObjUtil;

public class BPCommandHandlerClient extends BPCommandHandlerBase
{
	public final static String CN_CLIENT_USE = "client_use";
	public final static String CN_CLIENT_LIST = "client_list";
	public final static String CN_CLIENT_CALL = "client_call";

	public BPCommandHandlerClient()
	{
		m_cmdnames = ObjUtil.makeList(CN_CLIENT_LIST, CN_CLIENT_CALL);
	}

	public BPCommandResult call(BPCommand cmd)
	{
		String cmdname = cmd.name.toLowerCase();

		switch (cmdname)
		{
			case CN_CLIENT_USE:
			{
				return useClient(getPSStringArr(cmd.ps));
			}
			case CN_CLIENT_LIST:
			{
				return listClient(getPSStringArr(cmd.ps));
			}
			case CN_CLIENT_CALL:
			{
				return callClient(getPSStringArr(cmd.ps));
			}
		}
		return null;
	}

	protected BPCommandResult useClient(String[] ps)
	{
		return BPCommandResult.OK(null);
	}

	protected BPCommandResult callClient(String[] ps)
	{
		String clientname = ps[0];
		Map<String, Object> fps = ps.length > 1 ? JSONUtil.decode(ps[1]) : null;
		String actionname = ps.length > 2 ? ps[2] : null;
		List<Object> rps = new ArrayList<Object>();
		for (int i = 3; i < ps.length; i++)
		{
			rps.add(JSONUtil.decode(ps[i]));
		}
		BPClient fac = BPClientManager.get(clientname, fps);
		Object rc = fac.call(actionname, rps.toArray());
		return BPCommandResult.OK(rc);
	}

	protected BPCommandResult listClient(String[] ps)
	{
		StringBuilder sb = new StringBuilder();
		List<BPClientFactory> facs = BPClientManager.list();
		for (BPClientFactory fac : facs)
		{
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(fac.getName());
		}
		return BPCommandResult.OK(sb.toString());
	}

	public String getName()
	{
		return "Client";
	}
}
