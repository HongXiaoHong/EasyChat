package com.hong.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class NetWork {

	MsgCallBack msg;
	//使用collections的同步方法将集合进行同步
	Map<String, Socket> userMap = Collections.synchronizedMap(
									new HashMap<String, Socket>());
	public NetWork(MsgCallBack msg){
		this.msg = msg;
	}
	//开启服务器
	public void startServer(final int port){
		//开启服务器
		
		(new Thread(){
			public void run(){
				ServerSocket server = null;
				try {
					server = new ServerSocket(port);
					msg.onCreateServer(true);
					
					while(true){//服务器不断接受客户端的请求
						
						Socket socket = server.accept();
						String ip = socket.getInetAddress().getHostAddress();
						System.out.println("[NETWORK]收到一个客户端："+ip);
						msg.onAccepted();
						proccess(socket);
					}
					
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.onCreateServer(false);
					e.printStackTrace();
					System.out.println("在服务器中的启动服务器收到的异常信息："+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}finally{
					
				}
			}
		}).start();
		
	}
	//
	public String startConnect(final String ip, final int port){
		
		Callable<String> task = new Callable<String>(){

			@Override
			public String call() throws Exception {
				String socketId = null;
				// TODO Auto-generated method stub
				Socket socket = null;
				try {
					socket = new Socket(ip,port);
					msg.OnConntextEvent(true);
					socketId = proccess(socket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.OnConntextEvent(false);
					e.printStackTrace();
				}finally{
					
				}
				
				return socketId;
			}
			
		};
		
		FutureTask<String> ft = new FutureTask<String>(task);
		Thread th = new Thread(ft);
		th.start();
		
		String socketId = null;
		try {
			socketId = ft.get();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return socketId;
	}
	
	public String proccess(Socket socket){
		/*
		 * 
		 */
		
			//String socketId = UUID.randomUUID().toString();
		String socketId = null;
		try{
			
			socketId = String.valueOf((int)(Math.random()*10000));
			
			System.out.println("【network】  socketId = "+socketId);
			userMap.put(socketId,socket);
			read(socketId);
			return socketId;
		}catch (Exception e){
			throw new RuntimeException(socketId);
		}
		
	}
	
	boolean read(final String socketId){
		(new Thread(){
			public void run(){
				int count = 0;
				byte[] buffer = new byte[1024];
				try {
					Socket socket = userMap.get( socketId );
					InputStream is = socket.getInputStream();
					while(true){
						count = is.read(buffer);
						if(count > 0){
							msg.onReceived(buffer,count,socketId);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					msg.onReveiveFailed(socketId);
				}
			}
		}).start();
		
		return false;
	}
	
	public void send(String socketId, String content){
		System.out.println("content"+content);
		Socket socket = userMap.get(socketId);
		if (socket!=null){
			try {
				OutputStream out = socket.getOutputStream();
				out.write(content.getBytes());
				
			} 
			catch (IOException e) {e.printStackTrace();}
		} else{
			System.out.println("【NETWORK】找不到相应的socket");
		}
	}
	
	public MsgCallBack callBack(){
		return msg;
	}
	
	public InetSocketAddress getAddress(String socketId){
		Socket socket = userMap.get(socketId);
		InetSocketAddress isa = null;
		if (socket!=null){
			isa = new InetSocketAddress (
					socket.getInetAddress(), socket.getPort());
		}
		return isa;
	}
}
