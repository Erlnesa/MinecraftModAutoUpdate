package com.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.dtools.ini.BasicIniFile;
import org.dtools.ini.IniFile;
import org.dtools.ini.IniFileReader;
import org.dtools.ini.IniSection;

class Send_Mod implements Runnable{
    //mods文件夹位置
    //public static String mods_files = "/home/minecraft/Minecraft-1.12.2/mods/";
    //mod列表
    static List<String> mod_list = new ArrayList<>();
    public Socket socket = null;
    Send_Mod(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        try {
            //获取输入输出流
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            //等待请求
            String instruct_buff = "";
            byte[] instruct = new byte[1];
            while (!instruct_buff.endsWith("\r\n")) {
                in.read(instruct);
                String str = new String(instruct);
                instruct_buff += str;
            }
            boolean Is_files_ask_flag = true;
            if (instruct_buff.equals("mod_list\r\n") && ModUpdate_Server.mods_enable == 1) {
                Is_files_ask_flag = false;
                System.out.println("回传mod列表");
                //清空list
                mod_list.clear();
                //从新扫描list
                searchDir(ModUpdate_Server.mods_files);
                for (int list_index = 0; list_index < mod_list.size(); list_index++) {
                    //System.out.println(mod_list.get(list_index));
                    out.write((mod_list.get(list_index) + "\n").getBytes());
                }
                out.write(("\r\n").getBytes());
            }
            if(instruct_buff.equals("config_list\r\n") && ModUpdate_Server.config_enable == 1){
                Is_files_ask_flag = false;
                System.out.println("回传config列表");
                //清空list
                mod_list.clear();
                //从新扫描list
                searchDir(ModUpdate_Server.config_files);
                for (int list_index = 0; list_index < mod_list.size(); list_index++) {
                    //System.out.println(mod_list.get(list_index));
                    out.write((mod_list.get(list_index) + "\n").getBytes());
                }
                out.write(("\r\n").getBytes());
            }
            if(instruct_buff.equals("shaderpacks_list\r\n") && ModUpdate_Server.shaderpacks_enable == 1){
                Is_files_ask_flag = false;
                System.out.println("回传shaderpacks列表");
                //清空list
                mod_list.clear();
                //从新扫描list
                searchDir(ModUpdate_Server.shaderpacks_files);
                for (int list_index = 0; list_index < mod_list.size(); list_index++) {
                    //System.out.println(mod_list.get(list_index));
                    out.write((mod_list.get(list_index) + "\n").getBytes());
                }
                out.write(("\r\n").getBytes());
            }
            if(Is_files_ask_flag){
                System.out.println(instruct_buff.split("\r")[0]);
                //发送文件
                File file = new File(instruct_buff.split("\r")[0]);
                InputStream file_in = new FileInputStream(file);
                byte[] data = new byte[1024];
                int i = 0;
                while((i = file_in.read(data)) != -1) {
                    out.write(data, 0, i);
                }
                file_in.close();
            }
            //释放资源
            in.close();
            out.close();
            socket.close();
        }catch (IOException e) {
            System.err.println(e);
        }
    }
    public static void searchDir(String dirPath){
        File file = new File(dirPath);
        File[] files = file.listFiles();
        if(files == null){
            //System.out.println(file);
            mod_list.add(dirPath.toString());
        }else{
            for (int i = 0; i < files.length; i++){
                searchDir(files[i].getAbsolutePath());
            }
        }
    }
}

public class ModUpdate_Server {
	//运行端口号
	public static int Server_Port = 12345;
	//socket的timeout，单位ms
	public static int Socket_timeout = 200;
	//mods文件夹路径和使能
	public static String mods_files = "";
	public static int mods_enable = 0;
	//config文件夹路径和使能
	public static String config_files = "";
	public static int config_enable = 0;
	//shaderpacks文件夹路径和使能
	public static String shaderpacks_files = "";
	public static int shaderpacks_enable = 0;
	
	public static int read_ini(){
		IniFile iniFile=new BasicIniFile();
		File file=new File("ModUpdate_Server.ini");
		IniFileReader rad=new IniFileReader(iniFile, file);
		try {
			rad.read();
			IniSection iniSection=iniFile.getSection(0);
			//读端口
			Server_Port = Integer.parseInt(iniSection.getItem("Server_Port").getValue());
			//读timeout
			Socket_timeout = Integer.parseInt(iniSection.getItem("Socket_Timeout").getValue());
			//读取mods文件夹绝对路径和使能
			mods_files = iniSection.getItem("mods_files").getValue();
			mods_enable = Integer.parseInt(iniSection.getItem("mods_enable").getValue());
			//读取config文件夹绝对路径和使能
			config_files = iniSection.getItem("config_files").getValue();
			config_enable = Integer.parseInt(iniSection.getItem("config_enable").getValue());
			//读取shaderpacks_files文件夹绝对路径和使能
			shaderpacks_files = iniSection.getItem("shaderpacks_files").getValue();
			shaderpacks_enable = Integer.parseInt(iniSection.getItem("shaderpacks_enable").getValue());
		} catch (IOException e) {
			return -1;
		}
		return 1;
	}
	
	
    public static void main(String[] args) throws IOException {
    	//加载ini设置
    	read_ini();

    	//System.out.println(mods_files);
        //Send_Mod.searchDir(mods_files);

    	//System.exit(-1);
    	
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress(Server_Port));
        System.out.println("等待客户端请求");
        //等待客户端建立连接
        while (true) {
            Socket socket = ss.accept();
            //设置timeout
            socket.setSoTimeout(Socket_timeout);
            new Thread(new Send_Mod(socket)).start();
            System.out.println("等待客户端请求");
        }
        
    }
}