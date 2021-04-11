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

    public void createTable(DefaultTableModel model) {
        table = new JTable(model);
        table.setRowHeight(35);      //���������и� ..
        JTableHeader header = table.getTableHeader();
        header.setResizingAllowed(false);

        //[2] �ѱ����������
        JScrollPane scrPane = new JScrollPane(table);
        scrPane.setSize(450, 350);
        //[3] ���뵽 JFrame ���� ..
        add(scrPane);
    }

    public void initFrame(int port) {
        setBounds(150, 150, 500, 450);
        setResizable(false);
        setVisible(true);
        setTitle("���ķ�����  Port:" + port);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //[PS] ��������ģ��
    public DefaultTableModel createTableModel() {
        //[1] ׼���ñ�ͷ������
        Vector<Object> colNames = new Vector<Object>();
        colNames.add("���");
        colNames.add("SocketId");
        colNames.add("�û���");
        colNames.add("IP ��ַ");
        colNames.add("�˿ں�");
        colNames.add("��½ʱ��");
        return new DefaultTableModel(colNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public void addUser(User user) {
        //[1] ��ȡ��������ģ��
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        //[2] ����һ�� Vector �������������� [ һ�е����� ]
        Vector<Object> data = new Vector<Object>();
        int rowCnt = model.getRowCount();  //[PS] ��ȡ����
        data.add(rowCnt + 1);            //[1] ���
        data.add(user.getSocketId());    //[2] SocketId
        data.add(user.getName());        //[3] �û��� Name
        data.add(user.getIp());          //[4] IP ��ַ
        data.add(user.getPort());        //[5] �˿ں�
        data.add(user.getLoginTime());   //[6] ��½ʱ��

        model.addRow(data);       //[3] ��β������һ�е�Ԫ�� ..
        //table.updateUI();         //[4] ˢ�±��Ľ��� ..
    }

    public void removeUser(User user) {

    }

    public void clearData() {

    }

    public ServerFrame(int port) {
        DefaultTableModel model = createTableModel();
        createTable(model);
        initFrame(port);
    }

}
