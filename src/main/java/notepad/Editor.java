package notepad;

import com.lowagie.text.DocumentException;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.undo.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import java.text.DateFormat;


class Editor extends JFrame implements ActionListener,DocumentListener
{ //菜单
    JMenu fileMenu,editMenu,viewMenu,helpMenu;
    //右键弹出菜单项
    JPopupMenu popupMenu;
    JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,popupMenu_Delete,popupMenu_SelectAll;
    //“文件”的菜单项
    JMenuItem fileMenu_Export,fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,fileMenu_Print,fileMenu_Exit;
    //“编辑”的菜单项
    JMenuItem editMenu_Undo,editMenu_Cut,editMenu_Copy,editMenu_Paste,editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_SelectAll;
    //“查看”的菜单项
    JCheckBoxMenuItem viewMenu_Status;
    //“帮助”的菜单项
    JMenuItem helpMenu_AboutNotepad;
    //“文本”编辑区域
    JTextArea editArea;
    //状态栏标签
    JLabel statusLabel;
    //系统剪贴板
    Toolkit toolkit=Toolkit.getDefaultToolkit();
    Clipboard clipBoard=toolkit.getSystemClipboard();
    //创建撤销操作管理器(与撤销操作有关)
    protected UndoManager undo=new UndoManager();
    protected UndoableEditListener undoHandler=new UndoHandler();
    //其他变量
    String oldValue;//存放编辑区原来的内容，用于比较文本是否有改动
    String tempString;
    boolean isNewFile=true;//是否新文件(未保存过的)
    File currentFile;//当前文件名
    //构造函数开始
    public void editor()
    {
        this.setTitle("Java记事本");
        //改变系统默认字体
        Font font = new Font("Dialog", Font.PLAIN, 12);
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
        //创建菜单条
        JMenuBar menuBar=new JMenuBar();
        //创建文件菜单及菜单项并注册事件监听
        fileMenu=new JMenu("File(F)");
        fileMenu.setMnemonic('F');//设置快捷键ALT+F

        fileMenu_New=new JMenuItem("新建(N)");
        fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_MASK));
        fileMenu_New.addActionListener(this);

        fileMenu_Open=new JMenuItem("打开(O)...");
        fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
        fileMenu_Open.addActionListener(this);

        fileMenu_Save=new JMenuItem("保存(S)");
        fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
        fileMenu_Save.addActionListener(this);

        fileMenu_SaveAs=new JMenuItem("另存为(A)...");
        fileMenu_SaveAs.addActionListener(this);

        fileMenu_Print=new JMenuItem("打印(P)...");
        fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        fileMenu_Print.addActionListener(this);

        fileMenu_Export = new JMenuItem("导出");
        fileMenu_Export.addActionListener(this);

        fileMenu_Exit=new JMenuItem("退出(X)");
        fileMenu_Exit.addActionListener(this);

        //创建编辑菜单及菜单项并注册事件监听
        editMenu=new JMenu("Edit(E)");
        editMenu.setMnemonic('E');//设置快捷键ALT+E
        //当选择编辑菜单时，设置剪切、复制、粘贴、删除等功能的可用性
        editMenu.addMenuListener(new MenuListener()
        { public void menuCanceled(MenuEvent e)//取消菜单时调用
        { checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
        }
            public void menuDeselected(MenuEvent e)//取消选择某个菜单时调用
            { checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
            }
            public void menuSelected(MenuEvent e)//选择某个菜单时调用
            { checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
            }
        });

        editMenu_Undo=new JMenuItem("撤销(U)");
        editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
        editMenu_Undo.addActionListener(this);
        editMenu_Undo.setEnabled(false);

        editMenu_Cut=new JMenuItem("剪切(T)");
        editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
        editMenu_Cut.addActionListener(this);

        editMenu_Copy=new JMenuItem("复制(C)");
        editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
        editMenu_Copy.addActionListener(this);

        editMenu_Paste=new JMenuItem("粘贴(P)");
        editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
        editMenu_Paste.addActionListener(this);

        editMenu_Delete=new JMenuItem("删除(D)");
        editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
        editMenu_Delete.addActionListener(this);

        editMenu_Find=new JMenuItem("查找(F)...");
        editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
        editMenu_Find.addActionListener(this);

        editMenu_FindNext=new JMenuItem("查找下一个(N)");
        editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3,0));
        editMenu_FindNext.addActionListener(this);

        editMenu_SelectAll = new JMenuItem("全选",'A');
        editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        editMenu_SelectAll.addActionListener(this);

        //创建查看菜单及菜单项并注册事件监听
        viewMenu=new JMenu("View(V)");
        viewMenu.setMnemonic('V');//设置快捷键ALT+V

        viewMenu_Status=new JCheckBoxMenuItem("状态栏(S)");
        viewMenu_Status.setMnemonic('S');//设置快捷键ALT+S
        viewMenu_Status.setState(true);
        viewMenu_Status.addActionListener(this);

        //创建帮助菜单及菜单项并注册事件监听
        helpMenu = new JMenu("Help(H)");
        helpMenu.setMnemonic('H');//设置快捷键ALT+H

        helpMenu_AboutNotepad = new JMenuItem("关于记事本(A)");
        helpMenu_AboutNotepad.addActionListener(this);

        //向菜单条添加"文件"菜单及菜单项
        menuBar.add(fileMenu);
        fileMenu.add(fileMenu_New);
        fileMenu.add(fileMenu_Open);
        fileMenu.add(fileMenu_Save);
        fileMenu.add(fileMenu_SaveAs);
        fileMenu.addSeparator(); //分隔线
        fileMenu.add(fileMenu_Print);
        fileMenu.add(fileMenu_Export);
        fileMenu.addSeparator(); //分隔线
        fileMenu.add(fileMenu_Exit);

        //向菜单条添加"编辑"菜单及菜单项
        menuBar.add(editMenu);
        editMenu.add(editMenu_Undo);
        editMenu.addSeparator(); //分隔线
        editMenu.add(editMenu_Cut);
        editMenu.add(editMenu_Copy);
        editMenu.add(editMenu_Paste);
        editMenu.add(editMenu_Delete);
        editMenu.addSeparator(); //分隔线
        editMenu.add(editMenu_Find);
        editMenu.add(editMenu_FindNext);
        editMenu.addSeparator(); //分隔线
        editMenu.add(editMenu_SelectAll);

        //向菜单条添加"查看"菜单及菜单项
        menuBar.add(viewMenu);
        viewMenu.add(viewMenu_Status);

        //向菜单条添加"帮助"菜单及菜单项
        menuBar.add(helpMenu);
        helpMenu.addSeparator();
        helpMenu.add(helpMenu_AboutNotepad);

        //向窗口添加菜单条
        this.setJMenuBar(menuBar);

        //创建文本编辑区并添加滚动条
        editArea=new JTextArea(20,50);
        JScrollPane scroller=new JScrollPane(editArea);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scroller,BorderLayout.CENTER);//向窗口添加文本编辑区
        editArea.setWrapStyleWord(true);//设置单词在一行不足容纳时换行
        editArea.setLineWrap(true);//设置文本编辑区自动换行默认为true,即会"自动换行"
        //this.add(editArea,BorderLayout.CENTER);//向窗口添加文本编辑区
        oldValue=editArea.getText();//获取原文本编辑区的内容

        //编辑区注册事件监听(与撤销操作有关)
        editArea.getDocument().addUndoableEditListener(undoHandler);
        editArea.getDocument().addDocumentListener(this);

        //创建右键弹出菜单
        popupMenu=new JPopupMenu();
        popupMenu_Undo=new JMenuItem("撤销(U)");
        popupMenu_Cut=new JMenuItem("剪切(T)");
        popupMenu_Copy=new JMenuItem("复制(C)");
        popupMenu_Paste=new JMenuItem("粘帖(P)");
        popupMenu_Delete=new JMenuItem("删除(D)");
        popupMenu_SelectAll=new JMenuItem("全选(A)");

        popupMenu_Undo.setEnabled(false);

        //向右键菜单添加菜单项和分隔符
        popupMenu.add(popupMenu_Undo);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_Cut);
        popupMenu.add(popupMenu_Copy);
        popupMenu.add(popupMenu_Paste);
        popupMenu.add(popupMenu_Delete);
        popupMenu.addSeparator();
        popupMenu.add(popupMenu_SelectAll);

        //文本编辑区注册右键菜单事件
        popupMenu_Undo.addActionListener(this);
        popupMenu_Cut.addActionListener(this);
        popupMenu_Copy.addActionListener(this);
        popupMenu_Paste.addActionListener(this);
        popupMenu_Delete.addActionListener(this);
        popupMenu_SelectAll.addActionListener(this);

        //文本编辑区注册右键菜单事件
        editArea.addMouseListener(new MouseAdapter()
        { public void mousePressed(MouseEvent e)
        { if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
        { popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
        }
            checkMenuItemEnabled();//设置剪切，复制，粘帖，删除等功能的可用性
            editArea.requestFocus();//编辑区获取焦点
        }
            public void mouseReleased(MouseEvent e)
            { if(e.isPopupTrigger())//返回此鼠标事件是否为该平台的弹出菜单触发事件
            { popupMenu.show(e.getComponent(),e.getX(),e.getY());//在组件调用者的坐标空间中的位置 X、Y 显示弹出菜单
            }
                checkMenuItemEnabled();//设置剪切，复制，粘帖，删除等功能的可用性
                editArea.requestFocus();//编辑区获取焦点
            }
        });//文本编辑区注册右键菜单事件结束

        //创建和添加状态栏
        statusLabel=new JLabel("　按F1获取帮助");
        this.add(statusLabel,BorderLayout.SOUTH);//向窗口添加状态栏标签
        //设置窗口在屏幕上的位置、大小和可见性
        this.setLocation(100,100);
        this.setSize(700,600);
        this.setVisible(true);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        editArea.setText(dateFormat.format(System.currentTimeMillis()));
        //添加窗口监听器
        addWindowListener(new WindowAdapter()
        { public void windowClosing(WindowEvent e)
        { exitWindowChoose();
        }
        });

        checkMenuItemEnabled();
        editArea.requestFocus();
    }//构造函数Notepad结束

    //设置菜单项的可用性：剪切，复制，粘帖，删除功能
    public void checkMenuItemEnabled()
    { String selectText=editArea.getSelectedText();
        if(selectText==null)
        { editMenu_Cut.setEnabled(false);
            popupMenu_Cut.setEnabled(false);
            editMenu_Copy.setEnabled(false);
            popupMenu_Copy.setEnabled(false);
            editMenu_Delete.setEnabled(false);
            popupMenu_Delete.setEnabled(false);
        }
        else
        { editMenu_Cut.setEnabled(true);
            popupMenu_Cut.setEnabled(true);
            editMenu_Copy.setEnabled(true);
            popupMenu_Copy.setEnabled(true);
            editMenu_Delete.setEnabled(true);
            popupMenu_Delete.setEnabled(true);
        }
        //粘帖功能可用性判断
        Transferable contents=clipBoard.getContents(this);
        if(contents==null)
        { editMenu_Paste.setEnabled(false);
            popupMenu_Paste.setEnabled(false);
        }
        else
        { editMenu_Paste.setEnabled(true);
            popupMenu_Paste.setEnabled(true);
        }
    }//方法checkMenuItemEnabled()结束

    //关闭窗口时调用
    public void exitWindowChoose()
    { editArea.requestFocus();
        String currentValue=editArea.getText();
        if(currentValue.equals(oldValue)==true)
        { System.exit(0);
        }
        else
        { int exitChoose=JOptionPane.showConfirmDialog(this,"您的文件尚未保存，是否保存？","退出提示",JOptionPane.YES_NO_CANCEL_OPTION);
            if(exitChoose==JOptionPane.YES_OPTION)
            { //boolean isSave=false;
                if(isNewFile)
                {
                    String str=null;
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setApproveButtonText("确定");
                    fileChooser.setDialogTitle("另存为");

                    int result=fileChooser.showSaveDialog(this);

                    if(result==JFileChooser.CANCEL_OPTION)
                    { statusLabel.setText("　您没有保存文件");
                        return;
                    }

                    File saveFileName=fileChooser.getSelectedFile();

                    if(saveFileName==null||saveFileName.getName().equals(""))
                    { JOptionPane.showMessageDialog(this,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                    }
                    else
                    { try
                    { FileWriter fw=new FileWriter(saveFileName);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();

                        isNewFile=false;
                        currentFile=saveFileName;
                        oldValue=editArea.getText();

                        this.setTitle(saveFileName.getName()+" - 记事本");
                        statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());
                        //isSave=true;
                    }
                    catch(IOException ioException){
                    }
                    }
                }
                else
                {
                    try
                    { FileWriter fw=new FileWriter(currentFile);
                        BufferedWriter bfw=new BufferedWriter(fw);
                        bfw.write(editArea.getText(),0,editArea.getText().length());
                        bfw.flush();
                        fw.close();
                        //isSave=true;
                    }
                    catch(IOException ioException){
                    }
                }
                System.exit(0);
                //if(isSave)System.exit(0);
                //else return;
            }
            else if(exitChoose==JOptionPane.NO_OPTION)
            { System.exit(0);
            }
            else
            { return;
            }
        }
    }//关闭窗口时调用方法结束


    public void actionPerformed(ActionEvent e)
    { //新建
        if(e.getSource()==fileMenu_New)
        {
            File_fuc.new_file(this,editArea,oldValue,statusLabel,isNewFile,currentFile,undo, editMenu_Undo);

        }//新建结束
        //打开
        else if(e.getSource()==fileMenu_Open)
        {
            File_fuc.open(this,editArea,oldValue,statusLabel,isNewFile,currentFile,undo, editMenu_Undo);
        }//打开结束
        //保存
        else if(e.getSource()==fileMenu_Save)
        {
            File_fuc.save(this,editArea,oldValue,statusLabel,isNewFile,currentFile,undo, editMenu_Undo);
        }//保存结束
        //另存为
        else if(e.getSource()==fileMenu_SaveAs)
        {
            File_fuc.saveas(this,editArea,oldValue,statusLabel,isNewFile,currentFile,undo, editMenu_Undo);
        }//另存为结束
        //打印
        else if(e.getSource()==fileMenu_Print)
        {
            File_fuc.print(this,editArea);
        }//打印结束

        else if (e.getSource() == fileMenu_Export){
            try {
                File_fuc.export(editArea, this);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (DocumentException ex) {
                ex.printStackTrace();
            }
        }
        //退出
        else if(e.getSource()==fileMenu_Exit)
        {
            File_fuc.exit(this);
        }//退出结束
        //编辑
//        else if(e.getSource()==editMenu)
//        { checkMenuItemEnabled();//设置剪切、复制、粘贴、删除等功能的可用性
//        }
//        编辑结束
        //撤销
        else if(e.getSource()==editMenu_Undo || e.getSource()==popupMenu_Undo)
        { Edit_fuc.undo(editArea,undo,editMenu_Undo);
        }//撤销结束
        //剪切
        else if(e.getSource()==editMenu_Cut || e.getSource()==popupMenu_Cut)
        {
            Edit_fuc.cut(this,editArea,clipBoard);
        }//剪切结束
        //复制
        else if(e.getSource()==editMenu_Copy || e.getSource()==popupMenu_Copy)
        { tempString = editArea.getSelectedText();
        }//复制结束
        //粘帖
        else if(e.getSource()==editMenu_Paste || e.getSource()==popupMenu_Paste)
        {
            Edit_fuc.paste(editArea, tempString);
        }//粘帖结束
        //删除
        else if(e.getSource()==editMenu_Delete || e.getSource()==popupMenu_Delete)
        { Edit_fuc.delete(this, editArea, clipBoard);
        }//删除结束
        //查找
        else if(e.getSource()==editMenu_Find)
        {
            Edit_fuc.find(this, editArea);
        }//查找结束
        //查找下一个
        else if(e.getSource()==editMenu_FindNext)
        {
            Edit_fuc.find(this, editArea);
        }//查找下一个结束

        //全选
        else if(e.getSource()==editMenu_SelectAll || e.getSource()==popupMenu_SelectAll)
        {
            Edit_fuc.selectall(editArea);
        }//全选结束

        //设置状态栏可见性
        else if(e.getSource()==viewMenu_Status)
        { if(viewMenu_Status.getState())
            statusLabel.setVisible(true);
        else
            statusLabel.setVisible(false);
        }//设置状态栏可见性结束
        //帮助主题
        else if(e.getSource()==helpMenu_AboutNotepad)
        {
            View_fuc.about(this, editArea);
        }//帮助主题结束
        //关于

    }//方法actionPerformed()结束

    //实现DocumentListener接口中的方法(与撤销操作有关)
    public void removeUpdate(DocumentEvent e)
    { editMenu_Undo.setEnabled(true);
    }
    public void insertUpdate(DocumentEvent e)
    { editMenu_Undo.setEnabled(true);
    }
    public void changedUpdate(DocumentEvent e)
    { editMenu_Undo.setEnabled(true);
    }//DocumentListener结束

    //实现接口UndoableEditListener的类UndoHandler(与撤销操作有关)
    class UndoHandler implements UndoableEditListener
    { public void undoableEditHappened(UndoableEditEvent uee)
    {
        undo.addEdit(uee.getEdit());
    }
    }

    //main函数开始
    public static void main(String args[])
    {   Editor notepad=new Editor();
        notepad.editor();
        notepad.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//使用 System exit 方法退出应用程序
    }//main函数结
}

