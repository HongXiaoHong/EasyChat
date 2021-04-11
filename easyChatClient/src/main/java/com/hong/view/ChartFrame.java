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
        setTitle("与" + nickName + "在交谈");
        txtOther = new JLabel("对方的消息：");
        //-----------------原来的文本域-------------------
        //txtOtherMsg = new JTextField();
        //------------------------------------
        otherMsg = new JTextArea();
        otherScroll = new JScrollPane(otherMsg);
        txtSelf = new JLabel("你的消息：");
        txtSelfMsg = new JTextField();
        btnSend = new JButton("发送");

        txtOther.setBounds(20, 10, 100, 20);
        otherScroll.setBounds(20, 50, 400, 150);

        txtSelf.setBounds(20, 200, 100, 20);
        txtSelfMsg.setBounds(20, 220, 400, 80);

        btnSend.setBounds(180, 300, 90, 40);
        btnSend.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("发送消息");
                        String selfMsg = txtSelfMsg.getText();
                        callBack.doSend(socketId, selfMsg);//等会回来写
                        //将自己的对话框设置为空
                        txtSelfMsg.setText("");
                        //获取发送给对方的消息然后将新的消息设置到对话框中
                        StringBuffer sb = new StringBuffer(otherMsg.getText());
                        sb.append("\n我\n");
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

        setVisible(true);//具有刷新作用，如果太早加上的话，可能要刷新才可以加载图片之类的
    }

    public void prt(Object obj) {
        System.out.println("【CLIENT】" + obj);
    }

    public static void main(String[] args) {
        new ChartFrame("洪晓鸿");
    }
}
