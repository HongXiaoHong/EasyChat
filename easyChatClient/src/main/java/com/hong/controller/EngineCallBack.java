package com.hong.controller;

import com.hong.model.User;

import javax.swing.*;

public interface EngineCallBack {
    public void doLogin(User user);

    public void doRegister(User user);

    public void doSend(String target, String message);

    public User getUser(String socketId);

    public ImageIcon getUserHead(String socketId);
}
