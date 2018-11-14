package nlp;

import java.util.HashMap;
import java.util.Map;

public class SearchTriples {

    private Map<String, NLPNode> roots;

    public SearchTriples() {
        this.roots = new HashMap<>();
    }

    public void insertTriples(String[] triples) {
        if (triples.length == 0) {
            System.err.println("No strings in triples");
            return;
        }

        int count = 0;
        NLPNode current;

        if (roots.containsKey(triples[0])) {
            current = roots.get(triples[0]);
        } else {
            current = new NLPNode(triples[0]);
            roots.put(triples[0], current);
        }

        for (int i = 1; i < triples.length; i++) {
            if (current.contains(triples[i])) {
                current = current.get(triples[i]);
            } else {
                NLPNode temp = new NLPNode(triples[i]);
                current.addNLPNode(triples[i], temp);
                current = temp;
            }
        }
    }

    public boolean exists(String[] searchArray) {
        if (searchArray.length == 0) {
            System.err.println("searchArray is empty");
            return false;
        }
        System.out.println("Start search");
        NLPNode current;

        if (roots.containsKey(searchArray[0])) {
            current = roots.get(searchArray[0]);
        } else {
            return false;
        }

        for (int i = 1; i < searchArray.length; i++) {
            if (current.contains(searchArray[i])) {
                current = current.get(searchArray[i]);
            } else {
                return false;
            }
        }
        return true;
    }

    public void printRoots() {
        for (String str: roots.keySet()) {
            System.out.print("|" + str + "|");
        }
        System.out.println();
    }
}
