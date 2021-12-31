package organizer.TRIE;


import java.io.Serializable;
import java.util.HashSet;

/**
 *
 */

public class Trie implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Node root = new Node();
    private int keys = 0;

    /**
     * Constructor.
     */
    public Trie() {
    }

    /**
     * @return Root node.
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * If key is not present,adds it,if it is present the add method
     * returns false.
     *
     * @param key    Key to add.
     * @param points Points for the key.
     * @return If adding the key was successful or not.
     */
    public boolean add(String key, int points) {
        Node pointer = this.root;
        int added = 0;
        char[] keyChars = key.toCharArray();
        for (int j = 0; j < keyChars.length; j++) {

            if (pointer.getChildren()[Character.getNumericValue(
                    keyChars[j]) - 10] == null) {
                pointer.getChildren()[Character.getNumericValue(
                        keyChars[j]) - 10] = new Node(keyChars[j]
                        , pointer);
                Node help = pointer;
                pointer = pointer.getChildren()[
                        Character.getNumericValue(keyChars[j]) - 10];
                pointer.setParent(help);
                added++;
                pointer.getPoints().add(points);
            } else {
                pointer = pointer.getChildren()

                        [Character.getNumericValue(keyChars[j]) - 10];
                pointer.getPoints().add(points);

            }
        }
        if (added > 0) {

            keys++;
        } else if (pointer.getPoints() == null) {
            pointer.getPoints().add(points);
            added++;
        }


        return (added > 0);

    }

    /**
     * Removes the given key.Returns false if key is not present.
     *
     * @param key Key to removes.
     * @return If removing was successfull or not.
     */
    public boolean remove(String key) {

        if (keys == 1) {
            for (int i = 0; i < this.root.getChildren().length; i++) {
                this.root.getChildren()[i] = null;
            }
        } else {
            Node pointer = this.root;
            char[] keyChars = key.toCharArray();
            for (int j = 0; j < keyChars.length; j++) {
                if (pointer.getChildren()[Character.getNumericValue(
                        keyChars[j]) - 10] != null) {
                    pointer = pointer.getChildren()[
                            Character.getNumericValue(keyChars[j]) - 10];

                } else {
                    return false;
                }
            }
            if (pointer.getPoints() != null) {
                pointer.remove();
            } else {
                return false;
            }


        }
        keys--;
        return true;
    }

    /**
     * Changes the point value of the given key.
     *
     * @param key    Key which point value will be changed.
     * @param points New point value.
     * @return If changing the point value was successful or not.
     */
    public boolean change(String key, int points) {
        Node pointer = this.root;
        char[] keyChars = key.toCharArray();
        for (int j = 0; j < keyChars.length; j++) {
            if (pointer.getChildren()[Character.getNumericValue(
                    keyChars[j]) - 10] != null) {
                pointer = pointer.getChildren()[Character.getNumericValue(
                        keyChars[j]) - 10];
            } else {
                return false;
            }
        }
        if (pointer.getPoints() == null) {
            return false;
        } else {
            pointer.getPoints().add(points);
        }

        return true;
    }

    /**
     * Returns null if the key is not present or an Integer.
     *
     * @param key Key to search for.
     * @return Point value of the given key,can return null if key does
     * not exists.
     */
    public HashSet<Integer> points(String key) {
        Node pointer = this.root;
        char[] keyChars = key.toCharArray();
        for (int j = 0; j < keyChars.length; j++) {
            if (pointer.getChildren()[Character.getNumericValue(
                    keyChars[j]) - 10] != null) {
                pointer = pointer.getChildren()[Character.getNumericValue(
                        keyChars[j]) - 10];
            } else {
                return new HashSet<>();
            }
        }
        return new HashSet<>(pointer.getPoints());
    }

    /**
     * Builds a string representing the trie.
     *
     * @param pointer       Root node of the current trie looked at
     *                      in the recursion.
     * @param stringBuilder Stringbuilder that holds the string represen-
     *                      ting the trie
     * @return String representing the trie.
     */
    public String toString(Node pointer, StringBuilder stringBuilder) {
        for (int i = 0; i < pointer.getChildren().length; i++) {
            if (pointer.getChildren()[i] != null) {
                stringBuilder.append(pointer.getChildren()[i].toString());
                stringBuilder.append("(");
                toString(pointer.getChildren()[i], stringBuilder);
                stringBuilder.append(")");
            }
        }
        return stringBuilder.toString();
    }

}





