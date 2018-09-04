package com.hong.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hong.controller.ClientEnigine;
import com.hong.model.User;
import com.hong.utils.PathUtil;
/*
把图片所在的JLabel直接加到了JPanel中,这样加图片,
因为JPanel的布局默认是流布局FlowLayout所以会有空隙.
你把两个JPanel的布局都设为边界布局BorderLayout,
然后把JLabel加到边界布局的中心BorderLayout.CENTER,
就应该没有空隙了.
 */
public class Regist extends JFrame{

	private JPanel top;
	private JLabel nameLabel;
	private JTextField name;
	private JLabel passLabel;
	private JTextField pass;
	private JLabel conPassLabel;
	private JTextField conPass;
	private JLabel nicknameLabel;
	private JTextField nickname;
	private JLabel markLabel;
	private JTextField mark;
	private JLabel imgLabel;
	private JTextField img;
	private JComboBox imgComboBox;
	private JButton regist;
	
	private ClientEnigine server;
	
	Regist(final ClientEnigine server){
		
		this.server = server;
		ImageIcon hong = new ImageIcon("F:\\test\\images\\hong.jpg");
		setIconImage(hong.getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setBounds(500,200,400,350);
		setTitle("注册");
		ImageIcon icon = PathUtil.getImageIcon("hong.jpg");
		setIconImage(icon.getImage());
		
		top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBounds(0,0,429,66);
		String path = "F:\\test\\images\\reg_head.jpg";
		JLabel topLabel = new JLabel(new ImageIcon(path));
		top.add(topLabel);
		
		nameLabel = new JLabel("用户名:");
		name = new JTextField();
		nameLabel.setBounds(50,80,80,20);
		name.setBounds(130,80,200,20);
		
		passLabel = new JLabel("密码:");
		pass = new JTextField();
		passLabel.setBounds(50,110,80,20);
		pass.setBounds(130,110,200,20);
		
		
		conPassLabel = new JLabel("确认密码:");
		conPass = new JTextField();
		conPassLabel.setBounds(50,140,80,20);
		conPass.setBounds(130,140,200,20);
		
		nicknameLabel = new JLabel("昵称:");
		nickname = new JTextField();
		nicknameLabel.setBounds(50,170,80,20);
		nickname.setBounds(130,170,200,20);
		
		markLabel = new JLabel("签名:");
		mark =new JTextField();
		markLabel.setBounds(50,200,80,20);
		mark.setBounds(130,200,200,20);
		
		imgLabel = new JLabel("头像:");
		//img = new JTextField();
		imgLabel.setBounds(50,230,80,20);
		//img.setBounds(130,230,200,20);
		imgComboBox = new JComboBox();
		imgComboBox.addItem("1");
		imgComboBox.addItem("2");
		imgComboBox.addItem("3");
		imgComboBox.addItem("4");
		imgComboBox.addItem("5");
		imgComboBox.addItem("6");
		imgComboBox.addItem("7");
		imgComboBox.addItem("8");
		imgComboBox.setBounds(130,230,200,20);
		
		regist = new JButton ("注册");
		regist.setBounds(150,270,120,25);
		regist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//注册动作
				if(pass.getText()!=null && conPass.getText()!=null 
						&& pass.getText().equals(conPass.getText())){
					if(pass.getText()!=null && conPass.getText()!=null && 
							pass.getText().equals(conPass.getText())){
						
						User user = new User();
						user.setName(name.getText());
						user.setPass(pass.getText());
						user.setNickName(nickname.getText());
						user.setMark(mark.getText());
						user.setImg(imgComboBox.getSelectedItem().toString());
						server.connect("localhost", 1090);
						server.doRegist(user);
						//System.out.println(user);
						SucRegFra suc = new SucRegFra();
					}
				}
			}
		});
		add(top);
		add(nameLabel);
		add(name);
		add(passLabel);
		add(pass);
		add(conPassLabel);
		add(conPass);
		add(nicknameLabel);
		add(nickname);
		add(markLabel);
		add(mark);
		add(imgLabel);
		//add(img);
		add(imgComboBox);
		
		add(regist);
		setVisible(true);
	}
	
	}
