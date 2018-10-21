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

    private ArrayList<ArrayList<String>> formulae = FormulaeDatabase.FORMULAE;
    private int currentFormulaIndex;
    private ArrayList<String> currentFormula;
    private JButton newButton;
    private JList<String> formulaeList;
    private FormulaeListModel formulaeListModel = new FormulaeListModel();

    private JTextField expressionTextField;
    private JTextArea descriptionTextArea;
    private JLabel formulaNotSavedLabel;
    private JButton deleteButton;
    private JButton doneButton;

    private JTextPane unitsTextPane;
    private JButton saveUnitsButton;

    private JTextPane unitNamesTextPane;
    private JButton saveUnitNamesButton;

    private JTextPane symbolNameTextPane;
    private JButton saveSymbolNamesButton;

    // created by Raymond 5020 to initialize window
    private EditFrame() {
        // basic initialization
        setContentPane(editPanel);
        setLocation(150, 250);
        setSize(600, 300);

        // exit when this window closes
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        // --- formula list ---

        formulaeList.setModel(formulaeListModel);

        formulaeList.addListSelectionListener(e -> {
            currentFormulaIndex = formulaeList.getSelectedIndex();
            if (currentFormulaIndex == -1) {  // if nothing selected
                setDetailEnabled(false);
                return;
            }
            currentFormula = FormulaeDatabase.FORMULAE.get(currentFormulaIndex);  // change current formula

            // update detail panel
            updateDetail();
            setDetailEnabled(true);
            formulaNotSavedLabel.setVisible(false);
        });

        newButton.addActionListener(e -> formulaeListModel.addNewFormula());


        // --- detail panel ---

        setDetailEnabled(false);

        // add text changed event
        var formulaChangeAnnouncer = new ChangeAnnouncer(formulaNotSavedLabel);
        expressionTextField.getDocument().addDocumentListener(formulaChangeAnnouncer);
        descriptionTextArea.getDocument().addDocumentListener(formulaChangeAnnouncer);

        // add save event
        doneButton.addActionListener(e -> {
            saveDetail();
            formulaeList.updateUI();
            formulaNotSavedLabel.setVisible(false);
        });

        // add remove event
        deleteButton.addActionListener(e -> formulaeListModel.removeFormula(currentFormulaIndex));


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

    // created by Raymond 5020 to quick setup properties edit panels
    private void setupPropertiesEditPane(
            JTextPane propertiesPane, JButton saveButton, HashMap<String, String> properties) {

        // set text (key=value)
        propertiesPane.setText(readFromMap(properties));

        // show that save button when edited
        var changeAnnouncer = new ChangeAnnouncer(saveButton);
        propertiesPane.getDocument().addDocumentListener(changeAnnouncer);

        saveButton.addActionListener(e -> {
            try {
                // convert key=value to HashMap
                var newMap = writeToMap(propertiesPane.getText());

                // reset properties completely
                properties.clear();
                properties.putAll(newMap);

                // hide save button
                saveButton.setVisible(false);
            } catch (Exception e1) {
                // display syntax error message
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
            if (line.isEmpty())  // skip lines with only a return
                continue;

            var keyValue = line.split("=");
            if (keyValue.length != 2)  // there should a key and a value at each side of =
                throw new Exception("Wrong syntax at line: " + line);

            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    // whole class created by Raymond 5020 to customize list behavior (change the formula list together with appearance)
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


