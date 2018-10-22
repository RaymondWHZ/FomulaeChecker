package software.gui;

import software.core.FormulaeDatabase;
import software.core.FormulaeSearchAgent;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
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
    private static final int FIXED_WIDTH = 600;
    private static final int FIXED_HEIGHT = 350;

    private JPanel searchPanel;

    private JLabel logoLabel;
    private JTextField searchTextField;

    private JList<String> itemList;
    private JPanel controlPanel;
    private JLabel formulaNameLabel;
    private JTextPane formulaDescriptionTextPane;

    // menu that pops when logo been clicked
    private JPopupMenu optionMenu = new JPopupMenu();

    private FormulaeSearchAgent searchAgent = new FormulaeSearchAgent();
    private List<String> associatedNames;
    private List<ArrayList<String>> searchResult;
    private ResultListModel resultListModel = new ResultListModel();

    // created by Raymond 5020 to initialize window
    private SearchFrame() {
        // remove standard bar
        setUndecorated(true);
        setOpacity(0.9f);

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

        formulaDescriptionTextPane.setEditable(false);

        itemList.addListSelectionListener(e -> {
            var index = itemList.getSelectedIndex();
            var asSize = associatedNames.size();

            if (index < asSize) {
                // no formula selected
                formulaNameLabel.setText("");
                formulaDescriptionTextPane.setText("");
            }
            else {
                // formula selected
                var selectedFormula = searchResult.get(index - asSize);
                formulaNameLabel.setText(selectedFormula.get(FormulaeDatabase.EXPRESSION));
                formulaDescriptionTextPane.setText(selectedFormula.get(FormulaeDatabase.DESCRIPTION));
            }
        });


        // ---Key events (switch between text field and result list, press enter to replace text)---

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && resultListModel.getSize() > 0) {
                    // move the focus to the item list
                    itemList.grabFocus();
                    itemList.setSelectedIndex(0);
                }
            }
        });

        itemList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                var selectedIndex = itemList.getSelectedIndex();
                if (selectedIndex < 0)
                    return;

                if (e.getKeyCode() == KeyEvent.VK_UP && selectedIndex == 0) {
                    // remove the focus in item list is put it into text field
                    itemList.clearSelection();
                    searchTextField.grabFocus();
                }
                // replace text with associated name when clicked enter
                else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    loadTextFromSelection();
            }
        });

        itemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // replace text with associated name when double click
                if (e.getClickCount() == 2)
                    loadTextFromSelection();
            }
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
     * created by Raymond 5020 to add prefix to searching text
     *
     * @param prefix Add this to the front of keyword is it is not currently.
     */
    private void ensurePrefix(String prefix) {
        var text = searchTextField.getText();
        if (!searchTextField.getText().startsWith(prefix)) {
            var newText = prefix + " " + text;
            searchTextField.setText(newText);
        }
    }

    // created by Raymond 5020 to load selected associated name from selection
    private void loadTextFromSelection() {
        var index = itemList.getSelectedIndex();
        if (index >= 0 && index < associatedNames.size())
            // make sure the index is in associated name range
            searchTextField.setText(associatedNames.get(index));
    }

    // created by Raymond 5020 to resize the window according to current need
    private void autoResize() {
        var itemListPrefer = itemList.getPreferredSize().height;
        // if there is content in the list, make the window high enough
        var newHeight = (itemListPrefer > 0) ? FIXED_HEIGHT : controlPanel.getPreferredSize().height;
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
