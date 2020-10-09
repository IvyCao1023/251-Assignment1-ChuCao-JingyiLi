package test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;

public class test extends JFrame{

    JFrame f;
    JLabel label;
    JTextArea textArea;
    JFileChooser fileChooser;
    FileInputStream fileInStream;
    JMenuItem item0, item1, item2, item3, item4, editItem0, editItem1, editItem2, editItem3, editItem4, editItem5, aboutItem;
    JMenu menu1, menu2, menu3;

    int boundX = 400, boundY = 300;

    public test() {

        init();
        eventListener();
    }

    public void init(){
        // TODO Auto-generated constructor stub
        f=new JFrame("Test Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(boundX, boundY, 400, 200);

        Container contentPane=f.getContentPane();
        textArea=new JTextArea();
        JScrollPane scrollPane=new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 300));
        JPanel panel=new JPanel();
        JMenuBar menuBar1=new JMenuBar();  //添加菜单条组件
        f.setJMenuBar(menuBar1);          //将菜单栏添加到顶层容器中

        menu1=new JMenu("File");
        menu2=new JMenu("Edit");
        menu3=new JMenu("About");

        //将菜单组件添加到菜单条组件中
        menuBar1.add(menu1);
        menuBar1.add(menu2);
        menuBar1.add(menu3);

        //创建file菜单项组件
        item0 = new JMenuItem("New");
        item1=new JMenuItem("Open");
        item2=new JMenuItem("Save");
        item3=new JMenuItem("Print");
        item4=new JMenuItem("Exit");
        menu1.add(item0);
        menu1.add(item1);
        menu1.add(item2);
        menu1.addSeparator();           //菜单项之间的分隔线组件
        menu1.add(item3);
        menu1.addSeparator();
        menu1.add(item4);

        //创建edit菜单项组件
        editItem0 = new JMenuItem("Search");
        editItem1 = new JMenuItem("Select All");
        editItem2 = new JMenuItem("Copy");
        editItem3 = new JMenuItem("Past");
        editItem4 = new JMenuItem("Cut");
        editItem5 = new JMenuItem("Time And Date");
        menu2.add(editItem0);
        menu2.addSeparator();
        menu2.add(editItem1);
        menu2.add(editItem2);
        menu2.add(editItem3);
        menu2.add(editItem4);
        menu2.addSeparator();
        menu2.add(editItem5);

        //创建about组件
        aboutItem = new JMenuItem("About");
        menu3.add(aboutItem);

        //设置顶层容器类的可见性
        f.setVisible(true);
        label=new JLabel("",JLabel.CENTER);
        contentPane.add(label, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(panel, BorderLayout.SOUTH);
        f.pack();
    }

    public void eventListener(){
        //事件监听
        // new事件监听
        item0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int i=JOptionPane.showConfirmDialog(null, "Please confirm your file was saved before open a new",
                        "Warning",JOptionPane.YES_NO_CANCEL_OPTION);
                //通过对话框中按钮的选择来决定结果，单机yes时，窗口直接消失
                if(i==0){
                    f.dispose();
                    boundX += 100;
                    boundY += 100;
                    init();
                    eventListener();
                }

            }
        });

        //open 事件
        item1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(item1)==JFileChooser.APPROVE_OPTION) {//
                    File file = chooser.getSelectedFile();
                    textArea.setText(file.getName()+":"+file.getPath()+"\n"+file.length());
                    readFile(file);
                };

            }
        });

        //save事件
        item2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(item2)==JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    writeFile(file.getPath());
                }
            }
        });

        //处理退出菜单项的动作事件
        item4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e) {
                // TODO Auto-generated method stub
                int i=JOptionPane.showConfirmDialog(null, "是否真的退出系统",
                        "退出确认对话框",JOptionPane.YES_NO_CANCEL_OPTION);
                //通过对话框中按钮的选择来决定结果，单机yes时，窗口直接消失
                if(i==0)
                    f.dispose();

            }
        });

        //time and date事件
        editItem5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar rightNow=Calendar.getInstance();
                Date date=rightNow.getTime();
                textArea.insert(date.toString(),textArea.getCaretPosition());
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null, "Members: Wang Kexin, Zhao Yizhen","About us", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public void copyFile(File file){//复制文件
        File to=new File(file.getAbsolutePath()+"_copy");
        if (file.isFile()) {
            byte[] buf = new byte[1024];//字节流
            int length=0;
            try {
                FileInputStream in=new FileInputStream(file);
                FileOutputStream out=new FileOutputStream(to);
                while((length=in.read(buf))>0){
                    out.write(buf,0,length);
                }
                out.flush();
                in.close();
                out.close();
            } catch (Exception e) {
                // TODO Auto-generated catchblock
                e.printStackTrace();
            }

        }
    }

    public void readFile(File file){//读文件
        BufferedReader bReader;
        try {
            bReader=new BufferedReader(new FileReader(file));
            StringBuffer sBuffer=new StringBuffer();
            String str;
            while((str=bReader.readLine())!=null){
                sBuffer.append(str+'\n');
                System.out.println(str);
            }
            textArea.setText(sBuffer.toString());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public void writeFile(String savepath){//写文件
        FileOutputStream fos= null;
        try {
            fos=new FileOutputStream(savepath);
            fos.write(textArea.getText().getBytes());
            fos.close();
            System.out.println("已保存");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        textArea.getText();
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new test();

    }
}
