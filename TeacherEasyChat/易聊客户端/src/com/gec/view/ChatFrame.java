package com.gec.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import com.gec.controller.EngineCallBack;


public class ChatFrame extends AbstractChat {
	
	private String targetId;
	private EngineCallBack callBack;

	public ChatFrame(EngineCallBack cb, String nickName, String socketId) {
		this.targetId = socketId;
		this.callBack = cb;
		setTitle("与"+ nickName +"交谈中 ...");
		initViews();
		setButtonEvents();
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Window 正在关闭 ...");
				callBack.closeFrame( socketId );
			} } 
		);
	}

	protected void setButtonEvents(){
		btnSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String myMsg = selfArea.getText();
				selfArea.setText("");
				addText("我", myMsg);
				callBack.doSend(targetId, myMsg);
			}
		} );
	}

	public void addText(String nickName, String message){
		insertText( styDoc, nickName +"\n", "Style01" );
		insertText( styDoc, message +"\n", "Style01" );
	}

	public void setText(LinkedList<String[]> list) {
		for(String[] arr : list ){
			addText( arr[0], arr[1] );
		}
	}
}
