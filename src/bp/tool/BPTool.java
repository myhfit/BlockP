package bp.tool;

import bp.typeext.Nameable;

public interface BPTool extends Nameable
{
	String getName();

	void run();
}
