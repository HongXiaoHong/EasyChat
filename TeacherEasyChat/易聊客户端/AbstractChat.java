package com.gec.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.gec.controller.EngineCallBack;

public abstract class AbstractChat extends JFrame {
	
	protected StyledDocument styDoc = new DefaultStyledDocument();
	protected JTextPane txtPane;
	protected JTextArea selfArea;
	protected JButton btnSend;
	
	private JPanel makeTop(){
		txtPane = new JTextPane( styDoc );
		JScrollPane sclPane = new JScrollPane( txtPane );
		txtPane.setEditable( false );
		JPanel hisPanel = new JPanel( new BorderLayout() );
		hisPanel.add( sclPane, BorderLayout.CENTER );
		hisPanel.setPreferredSize( new Dimension(500,350) );
		return hisPanel;
	}
	
	private JPanel makeMsgPanel( JPanel bottomPanel ){
		JPanel msgPanel = new JPanel( new BorderLayout(0,0) );
		selfArea = new JTextArea();
		selfArea.setFont( new Font( "SimSun", 0, 15 ) );
		selfArea.setPreferredSize( new Dimension(500,100) );
		selfArea.setBorder(new LineBorder( Color.GRAY, 1, false));
		
		msgPanel.add( selfArea, BorderLayout.CENTER );
		msgPanel.add( bottomPanel, BorderLayout.SOUTH );
		
		msgPanel.setPreferredSize( new Dimension(500,150) );
		return msgPanel;
	}
	
	private JPanel makeBtnPanel(){
		JPanel btnPanel = new JPanel();
		btnSend = new JButton("发送消息");
		btnSend.setPreferredSize( new Dimension(92, 28) );
		
		JButton btnClose = new JButton("关闭窗口");
		btnClose.setPreferredSize( new Dimension(92, 28) );
		
		btnPanel.setPreferredSize( new Dimension(500, 35) );
		btnPanel.add( btnSend );
		btnPanel.add( btnClose );
		return btnPanel;
	}
	
	private JPanel makeBottomPanel( JPanel btnPanel ){
		JPanel bottomPanel = new JPanel( new BorderLayout(0,0) );
		bottomPanel.setPreferredSize( new Dimension(500, 37) );
		bottomPanel.add( btnPanel );
		return btnPanel;
	}
	
	protected void initViews(){

		JPanel hisPanel = makeTop();
		JPanel btnPanel = makeBtnPanel();
		JPanel bottomPanel = makeBottomPanel( btnPanel );
		JPanel msgPanel = makeMsgPanel( bottomPanel );
		
		JSplitPane jSpPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
								hisPanel, msgPanel);
		jSpPane.setDividerLocation( 320 );   //设置分隔条的位置 ...
		setContentPane( jSpPane );
		
		//[PS] Content: 布局 ..
		setBounds( 500, 400, 500, 500 );
		setResizable( false );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		setVisible(true);

		createStyle("Style01", styDoc, 15, Color.RED);
	}

	protected void insertText(StyledDocument styledDoc, String content, 
			String style) {
		try {
			styledDoc.insertString( styledDoc.getLength(), content, 
					styledDoc.getStyle(style) );
		} catch (BadLocationException e) {
			System.err.println("BadLocationException: " + e);
		}
	}

	private void createStyle(String styName, StyledDocument doc, 
			int size, Color color) {
		Style sys = StyleContext.getDefaultStyleContext()
				.getStyle( StyleContext.DEFAULT_STYLE );
		//[PS] 先删除这种 Style, 假如他存在。
		try { doc.removeStyle(styName); }
		catch (Exception e) { 	}

		Style sty = doc.addStyle(styName, sys);    //[1] 加入样式到文档
		StyleConstants.setFontSize(sty, size);     //[2] 设置字体大小
		StyleConstants.setForeground(sty, color);  //[3] 设置字体颜色
		StyleConstants.setAlignment(sty, StyleConstants.ALIGN_RIGHT);
	}
}