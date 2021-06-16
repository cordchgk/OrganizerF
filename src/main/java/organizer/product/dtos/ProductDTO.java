package organizer.product.dtos;


import java.io.Serializable;

/**
 * Created by cord on 07.06.16.
 */
public class ProductDTO implements Serializable {

    private static final long serialVersionUID = 3461340127831352062L;


    private int pID;
    private int gID;


    private String name;
    private int count;
    private boolean ordered;

    private String description;

    public int getpID() {
        return pID;
    }

    public void setpID(int pID) {
        this.pID = pID;
    }

    public int getgID() {
        return gID;
    }

    public void setgID(int gID) {
        this.gID = gID;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int diff = 0;

    public boolean getOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public int getDiff() {
        return diff;
    }

    public void setDiff(int diff) {
        this.diff = diff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();






        return sb.toString();
    }
}
