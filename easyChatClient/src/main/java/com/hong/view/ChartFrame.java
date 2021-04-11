package com.hong.view;

import com.hong.controller.ClientEnigine;
import com.hong.controller.EngineCallBack;
import com.hong.model.User;
import com.hong.utils.PathUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChartFrame extends JFrame {


    private JLabel txtOther;
    private JTextArea otherMsg;
    private JScrollPane otherScroll;
    private JLabel txtSelf;
    private JTextField txtSelfMsg;
    private JButton btnSend;
    private ClientEnigine client;

    private EngineCallBack callBack;
    private String socketId;

    public ChartFrame(EngineCallBack cb, String nickName, String socketId) {
        this(nickName);
        this.callBack = cb;
        this.socketId = socketId;
    }

    public void settxtOtherMsgText(String msg) {
        User user = callBack.getUser(socketId);
        System.out.println(user.getName());
        String name = user.getName();
        otherMsg.setText(otherMsg.getText() + "\n" + name + "\n" + msg);
    }

    public ClientEnigine getClient() {
        return client;
    }

    public void setClient(ClientEnigine client) {
        this.client = client;
    }

    public ChartFrame(String nickName) {
        ImageIcon hong = PathUtil.getImageIcon("hong.jpg");
        setIconImage(hong.getImage());
        setLayout(null);
        setBounds(100, 100, 500, 400);
        setTitle("��" + nickName + "�ڽ�̸");
        txtOther = new JLabel("�Է�����Ϣ��");
        //-----------------ԭ�����ı���-------------------
        //txtOtherMsg = new JTextField();
        //------------------------------------
        otherMsg = new JTextArea();
        otherScroll = new JScrollPane(otherMsg);
        txtSelf = new JLabel("�����Ϣ��");
        txtSelfMsg = new JTextField();
        btnSend = new JButton("����");

        txtOther.setBounds(20, 10, 100, 20);
        otherScroll.setBounds(20, 50, 400, 150);

        txtSelf.setBounds(20, 200, 100, 20);
        txtSelfMsg.setBounds(20, 220, 400, 80);

        btnSend.setBounds(180, 300, 90, 40);
        btnSend.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("������Ϣ");
                        String selfMsg = txtSelfMsg.getText();
                        callBack.doSend(socketId, selfMsg);//�Ȼ����д
                        //���Լ��ĶԻ�������Ϊ��
                        txtSelfMsg.setText("");
                        //��ȡ���͸��Է�����ϢȻ���µ���Ϣ���õ��Ի�����
                        StringBuffer sb = new StringBuffer(otherMsg.getText());
                        sb.append("\n��\n");
                        sb.append(selfMsg);
                        otherMsg.setText(sb.toString());
                    }

                }
        );

        add(txtOther);
        //add(txtOtherMsg);
        add(otherScroll);
        add(txtSelf);
        add(txtSelfMsg);
        add(btnSend);

        setVisible(true);//����ˢ�����ã����̫����ϵĻ�������Ҫˢ�²ſ��Լ���ͼƬ֮���
    }

    public void prt(Object obj) {
        System.out.println("��CLIENT��" + obj);
    }

    public static void main(String[] args) {
        new ChartFrame("������");
    }
}
