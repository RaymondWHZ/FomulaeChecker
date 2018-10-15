package software.core;

import software.util.HashMapDatabase;
import software.util.SharedConstants;

import java.util.ArrayList;
import java.util.HashMap;

public class FormulaeShowProxy {

    /**
     * The core database to store formulae
     */
    private static HashMapDatabase formulaeDatabase = HashMapDatabase.getDatabase("FormulaeDatabase");

    private ArrayList<HashMap<String, String>> currentList;
    private ArrayList<String> showList;

    /**
     * keyword = Prefix + space + Body
     *
     * Prefix accepts: 1) name 2) symbol 3) unit 4 (blank)
     * Body accepts (can be multiple): e.g. 1) force 2) F 3) N
     * Another kind of body: (any above) -> (any above)
     *
     * Examples:
     * 1. F
     * 2. unit N
     * 3. mass -> force
     * 4. F -> work
     * 5. unit A -> V
     *
     * @param keyword The keyword to specify search.
     */
    public void setSearchKeyword(String keyword) {
        showList = null;

        var copy = formulaeDatabase.getDatabase().clone();  // shallow copy: the HashMaps are not copied
        currentList = (ArrayList<HashMap<String, String>>) copy;

        // TODO implement general search (filter currentList with the keyword)
    }

    public ArrayList<String> getShowList() {
        if (showList == null) {
            showList = new ArrayList<>();
            for (HashMap<String, String> hashMap : currentList) {
                showList.add(hashMap.get(SharedConstants.EXPRESSION));
            }
        }
        return showList;
    }

    public HashMap<String, String> get(int index) {
        return currentList.get(index);
    }
}
