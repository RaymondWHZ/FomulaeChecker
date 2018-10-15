package software.util;

import java.io.*;

public class ObjectIO {

    public static void exportObject(File file, Object object) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(object);
        }
    }

    public static <T> T importObject(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        }
    }
}
