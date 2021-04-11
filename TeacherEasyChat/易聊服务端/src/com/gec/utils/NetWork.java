package com.gec.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class NetWork {
    MsgCallBack callBack;
    Map<String, Socket> userMap = Collections.synchronizedMap(
            new HashMap<String, Socket>());

    public NetWork(MsgCallBack callBack) {
        this.callBack = callBack;
    }

    public void startServer(int _port) {
        Runnable task = new Runnable() {
            public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket(_port);
                    callBack.onCreatedServer(true);
                    while (true) {   //[�Ķ�1] ����ȥ�����ͻ��˵�����,  ��һ����һ����
                        Socket socket = server.accept();
                        String ip = socket.getInetAddress().getHostAddress();
                        System.out.println("[NetWork] �յ�һ���ͻ���: " + ip);
                        callBack.onAccepted();
                        proccess(socket);
                    }
                } catch (IOException e) {
                    callBack.onCreatedServer(false);
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
    }

    //[PS] �ͻ��˱����õ��Լ��� SocketId, ���� SocketId ���Դ� Map ��ȡ�� Socket��
    public String startConnect(String _ip, int _port) {
        Callable<String> task = new Callable<String>() {
            public String call() throws Exception {
                String socketId = null;
                Socket socket = null;
                try {
                    socket = new Socket(_ip, _port);
                    callBack.onConnectEvent(true);
                    socketId = proccess(socket);
                } catch (IOException e) {
                    callBack.onConnectEvent(false);
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
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return socketId;
    }

    private String proccess(Socket socket) {
        String socketId = null;
        //[1] ����һ�������: 10000  ���� ----> String
        int _ran = (int) (Math.random() * 10000);
        socketId = String.valueOf(_ran);

        System.out.println("[NetWork] socketId = " + socketId);
        //[2] ��  [key-socket] ��ֵ�Դ���  map ӳ�䵱�С�
        userMap.put(socketId, socket);

        //[3] ���� read() ���� ��
        read(socketId);
        return socketId;
    }

    private void read(String socketId) {
        Runnable task = new Runnable() {
            public void run() {
                byte[] buff = new byte[1024];
                int count = 0;
                try {
                    Socket socket = userMap.get(socketId);
                    InputStream is = socket.getInputStream();

                    //[1] ���ϵ�ȥ�� is ���е����� (ѭ��)��
                    while (true) {   //while û�д���, û���������ơ�(��Զȥ��)
                        count = is.read(buff);
                        //[2] ������������, ͨ��  callBack �ص���Ӧ�ķ�����
                        if (count > 0) {
                            callBack.onReceived(buff, count, socketId);
                        }
                    }
                } catch (IOException e) {
                    //[3] ���Է�, ����������, �����ϲ�Ӧ�á�
                    callBack.onReceivedFailed();
                }
            }
        };
        //[3] ��һ���߳��������ϵ����� (�����ڲ���)��
        Thread th = new Thread(task);
        th.start();
    }

    public void send(String socketId, String content) {
        //[1] ͨ�� os ������   content �е����ݡ�
        Socket socket = userMap.get(socketId);
        if (socket != null) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(content.getBytes());
            } catch (SocketException e) {
                System.out.println("[NetWork] ������������ ..");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("[NetWork] �Ҳ�����Ӧ�� Socket �׽��֡�");
        }
    }

    public InetSocketAddress getAddress(String socketId) {
        Socket socket = userMap.get(socketId);
        if (socket != null) {
            InetSocketAddress isa = new InetSocketAddress(
                    socket.getInetAddress(), socket.getPort());
            return isa;
        }
        return null;
    }

}
