package nlp;

import java.util.HashMap;
import java.util.Map;

public class NLPNode {

    private Map<String, NLPNode> children;
    private String key;

    public NLPNode(String key) {
        this.children = new HashMap<>();
        this.key = key;
    }

    public void addNLPNode(String key, NLPNode node) {
        children.put(key, node);
    }

    public NLPNode get(String key) {
        return children.get(key);
    }

    public boolean contains(String key) {
        return children.containsKey(key);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public void printChildren() {
        for (String key: children.keySet()) {
            System.out.print("|" + key + "|");
        }
        System.out.println();
    }

}
