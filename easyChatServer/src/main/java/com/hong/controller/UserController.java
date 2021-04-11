package com.hong.controller;

import com.hong.dao.UserDao;
import com.hong.model.User;
import com.hong.utils.Command;

public class UserController {

    UserDao dao = new UserDao();

    public Command regist(User user) {
        User u = dao.getUser(user, User.REGISTER);
        if (u == null) {
            System.out.println("�û��������ڣ����Խ���ע��");
            if (dao.regist(user))
                return Command.makeUserReply("regist", "yes", user);
        } else {
            System.out.println("�Ѿ�ӵ�и��û��������ظ�ע�ᣡ����");
            return Command.makeUserReply("regist", "no", user);
        }
        return null;
    }

    public Command login(User user) {
        User u = dao.getUser(user, User.LOGIN);
        if (u == null) {
            System.out.println("û�и��û��������ע�ᣡ����");
            return Command.makeUserReply("login", "no", user);
        } else {
            if (u.getName().equals(user.getName())
                    && u.getPass().equals(user.getPass())) {
                System.out.println("��¼�ɹ�");
                user.setNickname(u.getNickname());
                user.setMark(u.getMark());
                user.setImg(u.getImg());
                return Command.makeUserReply("login", "yes", user);
            } else {
                System.out.println("��¼ʧ��");
                return Command.makeUserReply("login", "no", user);
            }
        }
    }
//	public static void main(String[] args) {
//		UserController con = new UserController();
//		String name = "˹�ٷ�";
//		String pass = "123456";
//		User user = new User(name, pass);
///*		user.setNickname("");
//		user.setMark("");
//		user.setImg("3");*/
//		con.login(user);
//		System.out.println(user);
//	}
}
