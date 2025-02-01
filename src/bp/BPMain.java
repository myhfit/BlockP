package bp;

import bp.config.FormatAssocs;
import bp.env.BPEnvs;
import bp.tool.BPToolManager;
import bp.util.CommandLineArgs;

public class BPMain
{
    public final static void main(String[] args)
    {
    	CommandLineArgs cliargs=new CommandLineArgs(args);
    	BPCore.setCommandLineArgs(cliargs);
		BPCore.registerConfig(new BPToolManager());
		BPCore.registerConfig(new FormatAssocs());
		BPCore.registerConfig(new BPEnvs());
		BPCore.start(cliargs.contextpath);
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				BPCore.stop();
			}
		});
    }
}