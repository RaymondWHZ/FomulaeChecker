package software.core;

import software.util.ObjectIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//whole class designed and created by Raymond 5020 to auto save data
public class FormulaeDatabase {

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
     * Access the variable symbol corresponding to unit 'u' with:
     * FormulaeDatabase.UNITS.get(u);
     * FormulaeDatabase.UNITS.set(u, value)
     */
    public static final HashMap<String, String> UNITS;

    /**
     * Access the name of symbol 's' with:
     * FormulaeDatabase.SYMBOL_NAMES.get(s);
     * FormulaeDatabase.SYMBOL_NAMES.set(s, value)
     */
    public static final HashMap<String, String> SYMBOL_NAMES;

    /**
     * Access the symbol of unit name 'n' with:
     * FormulaeDatabase.UNIT_NAMES.get(n);
     * FormulaeDatabase.UNIT_NAMES.set(n, value)
     */
    public static final HashMap<String, String> UNIT_NAMES;

    /**
     * The directory under which the JAR file or class files exists.
     */
    private static final String DEFAULT_DIRECTORY;

    /**
     * The default file extension for every database objects.
     */
    private static final String FILE_EXTENSION = ".data";

    /**
     * Read object from file if the file exists otherwise use the default instance,
     * creating a save event at the same time.
     */
    private static <T> T makeAutoSaveProperty(String defaultFileName, T defaultInstance) {
        var defaultFile = new File(DEFAULT_DIRECTORY + defaultFileName + FILE_EXTENSION);

        // create from file or newInstance
        T instance;
        try {
            instance = (defaultFile.exists()) ? ObjectIO.importObject(defaultFile) : defaultInstance;
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

    static {

        // there may be multiple directories in class path
        // we only need the first one
        var sepChar = System.getProperty("path.separator");
        var directory = System.getProperty("java.class.path").split(sepChar)[0];

        if (directory.endsWith(".jar")) {  // set to the jar file if running as a jar
            var lastIndex = directory.lastIndexOf("/");
            DEFAULT_DIRECTORY = directory.substring(0, lastIndex + 1);
        }
        else  // set to current working directory if running as class files
            DEFAULT_DIRECTORY = "";

        // initialize databases
        FORMULAE = makeAutoSaveProperty("Formulae", new ArrayList<>());
        UNITS = makeAutoSaveProperty("Units", new HashMap<>());
        SYMBOL_NAMES = makeAutoSaveProperty("Symbol Names", new HashMap<>());
        UNIT_NAMES = makeAutoSaveProperty("Unit Names", new HashMap<>());
    }
}
