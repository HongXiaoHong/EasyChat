package com.hong.view;

import com.hong.controller.ClientEnigine;
import com.hong.controller.EngineCallBack;
import com.hong.model.User;
import com.hong.utils.PathUtil;
import com.hong.utils.TreadPoolUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class ListFrame extends JFrame {

    ImageIcon selfHead;
    JLabel selfNick;
    JLabel selfMark;
    JTable table;
    private Image emptyImg;
    private Map<String, ChartFrame> chats =
            new LinkedHashMap<String, ChartFrame>();
    private Map<String, FlashTask> fTasks =
            new HashMap<String, FlashTask>();
    EngineCallBack callBack;
    private String name;
    ClientEnigine client;

    public ClientEnigine getClient() {
        return client;
    }

    public void setClient(ClientEnigine client) {
        this.client = client;
    }

    public ListFrame(final EngineCallBack callBack, User user) {

        this.callBack = callBack;
        this.name = user.getName();
        this.emptyImg = PathUtil.getImageIcon("empty.jpg").getImage();
        selfHead = PathUtil.getImageIcon("face" + user.getImg() + ".jpg");
        JLabel lblHead = new JLabel(selfHead);
        System.out.println(user.getNickname() + user.getMark());
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
        table.setBackground(new Color(237, 237, 237));
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
    }


    public DefaultTableModel mokeModel() {
        // 创建表头
        Vector<Object> headers = new Vector<Object>();
        headers.add("ͷ��");
        headers.add("�ǳ�");
        headers.add("SocketId");
        return new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int row, int colunm) {
                return false;
            }
        };
    }

    public void showMessage(String socketId, String message) {
        ImageIcon head = callBack.getUserHead(socketId);
        System.out.println("showMessage:-------head:----------" + head);
        ChartFrame chart = null;
        User user = callBack.getUser(socketId);
        String nickName = user.getNickName();
        if (chats.get(socketId) == null) {
            chart = new ChartFrame(callBack, nickName, socketId);
            this.chats.put(socketId, chart);
        } else {
            chart = chats.get(socketId);
        }
        chart.settxtOtherMsgText(message);
        chart.setVisible(false);
        if (fTasks.get(socketId) == null) {
            FlashTask task = new FlashTask(head);
            fTasks.put(socketId, task);
            TreadPoolUtils.submit(task);
        }
    }

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
            if (row % 2 == 0) {
                setBackground(new Color(237, 237, 237));
            } else {
                setBackground(Color.WHITE);
            }
            return super.getTableCellRendererComponent(table, cell, isSelected,
                    hasFocus, row, column);
        }
    }

    class TableAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                processClick(e);
            }
        }
    }

    class FlashTask implements Runnable {

        ImageIcon head;
        boolean stop = false;

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
            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }
    }

    public void processClick(MouseEvent e) {
        //˫���¼�
        Point point = e.getPoint();
        int row = table.rowAtPoint(point);
        int col = table.columnAtPoint(point);
        System.out.println("����" + row + ",����" + col);
        String nickName = (String) table.getModel().getValueAt(row, 1);
        String socketId = (String) table.getModel().getValueAt(row, 2);
        System.out.println("socketId" + socketId);
        ChartFrame chart = null;
        if (chats.get(socketId) == null) {
            chart = new ChartFrame(callBack, nickName, socketId);
            this.chats.put(socketId, chart);
        } else {
            chart = chats.get(socketId);
        }
        chart.setVisible(true);
        FlashTask flash = fTasks.get(socketId);
        if (flash != null) {
            flash.stop = true;
            fTasks.remove(socketId);
        }
    }

}
