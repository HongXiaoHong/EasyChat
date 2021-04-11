package com.hong.test;

import com.hong.controller.ClientEnigine;

public class ClientMain {

	public static void main(String[] args) {
		ClientEnigine server = new ClientEnigine();
		server.startGame();
	}
/*
\\{

op:([^,]+),?
([^:]+:(\\{[^}]*\\}))?
\\}
 */
}
