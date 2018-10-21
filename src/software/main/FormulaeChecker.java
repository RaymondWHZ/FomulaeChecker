package software.main;

import software.gui.EditFrame;
import software.gui.SearchFrame;

import javax.swing.*;
import java.security.InvalidParameterException;

public class FormulaeChecker {

    // created by Raymond 5020 as master function
    public static void main(String[] args) {
        // set up default look and feel (typically useful on windows)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to set up default appearance:" + e.toString());
        }

        // if the user wants to change the database, he/she can open the software in edit-mode through command line
        if (args.length > 0) {
             if (args[0].equals("edit-mode"))
                 EditFrame.showWindow();
             else
                 throw new InvalidParameterException("Unknown parameter: \"" + args[0] + "\"");
        }
        else
            SearchFrame.showWindow();
    }
}
