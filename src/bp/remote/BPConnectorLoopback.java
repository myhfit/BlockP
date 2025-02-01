package bp.remote;

import java.util.function.Function;

import bp.BPCore;
import bp.data.BPCommand;
import bp.data.BPCommandResult;

public class BPConnectorLoopback implements BPConnector
{
//	private volatile WeakReference<Function<BPCommand, BPCommandResult>> m_handlerref;
//	private volatile Function<BPCommand, BPCommandResult> m_handler;

	public boolean connect()
	{
		return true;
//		WeakReference<Function<BPCommand, BPCommandResult>> handlerref = m_handlerref;
//		if (handlerref != null)
//		{
//			m_handler = handlerref.get();
//		}
//		return m_handler != null;
	}

	public void disconnect()
	{
//		m_handler = null;
	}

	public BPCommandResult call(BPCommand cmd)
	{
		return BPCore.callCommand(cmd);
//		return m_handler.apply(cmd);
	}

	public void bindHandler(Function<BPCommand, BPCommandResult> handler)
	{
//		m_handler = handler;
	}
}
