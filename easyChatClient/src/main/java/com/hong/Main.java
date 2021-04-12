package com.hong;

import com.hong.controller.ClientEnigine;
import com.hong.controller.EngineCallBack;
import com.hong.model.User;
import com.hong.utils.PathUtil;
import com.hong.view.Regist;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.StandardBorderPainter;
import org.jvnet.substance.button.StandardButtonShaper;
import org.jvnet.substance.painter.StandardGradientPainter;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.theme.SubstanceTerracottaTheme;
import org.jvnet.substance.watermark.SubstanceBubblesWatermark;

import javax.swing.*;
import java.awt.*;


public class Main extends JFrame {

    private static ClientEnigine server = new ClientEnigine();

    private EngineCallBack callBack;
    private boolean waitFlag;

    public void setWaoitFlag(boolean wait) {
        this.waitFlag = wait;
    }

    public Main() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(200, 200, 400, 400);
        setLayout(null);
        setTitle("登录");
        ImageIcon icon = PathUtil.getImageIcon("hong.jpg");
        setIconImage(icon.getImage());
        JLabel userLabel = new JLabel("用户名 :");
        final JTextField user = new JTextField();
        JLabel passLabel = new JLabel("密码:");
        final JTextField pass = new JTextField();
        JButton login = new JButton("登录");
        JButton regist = new JButton("注册");

        userLabel.setBounds(50, 80, 80, 40);
        userLabel.setFont(new Font("楷体", Font.BOLD, 15));
        user.setBounds(130, 80, 200, 40);

        passLabel.setBounds(50, 150, 80, 40);
        passLabel.setFont(new Font("楷体", Font.BOLD, 15));
        pass.setBounds(130, 150, 200, 40);

        login.setBounds(80, 240, 100, 40);
        login.setFont(new Font("楷体", Font.BOLD, 15));
        regist.setBounds(220, 240, 100, 40);
        regist.setFont(new Font("楷体", Font.BOLD, 15));

        login.addActionListener(e -> {
            // 登录的动作
            if (!waitFlag) {
                String userName = user.getText();
                String password = pass.getText();
                User user1 = new User(userName, password);
                server.connect("localhost", 1090);
                server.doLogin(user1);

            } else {
                System.out.println("正在连接，请稍后");
            }

            //新加的
            //---------------------------------------------

            //---------------------------------------------


            dispose();
        });
        regist.addActionListener(e -> {
            // 注册的动作
            Regist regist1 = new Regist(server);
            dispose();
        });

        add(userLabel);
        add(user);
        add(passLabel);
        add(pass);
        add(login);
        add(regist);


        setVisible(true);
    }


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new SubstanceLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            SubstanceLookAndFeel.setCurrentTheme(
                    new SubstanceTerracottaTheme());
            SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
            SubstanceLookAndFeel.setCurrentButtonShaper(new StandardButtonShaper());
            SubstanceLookAndFeel.setCurrentWatermark(new SubstanceBubblesWatermark());
            SubstanceLookAndFeel.setCurrentBorderPainter(new StandardBorderPainter());
            SubstanceLookAndFeel.setCurrentGradientPainter(new StandardGradientPainter());
            final Main client = new Main();

        } catch (UnsupportedLookAndFeelException e) {

            e.printStackTrace();
        }


    }
}