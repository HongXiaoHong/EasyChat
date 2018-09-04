package com.gec.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.gec.controller.EngineCallBack;
import com.gec.model.User;

public class LoginFrame extends JFrame {
	
	JTextField name = new JTextField();
	JTextField pass = new JTextField();
	
	private EngineCallBack callBack;
	private boolean waitFlag;
	public void setWaitFlag(boolean wait){ this.waitFlag = wait; }
	
	public void initViews(){
		setBounds(100,150,500,450);  //设置尺寸大小与位置
		setLayout( null );
		
		JLabel jlbName = new JLabel("用户名");
		ImageIcon loginIcon = ImageLoader.getImageIcon("login.png");
		JLabel btnLogin = new JLabel( loginIcon );
		
		name.setBounds( 125, 150, 150, 30);
		pass.setBounds( 125, 190, 150, 30);
		jlbName.setBounds(75,150,75,30);
		btnLogin.setBounds(145, 220, 196, 32);
		
		add( name );   //添加到窗体上
		add( pass );   //添加到窗体上
		add( jlbName );
		add( btnLogin );
		
		//[1] 对登陆按钮添加一个点击事件 ..
		btnLogin.addMouseListener( 
			new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if( !waitFlag ){     //如果你不处于等待的状态, 可以去连接服务器
						String _name = name.getText();
						String _pass = pass.getText();
						User user = new User();
						user.setName( _name );
						user.setPass( _pass );
						callBack.doLogin( user );
					}else{
						System.out.println("[LoginFrame] 正在连接, 请稍等 ..");
					}
				}
			}
		);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setVisible(true);   //使窗体可见 
	}
	
	//EngineCallBack: 接收的对象是  ClientEngine
	public LoginFrame(EngineCallBack callBack){
		this.callBack = callBack;
		initViews();
	}

	public void showMessage(String string) {
		//--------------- [ 待定 ] ---------------
	}
	
}
