package com.hong.view;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.hong.model.User;
import com.hong.utils.PathUtil;

public class ServerFrame extends JFrame{

	JTable table ;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ServerFrame table = new ServerFrame();
	}
	
	public ServerFrame(){
		DefaultTableModel model = createTableModel();
		createTable(model);
	}

	public void createTable(DefaultTableModel model){
		table = new JTable(model);
		//设置行高
		table.setRowHeight(50);
		//设置滚动条
		JScrollPane srcPane = new JScrollPane(table);
		srcPane.setSize(450, 350);
		add(srcPane);
		
		setBounds(150,150,500,450);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String imagePath = PathUtil.getTxt("hong.jpg");
		System.out.println(imagePath);
		ImageIcon hong = new ImageIcon(imagePath);
		setIconImage(hong.getImage());
		setResizable(false);
	}
	
	public DefaultTableModel createTableModel(){
		Vector<Object> colNames = new Vector<Object>();
		colNames.add("序号");
		colNames.add("SocketId");
		colNames.add("用户名");
		colNames.add("IP地址");
		colNames.add("端口号");
		colNames.add("登陆时间");
		return new DefaultTableModel( colNames , 0 );
	}
	
	public void addUser(User user){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		Vector<Object> data = new Vector<Object>();
		int rowCount = model.getRowCount();//获取行数
		data.add(rowCount+1);//序号
		data.add(user.getSocketId());//socketId
		data.add(user.getName());//用户名
		data.add(user.getIp());//ip
		data.add(user.getPort());//port
		data.add(user.getLoginTime());//d登录时间
		model.addRow(data);//在尾部添加一行单元格
		//table.updateUI();//刷新数据，不刷新，数据不出来
		
	}
	public void removeUser(User user){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		int rowCount = model.getRowCount();
		int columnCount = model.getColumnCount();
		System.out.println("rowCount"+rowCount+"columnCount"+columnCount);
		int rowNow = 0;
		int columnNom = 0;
		String temp =null;
		for(;rowNow<rowCount;rowNow++){
				temp = (String) model.getValueAt(rowNow, 1);
				System.out.println("“当前行数为“"+(rowNow+1)+"”这个用户的socketId是：”"+temp);
				if(user.getSocketId().equals(temp)){
					model.removeRow(rowNow);//将该行移除掉
					
					break;
				}
		}
	}
	public void clearData(User user){
		
	}
}
