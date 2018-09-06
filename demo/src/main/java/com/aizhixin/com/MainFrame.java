package com.aizhixin.com;
/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-04
 */
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

public class MainFrame extends JFrame{
    public static void main(String[] args) {
        MainPanel panel = new MainPanel();
        MainFrame frame = new MainFrame("五子棋");
        frame.setSize(680, 680);
        panel.setBackground(Color.GRAY);
        frame.add(panel,BorderLayout.CENTER);
        frame.setResizable(false);
        panel.addMouseListener(panel);
        frame.setVisible(true);
    }
    public MainFrame(){
        super();
    }
    public MainFrame(String str){
        super(str);
    }
}