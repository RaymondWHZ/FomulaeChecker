package software.gui;

import software.core.FormulaeDatabase;
import software.core.FormulaeSearchAgent;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SearchFrame extends JFrame {

    // basic window control
    private static SearchFrame instance;
    public static void showWindow() {
        if (instance == null)
            instance = new SearchFrame();
        instance.setVisible(true);
    }

    // configuration variables
    private static final int FIXED_WIDTH = 500;
    private static final int HEIGHT_LIMIT = 400;

    // system reference
    private JPanel searchPanel;
    private JTextField searchTextField;
    private JList<String> itemList;
    private JLabel logoLabel;

    // menu that pops when logo been clicked
    private JPopupMenu optionMenu = new JPopupMenu();

    private FormulaeSearchAgent searchAgent = new FormulaeSearchAgent();
    private List<String> associatedNames;
    private List<ArrayList<String>> searchResult;
    private ResultListModel resultListModel = new ResultListModel();

    private SearchFrame() {
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
            private void change() {
                var keyword = searchTextField.getText();

                if (keyword.isEmpty()) {  // do nothing for an empty keyword
                    associatedNames = List.of();
                    searchResult = List.of();
                }
                else {  // search for formulae through search agent
                    searchAgent.setKeyword(searchTextField.getText());
                    associatedNames = searchAgent.getAssociatedNames();
                    searchResult = searchAgent.getFormulaeResult();
                }

                // update list and window appearance
                itemList.setModel(resultListModel);
                itemList.updateUI();
                autoResize();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        itemList.addListSelectionListener(e -> {
            var index = itemList.getSelectedIndex();

            // deal with associated names clicked
            if (index >= 0 && index < associatedNames.size())
                searchTextField.setText(associatedNames.get(index));
        });


        // ---set up pop menu---

        JMenuItem symButton = new JMenuItem("Search symbol");
        symButton.addActionListener(
                e -> ensurePrefix(FormulaeSearchAgent.PREFIXES.get(FormulaeSearchAgent.SYMBOL_PREFIX)));
        optionMenu.add(symButton);

        JMenuItem unitButton = new JMenuItem("Search unit");
        unitButton.addActionListener(
                e -> ensurePrefix(FormulaeSearchAgent.PREFIXES.get(FormulaeSearchAgent.UNIT_PREFIX)));
        optionMenu.add(unitButton);

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

    /**
     * @param prefix Add this to the front of keyword is it is not currently.
     */
    private void ensurePrefix(String prefix) {
        var text = searchTextField.getText();
        if (!searchTextField.getText().startsWith(prefix)) {
            var newText = prefix + " " + text;
            searchTextField.setText(newText);
        }
    }

    private void autoResize() {
        var preferredHeight = getPreferredSize().height;
        // ensure the height do not run beyond limit
        var newHeight = (preferredHeight > HEIGHT_LIMIT) ? HEIGHT_LIMIT : preferredHeight;
        setSize(FIXED_WIDTH, newHeight);
    }

    // whole class created by Raymond 5020 to customize list behavior
    private class ResultListModel extends AbstractListModel<String> {
        @Override
        public int getSize() {
            return associatedNames.size() + searchResult.size();
        }

        @Override
        public String getElementAt(int index) {
            var asSize = associatedNames.size();
            if (index < asSize)
                return ">> " + associatedNames.get(index);  // add associated names
            return searchResult.get(index - asSize).get(FormulaeDatabase.EXPRESSION);
        }
    }
}
