package com.hong.controller;

import com.hong.model.User;
import com.hong.utils.CmdParser;
import com.hong.utils.Command;
import com.hong.utils.MsgCallBack;
import com.hong.utils.NetWork;
import com.hong.view.ServerFrame;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ServerEnigine implements MsgCallBack {

    NetWork netWork = new NetWork(this);
    Map<String, User> onlineUsers = Collections.synchronizedMap(
            new LinkedHashMap<String, User>());
    UserController controller = new UserController();
    ServerFrame serverFrame;

    public void startGame() {
        netWork.startServer(1090);
        serverFrame = new ServerFrame();
		/*Scanner sc = new Scanner(System.in);
		while(true){
			prt("请输入SOCKETID与消息,用逗号分隔    ：");
			
			String line = sc.next();
			String[] arr = line.split(",");
			netWork.send(arr[0], arr[1]);
		}*/
    }

    @Override
    public void onCreateServer(boolean flag) {
        // 打印创建服务器是否成功
        String retStr = flag ? "成功" : "失败";
        prt("创建服务器：" + retStr);
    }

    @Override
    public void onAccepted() {
        // 打印接收到一个客户端
        prt("接收到一个客户端");
    }

    //接受一个客户端
    @Override
    @Deprecated
    public void OnConntextEvent(boolean flag) {
    }

    //接受一个数据，并且通过协议对字符串进行解析从而进行不同的操作
    @Override
    public void onReceived(byte[] buff, int count, String socketId) {
        // 核心业务方法
        String line = new String(buff, 0, count);
        prt("收到消息来自“" + socketId + "”：" + line);
        //解析来自客户端发来的信息，
        //可能有如下的情形：
        // login
        // regist
        // getList
        // send[发送消息]
        boolean ret = false;//用于判断是否登录，如果是登录就将在线用户列表传递给客户端
        //解析字符串返回一个command对象，解析过程中会将用户的信息放置到data的map映射中
        Command command = CmdParser.parseClientCommand(line);
        Command retCom = null;
        Map<String, String> data = null;
        User user = null;
        switch (command.op) {
            case "login":
                data = command.data;
                user = CmdParser.makeUser(data);

                //不允许用户重复登录
                Set<String> keySet = onlineUsers.keySet();
                for (String soc : keySet) {
                    User u = onlineUsers.get(soc);
                    if (u.getName().equals(user.getName())) {
                        return;
                    }
                }


                //设置user的socketId
                user.setSocketId(socketId);
                retCom = controller.login(user);
                if ("yes".equals(retCom.result)) {
                    //从network中的到用户的端口和IP信息
                    //并将其设置到user
                    InetSocketAddress isa = netWork.getAddress(socketId);
                    user.setIpAndPort(isa);
                    //将socketId和用户放入到在线的映射中
                    onlineUsers.put(socketId, user);
                    serverFrame.addUser(user);
                    ret = true;
                }
                break;
            case "register":
                data = command.data;
                user = CmdParser.makeUser(data);
                retCom = controller.regist(user);
                break;
            case "send":
                retCom = new Command("message", null);
                data = command.data;
                System.out.println("服务器的data" + data);
                System.out.println("服务器的command对象的data" + command.data);
                retCom.line = "{op:message,content:{msg:"
                        + data.get("msg") + ",from:" + socketId + "}}";
                socketId = data.get("target");
                break;
        }
        System.out.println("command.line:" + retCom.line);
        netWork.send(socketId, retCom.line);//把消息返回给客户端
        if (ret) {
            sendAllUser();
        }
    }

    private void sendAllUser() {

        String allUsers = CmdParser.makeUserList(onlineUsers);
        for (String socketId : onlineUsers.keySet()) {
            netWork.send(socketId, allUsers);
        }
    }

    @Override
    public void onReveiveFailed(String socketId) {

        serverFrame.removeUser(onlineUsers.get(socketId));
        onlineUsers.remove(socketId);
        sendAllUser();

    }

    public void prt(Object obj) {
        System.out.println("【服务器】" + obj);
    }

    @Override
    public void onReveived(byte[] buff) {


    }

}
