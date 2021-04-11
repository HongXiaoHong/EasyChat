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
    JLabel selfNick;           //���ѵ��ǳ�
    JLabel selfMark;           //���ѵĸ���ǩ��
    JTable table;

    EngineCallBack callBack;   //ʵ�ʴ�������������: "ClientEngine"

    private Image emptyImg;                //[1] ͷ������(�հ�ͼ��)
    private Map<String, ChatFrame> chats = new HashMap<String, ChatFrame>();   //[2] ����������
    //    ÿһ���û���Ӧһ���������
    //    ��ʲôʱ���������������, �Ժ����ۡ�

    private Map<String, FlashTask> fTasks = new HashMap<String, FlashTask>();  //[3] ͷ������������
    //    ������Ϣ����, �����û���ͷ��������
    //    ��˫��ͷ��, ֹͣͷ�������, �� ChatFrame��
    private String path = "e:\\dir\\";     //[4] �����¼��š�
    private String name;                   //[5] ���������ơ�

    //[1] �����Լ�ͨ�� User ������� ..
    public ListFrame(EngineCallBack callBack, User user) {
        this.callBack = callBack;   //����  "ClientEngine" �����á�
        this.name = user.getName();
        this.emptyImg = ImageLoader.getImageIcon("empty.jpg").getImage();

        selfHead = ImageLoader.getImageIcon("face" + user.getImg() + ".jpg");
        JLabel lblHead = new JLabel(selfHead);
        lblHead.setBounds(7, 7, 50, 50);

        //[2] ��һ��������װ ͷ��, ������Ϣ ...
        JPanel banner = new JPanel(null);
        banner.setBounds(0, 0, 200, 65);
        banner.setBackground(new Color(0, 105, 165));
        banner.add(lblHead);

        setLayout(null);   //ȡ��ԭ�еĲ���  ..
        add(banner);

        //-----------------------------------------------------------------------
        DefaultTableModel model = makeModel();
        table = new JTable(model);
        table.setRowHeight(50);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.addMouseListener(new TableAdapter());

        TableColumnModel col = table.getColumnModel();

        //[1] �õ��� 0 ��, ���õ�Ԫ����Ⱦ����
        col.getColumn(0).setCellRenderer(new ImageRender());

        col.getColumn(0).setPreferredWidth(55);   //[1] �� 0 ��
        col.getColumn(1).setPreferredWidth(210);  //[2] �� 1 ��

        col.getColumn(2).setPreferredWidth(0);
        col.getColumn(2).setMaxWidth(0);   //[1] �������� ..
        col.getColumn(2).setMinWidth(0);   //[2] �������� ..

        table.setBounds(0, 66, 220, 600);
        //table.setBackground( Color.DARK_GRAY );
        add(table);

        //-----------------------------------------------------------------------
        setBounds(150, 150, 200, 750);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showList(Map<String, User> users) {

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);   //[1] �����ԭ�������� ..
        //[2] ���������û�
        Set<String> socketIds = users.keySet();
        for (String socketId : socketIds) {
            Vector<Object> rowData = new Vector<Object>();

            User user = users.get(socketId);
            ImageIcon head = ImageLoader.getImageIcon("face" + user.getImg() + ".jpg");
            user.setHead(head);   //�����û�ͷ��  ..

            rowData.add(head);    //�� 0 �з����û�ͷ�� ..
            rowData.add(user.getNickName());    //�� 1 �з����û��ǳ� ..
            rowData.add(socketId);

            model.addRow(rowData);   //���뵽��������Դ��  ..
        }
    }

    public void showMessage(String socketId, String message) {
//		[1] �� chats(Map) �л�ȡ ChatFrame [�����жϴ����Ƿ�������ʾ]��
        ChatFrame chatFrame = chats.get(socketId);
//		[2] ���� callBack ��ȡ  User ����
        User user = callBack.getUser(socketId);
//		[3] ��ȡ User ���ǳơ�
        String nickName = (user != null) ? user.getNickName() : "δ֪�û�";

//		[4] ��� ChatFrame ����������ʾ��
        if (chatFrame != null) {   //֤������������ʾ ..
            //[4]-1  ����Ϣ��ʾ��  ChatFrame �С�
            chatFrame.addText(nickName, message);
        } else {  //���, ����û����ʾ  ..
            //[4-4] ������Ӧ���û�ͷ���Ƿ�������, ���������  ---> ����ͷ��, ����ʾ�û���
            FlashTask task = fTasks.get(socketId);
            if (task == null) {  //���, ������  ---> ȥ����ͷ�� ..
                task = new FlashTask(user.getHead());   //��������������
                fTasks.put(socketId, task);  //��������� fTask {Map}
                Thread th = new Thread(task);
                th.start();   //[4-7] �����߳�ִ��֮ ..
            }
            appendToFile(socketId, message);   //[4-8] д�뵽�ı� (appendToFile) ..
        }
    }

    //-------------------------- ���ڵķ��� ----------------------------------
    //����: ��һ����Ϣд�뵽�ı���
    //��ʽ: �ǳ�,��Ϣ,ʱ��
    //�� e:\dir\ Ϊ��, �������˺�Ϊ: andy
    private void appendToFile(String socketId, String msg) {
        //[1] �ȿ� e:\dir\andy �Ƿ����, �����ڴ���Ŀ¼��
        //path: ����·��, name: �������û���
        File dir = new File(path + name);
        if (!dir.exists()) {
            dir.mkdirs();   //�����ڴ���Ŀ¼ ..
        }
        //[2] ��ȡ socketId ����Ӧ���û��ǳơ�
        User user = callBack.getUser(socketId);
        String nickName = (user != null) ? user.getNickName() : "δ֪�û�";
        String time = getTime();   //[4] ͨ�� getTime() ��ȡʱ�䡣

        //[3] ��׷����ʽд���ı���
        PrintWriter writer = null;
        File file = new File(dir, socketId + ".txt");
        try {
            writer = new PrintWriter(new FileWriter(file, true));
            writer.printf("%s,%s,%s\n", nickName, msg, time);  //�ǳ�,��Ϣ,ʱ��
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    public String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    //[Note] ����˫���¼�  ..
    private void proccessClick(MouseEvent e) {
        Point point = e.getPoint();
        int rowIndex = table.rowAtPoint(point);

        String nickName = (String) table.getModel().getValueAt(rowIndex, 1);
        String socketId = (String) table.getModel().getValueAt(rowIndex, 2);

        //[1] �� chats �л�ȡ ChatFrame
        ChatFrame chatFrame = chats.get(socketId);
        if (chatFrame != null) {   //[2] ����õ�  chat �򽫽��㵽 Chat �ϡ�
            //[A1] chatFrame  ��ʾ����������Ϸ� ��
        } else {    //[B1] --> [ELSE] ����ò��� chat ����
            FlashTask task = fTasks.get(socketId);
            if (task != null) {               //[B2] ���ͷ��������, ��ȡ��������
                task.stop = true;           //[B3] �� task ��ֹͣ��־����Ϊ  true ��
                fTasks.remove(socketId);  //[B4] �� Map ���Ƴ� task ( ���� )��
            }
            //[B5] �½� ChatFrame ����, CallBack: ClientEngine
            chatFrame = new ChatFrame(callBack, nickName, socketId);
            //[B6] ����  readMsgFromText ��ȡ��Ϣ��
            LinkedList<String[]> list = readMsgFromText(socketId);
            //[B7] ����Ϣ���õ� chatFrame �С�
            chatFrame.setText(list);
            //[B8] �� chatFrame ���뵽 Map [chats] �С�
            chats.put(socketId, chatFrame);
        }
    }

    //path = e:\dir\
    //name = andy
    //socketId.txt
    private LinkedList<String[]> readMsgFromText(String targetId) {
        //[1] ������·��:
        String _path = String.format("%s%s\\%s.txt", path, name, targetId);
        File file = new File(_path);
        LinkedList<String[]> list = new LinkedList<String[]>();
        if (file.exists()) {   //[2] �ļ�����, ��ȥ��ȡ���� ..
            BufferedReader reader = null;
            String line = null;
            try {
                reader = new BufferedReader(new FileReader(_path));
                String[] arr = null;
                while ((line = reader.readLine()) != null) {
                    arr = line.split(",");
                    list.add(arr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return list;
    }

    //--------------------------- �ڲ�������� ---------------------------------
    class ImageRender implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table,
                                                       Object imgIcon, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            ImageIcon icon = (ImageIcon) imgIcon;
            JLabel label = new JLabel(icon);
            return label;
        }
    }

    //[�ڲ���1] ��дһ���ڲ��� TableAdapter
    class TableAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                proccessClick(e);
            }
        }
    }

    class FlashTask implements Runnable {
        //[1] �����û���ͼ�����
        ImageIcon head;
        //[2] stop ��־��, ����ֹͣͷ�����˸ ..
        boolean stop = false;

        public FlashTask(ImageIcon head) {
            this.head = head;
        }

        @Override
        public void run() {
            Image srcImg = head.getImage();   //[1] ԭͼ������ ..
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
            head.setImage(srcImg);    //[2] ��ס, ���û�ԭ����ͷ�� ..
        }

        private void delay() {
            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }
    }

    //--------------------------- �ڲ�������� [END] ---------------------------------

    //--------------------------- �ڲ�������� ---------------------------------
    public DefaultTableModel makeModel() {
        //[1] ������ͷ  [Table Header] ..
        Vector<Object> headers = new Vector<Object>();
        headers.add("�û�ͷ��");
        headers.add("�û��ǳ�");
        headers.add("SocketId");
        return new DefaultTableModel(headers, 0) {
            //[PS] �ѵ�Ԫ��ı༭����ȥ��, ����˫��ʱ, ��Ԫ��ı༭״̬�����
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void removeChatFrame(String socketId) {
        //[1] �� Map [chats] ���Ƴ�  ChatFrame ��
        chats.remove(socketId);
    }

}
