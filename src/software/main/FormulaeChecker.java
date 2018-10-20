package software.main;

import software.gui.EditFrame;
import software.gui.SearchFrame;

import javax.swing.*;
import java.security.InvalidParameterException;
import java.util.List;

public class FormulaeChecker {

    // created by Raymond 5020 as master function
    public static void main(String[] args) {
        // set up default look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Failed to set up default appearance:" + e.toString());
        }

        // if the user want to change the database, he/she can open the software in edit-mode
        if (args.length > 0) {
             if (args[0].equals("edit-mode"))
                 EditFrame.showWindow();
             else
                 throw new InvalidParameterException("Unknown parameter: \"" + args[0] + "\"");
        }
        else
            SearchFrame.showWindow();

        /*
        var newFormula = FormulaeDatabase.newFormula();
        newFormula.set(FormulaeDatabase.EXPRESSION, "f = a * m");
        newFormula.set(FormulaeDatabase.DESCRIPTION, "A formula to calculate force.");
        FormulaeDatabase.FORMULAE.add(newFormula);

        FormulaeDatabase.SYMBOL_NAMES.put("force", "F");
        FormulaeDatabase.UNITS.put("N", "F");
        */
        /*
        System.out.println(FormulaeDatabase.FORMULAE);
        System.out.println(FormulaeDatabase.SYMBOL_NAMES);
        System.out.println(FormulaeDatabase.UNITS);
        */
    }
}
