package bp.parser;

import java.util.ArrayDeque;
import java.util.Deque;

public class BPParserJSON implements BPParserText<BPParserJSON.BPParserTreeNodeJSON>
{
	public BPParserTreeNodeJSON parse(String source)
	{
		BPParserTreeNodeJSON node = null;
		// BPParserScannerJSON scanner = new BPParserScannerJSON();
		// node = scanner.scan();
		return node;
	}

	public final static class BPParserTreeNodeJSON extends BPParserTreeNodeBase<BPJSONObject>
	{

	}

	public final static class BPParserScannerJSON extends BPParserScannerText
	{
		public final static int RAW = 0;
		public final static int INMAP = 1;
		public final static int INARRAY = 2;
		public final static int INSTR = 3;
		public final static int INBACKSLASH = 4;

		public BPParserTreeNodeJSON startScan()
		{
			BPParserTreeNodeJSON root = new BPParserTreeNodeJSON();

			Deque<Integer> chx = new ArrayDeque<Integer>();
			Deque<Character> posx = new ArrayDeque<Character>();

			scan(0, root, chx, posx);
			return root;
		}

		private void scan(int pos, BPParserTreeNodeJSON node, Deque<Integer> chx, Deque<Character> posx)
		{
			char c = str.charAt(pos);
			while (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\b' || c == '\f')
				c = str.charAt(++pos);
			switch (c)
			{
				case '{':
				{
					break;
				}
			}
		}

	}

	public final static class BPJSONObject
	{
		public BPJSONObjectType otype;
		public BPParserScannerText scanner;
		public int pos0;
		public int pos1;
	}

	public static enum BPJSONObjectType
	{
		OBJECT, ARRAY, STRING, NUMBER, TRUE, FALSE, NULL
	}
}
