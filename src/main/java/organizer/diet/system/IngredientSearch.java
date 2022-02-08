package organizer.diet.system;


import lombok.Getter;
import lombok.Setter;
import organizer.TRIE.Trie;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Search service for the ingredient search,holds the current search TRIE and provides
 * methods for the search itself and for adding ingredients
 *
 *
 *
 */
@ApplicationScoped
@Getter
@Setter
public class IngredientSearch implements Serializable {
    private static final IngredientSearch instance = new IngredientSearch();

    private Trie trie;
    private List<IngredientDTO> ingredients;

    public static IngredientSearch getInstance() {
        return instance;
    }


    /**
     * Constructor that is only called ONCE in the application lifecycle,
     * loads the tree and the ingredient list from the database
     */
    private IngredientSearch() {

        this.trie = new Trie();
        IngredientDAO ingredientDAO = new IngredientDAO();
        this.ingredients = new ArrayList<>(ingredientDAO.getIngredientsForTrie());
        this.addAllToTrie(this.ingredients);


    }


    /**
     * Adds the {@param word} to the current trie (and all possible suffixes) and adds the {@param value} to the
     * node's lists
     *
     * @param word Word to add to the trie
     * @param value Unique identifier (the ingredient id)
     */
    public void add(String word, int value) {

        this.trie.add(word, value);

        while (word.length() > 0) {
            word = word.substring(1);
            this.trie.add(word, value);

        }

    }


    /**
     * Adds the {@param i_DTO} to the list of all ingredients
     *
     *
     * @param i_DTO IngredientDTO to add to the list of all ingredients
     */
    public void addToList(IngredientDTO i_DTO) {
        this.ingredients.add(i_DTO);
    }


    /**
     *
     *
     *
     *
     * @param searchWord
     * @return
     */
    public List<Integer> search(String searchWord) {
        String[] words = searchWord.split(" ");

        ArrayList<HashSet<Integer>> all = new ArrayList<>();

        for (String s : words) {
            HashSet set = new HashSet(this.trie.points(s));
            all.add(set);
        }

        return this.filter(all);

    }




    private void addAllToTrie(List<IngredientDTO> dtos) {
        for (IngredientDTO dto : dtos) {
            this.add(dto.getName(), dto.getIID());
            this.add(dto.getBrand(), dto.getIID());
        }
    }


    private ArrayList<Integer> filter(ArrayList<HashSet<Integer>> allResults) {

        HashSet<Integer> toReturn = allResults.get(0);

        while (allResults.size() > 0) {
            toReturn.retainAll(allResults.get(0));
            allResults.remove(0);
        }

        return new ArrayList<>(toReturn);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return this.trie.toString(this.trie.getRoot(), stringBuilder);
    }


}
