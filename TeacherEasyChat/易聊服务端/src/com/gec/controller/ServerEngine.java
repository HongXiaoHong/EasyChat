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

    //[Member] ���������û�Ⱥ  [���ǵ�½�ɹ����û�, �����뵽�˼���]
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
        String retStr = (ret) ? "�ɹ�" : "ʧ��";
        prt("����������: " + retStr);
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
        prt("�յ���Ϣ [" + socketId + "] : " + line);

        //[1] �����ͻ��˷�����ָ�
        //    ������ָ���п��������µ����:
        //    (a) login    [ ��½���� ]
        //    (b) register [ ע����� ]
        //    (c) getList  [ ��ȡ�����û��嵥 ]
        //    (d) send     [ ������Ϣ ]
        Command cmd = CmdParser.parseClientCommand(line);
        Command retCmd = null;
        User cmdUser = null;
        boolean sendUsers = false;
        switch (cmd.op) {
            case "login":
                //[1] �� cmd.data ת��Ϊ User ����
                cmdUser = CmdParser.makeUser(cmd.data);
                //[2] ��  SocketId ���õ� User �����С�
                cmdUser.setSocketId(socketId);
                //[3] ���ÿ������㷽��, �� login �Ĳ�����
                retCmd = controller.login(cmdUser);
                //[4] �����½�ɹ�, �� User ���� onLineUsers �С�
                if (retCmd.result.equals("yes")) {
                    InetSocketAddress isa = work.getAddress(socketId);
                    cmdUser.setIpAndPort(isa);    //[PS] �����û��� IP �� �˿���Ϣ ..
                    onLineUsers.put(socketId, cmdUser);
                    serFrame.addUser(cmdUser);    //[PS] ��ʾ������� ..
                }
                //[5] ���� sendUsers ��־λΪ�档��
                sendUsers = true;
                break;
            case "register":
                //[1] �� Map ת��Ϊ User ����
                cmdUser = CmdParser.makeUser(cmd.data);
                //[2] ���ÿ������㷽��, ��ע��Ĳ�����
                retCmd = controller.register(cmdUser);
                break;
            case "send":
                retCmd = new Command("message", null);
                String message = cmd.data.get("msg");  //message == "�����"
                //[2] ƴ����Ϣָ��: {op:message,content:{msg:�����,from:xxx}}
                retCmd.line = "{op:message,content:{msg:" + message
                        + ",from:" + socketId + "}}";
                socketId = cmd.data.get("target");   //�� socketId ��Ϊ �Է��� SocketId
                break;
        }
        work.send(socketId, retCmd.line);  //[1] ����Ϣ���ظ��ͻ���
        if (sendUsers) {
            sendUserToAll();     //[2] ���������û��嵥���ͻ��� ..
        }
    }

    private void sendUserToAll() {
        //[1] line : �����û�����Ϣ��
        String line = CmdParser.makeUserList(onLineUsers);
        //[2] ���͸������û���
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

