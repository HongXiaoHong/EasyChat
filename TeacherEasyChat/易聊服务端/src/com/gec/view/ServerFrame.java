package com.gec.view;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.gec.model.User;

public class ServerFrame extends JFrame {

	JTable table;
	public void createTable(DefaultTableModel model){
		table = new JTable( model );
		table.setRowHeight( 35 );      //设置它的行高 ..
		JTableHeader header = table.getTableHeader();
		header.setResizingAllowed(false);
		
		//[2] 把表格放入滚动条
		JScrollPane scrPane = new JScrollPane( table );
		scrPane.setSize( 450, 350 );
		//[3] 加入到 JFrame 当中 ..
		add( scrPane );
	}
	public void initFrame(int port){
		setBounds( 150, 150, 500, 450 );
		setResizable( false );
		setVisible( true );
		setTitle("易聊服务器  Port:"+ port);
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	//[PS] 创建数据模型
	public DefaultTableModel createTableModel(){
		//[1] 准备好表头的数据
		Vector<Object> colNames = new Vector<Object>();
		colNames.add("序号");
		colNames.add("SocketId");
		colNames.add("用户名");
		colNames.add("IP 地址");
		colNames.add("端口号");
		colNames.add("登陆时间");
		return new DefaultTableModel(colNames, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};	
	}
	
	public void addUser( User user ){
		//[1] 获取表格的数据模型
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		//[2] 创建一个 Vector 集合来承载数据 [ 一行的数据 ]
		Vector<Object> data = new Vector<Object>();
		int rowCnt = model.getRowCount();  //[PS] 获取行数
		data.add( rowCnt + 1 );            //[1] 序号
		data.add( user.getSocketId() );    //[2] SocketId
		data.add( user.getName() );        //[3] 用户名 Name
		data.add( user.getIp() );          //[4] IP 地址
		data.add( user.getPort() );        //[5] 端口号
		data.add( user.getLoginTime() );   //[6] 登陆时间
		
		model.addRow( data );       //[3] 在尾部加入一行单元格 ..
		//table.updateUI();         //[4] 刷新表格的界面 ..
	}
	public void removeUser( User user ){
		
	}
	public void clearData(){
		
	}

	public ServerFrame(int port){
		DefaultTableModel model = createTableModel();
		createTable( model );
		initFrame( port );
	}
	
}
