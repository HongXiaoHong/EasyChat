package com.gec.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.gec.controller.EngineCallBack;
import com.gec.model.User;

public class ListFrame extends JFrame {
	
	ImageIcon selfHead;
	JLabel selfNick;           //自已的昵称
	JLabel selfMark;           //自已的个性签名
	JTable table;
	
	EngineCallBack callBack;   //实际传进来的引用是: "ClientEngine"
	
	private Image emptyImg;                //[1] 头像闪动(空白图像)
	private Map<String,ChatFrame> chats = new HashMap<String,ChatFrame>();   //[2] 存放聊天面板
		                                   //    每一个用户对应一个聊天面板
		                                   //    在什么时候来产生聊天面板, 稍后讨论。
	
	private Map<String,FlashTask> fTasks = new HashMap<String,FlashTask>();  //[3] 头像闪动的任务。
		                                   //    当有消息过来, 就让用户的头像闪动。
		                                   //    当双击头像, 停止头像的闪动, 打开 ChatFrame。
	private String path = "e:\\dir\\";     //[4] 聊天记录存放。
	private String name;                   //[5] 本方的名称。
	
	//[1] 把你自己通过 User 传入进来 ..
	public ListFrame(EngineCallBack callBack, User user){
		this.callBack = callBack;   //接收  "ClientEngine" 的引用。
		this.name = user.getName();
		this.emptyImg = ImageLoader.getImageIcon("empty.jpg").getImage();
		
		selfHead = ImageLoader.getImageIcon( "face"+ user.getImg() +".jpg" );
		JLabel lblHead = new JLabel( selfHead );
		lblHead.setBounds( 7, 7, 50, 50 );
		
		//[2] 用一个容器来装 头像, 各种信息 ...
		JPanel banner = new JPanel( null );
		banner.setBounds( 0, 0, 200, 65 );
		banner.setBackground( new Color(0,105,165) );
		banner.add( lblHead );
		
		setLayout( null );   //取消原有的布局  ..
		add( banner );
		
		//-----------------------------------------------------------------------
		DefaultTableModel model = makeModel();
		table = new JTable( model );
		table.setRowHeight( 50 );
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.addMouseListener( new TableAdapter() );
		
		TableColumnModel col = table.getColumnModel();
		
		//[1] 拿到第 0 列, 设置单元格渲染器。
		col.getColumn(0).setCellRenderer( new ImageRender() );
		
		col.getColumn(0).setPreferredWidth( 55 );   //[1] 第 0 列
		col.getColumn(1).setPreferredWidth( 210 );  //[2] 第 1 列

		col.getColumn(2).setPreferredWidth(0);
		col.getColumn(2).setMaxWidth(0);   //[1] 让它隐藏 ..
		col.getColumn(2).setMinWidth(0);   //[2] 让它隐藏 ..
		
		table.setBounds( 0, 66, 220, 600 );
		//table.setBackground( Color.DARK_GRAY );
		add( table );
		
		//-----------------------------------------------------------------------
		setBounds( 150, 150, 200, 750 );
		setResizable( false );
		setVisible( true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
	
	public void showList(Map<String,User> users){
		
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		model.setRowCount( 0 );   //[1] 先清空原表格的数据 ..
		//[2] 迭代所有用户
		Set<String> socketIds = users.keySet();
		for( String socketId : socketIds ){
			Vector<Object> rowData = new Vector<Object>();
			
			User user = users.get( socketId );
			ImageIcon head = ImageLoader.getImageIcon( "face"+ user.getImg() +".jpg" );
			user.setHead( head );   //设置用户头像  ..
			
			rowData.add( head );    //第 0 列放入用户头像 ..
			rowData.add( user.getNickName() );    //第 1 列放入用户昵称 ..
			rowData.add( socketId );
			
			model.addRow( rowData );   //加入到表格的数据源中  ..
		}
	}
	
	public void showMessage(String socketId, String message){
//		[1] 从 chats(Map) 中获取 ChatFrame [用来判断窗体是否正在显示]。
		ChatFrame chatFrame = chats.get( socketId );		
//		[2] 利用 callBack 获取  User 对象。
		User user = callBack.getUser(socketId);		
//		[3] 获取 User 的昵称。
		String nickName = (user!=null) ? user.getNickName() : "未知用户";
		
//		[4] 如果 ChatFrame 窗体正在显示。
		if( chatFrame!=null ){   //证明窗体正在显示 ..
			//[4]-1  则将消息显示在  ChatFrame 中。
			chatFrame.addText( nickName, message );
		}else{  //如果, 窗体没有显示  ..
			//[4-4] 看看对应的用户头像是否有闪动, 如果有无则  ---> 闪动头像, 以提示用户。
			FlashTask task = fTasks.get( socketId );
			if( task==null ){  //如果, 不存在  ---> 去闪动头像 ..
				task = new FlashTask( user.getHead() );   //创建闪动的作务
				fTasks.put( socketId, task );  //将任务放入 fTask {Map}
				Thread th = new Thread( task );
				th.start();   //[4-7] 创建线程执行之 ..
			}
			appendToFile( socketId, message );   //[4-8] 写入到文本 (appendToFile) ..
		}
	}
	
	//-------------------------- 对内的方法 ----------------------------------
	//功能: 将一条消息写入到文本。
	//格式: 昵称,消息,时间
	//以 e:\dir\ 为例, 本方的账号为: andy
	private void appendToFile(String socketId, String msg){ 
		//[1] 先看 e:\dir\andy 是否存在, 不存在创建目录。
		//path: 数据路径, name: 本方的用户名
		File dir = new File(path + name);
		if( !dir.exists() ){
			dir.mkdirs();   //不存在创建目录 ..
		}
		//[2] 获取 socketId 所对应的用户昵称。
		User user = callBack.getUser(socketId);
		String nickName = (user!=null) ? user.getNickName() : "未知用户";
		String time = getTime();   //[4] 通过 getTime() 获取时间。
		
		//[3] 以追加形式写入文本。
		PrintWriter writer = null;
		File file = new File(dir, socketId +".txt");
		try {
			writer = new PrintWriter( new FileWriter( file, true ) );
			writer.printf( "%s,%s,%s\n", nickName, msg, time );  //昵称,消息,时间
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			writer.close();
		}
	}
	
	public String getTime(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(date);
	}
	
	//[Note] 处理双击事件  .. 
	private void proccessClick(MouseEvent e){
		Point point = e.getPoint();
		int rowIndex = table.rowAtPoint( point );
		
		String nickName = (String)table.getModel().getValueAt(rowIndex, 1);
		String socketId = (String)table.getModel().getValueAt(rowIndex, 2);
		
		//[1] 从 chats 中获取 ChatFrame
		ChatFrame chatFrame = chats.get( socketId );
		if( chatFrame!=null ){   //[2] 如果拿到  chat 则将焦点到 Chat 上。
			      //[A1] chatFrame  显示到桌面的最上方 。
		}else{    //[B1] --> [ELSE] 如果拿不到 chat 对象。
			FlashTask task = fTasks.get( socketId );
			if( task!=null ){               //[B2] 如果头像有闪动, 则取消闪动。
				task.stop = true;           //[B3] 将 task 的停止标志设置为  true 。
				fTasks.remove( socketId );  //[B4] 从 Map 中移除 task ( 任务 )。
			}
			//[B5] 新建 ChatFrame 窗体, CallBack: ClientEngine
			chatFrame = new ChatFrame( callBack, nickName, socketId );
			//[B6] 调用  readMsgFromText 读取消息。
			LinkedList<String[]> list = readMsgFromText( socketId );
			//[B7] 将消息设置到 chatFrame 中。
			chatFrame.setText( list );
			//[B8] 将 chatFrame 放入到 Map [chats] 中。 
			chats.put( socketId, chatFrame );
		}
	}

	//path = e:\dir\
	//name = andy
	//socketId.txt
	private LinkedList<String[]> readMsgFromText(String targetId){
		//[1] 完整的路径: 
		String _path = String.format("%s%s\\%s.txt", path, name, targetId);
		File file = new File( _path );
		LinkedList<String[]> list = new LinkedList<String[]>();
		if( file.exists() ){   //[2] 文件存在, 则去读取数据 ..
			BufferedReader reader = null;
			String line = null;
			try {
				reader = new BufferedReader( new FileReader(_path) );
				String[] arr = null;
				while( (line=reader.readLine())!=null ){
					arr = line.split(",");
					list.add( arr );
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally{
				if( reader!=null ){
					try { reader.close(); }
					catch (IOException e) { }
				}
			}
		}
		return list;
	}
	
	//--------------------------- 内部类的区域 ---------------------------------
	class ImageRender implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, 
				Object imgIcon, boolean isSelected, 
				boolean hasFocus, int row, int column) {
			ImageIcon icon = (ImageIcon)imgIcon;
			JLabel label = new JLabel( icon );
			return label;
		}
	}
	
	//[内部类1] 编写一个内部类 TableAdapter
	class TableAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			if( e.getClickCount()==2 ){
				proccessClick( e );
			}
		}
	}
	class FlashTask implements Runnable {
		//[1] 保存用户的图标对象
		ImageIcon head;
		//[2] stop 标志量, 用来停止头像的闪烁 ..
		boolean stop = false;
		public FlashTask( ImageIcon head ){
			this.head = head;
		}
		@Override
		public void run() {
			Image srcImg = head.getImage();   //[1] 原图像数据 ..
			boolean flag = true;
			while( !stop ){
				if( flag ){
					head.setImage( emptyImg );
					flag = false;
				}else{
					head.setImage( srcImg );
					flag = true;
				}
				delay();
				table.updateUI();
			}
			head.setImage( srcImg );    //[2] 记住, 设置回原本的头像 ..
		}
		private void delay(){
			try{ Thread.sleep(250); }
			catch(Exception e){}
		}
	}
	
	//--------------------------- 内部类的区域 [END] ---------------------------------
	
	//--------------------------- 内部类的区域 ---------------------------------
	public DefaultTableModel makeModel(){
		//[1] 创建表头  [Table Header] ..
		Vector<Object> headers = new Vector<Object>();
		headers.add( "用户头像" );
		headers.add( "用户昵称" );
		headers.add( "SocketId" );
		return new DefaultTableModel(headers, 0){
			//[PS] 把单元格的编辑功能去掉, 避免双击时, 单元格的编辑状态被激活。
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	public void removeChatFrame(String socketId) {
		//[1] 从 Map [chats] 中移除  ChatFrame 。
		chats.remove( socketId );
	}

}
