package com.hong.model;

import javax.swing.*;

public class User {

    public static int LOGIN = 1;
    public static int REGISTER = 2;
    private String name;
    private String pass;
    private String nickName;
    private String mark;
    private String img;
    private String socketId;
    private ImageIcon head;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public ImageIcon getHead() {
        return head;
    }

    public void setHead(ImageIcon head) {
        this.head = head;
    }

    public boolean equals(User user, int flag) {
        boolean ret = false;
        if (flag == User.LOGIN) {
            if (user.name != null && user.pass != null
                    && name.equals(user.getName()) && pass.equals(user.getPass())) {
                ret = true;
            }
        } else if (flag == User.REGISTER) {
            if (user.name != null && name.equals(user.getName())) {
                ret = true;
            }
        } else {

        }
        return ret;
    }

    public User() {
        super();
    }

    public User(String name, String pass, String nickName, String mark,
                String img) {
        super();
        this.name = name;
        this.pass = pass;
        this.nickName = nickName;
        this.mark = mark;
        this.img = img;
    }

    public User(String name, String pass) {

        super();
        this.name = name;
        this.pass = pass;
    }

    public String getNickname() {
        return nickName;
    }

    public void setNickname(String nickName) {
        this.nickName = nickName;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    @Override
    public String toString() {
        return "name:" + getName() + ",pass:" + getPass() + ",nickname:" + getNickname() +
                ",mark:" + getMark() + ",img:" + getImg() + ",socketId:" + getSocketId();
    }
}
