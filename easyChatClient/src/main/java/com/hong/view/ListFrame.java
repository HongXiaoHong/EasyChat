package com.hong.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.hong.controller.ClientEnigine;
import com.hong.controller.EngineCallBack;
import com.hong.model.User;
import com.hong.utils.PathUtil;

public class ListFrame extends JFrame {

	ImageIcon selfHead;//头像
	JLabel selfNick;
	JLabel selfMark;
	JTable table;
	private Image emptyImg;//空白的图片，用于闪动
	private Map<String,ChartFrame> chats = 
			new LinkedHashMap<String, ChartFrame>();//每个用户对应一个聊天面板
	private Map<String,FlashTask> fTasks = 
			new HashMap<String, FlashTask>();//头像闪动的runable，当有信息的时候，就让用户头像闪动
	EngineCallBack callBack;//实际传进来的是“ClientEngine”
	private final static String path = "F:\\test\\";//聊天记录存放路劲
	private String name;
	ClientEnigine client;

	public ClientEnigine getClient() {
		return client;
	}

	public void setClient(ClientEnigine client) {
		this.client = client;
	}

	public ListFrame(final EngineCallBack callBack,User user) {
		
		this.callBack = callBack;
		this.name = user.getName();//为了区分文件夹，保存本方的名字
		this.emptyImg = PathUtil.getImageIcon("empty.jpg").getImage();
		//final String socketId = user.getSocketId();
		selfHead = PathUtil.getImageIcon("face" + user.getImg() + ".jpg");
		JLabel lblHead = new JLabel(selfHead);
		System.out.println(user.getNickname()+user.getMark());
		selfNick = new JLabel(user.getNickName());
		selfMark = new JLabel(user.getMark());
		
		lblHead.setBounds(7, 7, 50, 50);
		selfNick.setBounds(100, 14, 50, 20);
		selfMark.setBounds(100, 35, 250, 20);
		

		JPanel banner = new JPanel(null);
		banner.setBounds(0, 0, 350, 65);
		banner.setBackground(new Color(0, 105, 165));
		banner.add(lblHead);
		banner.add(selfNick);
		banner.add(selfMark);

		setLayout(null);
		add(banner);

		DefaultTableModel model = mokeModel();
		table = new JTable(model);
		table.setRowHeight(50);
		table.setBackground(new Color(237, 237, 237)  );
		table.addMouseListener(new TableAdapter());
		TableColumnModel col = table.getColumnModel();

		col.getColumn(0).setCellRenderer(new ImageRender());
		col.getColumn(1).setCellRenderer(new ColorRender());
		
		
		col.getColumn(0).setPreferredWidth(50);
		col.getColumn(1).setPreferredWidth(200);

		col.getColumn(2).setPreferredWidth(0);
		col.getColumn(2).setMaxWidth(0);
		col.getColumn(2).setMaxWidth(0);

		table.setBounds(0, 66, 270, 600);
		// table.setBackground(Color.cyan);
		add(table);

		setBounds(150, 150, 250, 750);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ImageIcon hong = PathUtil.getImageIcon("hong.jpg");
		setIconImage(hong.getImage());
		setTitle(user.getName());
		setVisible(true);

	}

	public void showList(Map<String, User> users) {
		Set<String> socketIds = users.keySet();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		//清空之前存在的列表
		model.setRowCount(0);
		for (String socketId : socketIds) {
			Vector<Object> rowData = new Vector<Object>();

			User user = users.get(socketId);
			ImageIcon head = PathUtil.getImageIcon("face" + user.getImg() + ".jpg");
			user.setHead(head);

			rowData.add(head);
			rowData.add(user.getNickName());
			rowData.add(socketId);

			model.addRow(rowData);
		}
		//table.updateUI();
	}


	public DefaultTableModel mokeModel() {
		// 鍒涘缓琛ㄥご
		Vector<Object> headers = new Vector<Object>();
		headers.add("头像");
		headers.add("昵称");
		headers.add("SocketId");
		return new DefaultTableModel(headers, 0) {
			@Override
			public boolean isCellEditable(int row, int colunm) {
				return false;
			}
		};
	}

	public void showMessage(String socketId, String message){
		ImageIcon head = callBack.getUserHead(socketId);
		System.out.println("showMessage:-------head:----------"+head);
		ChartFrame chart = null;
		User user = callBack.getUser(socketId);
		String nickName = user.getNickName();
		if(chats.get(socketId)==null){
			chart = new ChartFrame(callBack, nickName, socketId);
			this.chats.put(socketId, chart);
		}else{
			chart = chats.get(socketId);
		}
		chart.settxtOtherMsgText(message);
		chart.setVisible(false);
		if(fTasks.get(socketId)==null){
			FlashTask task = new FlashTask(head);
			fTasks.put(socketId, task);
			Thread th =new Thread(task);
			th.start();
		}
	}
	
	//----------------------------------------------------------------------------------
	//------------------------------以下是内部类区域--------------------------------------------
	//----------------------------------------------------------------------------------
	class ImageRender implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object imgIcon, boolean isSelected,
				boolean hasFocus, int row, int column) {
			ImageIcon icon = (ImageIcon) imgIcon;
			JLabel label = new JLabel(icon);
			
			return label;
		}
	}
	
	class ColorRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object cell, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if( row%2==0 ) {
				setBackground( new Color(237, 237, 237) ); 
			}else {
				setBackground( Color.WHITE ); 
			}
			return super.getTableCellRendererComponent(table, cell, isSelected,
					hasFocus, row, column);
		}
	}
	
	class TableAdapter extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(e.getClickCount()==2){
				processClick(e);
			}
		}
	}
	
	class FlashTask implements Runnable{

		ImageIcon head;
		boolean stop =false;
		public FlashTask(ImageIcon head) {
			this.head = head;
		}
		@Override
		public void run() {
			Image srcImg = head.getImage();
			boolean flag = true;
			while (!stop) {
				if (flag) {
					head.setImage(emptyImg);
					flag = false;
				} else {
					head.setImage(srcImg);
					flag = true;
				}
				delay();
				table.updateUI();
			}
			head.setImage(srcImg);
		}

		private void delay() {
			try { Thread.sleep(250); }
			catch (Exception e) { }
		}
	}
	//----------------------------------------------------------------------------------
	//------------------------------以上是内部类区域--------------------------------------------
	//----------------------------------------------------------------------------------

	public void processClick(MouseEvent e) {
		//双击事件
		Point point = e.getPoint();
		int row = table.rowAtPoint(point);
		int col = table.columnAtPoint(point);
		System.out.println("行是"+row+",列是"+col);
		String nickName = (String)table.getModel().getValueAt(row, 1);
		String socketId = (String)table.getModel().getValueAt(row, 2);
		System.out.println("socketId"+socketId);
		ChartFrame chart = null;
		if(chats.get(socketId)==null){
			chart = new ChartFrame(callBack, nickName, socketId);
			this.chats.put(socketId, chart);
		}else{
			chart = chats.get(socketId);
		}
		chart.setVisible(true);
		FlashTask flash = fTasks.get(socketId);
		if(flash!=null){
			flash.stop = true;
			fTasks.remove(socketId);
		}
	}
	
	/*public static void main(String[] args) {
		User user = new User();
		user.setImg("1");
		ListFrame frame = new ListFrame(user);
		Map<String, User> users = new LinkedHashMap<String, User>();
		String[] names = { "andy", "John", "linke", "boby", "lucy", "bob", "terry", "jun" };
		for (int i = 1; i <= 8; i++) {
			User tmpUser = new User();
			tmpUser.setImg(String.valueOf(i));
			tmpUser.setNickName(names[i - 1]);
			tmpUser.setSocketId("socketId" + i);
			users.put("socketId" + i, tmpUser);
		}
		frame.showList(users);
	}*/

}
