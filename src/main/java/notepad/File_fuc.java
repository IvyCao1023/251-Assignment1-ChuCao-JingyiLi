package notepad;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import sun.rmi.runtime.NewThreadAction;

import javax.print.*;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.io.*;

public class File_fuc {
    private static final String FONT = "C:\\Windows\\Fonts\\simhei.ttf";
    public static void new_file(Editor editor, JTextArea editArea, String oldValue, JLabel statusLabel, boolean isNewFile, File currentFile ,UndoManager undo,JMenuItem editMenu_Undo){
        editArea.requestFocus();
        String currentValue=editArea.getText();
        boolean isTextChange=(currentValue.equals(oldValue))?false:true;
        if(isTextChange)
        { int saveChoose= JOptionPane.showConfirmDialog(editArea,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);
            if(saveChoose==JOptionPane.YES_OPTION)
            { String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                //fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("另存为");
                int result=fileChooser.showSaveDialog(editor);
                if(result==JFileChooser.CANCEL_OPTION)
                { statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File saveFileName=fileChooser.getSelectedFile();
                if(saveFileName==null || saveFileName.getName().equals(""))
                { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                }
                else
                { try
                { FileWriter fw=new FileWriter(saveFileName);
                    BufferedWriter bfw=new BufferedWriter(fw);
                    bfw.write(editArea.getText(),0,editArea.getText().length());
                    bfw.flush();//刷新该流的缓冲
                    bfw.close();
                    isNewFile=false;
                    currentFile=saveFileName;
                    oldValue=editArea.getText();
                    editor.setTitle(saveFileName.getName()+" - 记事本");
                    statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
                }
                catch (IOException ioException)
                {
                }
                }
            }
            else if(saveChoose==JOptionPane.NO_OPTION)
            { editArea.replaceRange("",0,editArea.getText().length());
                statusLabel.setText(" 新建文件");
                editor.setTitle("无标题 - 记事本");
                isNewFile=true;
                undo.discardAllEdits(); //撤消所有的"撤消"操作
                editMenu_Undo.setEnabled(false);
                oldValue=editArea.getText();
            }
            else if(saveChoose==JOptionPane.CANCEL_OPTION)
            { return;
            }
        }
        else
        { editArea.replaceRange("",0,editArea.getText().length());
            statusLabel.setText(" 新建文件");
            editor.setTitle("无标题 - 记事本");
            isNewFile=true;
            undo.discardAllEdits();//撤消所有的"撤消"操作
            editMenu_Undo.setEnabled(false);
            oldValue=editArea.getText();
        }
    }

    public static void open(Editor editor, JTextArea editArea, String oldValue, JLabel statusLabel, boolean isNewFile, File currentFile ,UndoManager undo,JMenuItem editMenu_Undo){
        editArea.requestFocus();
        String currentValue=editArea.getText();
        boolean isTextChange=(currentValue.equals(oldValue))?false:true;
        if(isTextChange)
        { int saveChoose=JOptionPane.showConfirmDialog(editor,"您的文件尚未保存，是否保存？","提示",JOptionPane.YES_NO_CANCEL_OPTION);
            if(saveChoose==JOptionPane.YES_OPTION)
            { String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                //fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("另存为");
                int result=fileChooser.showSaveDialog(editor);
                if(result==JFileChooser.CANCEL_OPTION)
                { statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File saveFileName=fileChooser.getSelectedFile();
                if(saveFileName==null || saveFileName.getName().equals(""))
                { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                }
                else
                { try
                { FileWriter fw=new FileWriter(saveFileName);
                    BufferedWriter bfw=new BufferedWriter(fw);
                    bfw.write(editArea.getText(),0,editArea.getText().length());
                    bfw.flush();//刷新该流的缓冲
                    bfw.close();
                    isNewFile=false;
                    currentFile=saveFileName;
                    oldValue=editArea.getText();
                    editor.setTitle(saveFileName.getName()+" - 记事本");
                    statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
                }
                catch (IOException ioException)
                {
                }
                }
            }
            else if(saveChoose==JOptionPane.NO_OPTION)
            { String str=null;
                JFileChooser fileChooser=new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                //fileChooser.setApproveButtonText("确定");
                fileChooser.setDialogTitle("打开文件");
                int result=fileChooser.showOpenDialog(editor);
                if(result==JFileChooser.CANCEL_OPTION)
                { statusLabel.setText("您没有选择任何文件");
                    return;
                }
                File fileName=fileChooser.getSelectedFile();
                if(fileName==null || fileName.getName().equals(""))
                { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
                }
                else
                { try
                { FileReader fr=new FileReader(fileName);
                    BufferedReader bfr=new BufferedReader(fr);
                    editArea.setText("");
                    while((str=bfr.readLine())!=null)
                    { editArea.append(str);
                    }
                    editor.setTitle(fileName.getName()+" - 记事本");
                    statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());
                    fr.close();
                    isNewFile=false;
                    currentFile=fileName;
                    oldValue=editArea.getText();
                }
                catch (IOException ioException)
                {
                }
                }
            }
            else
            { return;
            }
        }
        else
        { String str=null;
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //fileChooser.setApproveButtonText("确定");
            fileChooser.setDialogTitle("打开文件");
            int result=fileChooser.showOpenDialog(editor);
            if(result==JFileChooser.CANCEL_OPTION)
            { statusLabel.setText(" 您没有选择任何文件 ");
                return;
            }
            File fileName=fileChooser.getSelectedFile();
            if(fileName==null || fileName.getName().equals(""))
            { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
            }
            else
            { try
            { FileReader fr=new FileReader(fileName);
                BufferedReader bfr=new BufferedReader(fr);
                editArea.setText("");
                while((str=bfr.readLine())!=null)
                { editArea.append(str);
                }
                editor.setTitle(fileName.getName()+" - 记事本");
                statusLabel.setText(" 当前打开文件："+fileName.getAbsoluteFile());
                fr.close();
                isNewFile=false;
                currentFile=fileName;
                oldValue=editArea.getText();
            }
            catch (IOException ioException)
            {
            }
            }
        }
    }

    public static void save(Editor editor, JTextArea editArea, String oldValue, JLabel statusLabel, boolean isNewFile, File currentFile ,UndoManager undo,JMenuItem editMenu_Undo){
        editArea.requestFocus();
        if(isNewFile)
        { String str=null;
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //fileChooser.setApproveButtonText("确定");
            fileChooser.setDialogTitle("保存");
            int result=fileChooser.showSaveDialog(editor);
            if(result==JFileChooser.CANCEL_OPTION)
            { statusLabel.setText("您没有选择任何文件");
                return;
            }
            File saveFileName=fileChooser.getSelectedFile();
            if(saveFileName==null || saveFileName.getName().equals(""))
            { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
            }
            else
            { try
            { FileWriter fw=new FileWriter(saveFileName);
                BufferedWriter bfw=new BufferedWriter(fw);
                bfw.write(editArea.getText(),0,editArea.getText().length());
                bfw.flush();//刷新该流的缓冲
                bfw.close();
                isNewFile=false;
                currentFile=saveFileName;
                oldValue=editArea.getText();
                editor.setTitle(saveFileName.getName()+" - 记事本");
                statusLabel.setText("当前打开文件："+saveFileName.getAbsoluteFile());
            }
            catch (IOException ioException)
            {
            }
            }
        }
        else
        { try
        { FileWriter fw=new FileWriter(currentFile);
            BufferedWriter bfw=new BufferedWriter(fw);
            bfw.write(editArea.getText(),0,editArea.getText().length());
            bfw.flush();
            fw.close();
        }
        catch(IOException ioException)
        {
        }
        }
    }

    public static void saveas(Editor editor, JTextArea editArea, String oldValue, JLabel statusLabel, boolean isNewFile, File currentFile ,UndoManager undo,JMenuItem editMenu_Undo){
        editArea.requestFocus();
        String str=null;
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //fileChooser.setApproveButtonText("确定");
        fileChooser.setDialogTitle("另存为");
        int result=fileChooser.showSaveDialog(editor);
        if(result==JFileChooser.CANCEL_OPTION)
        { statusLabel.setText("　您没有选择任何文件");
            return;
        }
        File saveFileName=fileChooser.getSelectedFile();
        if(saveFileName==null||saveFileName.getName().equals(""))
        { JOptionPane.showMessageDialog(editor,"不合法的文件名","不合法的文件名",JOptionPane.ERROR_MESSAGE);
        }
        else
        { try
        { FileWriter fw=new FileWriter(saveFileName);
            BufferedWriter bfw=new BufferedWriter(fw);
            bfw.write(editArea.getText(),0,editArea.getText().length());
            bfw.flush();
            fw.close();
            oldValue=editArea.getText();
            editor.setTitle(saveFileName.getName()+" - 记事本");
            statusLabel.setText("　当前打开文件:"+saveFileName.getAbsoluteFile());
        }
        catch(IOException ioException)
        {
        }
        }
    }

    public static void print(Editor editor, JTextArea editArea){
        editArea.requestFocus();
        JOptionPane.showMessageDialog(editor,"","提示",JOptionPane.WARNING_MESSAGE);
    }

    public static void export(JTextArea edit_text_area, Editor editor) throws IOException, DocumentException, DocumentException {
        File file = null;
        int result ;
        JFileChooser fileChooser = new JFileChooser("F:\\");
        fileChooser.setApproveButtonToolTipText("保存"); // 设置确认按钮的现实文本
        fileChooser.setDialogTitle("导出"); // 设置title
        result = fileChooser.showOpenDialog(editor); // 设置Dialog的根View 根布局

        //--------------------------------------------------------------------------
        if(result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile(); // 若点击了确定按钮，给file填文件路径
        }
        Document document = new Document();
        OutputStream os = new FileOutputStream(new File(file.getAbsolutePath()+".pdf"));
        PdfWriter.getInstance(document, os);
        document.open();
        //方法一：使用Windows系统字体(TrueType)
        BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        Font font = new Font(baseFont);
        document.add(new Paragraph(edit_text_area.getText(), font));
        document.close();
    }

    public static void exit(Editor editor){
        int exitChoose=JOptionPane.showConfirmDialog(editor,"确定要退出吗?","退出提示",JOptionPane.OK_CANCEL_OPTION);
        if(exitChoose==JOptionPane.OK_OPTION)
        { System.exit(0);
        }
        else
        { return;
        }
    }

    public static void print(){
        JFileChooser fileChooser = new JFileChooser();
        int state = fileChooser.showOpenDialog(null);
        if (state == fileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            HashPrintRequestAttributeSet requ = new HashPrintRequestAttributeSet();
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, requ);
            PrintService dafaultService = PrintServiceLookup.lookupDefaultPrintService();
            PrintService service = ServiceUI.printDialog(null,200,200,printService,dafaultService,flavor,requ);

                }
            }
        }
    }
}

