# Introduce
BlockP is a Collection of Tools for self-using. Here is the repo of core project. Target of the project is to save code for personal use in anytime(Especially with no other tool).
Sub projects will uploading after checking code.

# Sub Projects
|name|info|jar name|repo|requirement
|----|----|----|----|----|
|BlockPGUI|GUI base jar|bpgui.jar|not uploaded|Swing
|BlockPCLI|CLI base jar|bpcli.jar|not uploaded|
|BlockPCFs|Common Formats|bpcfs.jar|not uploaded|
|BlockPGUICFs|GUI for Common Formats|bpguicfs.jar|not uploaded|bpgui+bpcfs
|BlockPWebCFs|Web Common Formats|bpwebcfs.jar|not uploaded|bpcfs
|BlockPGUIWebCFs|GUI for Web Common Formats|bpguiwebcfs.jar|not uploaded|bpgui+bpcfs
|BlockPJDBC|JDBC|bpjdbc.jar|not uploaded|[jdbc drivers default load ojdbc8.jar+postgresql.jar+sqlite-jdbc.jar in "libs/"]
|BlockPGUIJDBC|GUI for JDBC|bpguijdbc.jar|not uploaded|bpgui+bpjdbc

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

