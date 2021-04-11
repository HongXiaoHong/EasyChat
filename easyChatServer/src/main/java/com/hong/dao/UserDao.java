package com.hong.dao;

import com.hong.model.User;
import com.hong.utils.PathUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDao {


    private static final String path = PathUtil.getTxt("userInfo.txt");

    private Map<String, User> users = new HashMap<String, User>();

    public User getUser(User user, int flag) {

        users = parseJson();
        return users.get(user.getName());

    }


    public boolean regist(User user) {

        BufferedWriter writer = null;
        try {
/*			writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(path,true),"GBK"));*/
            writer = new BufferedWriter(
                    new FileWriter(path, true));

            StringBuffer sb = new StringBuffer();
            sb.append("name:");
            sb.append(user.getName());
            sb.append(",pass:");
            sb.append(user.getPass());
            sb.append(",nickname:");
            sb.append(user.getNickname());
            sb.append(",mark:");
            sb.append(user.getMark());
            sb.append(",img:");
            sb.append(user.getImg());

            writer.write(sb.toString());
            writer.newLine();
            writer.flush();
            return true;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("resource")
    private static Map<String, User> parseJson() {
        BufferedReader reader = null;
        Map<String, User> map = new HashMap<String, User>();

        //
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path), "GBK"));
            String row = null;
            String reg = "([^:]+):([^,]+),?";
            Pattern pattern = Pattern.compile(reg);
            Matcher mat = null;
            String[] personInfo = new String[5];
            int index = 0;
            System.out.println("���ļ��ж�ȡ���ݵ��ڴ���");
            while ((row = reader.readLine()) != null) {
                if (row.length() > 0) {
                    index = 0;
                    mat = pattern.matcher(row);
                    while (mat.find())
                        personInfo[index++] = mat.group(2);

                    map.put(personInfo[0],
                            new User(personInfo[0], personInfo[1]
                                    , personInfo[2], personInfo[3], personInfo[4]));
                }
            }
            return map;
        } catch (FileNotFoundException e1) {
            System.out.println("��·����" + path + "����������·�������ڣ��Ҳ����ļ�������·���Ƿ���ȷ");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }


}
