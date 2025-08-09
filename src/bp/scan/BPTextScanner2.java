package bp.scan;

import bp.machine.BPStateMachineSeq;

public class BPTextScanner2<C> extends BPStateMachineSeq<Character, C, char[]>
{
	protected Character getElement(char[] src, long pos)
	{
		int p = (int) pos;
		if (p >= src.length)
			return null;
		return src[p];
	}
}