package organizer.diet.ingredient.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

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
        List<IngredientDTO> toReturn = new ArrayList<>();


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
                toAdd.setIID(result.getInt(1));
                toAdd.setName(result.getString(2));
                toAdd.setBrand(result.getString(3));
                toReturn.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toReturn;

    }


    public void addIngredientToMeal(MealDTO mealDTO, IngredientDTO ingredientDTO)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO mealingredients (mid, iid, amount) VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, mealDTO.getmID());
            statement.setInt(2, ingredientDTO.getIID());
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

        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO ingredient (name, fat, protein, carbs, calories, manufacturer, categorie, co) " +
                        "VALUES (?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ingredientDTO.getName());
            statement.setFloat(2, ingredientDTO.getFats());
            statement.setFloat(3, ingredientDTO.getProtein());
            statement.setFloat(4, ingredientDTO.getCarbs());
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

    public List<IngredientDTO> getUserIngredients(UserDTO userDTO) {
        List<IngredientDTO> toReturn = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredient.iid,ingredient.name,useringredients.amount,ingredient.manufacturer," +
                "ingredient.fat,ingredient.protein,ingredient.carbs,ingredient.calories FROM ingredient,useringredients WHERE " +
                "useringredients.uid = ? AND ingredient.iid = useringredients.iid";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query);

            statement.setInt(1, userDTO.getUserID());

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {


            result = statement.executeQuery();
            while (result.next()) {
                IngredientDTO toAdd = new IngredientDTO();
                toAdd.setIID(result.getInt(1));
                toAdd.setName(result.getString(2));
                toAdd.setAmount(result.getFloat(3));
                toAdd.setBrand(result.getString(4));
                toAdd.setFats(result.getFloat(5));
                toAdd.setProtein(result.getFloat(6));
                toAdd.setCarbs(result.getFloat(7));
                toAdd.setCalories(result.getFloat(8));
                toReturn.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toReturn;

    }


    public void addToUserIngredients(UserDTO userDTO, IngredientDTO ingredientDTO)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO useringredients (uid, iid, amount) VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userDTO.getUserID());
            statement.setInt(2, ingredientDTO.getIID());
            statement.setFloat(3, ingredientDTO.getAmount());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public boolean updateUserIngredientAmount(UserDTO userDTO, IngredientDTO ingredientDTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE useringredients SET amount = ? WHERE uid = ? AND iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setFloat(1, ingredientDTO.getAmount());
            statement.setInt(2, userDTO.getUserID());
            statement.setInt(3, ingredientDTO.getIID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }

    private boolean contains(UserDTO userDTO, IngredientDTO ingredientDTO) {
        Connection conn = pool.getConnection();
        String query = "SELECT useringredients.uid,useringredients.iid FROM useringredients WHERE useringredients.uid = ? AND useringredients.iid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement.setInt(1, userDTO.getUserID());
            statement.setInt(2, ingredientDTO.getIID());
            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {


            result = statement.executeQuery();
            while (result.next()) {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return false;
    }


}
