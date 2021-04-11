package com.gec.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import com.gec.controller.EngineCallBack;


public class ChatFrame extends AbstractChat {

    private String targetId;
    private EngineCallBack callBack;

    public ChatFrame(EngineCallBack cb, String nickName, String socketId) {
        this.targetId = socketId;
        this.callBack = cb;
        setTitle("��" + nickName + "��̸�� ...");
        initViews();
        setButtonEvents();
    }

    protected void setButtonEvents() {
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String myMsg = selfArea.getText();
                selfArea.setText("");
                addText("��", myMsg);
                callBack.doSend(targetId, myMsg);
            }
        });
    }

    public void addText(String nickName, String message) {
        insertText(styDoc, nickName + "\n", "Style01");
        insertText(styDoc, message + "\n", "Style01");
    }

    public void setText(LinkedList<String[]> list) {
        for (String[] arr : list) {
            addText(arr[0], arr[1]);
        }
    }
}
