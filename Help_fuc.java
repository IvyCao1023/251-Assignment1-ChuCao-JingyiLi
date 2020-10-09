package notepad;

import javax.swing.*;

public class Help_fuc {
    public static void about(Editor editor, JTextArea editArea){
        editArea.requestFocus();
        JOptionPane.showMessageDialog(editor,"路漫漫其修远兮，吾将上下而求索。","about",JOptionPane.INFORMATION_MESSAGE);
    }
}
