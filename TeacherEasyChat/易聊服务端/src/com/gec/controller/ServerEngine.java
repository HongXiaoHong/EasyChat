package com.gec.controller;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gec.model.User;
import com.gec.utils.CmdParser;
import com.gec.utils.Command;
import com.gec.utils.MsgCallBack;
import com.gec.utils.NetWork;
import com.gec.view.ServerFrame;

public class ServerEngine implements MsgCallBack {

    //[Member] 设置在线用户群  [凡是登陆成功的用户, 都加入到此集合]
    Map<String, User> onLineUsers = Collections.synchronizedMap(
            new LinkedHashMap<String, User>());
    UserController controller = new UserController();
    NetWork work = new NetWork(this);
    ServerFrame serFrame;

    public void startGame() {
        work.startServer(8085);
        serFrame = new ServerFrame(8085);
    }

    @Override
    public void onCreatedServer(boolean ret) {
        String retStr = (ret) ? "成功" : "失败";
        prt("创建服务器: " + retStr);
    }

    @Override
    public void onAccepted() {
    }

    @Override
    public void onConnectEvent(boolean ret) {
    }

    @Override
    public void onReceived(byte[] buff, int size, String socketId) {
        String line = new String(buff, 0, size);
        prt("收到消息 [" + socketId + "] : " + line);

        //[1] 解析客户端发来的指令。
        //    发来的指令有可能是如下的情况:
        //    (a) login    [ 登陆操作 ]
        //    (b) register [ 注册操作 ]
        //    (c) getList  [ 获取在线用户清单 ]
        //    (d) send     [ 发送消息 ]
        Command cmd = CmdParser.parseClientCommand(line);
        Command retCmd = null;
        User cmdUser = null;
        boolean sendUsers = false;
        switch (cmd.op) {
            case "login":
                //[1] 将 cmd.data 转化为 User 对象。
                cmdUser = CmdParser.makeUser(cmd.data);
                //[2] 将  SocketId 设置到 User 对象中。
                cmdUser.setSocketId(socketId);
                //[3] 调用控制器层方法, 做 login 的操作。
                retCmd = controller.login(cmdUser);
                //[4] 如果登陆成功, 将 User 放入 onLineUsers 中。
                if (retCmd.result.equals("yes")) {
                    InetSocketAddress isa = work.getAddress(socketId);
                    cmdUser.setIpAndPort(isa);    //[PS] 设置用户的 IP 与 端口信息 ..
                    onLineUsers.put(socketId, cmdUser);
                    serFrame.addUser(cmdUser);    //[PS] 显示到表格上 ..
                }
                //[5] 设置 sendUsers 标志位为真。。
                sendUsers = true;
                break;
            case "register":
                //[1] 将 Map 转化为 User 对象。
                cmdUser = CmdParser.makeUser(cmd.data);
                //[2] 调用控制器层方法, 做注册的操作。
                retCmd = controller.register(cmdUser);
                break;
            case "send":
                retCmd = new Command("message", null);
                String message = cmd.data.get("msg");  //message == "你好吗"
                //[2] 拼接消息指令: {op:message,content:{msg:你好吗,from:xxx}}
                retCmd.line = "{op:message,content:{msg:" + message
                        + ",from:" + socketId + "}}";
                socketId = cmd.data.get("target");   //将 socketId 改为 对方的 SocketId
                break;
        }
        work.send(socketId, retCmd.line);  //[1] 把消息返回给客户端
        if (sendUsers) {
            sendUserToAll();     //[2] 发送所有用户清单到客户端 ..
        }
    }

    private void sendUserToAll() {
        //[1] line : 所有用户的信息。
        String line = CmdParser.makeUserList(onLineUsers);
        //[2] 发送给所有用户。
        for (String socketId : onLineUsers.keySet()) {
            work.send(socketId, line);
        }
    }

    @Override
    public void onReceivedFailed() {
    }

    public void prt(Object obj) {
        System.out.println("[SERVER] " + obj);
    }

}

