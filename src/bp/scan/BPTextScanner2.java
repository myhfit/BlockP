package bp.scan;

import bp.machine.BPStateMachineSeq;

public class BPTextScanner2<C> extends BPStateMachineSeq<Character, C, char[]>
{
	public Character getElement(char[] src, long pos)
	{
		int p = (int) pos;
		if (p >= src.length)
			return null;
		return src[p];
	}
}