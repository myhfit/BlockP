package bp.scan.state;

import java.util.ArrayList;
import java.util.List;

import bp.machine.BPStateMachine.BPState;
import bp.machine.BPStateMachineSeq.BPStateBase;

public abstract class BPCLIArgsState extends BPStateBase<Character, BPCLIArgsState.BPCLIArgsContext>
{
	protected BPCLIArgsState m_parent;

	public void setParent(BPCLIArgsState par)
	{
		m_parent = par;
	}

	public static class BPCLIArgsState0 extends BPCLIArgsState
	{
		@SuppressWarnings("unchecked")
		public <T extends BPState<Character, BPCLIArgsContext>> T input(Character e, BPCLIArgsContext context)
		{
			char c = e;
			if (c != '"')
			{
				if (c != ' ')
				{
					context.sb.append(c);
				}
				else
				{
					String str = context.sb.toString();
					if (str.length() > 0)
					{
						context.result.add(str);
						context.sb.setLength(0);
					}
				}
				pos++;
				return (T) this;
			}
			else
			{
				context.sb.setLength(0);
				BPCLIArgsState1 s1 = new BPCLIArgsState1();
				s1.setParent(this);
				s1.pos = pos + 1;
				return (T) s1;
			}
		}

		public void end(BPCLIArgsContext context)
		{
			String str = context.sb.toString();
			if (str.length() > 0)
			{
				context.result.add(str);
				context.sb.setLength(0);
			}
		}
	}

	public static class BPCLIArgsState1 extends BPCLIArgsState
	{
		@SuppressWarnings("unchecked")
		public <T extends BPState<Character, BPCLIArgsContext>> T input(Character e, BPCLIArgsContext context)
		{
			char c = e;
			if (c != '"')
			{
				context.sb.append(c);
				pos++;
				return (T) this;
			}
			else
			{
				context.result.add(context.sb.toString());
				context.sb.setLength(0);
				m_parent.pos = pos + 1;
				return (T) m_parent;
			}
		}

		public void end(BPCLIArgsContext context)
		{
		}
	}

	public static class BPCLIArgsContext
	{
		public List<String> result = new ArrayList<>();
		public StringBuilder sb = new StringBuilder();
	}
}
