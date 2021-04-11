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
			prt("������SOCKETID����Ϣ,�ö��ŷָ�    ��");
			
			String line = sc.next();
			String[] arr = line.split(",");
			netWork.send(arr[0], arr[1]);
		}*/
    }

    @Override
    public void onCreateServer(boolean flag) {
        // ��ӡ�����������Ƿ�ɹ�
        String retStr = flag ? "�ɹ�" : "ʧ��";
        prt("������������" + retStr);
    }

    @Override
    public void onAccepted() {
        // ��ӡ���յ�һ���ͻ���
        prt("���յ�һ���ͻ���");
    }

    //����һ���ͻ���
    @Override
    @Deprecated
    public void OnConntextEvent(boolean flag) {
    }

    //����һ�����ݣ�����ͨ��Э����ַ������н����Ӷ����в�ͬ�Ĳ���
    @Override
    public void onReceived(byte[] buff, int count, String socketId) {
        // ����ҵ�񷽷�
        String line = new String(buff, 0, count);
        prt("�յ���Ϣ���ԡ�" + socketId + "����" + line);
        //�������Կͻ��˷�������Ϣ��
        //���������µ����Σ�
        // login
        // regist
        // getList
        // send[������Ϣ]
        boolean ret = false;//�����ж��Ƿ��¼������ǵ�¼�ͽ������û��б��ݸ��ͻ���
        //�����ַ�������һ��command���󣬽��������лὫ�û�����Ϣ���õ�data��mapӳ����
        Command command = CmdParser.parseClientCommand(line);
        Command retCom = null;
        Map<String, String> data = null;
        User user = null;
        switch (command.op) {
            case "login":
                data = command.data;
                user = CmdParser.makeUser(data);

                //�������û��ظ���¼
                Set<String> keySet = onlineUsers.keySet();
                for (String soc : keySet) {
                    User u = onlineUsers.get(soc);
                    if (u.getName().equals(user.getName())) {
                        return;
                    }
                }


                //����user��socketId
                user.setSocketId(socketId);
                retCom = controller.login(user);
                if ("yes".equals(retCom.result)) {
                    //��network�еĵ��û��Ķ˿ں�IP��Ϣ
                    //���������õ�user
                    InetSocketAddress isa = netWork.getAddress(socketId);
                    user.setIpAndPort(isa);
                    //��socketId���û����뵽���ߵ�ӳ����
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
                System.out.println("��������data" + data);
                System.out.println("��������command�����data" + command.data);
                retCom.line = "{op:message,content:{msg:"
                        + data.get("msg") + ",from:" + socketId + "}}";
                socketId = data.get("target");
                break;
        }
        System.out.println("command.line:" + retCom.line);
        netWork.send(socketId, retCom.line);//����Ϣ���ظ��ͻ���
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
        System.out.println("����������" + obj);
    }

    @Override
    public void onReveived(byte[] buff) {


    }

}
