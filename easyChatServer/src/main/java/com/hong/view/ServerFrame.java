package com.hong.view;

import com.hong.model.User;
import com.hong.utils.PathUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public class ServerFrame extends JFrame {

    JTable table;

    public static void main(String[] args) {


        ServerFrame table = new ServerFrame();
    }

    public ServerFrame() {
        DefaultTableModel model = createTableModel();
        createTable(model);
    }

    public void createTable(DefaultTableModel model) {
        table = new JTable(model);
        //�����и�
        table.setRowHeight(50);
        //���ù�����
        JScrollPane srcPane = new JScrollPane(table);
        srcPane.setSize(450, 350);
        add(srcPane);

        setBounds(150, 150, 500, 450);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String imagePath = PathUtil.getTxt("hong.jpg");
        System.out.println(imagePath);
        ImageIcon hong = new ImageIcon(imagePath);
        setIconImage(hong.getImage());
        setResizable(false);
    }

    public DefaultTableModel createTableModel() {
        Vector<Object> colNames = new Vector<Object>();
        colNames.add("���");
        colNames.add("SocketId");
        colNames.add("�û���");
        colNames.add("IP��ַ");
        colNames.add("�˿ں�");
        colNames.add("��½ʱ��");
        return new DefaultTableModel(colNames, 0);
    }

    public void addUser(User user) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Vector<Object> data = new Vector<Object>();
        int rowCount = model.getRowCount();//��ȡ����
        data.add(rowCount + 1);//���
        data.add(user.getSocketId());//socketId
        data.add(user.getName());//�û���
        data.add(user.getIp());//ip
        data.add(user.getPort());//port
        data.add(user.getLoginTime());//d��¼ʱ��
        model.addRow(data);//��β�����һ�е�Ԫ��
        //table.updateUI();//ˢ�����ݣ���ˢ�£����ݲ�����

    }

    public void removeUser(User user) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        int columnCount = model.getColumnCount();
        System.out.println("rowCount" + rowCount + "columnCount" + columnCount);
        int rowNow = 0;
        int columnNom = 0;
        String temp = null;
        for (; rowNow < rowCount; rowNow++) {
            temp = (String) model.getValueAt(rowNow, 1);
            System.out.println("����ǰ����Ϊ��" + (rowNow + 1) + "������û���socketId�ǣ���" + temp);
            if (user.getSocketId().equals(temp)) {
                model.removeRow(rowNow);//�������Ƴ���

                break;
            }
        }
    }

    public void clearData(User user) {

    }
}
