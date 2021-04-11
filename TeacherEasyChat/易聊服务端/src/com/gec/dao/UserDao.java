package com.gec.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.gec.model.User;
import com.gec.utils.CmdParser;

public class UserDao {

    String path = "e:\\dir\\user.txt";

    public User getUser(User user, int flag) {
        BufferedReader reader = null;
        String line = null;
        User daoUser = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            while ((line = reader.readLine()) != null) {
                daoUser = CmdParser.getUserByLine(line);
                if (user.equals(daoUser, flag)) {
                    return daoUser;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
        return null;
    }

    public boolean addUser(User _user) {
        PrintWriter writer = null;
        boolean ret = false;
        try {
            writer = new PrintWriter(new FileWriter(path, true));
            writer.println(_user);
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return ret;
    }

}
