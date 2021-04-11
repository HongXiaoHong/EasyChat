package com.gec.model;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User {

    public static final int LOGIN = 1;
    public static final int REGISTER = 2;

    private String name;
    private String pass;
    private String nickName;
    private String mark;
    private String img;
    private String socketId;

    private String ip;
    private int port;
    private String loginTime;

    public void setIpAndPort(InetSocketAddress isa) {
        ip = isa.getAddress().getHostAddress();
        port = isa.getPort();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        loginTime = sdf.format(date);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getLoginTime() {
        return loginTime;
    }


    public boolean equals(User user, int flag) {
        boolean ret = false;
        if (flag == LOGIN) {           // LOGIN ��֤ [ �û��� + ���� ]
            if (user.name != null && user.pass != null
                    && user.name.equals(name)
                    && user.pass.equals(pass)) {
                ret = true;
            }
        } else if (flag == REGISTER) {  //REGISTER: ��֤ [ �û��� ]
            if (user.name != null && user.name.equals(name)) {
                ret = true;
            }
        }
        return ret;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
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

    public String getSocketId() {
        return socketId;
    }

    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    public String toString() {
        return String.format("name:%s,pass:%s,nickName:%s,mark:%s,img:%s,socketId:%s",
                getName(), getPass(), getNickName(),
                getMark(), getImg(), getSocketId());
    }

}
