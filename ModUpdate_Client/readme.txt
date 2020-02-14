首先您需要打开ModUpdate_Client.ini文件进行一些配置
Server_IP=后填写服务器的IP地址，请不要直接填写域名
Server_Port=后填写您的服务器端mod更新程序设置的端口号，需要与服务器设置保持一致
Socket_Timeout=后填写您需要的timeout时间（毫秒），需要根据服务器带宽配置，如果您不知道如何估计值，保持默认的200即可

mods_files=后填写您的mods文件夹位置
比如说D:/MC/.minecraft/mods/
请注意您一定要在末尾加上/
其他的类同，可以设置config文件夹和shaderpacks文件夹（需要服务器支持）

之后您需要保证
ModUpdate_Client.jar
Update.bat
ModUpdate_Client.ini
这三个文件和.minecraft文件夹同目录

到此配置完成
您需要启动Update.bat来进行每一次的mod更新，切记不要直接运行ModUpdate_Client.jar
