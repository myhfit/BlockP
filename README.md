# Introduce
BlockP is a Collection of Tools for self-using. Here is the repo of core project.
Sub projects will uploading after checking code.

# Requirement
JRE>=8
Some sub project need higher version or jdk

# How to use
Build core project and other sub projects, put main jar on base path, then put other jars to "exts/", put dependency jars to "libs/"  

Example
![图片](https://github.com/user-attachments/assets/afead8ee-6fc5-47ca-9647-577fdd68e234)

Then run launcher class in bpgui.jar or bpcli.jar. 

Example:BPGUILauncher, with GUI to set context path and extensions(extension jars in "exts/" and dependency jars in "libs/")
> %JAVA_HOME%\bin\java -cp bp.jar;bpgui.jar bp.BPGUILauncher

Example:BPGUIMain, You need set full classpath
> %JAVA_HOME%\bin\java -cp BlockP\bin;BlockPGUI\bin bp.BPGUIMain

