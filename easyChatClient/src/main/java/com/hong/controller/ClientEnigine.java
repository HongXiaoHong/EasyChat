package com.hong.controller;

import com.hong.model.User;
import com.hong.utils.CmdParser;
import com.hong.utils.Command;
import com.hong.utils.MsgCallBack;
import com.hong.utils.NetWork;
import com.hong.view.ListFrame;

import javax.swing.*;
import java.util.Map;

import static com.hong.contants.Result.YES;

public class ClientEnigine implements MsgCallBack, EngineCallBack {
    private final static String THATSRIGHT = "yes";

    NetWork netWork = new NetWork(this);
    String socketId;
    Map<String, User> users;
    ListFrame listFrame = null;


    //�¼ӵ�
    public ListFrame getListFrame() {
        return listFrame;
    }

    public void setListFrame(ListFrame listFrame) {
        this.listFrame = listFrame;
    }

    public void startGame() {
        socketId = netWork.startConnect("127.0.0.1", 1090);//localhost,192.168.13.31
    }

    public void connect(String ip, int port) {
        //���ӷ�����
        if (socketId == null) {
            socketId = netWork.startConnect(ip, port);//localhost,192.168.13.31
        }
    }

    @Override
    public void doLogin(User user) {
        String cmd = String.format("{op:login,user:{name:%s,pass:%s}}",
                user.getName(), user.getPass());
        netWork.send(socketId, cmd);
    }

    public void doRegist(User user) {
        String cmd = String.format("{op:register,"
                        + "user:{name:%s,pass:%s,nickname:%s,mark:%s,img:%s,socketId:%s}}",
                user.getName(), user.getPass(), user.getNickname(), user.getMark(), user.getImg(), user.getSocketId());
        netWork.send(socketId, cmd);
    }

    @Override
    public void doSend(String target, String message) {
        String line = String.format("{op:send,content:{msg:%s,target:%s}}", message, target);
        netWork.send(socketId, line);
    }

    @Override
    public void onConntextEvent(boolean flag) {
        String retStr = flag ? "�ɹ�" : "ʧ��";
        prt("�ͻ������ӷ�����    ��" + retStr);
    }

    @Override
    public void onReceived(byte[] buff, int count, String socketId) {
        String line = new String(buff, 0, count);
        prt("�յ���Ϣ��" + line);
        Command command = CmdParser.parseServerCommand(line);
        String op = command.op;
        String selfId = null;

        switch (op) {
            case "login":

                prt("������������Ϣlogin��" + command.result);
                if (YES.equals(command.result)) {
                    //��ȡ���������ص�socketId
                    selfId = command.data.get("socketId");
                    System.out.println("�ͻ����յ���������" + selfId);
                    //���µ�socketId����netWork��map������
                    netWork.copySocket(this.socketId, selfId);
                    //���µ�ǰ��socketId
                    this.socketId = selfId;
                    //ʹ�ý�������������н�������һ��user����
                    User user = CmdParser.makeUser(command.data);
                    listFrame = new ListFrame(this, user);
                }
                break;
            case "getList"://��ȡ�õ��б�
                System.out.println("��ȡ�����б�");
                users = command.users;
                //��map�������Ƴ��Լ�
                users.remove(this.socketId);
                if (listFrame != null) {
                    listFrame.showList(users);
                }
                prtUsers();
                break;
            case "register":

                prt("������������Ϣregister��" + command.result);
                break;
            case "message"://���յ��ͻ��˷�������Ϣ
                prt("�յ���Ϣ��" + command.data);
                String msg = command.data.get("msg");
                String from = command.data.get("from");
                prt("�յ���" + from + "������Ϣ��" + msg + "��");
                //Ҫ����Ϊ����socketId�������Լ���socketId
                listFrame.showMessage(from, msg);
                break;
            default:
        }
    }

    private void prtUsers() {
        // ��ӡ�����û�
        int id = 0;
        System.out.println("users--------value------" + users);
        for (String socketId : users.keySet()) {
            id++;
            User user = users.get(socketId);
            System.out.printf("[%d][%s][%s]\n", id, socketId, user.getNickname());
        }
        System.out.println("�û��б��Ѿ����£�����ѡ�˷�����Ϣ");
    }

    @Override
    public void onReveiveFailed() {
        System.out.println("����ʧ��");
    }

    public void prt(Object obj) {
        System.out.println("�����Ŀͻ��ˡ�" + obj);
    }

    @Override
    @Deprecated
    public void onCreateServer(boolean flag) {
    }

    @Override
    @Deprecated
    public void onAccepted() {
    }

    @Override
    public void onReveived(byte[] buff) {

        String line = new String(buff);
        String[] buffer = line.split(",");
        netWork.send(buffer[0], buffer[1]);
    }

    @Override
    public void doRegister(User user) {
        // ---------------------����-------------------

    }

    @Override
    public User getUser(String socketId) {
        // ------------------------����socketId��Ӧ���û�----------------
        return users.get(socketId);
    }

    @Override
    public ImageIcon getUserHead(String socketId) {
        // ---------------------����--------------------
        System.out.println("�ͻ�������socketId" + socketId);
        User user = users.get(socketId);
        System.out.println("�ͻ�������socketId" + user);
        if (user != null) {
            return user.getHead();
        }
        System.out.println("�ͻ���------�ò���ImageIcon");
        return null;
    }

}
