package com.hong.view;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class SucRegFra extends JFrame{

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new SucRegFra();
	}

	public SucRegFra(){
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300,300,200,200);
		JLabel label = new JLabel("���Ѿ��ɹ�ע��");
		label.setBounds(40, 50, 200, 30);
		add(label);
		setVisible(true);
	}
}
