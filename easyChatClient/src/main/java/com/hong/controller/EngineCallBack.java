package com.hong.controller;

import javax.swing.ImageIcon;

import com.hong.model.User;

public interface EngineCallBack {
	public void doLogin(User user);
	public void doRegister(User user);
	public void doSend(String target, String message);
	public User getUser(String socketId);
	public ImageIcon getUserHead(String socketId);
}
