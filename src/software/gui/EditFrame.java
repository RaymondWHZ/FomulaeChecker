package software.gui;

import software.core.FormulaeShowProxy;

import javax.swing.*;

public class EditFrame extends JFrame {
    private JPanel editPanel;
    private JList formulaeList;
    private JTextField expressionTextField;
    private JTextArea descriptionTextArea;
    private JTextField unitTextField;
    private JButton doneButton;
    private JButton deleteButton;
    private JTextField searchTextField;
    private JTextField namesTextField;

    private FormulaeShowProxy showProxy = new FormulaeShowProxy();

    public EditFrame() {
        setContentPane(editPanel);
        setLocation(150, 250);

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void updateDetail() {

    }
}
