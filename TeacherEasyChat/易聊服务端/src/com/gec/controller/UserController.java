package com.gec.controller;

import com.gec.dao.UserDao;
import com.gec.model.User;
import com.gec.utils.Command;

public class UserController {

    UserDao userDao = new UserDao();

    public Command register(User _user) {
        //[1] �ж��û����ڲ������ݿ⵱�г��֡�
        User daoUser = userDao.getUser(_user, User.REGISTER);
        Command command = null;
        if (daoUser != null) {   //[2] �û������� , ������ע��
            System.out.println("[SERVER] �û������� , ������ע�ᡣ");
            command = Command.makeUserReply("register", "no", _user);
        } else {    //[3] �û��������� , ����ע��
            System.out.println("[SERVER] �û��������� , ����ע�ᡣ");
            boolean ret = userDao.addUser(_user);
            if (ret) {
                command = Command.makeUserReply("register", "yes", _user);
            } else {
                System.out.println("[UserController] ���ݿ�д��ʧ�ܡ�");
            }
        }
        return command;
    }

    public Command login(User _user) {
        //[1] ��֤  "�û���" + "����" �ڲ������ݿ⵱�г��֡�
        User daoUser = userDao.getUser(_user, User.LOGIN);
        Command command = null;
        if (daoUser != null) {    //����, ��½�ɹ�...
            System.out.println("[SERVER] ��½�ɹ�  ...");
            //[2] �������, �β���  _user ����Ϣ��
            //    _user ԭ�е���Ϣ: name, pass, socketId
            _user.setNickName(daoUser.getNickName());    // [ ���ݿ��л�� ]
            _user.setMark(daoUser.getMark());            // [ ���ݿ��л�� ]
            _user.setImg(daoUser.getImg());              // [ ���ݿ��л�� ]

            command = Command.makeUserReply("login", "yes", _user);
        } else {   //�������û�, ��½ʧ�� ...
            System.out.println("[SERVER] ��½ʧ�� ...");
            command = Command.makeUserReply("login", "no", _user);
        }
        return command;
    }

}


//UserController con = new UserController();
//User user = new User();
//user.setName( "kk" );
//user.setPass( "234" );
//user.setNickName( "����" );
//user.setMark( "��������" );
//user.setImg("1");
//con.register( user );


//UserController con = new UserController();
//User user = new User();
//Scanner sc = new Scanner( System.in );
//String line = sc.next();
//String[] arr = line.split(",");
//user.setName( arr[0] );
//user.setPass( arr[1] );
//con.login( user );
//System.out.println( user );





