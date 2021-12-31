package organizer.diet.system;


import organizer.TRIE.Trie;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class IngredientSearch {
    private static final IngredientSearch instance = new IngredientSearch();

    private Trie trie;
    private List<IngredientDTO> allIngredients;

    private IngredientSearch() {

        this.trie = new Trie();
        IngredientDAO ingredientDAO = new IngredientDAO();
        this.allIngredients = new ArrayList<>(ingredientDAO.getIngredientsForTrie());

        this.addAllToTrie(this.allIngredients);


    }

    public static IngredientSearch getInstance() {
        return instance;
    }



    private ArrayList<Integer> filter(ArrayList<HashSet<Integer>> allResults) {

        HashSet<Integer> toReturn = allResults.get(0);

        while (allResults.size() > 0) {
            toReturn.retainAll(allResults.get(0));
            allResults.remove(0);
        }

        return new ArrayList<>(toReturn);
    }

    public Trie getTrie() {
        return trie;
    }

    public void setTrie(Trie trie) {
        this.trie = trie;
    }

    private void addAllToTrie(List<IngredientDTO> dtos) {
        for (IngredientDTO dto : dtos) {
            this.add(dto.getName(), dto.getiID());

            this.add(dto.getBrand(), dto.getiID());
        }
    }

    public void add(String word, int value) {
        this.trie.add(word, value);
        while (word.length() > 0) {
            word = word.substring(1);
            this.trie.add(word, value);

        }

    }

    private void addToList(String word, int value) {
        IngredientDTO toAdd = new IngredientDTO();
        toAdd.setiID(value);
        toAdd.setName(word);
        this.allIngredients.add(toAdd);
    }

    public List<Integer> search(String searchWord) {
        String[] words = searchWord.split(" ");

        ArrayList<HashSet<Integer>> all = new ArrayList<>();

        for (String s : words) {
            HashSet set = new HashSet(this.trie.points(s));
            all.add(set);
        }

        return this.filter(all);

    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return this.trie.toString(this.trie.getRoot(), stringBuilder);
    }

    public List<IngredientDTO> getAllIngredients() {
        return allIngredients;
    }

    public void setAllIngredients(List<IngredientDTO> allIngredients) {
        this.allIngredients = allIngredients;
    }
}
