package software.core;

import software.util.ObjectIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// whole class designed and created by Raymond 5020 to auto save data
public class FormulaeDatabase {

    private static final String FILE_EXTENSION = ".data";

    /**
     * Indices to indicate expression and description
     */
    public static final int EXPRESSION = 0;
    public static final int DESCRIPTION = 1;

    /**
     * Access the expression at row 'i' (must exist) with:
     * FormulaeDatabase.FORMULAE.get(i).get(FormulaeDatabase.EXPRESSION);
     * FormulaeDatabase.FORMULAE.get(i).set(FormulaeDatabase.EXPRESSION, value);
     *
     * Access the description at row 'i' (must exist) with:
     * FormulaeDatabase.FORMULAE.get(i).get(FormulaeDatabase.DESCRIPTION);
     * FormulaeDatabase.FORMULAE.get(i).set(FormulaeDatabase.DESCRIPTION, value);
     *
     * Add a new formula with:
     * FormulaeDatabase.FORMULAE.add(FormulaeDatabase.newFormula());
     * Then you can access it the same way as above.
     */
    public static final ArrayList<ArrayList<String>> FORMULAE;
    public static ArrayList<String> newFormula() {
        return new ArrayList<>(List.of("", ""));
    }

    /**
     * Access the name of unit 'u' with:
     * FormulaeDatabase.UNITS.get(u);
     * FormulaeDatabase.UNITS.set(u, value)
     */
    public static final HashMap<String, String> UNITS;

    /**
     * Access the name of unit 'n' with:
     * FormulaeDatabase.SYMBOL_NAMES.get(n);
     * FormulaeDatabase.SYMBOL_NAMES.set(n, value)
     */
    public static final HashMap<String, String> SYMBOL_NAMES;

    /**
     * Access the name of unit 'n' with:
     * FormulaeDatabase.SYMBOL_NAMES.get(n);
     * FormulaeDatabase.SYMBOL_NAMES.set(n, value)
     */
    public static final HashMap<String, String> UNIT_NAMES;

    static {
        FORMULAE = makeAutoSaveProperty("Formulae", new ArrayList<>());
        UNITS = makeAutoSaveProperty("Units", new HashMap<>());
        SYMBOL_NAMES = makeAutoSaveProperty("Symbol Names", new HashMap<>());
        UNIT_NAMES = makeAutoSaveProperty("Unit Names", new HashMap<>());
    }

    private static <T> T makeAutoSaveProperty(String defaultFileName, T newInstance) {
        var defaultFile = new File(defaultFileName + FILE_EXTENSION);

        // create from file or newInstance
        T instance;
        try {
            instance = (defaultFile.exists()) ? ObjectIO.importObject(defaultFile) : newInstance;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // create exit save event
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ObjectIO.exportObject(defaultFile, instance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        return instance;
    }
}