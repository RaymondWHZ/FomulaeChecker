package software.gui;

import software.core.FormulaeDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.HashMap;

public class EditFrame extends JFrame {

    // basic window control
    private static EditFrame instance;
    public static void showWindow() {
        if (instance == null)
            instance = new EditFrame();
        instance.setVisible(true);
    }

    private JPanel editPanel;
    private JTabbedPane editTabbedPane;

    private JButton newButton;
    private JList<String> formulaeList;

    private JTextField expressionTextField;
    private JTextArea descriptionTextArea;
    private JButton deleteButton;
    private JButton doneButton;

    private JTextPane unitNamesTextPane;
    private JTextPane unitsTextPane;
    private JLabel formulaNotSavedLabel;
    private JButton saveUnitsButton;
    private JButton saveUnitNamesButton;
    private JTextPane symbolNameTextPane;
    private JButton saveSymbolNamesButton;

    private ArrayList<ArrayList<String>> formulae = FormulaeDatabase.FORMULAE;
    private FormulaeListModel formulaeListModel = new FormulaeListModel();
    private int currentIndex;
    private ArrayList<String> currentFormula;

    // created by Raymond 5020 to initialize window
    private EditFrame() {
        setContentPane(editPanel);
        setLocation(150, 250);
        setSize(600, 300);

        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // --- formula list ---

        formulaeList.setModel(formulaeListModel);

        formulaeList.addListSelectionListener(e -> {
            currentIndex = formulaeList.getSelectedIndex();
            if (currentIndex == -1) {
                setDetailEnabled(false);
                return;
            }

            setDetailEnabled(true);
            currentFormula = FormulaeDatabase.FORMULAE.get(currentIndex);
            updateDetail();
            formulaNotSavedLabel.setVisible(false);
        });

        newButton.addActionListener(e -> {
            formulaeListModel.addNewFormula();
        });


        // --- detail panel ---

        setDetailEnabled(false);

        var formulaChangeAnnouncer = new ChangeAnnouncer(formulaNotSavedLabel);
        expressionTextField.getDocument().addDocumentListener(formulaChangeAnnouncer);
        descriptionTextArea.getDocument().addDocumentListener(formulaChangeAnnouncer);

        doneButton.addActionListener(e -> {
            saveDetail();
            formulaeList.updateUI();
            formulaNotSavedLabel.setVisible(false);
        });

        deleteButton.addActionListener(e -> {
            formulaeListModel.removeFormula(currentIndex);
        });


        // --- edit panels (symbol name, unit, unit names) ---
        setupPropertiesEditPane(symbolNameTextPane, saveSymbolNamesButton, FormulaeDatabase.SYMBOL_NAMES);
        setupPropertiesEditPane(unitsTextPane, saveUnitsButton, FormulaeDatabase.UNITS);
        setupPropertiesEditPane(unitNamesTextPane, saveUnitNamesButton, FormulaeDatabase.UNIT_NAMES);
    }

    // create by Raymond 5020 to lock/unlock detail part
    private void setDetailEnabled(boolean b) {
        if (!b) {
            expressionTextField.setText("");
            descriptionTextArea.setText("");
        }

        expressionTextField.setEnabled(b);
        descriptionTextArea.setEnabled(b);

        doneButton.setEnabled(b);
        deleteButton.setEnabled(b);
    }

    // created by Raymond 5020 to refresh page
    private void updateDetail() {
        expressionTextField.setText(currentFormula.get(FormulaeDatabase.EXPRESSION));
        descriptionTextArea.setText(currentFormula.get(FormulaeDatabase.DESCRIPTION));
    }

    // created by Raymond 5020 to save changes
    private void saveDetail() {
        currentFormula.set(FormulaeDatabase.EXPRESSION, expressionTextField.getText());
        currentFormula.set(FormulaeDatabase.DESCRIPTION, descriptionTextArea.getText());
    }

    // created by Raymond 5020 to setup properties edit panels
    private void setupPropertiesEditPane(
            JTextPane propertiesPane, JButton saveButton, HashMap<String, String> properties) {
        propertiesPane.setText(readFromMap(properties));

        var unitsChangeAnnouncer = new ChangeAnnouncer(saveButton);
        propertiesPane.getDocument().addDocumentListener(unitsChangeAnnouncer);

        saveButton.addActionListener(e -> {
            try {
                var newMap = writeToMap(propertiesPane.getText());

                properties.clear();
                properties.putAll(newMap);
                saveButton.setVisible(false);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(this, e1.getMessage());
            }
        });
    }

    // created by Raymond 5020 to convert map into key=value
    private static String readFromMap(HashMap<String, String> map) {
        var keyValuePair = new StringBuilder();
        map.forEach((key, value) -> keyValuePair.append(key).append("=").append(value).append("\n"));
        return keyValuePair.toString();
    }

    // created by Raymond 5020 to convert key=value into map
    private static HashMap<String, String> writeToMap(String keyValuePair) throws Exception {
        var map = new HashMap<String, String>();

        var lines = keyValuePair.split("\n");
        for (var line : lines) {
            if (line.isEmpty())
                continue;

            var keyValue = line.split("=");
            if (keyValue.length != 2)
                throw new Exception("Wrong syntax at line: " + line);

            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    // whole class created by Raymond 5020 to customize list behavior
    private class FormulaeListModel extends AbstractListModel<String> {

        private static final String EMPTY_DISPLAY = "New Formula";

        private FormulaeListModel() { }

        @Override
        public int getSize() {
            return formulae.size();
        }

        @Override
        public String getElementAt(int index) {
            var currentFormula = formulae.get(index);
            var expression = currentFormula.get(FormulaeDatabase.EXPRESSION);
            return (expression.isEmpty()) ? EMPTY_DISPLAY : expression;
        }

        private void addNewFormula() {
            var index = formulae.size();
            formulae.add(FormulaeDatabase.newFormula());
            fireIntervalAdded(this, index, index);
        }

        private void removeFormula(int index) {
            formulae.remove(index);
            fireIntervalRemoved(this, index, index);
        }
    }

    // whole class created by Raymond 5020 to define document change action
    private static class ChangeAnnouncer implements DocumentListener {

        private JComponent indicator;

        private ChangeAnnouncer(JComponent indicator) {
            this.indicator = indicator;
        }

        private void announce() {
            indicator.setVisible(true);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            announce();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            announce();
        }

        @Override
        public void changedUpdate(DocumentEvent e) { }
    }
}


