package software.main;

import software.gui.EditFrame;
import software.gui.SearchFrame;

import javax.swing.*;
import java.security.InvalidParameterException;

public class FormulaeChecker {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to set up default appearance:" + e.toString());
        }
    }

    public static void main(String[] args) {
        // if the user want to change the database, he/she can open the software in edit-mode
        if (args.length > 0) {
             if (args[0].equals("edit-mode"))
                 new EditFrame().setVisible(true);
             else
                 throw new InvalidParameterException("Unknown parameter: \"" + args[0] + "\"");
        }
        else
            new SearchFrame().setVisible(true);
    }
}
