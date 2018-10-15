package software.gui;

import software.core.FormulaeShowProxy;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class SearchFrame extends JFrame {

    // configuration variables
    private static final int FIXED_WIDTH = 500;
    private static final int HEIGHT_LIMIT = 700;

    // system reference
    private JPanel searchPanel;
    private JTextField searchTextField;
    private JList<Object> itemList;
    private JLabel logoLabel;

    // menu that pops when logo been clicked
    private JPopupMenu optionMenu = new JPopupMenu();

    // search list support
    private FormulaeShowProxy showProxy = new FormulaeShowProxy();

    public SearchFrame() {
        // remove standard bar
        setUndecorated(true);
        setOpacity(0.8f);

        // basic initialization
        setContentPane(searchPanel);
        setLocation(200, 200);
        setResizable(false);  // this only restricts the user

        // set the window to preferred size
        autoResize();

        // exit when this window closes
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        // action when the text field is edited
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showProxy.setSearchKeyword(searchTextField.getText());
                itemList.setListData(showProxy.getShowList().toArray());
                autoResize();
            }

            @Override
            public void removeUpdate(DocumentEvent e) { }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });


        // ---set up pop menu---

        JMenuItem exitButton = new JMenuItem("Quit");
        exitButton.addActionListener(e -> System.exit(0));
        optionMenu.add(exitButton);

        logoLabel.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                optionMenu.show(logoLabel, e.getX(), e.getY());
            }
        });
    }

    private void autoResize() {
        var preferredHeight = getPreferredSize().height;
        // ensure the height do not run beyond limit
        var newHeight = (preferredHeight > HEIGHT_LIMIT) ? HEIGHT_LIMIT : preferredHeight;
        setSize(FIXED_WIDTH, newHeight);
    }
}
