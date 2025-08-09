# Introduce
BlockP is a collection of tools for self-using. Here is the repo of core project.   
Target of the project is to save code for personal use in anytime, Especially when you don't have other tools.

# Sub Projects
|NAME|INFO|JAR NAME|REPO|REQUIREMENT
|----|----|----|----|----|
|BlockPGUI|GUI|bpgui.jar|[Link](https://github.com/myhfit/BlockPGUI)|Swing
|BlockPCLI|CLI|bpcli.jar|[Link](https://github.com/myhfit/BlockPCLI)|
|BlockPCFs|Common Formats|bpcfs.jar|[Link](https://github.com/myhfit/BlockPCFs)|
|BlockPGUICFs|GUI for Common Formats|bpguicfs.jar|[Link](https://github.com/myhfit/BlockPGUICFs)|bpgui+bpcfs
|BlockPWebCFs|Web Common Formats|bpwebcfs.jar|[Link](https://github.com/myhfit/BlockPWebCFs)|bpcfs
|BlockPGUIWebCFs|GUI for Web Common Formats|bpguiwebcfs.jar|[Link](https://github.com/myhfit/BlockPGUIWebCFs)|bpgui+bpwebcfs
|BlockPNotes|Notes|bpnotes.jar|[Link](https://github.com/myhfit/BlockPNotes)|bpwebcfs+commonmark+commonmark-ext-gfm-tables
|BlockPGUINotes|GUI for Notes|bpguinotes.jar|[Link](https://github.com/myhfit/BlockGUIPNotes)|bpgui+bpnotes
|BlockPJDBC|JDBC|bpjdbc.jar|[Link](https://github.com/myhfit/BlockPJDBC)|
|BlockPGUIJDBC|GUI for JDBC|bpguijdbc.jar|[Link](https://github.com/myhfit/BlockPGUIJDBC)|bpgui+bpjdbc
|BlockPAria2GUI|Aria2C GUI|bparia2gui.jar|[Link](https://github.com/myhfit/BlockPAria2GUI)|bpguiwebcfs

# Requirement
JRE>=8, windows/linux with basic tested, x86 JRE also can be used and has better performance in simple use.

Some sub project need higher version or need jdk

# How to use
Build core project and other sub projects, put main jar to work dir, then put other jars to "exts/", put dependency jars to "libs/"  

Example
![图片](https://github.com/user-attachments/assets/afead8ee-6fc5-47ca-9647-577fdd68e234)

Then run launcher class in bpgui.jar or bpcli.jar. 

Example:BPGUILauncher, with GUI to set workspace path and extensions(extension jars in "exts/" and dependency jars in "libs/")
> %JAVA_HOME%\bin\java -cp bp.jar;bpgui.jar bp.BPGUILauncher

Example:BPGUIMain, You need set full classpath
> %JAVA_HOME%\bin\java -cp BlockP\bin;BlockPGUI\bin bp.BPGUIMain [workspace path] [other args]

