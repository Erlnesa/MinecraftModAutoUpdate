package com.main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.dtools.ini.*;

public class ModUpdate_Client {
	
	/*更新模式
	 * 1：mod模式
	 * 2：config模式
	 * 3：shaderpacks模式
	 */
	public static int update_mode = 1;
	
	//服务器IP
	public static String Server_IP = "";
	//运行端口号
	public static int Server_Port = 12345;
	//socket的timeout，单位ms
	public static int Socket_timeout = 200;
	//mods文件夹路径
	public static String mods_files = "";
	//config文件夹路径
	public static String config_files = "";
	//shaderpacks文件夹路径
	public static String shaderpacks_files = "";
	//mod服务器路径列表
	static List<String> mod_add_list = new ArrayList<>();
	//mod名称
	static List<String> mod_name_list = new ArrayList<>();
	/*
	 * 读取ini并配置，返回1正常读取，其他返回值读取异常
	 */
	public static int read_ini(){
		IniFile iniFile=new BasicIniFile();
		File file=new File("ModUpdate_Client.ini");
		IniFileReader rad=new IniFileReader(iniFile, file);
		try {
			rad.read();
			IniSection iniSection=iniFile.getSection(0);
			//读取IP
			Server_IP = iniSection.getItem("Server_IP").getValue();
			//读端口
			Server_Port = Integer.parseInt(iniSection.getItem("Server_Port").getValue());
			//读timeout
			Socket_timeout = Integer.parseInt(iniSection.getItem("Socket_Timeout").getValue());
			//读取mods文件夹绝对路径
			mods_files = iniSection.getItem("mods_files").getValue();
			//读取config文件夹绝对路径
			config_files = iniSection.getItem("config_files").getValue();
			//读取shaderpacks_files文件夹绝对路径
			shaderpacks_files = iniSection.getItem("shaderpacks_files").getValue();
		} catch (IOException e) {
			return -1;
		}
		return 1;
	}
	/*
	 * 清空文件夹
	 */
    public static void deleteDir(String dirPath){
		File file = new File(dirPath);
		File[] files = file.listFiles();
		if(files == null){
			//System.out.println(file);
			file.delete();
		}else{
			for (int i = 0; i < files.length; i++){
				deleteDir(files[i].getAbsolutePath());
			}
			//file.delete();
		}
	}
    
    /*public static void scanDir(String dirPath){
		File file = new File(dirPath);
		File[] files = file.listFiles();
		if(files == null){
			System.out.println(file);
			//file.delete();
		}else{
			for (int i = 0; i < files.length; i++){
				scanDir(files[i].getAbsolutePath());
			}
		}
	}*/
    
    public static void newFile(String fileName) throws IOException{
        File testFile = new File(fileName);
        File fileParent = testFile.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }if (!testFile.exists()){
            testFile.createNewFile();
        }
    }
    
    public static void main(String[] args) throws Exception {
    	//配置ini
    	read_ini();
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("请输入您希望更新的项目代码，按回车键确认");
    	System.out.println("1------更新mods文件------------");
    	System.out.println("2------更新config文件----------");
    	System.out.println("3------更新shaderpacks文件-----");
    	System.out.print("-->");
    	update_mode = scanner.nextInt();
    	if(update_mode != 1 && update_mode != 2 && update_mode != 3){
    		//更新项目不合法，异常退出
    		System.exit(-1);
    	}
    	//对接服务器
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(Server_IP, Server_Port));
        socket.setSoTimeout(Socket_timeout);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        if(update_mode == 1){
        	//请求Mod列表
        	System.out.println("准备请求Mod列表");
            out.write(("mod_list\r\n").getBytes());
        }else if(update_mode == 2){
        	//请求config列表
        	System.out.println("准备请求config列表");
            out.write(("config_list\r\n").getBytes());
        }else if(update_mode == 3){
        	//请求shaderpacks列表
        	System.out.println("准备请求shaderpacks列表");
            out.write(("shaderpacks_list\r\n").getBytes());
        }
        //接受Mod列表
        String list = "";
        byte[] by1 = new byte[1];
        while(!list.endsWith("\r\n")) {
            in.read(by1);
            String str = new String(by1);
            list += str;
        }
        //缓存mod路径到本地
        String[] mods = list.split("\n");
        for(int i = 0; i <= mods.length-2;i++){
        	mod_add_list.add(mods[i]);
        	//mod_name_list.add(mods[i].split("/")[5]);
        	String[] list_buff = mods[i].split("/");
        	mod_name_list.add(list_buff[list_buff.length-1]);
        }
        System.out.println("列表获取完成");
        in.close();
        out.close();
        socket.close();
        /*
        //list查重
        System.out.println("输出add_list");
        for(int test_i = 0;test_i < mod_add_list.size() ; test_i ++){
        	System.out.println(mod_add_list.get(test_i));
        }
        System.out.println("add_list输出完毕");*/
        System.out.println("共计"+mod_name_list.size()+"个文件项目");
        //下载全部mod
        DownLoad_ALL_MOD();
    }
    /*
     * 下载全部Mod
     */
    public static void DownLoad_ALL_MOD() throws IOException{
    	//清空目标文件夹
    	if(update_mode == 1){
    		deleteDir(mods_files);
    	}else if(update_mode == 2){
    		deleteDir(config_files);
    	}else if(update_mode == 3){
    		deleteDir(shaderpacks_files);
    	}
        //下载全部mod
        for(int index = 0; index < mod_add_list.size() ; index ++){
	        //对接服务器
	        Socket socket = new Socket();
	        socket.connect(new InetSocketAddress(Server_IP, Server_Port));
	        OutputStream out = socket.getOutputStream();
	        InputStream in = socket.getInputStream();
	        //发送请求目标的文件地址
	    	out.write((mod_add_list.get(index)+"\r\n").getBytes());
	    	//创建输文件出流，指定文件输出地址
	    	String path = "";
	    	if(update_mode == 1){
	    		path = mods_files+mod_add_list.get(index).split("mods/")[1];
	    	}else if(update_mode == 2){
	    		path = config_files+mod_add_list.get(index).split("config/")[1];
	    	}else if(update_mode == 3){
	    		path = shaderpacks_files+mod_add_list.get(index).split("shaderpacks/")[1];
	    	}
	    	System.out.println("正在下载"+mod_add_list.size()+"个文件中的第"+(index+1)+"个文件项目");
	    	newFile(path);
	        OutputStream file_out = new FileOutputStream(path);
	        //接收数据
	        byte[] b = new byte[2024];
	        int n = in.read(b);
	        while (n != -1) {
	        	file_out.write(b, 0, n);    //写入指定地方
	            n = in.read(b);
	        }
	        //System.out.println("接受到来自"+socket.getInetAddress().getHostAddress()+"上传的文件"+mod_name_list.get(index));
	        System.out.println(mod_name_list.get(index)+"下载完成");
	        file_out.close();
	        
	        
	        in.close();
	        out.close();
	        socket.close();
        }
    }
    
    
}
