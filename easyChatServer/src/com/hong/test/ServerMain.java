package com.hong.test;

import com.hong.controller.ServerEnigine;

public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try{
			ServerEnigine server = new ServerEnigine();
			server.startGame();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
