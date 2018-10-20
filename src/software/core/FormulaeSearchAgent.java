package software.core;

import java.util.*;

public class FormulaeSearchAgent {

    private String currentKeyword = "";
    private String lastWord;

    private List<List<String>> requiredSymbols = new ArrayList<>();

    public static final int SYMBOL_PREFIX = 0;
    public static final int UNIT_PREFIX = 1;
    public static final List<String> PREFIXES = List.of("sym", "unit");

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
    public void setKeyword(String keyword) {
        if (currentKeyword.equals(keyword))
            return;
        currentKeyword = keyword;

        var words = Arrays.asList(keyword.split(" "));

        var prefixNum = PREFIXES.indexOf(words.get(0));
        var hasPrefix = prefixNum != -1;

        requiredSymbols.clear();

        for (int i = (hasPrefix) ? 1 : 0; i < words.size(); i++) {
            var word = words.get(i);

            var acceptable = new ArrayList<String>();
            if (!hasPrefix || prefixNum == SYMBOL_PREFIX) {
                // a symbol can either be itself or a name
                var symbol = FormulaeDatabase.SYMBOL_NAMES.get(word);
                acceptable.add((symbol == null) ? word : symbol);
            }
            if (!hasPrefix || prefixNum == UNIT_PREFIX) {
                var unit = FormulaeDatabase.UNIT_NAMES.get(word);
                var symbol = FormulaeDatabase.UNITS.get((unit == null) ? word : unit);
                if (symbol != null) acceptable.add(symbol);
            }
            requiredSymbols.add(acceptable);
        }
    }

    // created by Raymond 5020 to create list of associated names
    public List<String> getAssociatedNames() {
        var nameList = new ArrayList<String>();

        FormulaeDatabase.SYMBOL_NAMES.keySet().forEach(name -> {
            // contains but not equal to
            if (name.startsWith(currentKeyword) && !name.equals(currentKeyword))
                nameList.add(PREFIXES.get(SYMBOL_PREFIX) + " " + name);
        });

        FormulaeDatabase.UNIT_NAMES.keySet().forEach(name -> {
            // contains but not equal to
            if (name.startsWith(currentKeyword) && !name.equals(currentKeyword))
                nameList.add(PREFIXES.get(UNIT_PREFIX) + " " + name);
        });

        return nameList;
    }

    // created by Raymond 5020 to filter the formulae list
    public List<ArrayList<String>> getFormulaeResult() {
        // if there's no required element, every formula is valid and there's no meaning of searching
        if (requiredSymbols.isEmpty())
            return List.of();

        var formulaList = new ArrayList<ArrayList<String>>();
        FormulaeDatabase.FORMULAE.forEach(formula -> {
            var expression = formula.get(FormulaeDatabase.EXPRESSION);

            outer:
            for (var acceptableSymbols : requiredSymbols) {
                // every required symbol must show up
                for (var symbol : acceptableSymbols) {
                    // one among each set of accepted alternatives shows up is OK
                    if (expression.contains(symbol)) {
                        continue outer;
                    }
                }
                return;  // code reaches here means there is at least one required element not found
            }

            formulaList.add(formula);  // the formula is relevant
        });

        return formulaList;
    }
}
