package software.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class HashMapDatabase implements Iterable<HashMap<String, String>> {

    /**
     * This class can create a form like database that allows you to store data.
     * The database will be saved at default directory when exit and opened when
     * the application starts again. This default directory will be under the same
     * directory as the exported jar file.
     */

    public static final String FILE_EXTENSION = ".hmd";

    private File saveFile;
    private ArrayList<HashMap<String, String>> database;

    private static HashMap<String, HashMapDatabase> databases = new HashMap<>();
    public static HashMapDatabase getDatabase(String name) {
        var database = databases.get(name);
        if (database == null) {
            database = new HashMapDatabase(name);
            databases.put(name, database);
        }
        return database;
    }

    private HashMapDatabase(String name) {
        // set the file pointer to current directory
        saveFile = new File(
                System.getProperty("user.dir") + "/" + name + FILE_EXTENSION);

        if (saveFile.exists()) {
            // read if exist
            read();
        }
        else {
            // create new if not
            database = new ArrayList<>();
        }

        // save when exit
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                save();
            }
        });
    }

    public ArrayList<HashMap<String, String>> getDatabase() {
        return database;
    }

    private void ensureCapacityTill(int row) {
        while (database.size() < row + 1)
            database.add(new HashMap<>());
    }

    public void read() {
        try {
            database = ObjectIO.importObject(saveFile);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            ObjectIO.exportObject(saveFile, database);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterator<HashMap<String, String>> iterator() {
        return database.iterator();
    }
}
