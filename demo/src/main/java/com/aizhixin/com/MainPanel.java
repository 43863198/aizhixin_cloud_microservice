package com.aizhixin.com;

/**
 * @author: Created by jianwei.wu
 * @E-mail: wujianwei@aizhixin.com
 * @Date: 2017-09-04
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;

public class MainPanel extends Panel implements MouseListener{
    private static final int COLUMN = 16;//列数
    private static final int ROW = 16;//行数
    private static final int GAP = 40;//间距
    private static boolean isBlack = true;
    private static int click_X;
    private static int click_Y;
    private char[][] allChess= new char[ROW][COLUMN];
    public MainPanel(){
        super();
        for(int i=0;i<allChess.length;i++){
            for(int j=0;j<allChess[i].length;j++){
                allChess[i][j]='*';
            }
        }
    }
    public void paint(Graphics g){
        for(int i=0;i<ROW;i++){//划横线
            g.setColor(Color.BLACK);
            g.drawLine(20, 20+i*GAP, 640-20, 20+i*GAP);
        }
        for(int i=0;i<COLUMN;i++){//划纵线
            g.setColor(Color.BLACK);
            g.drawLine(20+i*GAP, 20, 20+i*GAP, 640-20);
        }
        for(int i=0;i<allChess.length;i++){
            for(int j=0;j<allChess[i].length;j++){
                if(allChess[i][j]=='~'){
                    g.setColor(Color.WHITE);
                    g.fillOval(5+i*40, 5+j*40, 30, 30);
                    g.drawOval(5+i*40, 5+j*40, 30, 30);
                }
                if(allChess[i][j]=='!'){
                    g.setColor(Color.BLACK);
                    g.fillOval(5+i*40, 5+j*40, 30, 30);
                    g.drawOval(5+i*40, 5+j*40, 30, 30);
                }
            }
        }
    }
    public boolean isWin(int x,int y,boolean isColor){//判断是否为5个相同的棋子，是返回true，否返回false
        char ch=allChess[x][y];
        /*  横向判断    */
        int RLastX = x;
        while(RLastX>=0 && allChess[RLastX][y]==ch){//横向判断是否到达5个相同的棋子
            RLastX --;
        }
        int RNum = 0;//统计横向相同的棋子数
        RLastX ++;
        while(RLastX<allChess.length && allChess[RLastX][y]==ch){//横向判断是否到达5个相同的棋子
            RNum ++;
            RLastX ++;
        }
        /* 纵向判断   */
        int LLastY = y;
        while(LLastY>=0 && allChess[x][LLastY]==ch){//纵向判断是否到达5个相同的棋子
            LLastY --;
        }
        int LNum = 0;//统计纵向相同的棋子数
        LLastY ++;
        while(LLastY<allChess[x].length && allChess[x][LLastY]==ch){//纵向判断是否到达5个相同的棋子
            LLastY ++;
            LNum ++;
        }
        /* 左下右上判断  */
        int LDLastX = x;
        int RULastY = y;
        while(LDLastX>=0 && RULastY<allChess.length && allChess[LDLastX][RULastY]==ch){
            LDLastX --;
            RULastY ++;
        }
        int LDNum = 0;
        LDLastX ++;
        RULastY --;
        while(LDLastX<allChess.length && RULastY>=0 && allChess[LDLastX][RULastY]==ch){
            LDNum ++;
            LDLastX ++;
            RULastY --;
        }
        /* 左上右下判断  */
        int RULastX = x;
        int LDLastY = y;
        while(RULastX>=0 && LDLastY>=0 && allChess[RULastX][LDLastY]==ch){
            RULastX --;
            LDLastY --;
        }
        int RUNum = 0;
        RULastX ++;
        LDLastY ++;
        while(RULastX>=0 && LDLastY<allChess.length && allChess[RULastX][LDLastY]==ch){
            RULastX ++;
            LDLastY ++;
            RUNum ++;
        }
        if(RNum>=5||LNum>=5||RUNum>=5||LDNum>=5){
            return true;
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        //System.out.println(e.getX());
        //e.getY();
    }
    public void mousePressed(MouseEvent e) {//鼠标点击事件处理过程
        // TODO Auto-generated method stub
        int click_x = e.getX();
        int click_y = e.getY();
        int chess_x = Math.round((float)(click_x-20)/GAP);
        int chess_y = Math.round((float)(click_y-20)/GAP);
        click_X = chess_x;
        click_Y = chess_y;
        if(isBlack==true&&allChess[chess_x][chess_y]=='*'){
            allChess[chess_x][chess_y] = '!';
            isBlack = false;
        }
        if(isBlack==false&&allChess[chess_x][chess_y]=='*'){
            allChess[chess_x][chess_y] = '~';
            isBlack = true;
        }

        System.out.println(e.getX());
        System.out.println(e.getY());
        repaint();
        for(int j=0;j<16;j++){
            for(int i=0;i<16;i++){
                System.out.print(allChess[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
        if(isWin(chess_x,chess_y,isBlack)){
            System.out.println("你赢了");
        }
        if(isWin(chess_x,chess_y,isBlack)){
            if(isBlack){
                JOptionPane.showMessageDialog(null,"白子赢了");
            }else{
                JOptionPane.showMessageDialog(null,"黑子赢了");
            }
            System.exit(0);
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
    public void setAllChess(char[][] allChess) {
        this.allChess = allChess;
    }
    public char[][] getAllChess() {
        return allChess;
    }
}