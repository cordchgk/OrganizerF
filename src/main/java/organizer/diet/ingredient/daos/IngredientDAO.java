package organizer.diet.ingredient.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.diet.nutrients.macronutrient.dtos.CarbDTO;
import organizer.diet.nutrients.macronutrient.dtos.FatDTO;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.macronutrient.dtos.ProteinDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IngredientDAO {
    ConnectionPool pool = ConnectionPool.getInstance();

    public List<IngredientDTO> getIngredientsForTrie() {
        List<IngredientDTO> ingredientDTOS = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredient.iid,ingredient.name,ingredient.manufacturer FROM ingredient";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {


            result = statement.executeQuery();
            while (result.next()) {
                IngredientDTO toAdd = new IngredientDTO();
                toAdd.setiID(result.getInt(1));
                toAdd.setName(result.getString(2));
                toAdd.setBrand(result.getString(3));
                ingredientDTOS.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return ingredientDTOS;

    }


    public IngredientDTO getIngredient(IngredientDTO dto) {
        IngredientDTO toAdd = new IngredientDTO();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredient.name,ingredient.manufacturer,ingredient.calories,ingredient.fat," +
                "ingredient.protein,ingredient.carbs,mealingredients.amount," +
                "ingredient.categorie,ingredient.iid FROM ingredient,mealingredients WHERE " +
                "ingredient.iid = mealingredients.iid AND ingredient.iid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {
            statement.setInt(1, dto.getiID());

            result = statement.executeQuery();
            while (result.next()) {


                toAdd.setName(result.getString(1));
                toAdd.setBrand(result.getString(2));
                toAdd.setCalories(result.getFloat(3));
                MacroDTO macroDTO = new MacroDTO();
                FatDTO fatDTO = new FatDTO();
                ProteinDTO proteinDTO = new ProteinDTO();
                CarbDTO carbDTO = new CarbDTO();

                fatDTO.setAmount(result.getFloat(4));
                proteinDTO.setAmount(result.getFloat(5));
                carbDTO.setAmount(result.getFloat(6));
                macroDTO.setFatDTO(fatDTO);
                macroDTO.setProteinDTO(proteinDTO);
                macroDTO.setCarbDTO(carbDTO);

                toAdd.setAmount(result.getFloat(7));
                toAdd.setiID(result.getInt(9));

                toAdd.setMacroDTO(macroDTO);

            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toAdd;

    }


    public void addIngredientToMeal(MealDTO mealDTO, IngredientDTO ingredientDTO)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO mealingredients (mid, iid, amount) VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, mealDTO.getmID());
            statement.setInt(2, ingredientDTO.getiID());
            statement.setFloat(3, ingredientDTO.getAmount());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public void createNewIngredient(IngredientDTO ingredientDTO)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO ingredient (name, fat, protein, carbs, calories, manufacturer, categorie, co) VALUES (?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ingredientDTO.getName());
            statement.setFloat(2, ingredientDTO.getMacroDTO().getFatDTO().getAmount());
            statement.setFloat(3, ingredientDTO.getMacroDTO().getProteinDTO().getAmount());
            statement.setFloat(4, ingredientDTO.getMacroDTO().getCarbDTO().getAmount());
            statement.setFloat(5, ingredientDTO.getCalories());
            statement.setString(6, ingredientDTO.getBrand());
            statement.setString(7, "");
            statement.setString(8, "");
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }

}
