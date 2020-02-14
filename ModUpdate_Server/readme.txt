可以将ModUpdate_Server.jar与ModUpdate_Server.ini放在服务器的任意位置
但请注意保持这两个文件在相同的目录下

然后记事本或者vim或者其他任意文本编辑软件打开ModUpdate_Server.ini

Server_Port=后填写您希望服务器端软件运行的端口，注意请与客户端保持一致

Socket_Timeout=后填写您希望的timeout延时，单位是毫秒，取决于您的服务器带宽，如果您不知道如何填写可以保持默认的200不变，这应该适合绝大多数服务器带宽

mods_files=后填写您服务器的mods文件夹的绝对路径，例如
在ubuntu下您可以填写/home/mc/1-12/mods/
在windows下您可以填写D:/mc/mods/
其他类同

mods_enable=后填写1或0
如果您希望用户从服务器更新mods列表这里应该填1
其他类同

在配置完成后，您需要确保服务器的防火墙能放行您设置的端口，协议是TCP
如果您不知道该如何设置，可以将您设置的端口号告诉服务器运营商

最后您需要利用命令行启动Mod_Send.jar
在Windows环境下您需要按下win+R输入cmd然后回车，输入以下两行命令
cd 放置Mod_Send.jar的文件夹绝对路径
java -jar Mod_Send.jar
在linux环境下您需要启动终端，输入的代码同上
放置Mod_Send.jar文件夹的绝对路径例如/home/mc/1-12/


最后，作为服务器管理者，如果您需要在服务器端更新您的mod，只需要正常将mod文件放入mods文件夹即可，该软件会自动更新无需重新启动
如果您不放心，也可以在每次更新服务器端mod之后，重新启动本程序