package software.core;

import java.util.*;

public class FormulaeSearchAgent {

    private String currentKeyword = "";

    /**
     * The variable that stores search criteria, updated during analyzation process (setKeyword).
     *
     * Each sub array A in this array represent a needed element in the formula.
     * Each element E in A an acceptable alternative for the element that A represents.
     *
     * For example:
     *
     * When requiredSymbols = [[F], [s, t], [m, s]],
     * the program will go through [F], [s, t], [m, s]
     * respectively for each formula it tests. (getFormulaResult)
     *
     * 1. For [F], it tests if the formula includes symbol 'F';
     * 2. For [s, t], it tests if the formula includes one of 's' and 't';
     * 3. For [m, s], it tests if the formula includes one of 'm' and 's';
     *
     * If all of the three above yield true result, the formula is accepted.
     * If any of the three above yields false result, the formula is rejected.
     */
    private List<List<String>> requiredSymbols = new ArrayList<>();

    public static final int SYMBOL_PREFIX = 0;
    public static final int UNIT_PREFIX = 1;
    public static final List<String> PREFIXES = List.of("sym", "unit");

    /**
     * created by John 5076 to pre-analyze the keyword
     *
     * keyword = Prefix + space + Body
     *
     * Prefix accepts: 1) symbol 2) unit 3) (blank)
     * Body accepts: e.g. 1) force 2) F 3) Newton 4) N
     * (body elements can be multiple (F a m) and mixed (force a kilogram))
     *
     * Examples:
     * 1. F
     * 2. unit N
     * 3. F a m
     * 4. force
     * 5. Newton acceleration kilogram
     *
     * *Case sensitive*
     *
     * @param keyword The keyword to specify search.
     */
    public void setKeyword(String keyword) {
        // avoid reanalyze the same keyword
        if (currentKeyword.equals(keyword))
            return;
        currentKeyword = keyword;

        // split into words
        var words = Arrays.asList(keyword.split(" "));

        // get the first word and test if it is a preset prefix
        var prefixNum = PREFIXES.indexOf(words.get(0));
        var hasPrefix = prefixNum != -1;

        requiredSymbols.clear();

        for (int i = (hasPrefix) ? 1 : 0; i < words.size(); i++) {
            var word = words.get(i);

            var acceptable = new ArrayList<String>(2);
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

    /**
     * created by Ian 5013 to create list of associated names
     *
     * Example:
     *
     * With keyword 'm',
     * Returns ["sym mass", "unit minute", "unit meter", ...]
     *
     * @return a list of names with proper prefix.
     */
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

    /**
     * created by John 5076 to filter the formulae list
     *
     * Example:
     *
     * With requiredSymbols [[F], [m, s]],
     * Returns [F = a * m, W = F * s, ...]
     *
     * Detailed functions are explained at the top of the file.
     *
     * @return A list of formulae.
     */
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
