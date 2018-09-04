package com.gec.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class NetWork {	
	MsgCallBack callBack;
	Map<String,Socket> userMap = Collections.synchronizedMap( 
									new HashMap<String,Socket>() );
	public NetWork(MsgCallBack callBack){
		this.callBack = callBack;
	}
	public void startServer(int _port){
		Runnable task = new Runnable(){
			public void run(){
				ServerSocket server = null;
				try {
					server = new ServerSocket( _port );
					callBack.onCreatedServer( true );
					while( true ){   //[改动1] 不断去侦听客户端的连接,  遇一个拿一个。
						Socket socket = server.accept();
						String ip = socket.getInetAddress().getHostAddress();
						System.out.println( "[NetWork] 收到一个客户端: "+ ip );
						callBack.onAccepted();	
						proccess( socket );
					}
				}catch(IOException e){
					callBack.onCreatedServer( false );
				}
			}
		};
		Thread th = new Thread(task);
		th.start();
	}
	
	//[PS] 客户端必须拿到自己的 SocketId, 有了 SocketId 可以从 Map 中取出 Socket。
	public String startConnect(String _ip, int _port){
		Callable<String> task = new Callable<String>(){
			public String call() throws Exception {
				String socketId = null;
				Socket socket = null;
				try{
					socket = new Socket(_ip,_port);
					callBack.onConnectEvent( true );
					socketId = proccess( socket );
				}catch(IOException e){
					callBack.onConnectEvent( false );
				}
				return socketId;
			}
		};
		FutureTask<String> ft = new FutureTask<String>(task);
		Thread th = new Thread( ft );
		th.start();

		String socketId = null;
		try {
			socketId = ft.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return socketId;
	}

	private String proccess(Socket socket){
		String socketId = null;
		//[1] 产生一个随机数: 10000  以内 ----> String
		int _ran = (int)(Math.random()*10000);
		socketId = String.valueOf( _ran );
		
		System.out.println("[NetWork] socketId = "+ socketId );
		//[2] 把  [key-socket] 键值对存入  map 映射当中。
		userMap.put( socketId, socket );
		
		//[3] 调用 read() 方法 。
		read( socketId );
		return socketId;
	}
	private void read( String socketId ){
		Runnable task = new Runnable(){
			public void run(){
				byte[] buff = new byte[1024];
				int count = 0;
				try{
					Socket socket = userMap.get( socketId );
					InputStream is = socket.getInputStream();
					
					//[1] 不断的去读 is 流中的数据 (循环)。
					while( true ){   //while 没有次数, 没有数量限制。(永远去做)
						count = is.read( buff );
						//[2] 当读到有数据, 通过  callBack 回调相应的方法。
						if( count>0 ){
							callBack.onReceived( buff, count, socketId );
						}
					}
				}catch(IOException e){
					//[3] 当对方, 本方断线了, 告诉上层应用。
					callBack.onReceivedFailed();
				}
			}
		};
		//[3] 开一个线程来做以上的事情 (匿名内部类)。
		Thread th = new Thread( task );
		th.start();
	}

	public void send(String socketId, String content){
		//[1] 通过 os 来发送   content 中的数据。
		Socket socket = userMap.get( socketId );
		if( socket!=null ){
			try {
				OutputStream os = socket.getOutputStream();
				os.write( content.getBytes() );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("[NetWork] 找不到对应的 Socket 套接字。");
		}
	}
	
	public void copySocket(String oldId, String newId){
		Socket socket = userMap.get( oldId );
		if( socket!=null ){
			userMap.put( newId, socket );
		}
	}

}
