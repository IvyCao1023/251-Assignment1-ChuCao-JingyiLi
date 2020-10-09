package notepad;

import javax.swing.*;

public class View_fuc {

    public static void view(JCheckBoxMenuItem viewMenu_Status, JLabel statusLabel){
        if(viewMenu_Status.getState())
            statusLabel.setVisible(true);
        else
            statusLabel.setVisible(false);
    }
}
