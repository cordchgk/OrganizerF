package organizer.TRIE;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Node implements Serializable {
    private final Node[] children = new Node[26];
    private char atChar;
    private List<Integer> points = new ArrayList<>();
    private Node parent;

    /**
     *
     */
    public Node() {
    }


    /**
     * Node constructor.
     *
     * @param atChar Char for node.
     * @param parent Parent node.
     */
    public Node(char atChar, Node parent) {
        this.atChar = atChar;
        this.parent = parent;

    }

    /**
     * @return Node parent node.
     */

    /**
     * @param atChar Node char value.
     */
    public void setChar(char atChar) {
        this.atChar = atChar;
    }

    /**
     * @param parent Node parent node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return Array of nodes,children of the this node.
     */
    public Node[] getChildren() {
        return this.children;
    }

    /**
     * @param atChar Char for the node that will be set.
     * @param child  "Empty" node
     */
    private void setChild(char atChar, Node child) {
        this.children[
                Character.getNumericValue(atChar) - 10] = child;
        child.setChar(atChar);
    }

    /**
     * Searches for the children node of this that holds the char @param.
     *
     * @param ch Char to search for.
     * @return Node with char @param or null.
     */
    private Node getChild(char ch) {
        if (this.getChildren()[Character.getNumericValue(ch) - 10]
                != null) {
            return this.getChildren()[Character.getNumericValue(ch) - 10];
        }
        return null;
    }


    public List<Integer> getPoints() {
        return points;
    }

    public void setPoints(List<Integer> points) {
        this.points = points;
    }

    /**
     * Checks if node has any siblings by looking at the children array
     * of its parent.
     *
     * @return If node has siblings or not.
     */
    private boolean hasSiblings() {
        int counter = 0;
        Node pointer = this;
        if (pointer.parent == null) {
            return false;
        }
        if (pointer.parent != null) {
            for (int i = 0; i < pointer.parent.children.length; i++) {
                if (pointer.parent.children[i] != null) {
                    counter++;
                    if (counter > 1) {
                        return true;
                    }
                }
            }
        }


        return false;
    }

    /**
     * Removes node and all nodes that are useless now.
     */
    public void remove() {
        Node pointer = this;
        if (this.hasChildren()) {
            this.setPoints(null);
        } else {
            while (!pointer.hasSiblings()) {
                if (pointer.parent.points != null) {
                    break;
                } else {
                    pointer = pointer.parent;
                }
            }
            int index = Character.getNumericValue(pointer.atChar) - 10;
            pointer = pointer.parent;
            pointer.children[index] = null;
        }
    }

    /**
     * @return String representing a node.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.atChar);
        if (this.points != null) {
            stringBuilder.append("[" + this.points + "]");


        }
        return stringBuilder.toString();
    }

    /**
     * @return If node has children or not.
     */
    private boolean hasChildren() {
        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i] != null) {
                return true;
            }
        }
        return false;
    }


}
