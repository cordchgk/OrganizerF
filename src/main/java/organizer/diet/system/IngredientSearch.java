package organizer.diet.system;


import lombok.Getter;
import lombok.Setter;
import organizer.TRIE.Trie;
import organizer.diet.ingredient.daos.IngredientDAO;
import organizer.diet.ingredient.dtos.IngredientDTO;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
@Getter
@Setter
public class IngredientSearch {
    private static final IngredientSearch instance = new IngredientSearch();

    private Trie trie;
    private List<IngredientDTO> i_L;

    public static IngredientSearch getInstance() {
        return instance;
    }


    private IngredientSearch() {

        this.trie = new Trie();
        IngredientDAO ingredientDAO = new IngredientDAO();
        this.i_L = new ArrayList<>(ingredientDAO.getIngredientsForTrie());

        this.addAllToTrie(this.i_L);


    }


    private ArrayList<Integer> filter(ArrayList<HashSet<Integer>> allResults) {

        HashSet<Integer> toReturn = allResults.get(0);

        while (allResults.size() > 0) {
            toReturn.retainAll(allResults.get(0));
            allResults.remove(0);
        }

        return new ArrayList<>(toReturn);
    }


    private void addAllToTrie(List<IngredientDTO> dtos) {
        for (IngredientDTO dto : dtos) {
            this.add(dto.getName(), dto.getIID());

            this.add(dto.getBrand(), dto.getIID());
        }
    }

    public void add(String word, int value) {

        this.trie.add(word, value);

        while (word.length() > 0) {
            word = word.substring(1);
            this.trie.add(word, value);

        }

    }

    public void addToList(IngredientDTO i_DTO) {
        this.i_L.add(i_DTO);
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


}
