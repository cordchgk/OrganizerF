package organizer.shopping.list.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.ingredient.dtos.ShoppingListIngredientDTO;
import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.shopping.list.dtos.ShoppingListDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingListDAO {
    private final ConnectionPool pool = ConnectionPool.getInstance();


    public List<IngredientDTO> getIngredientssByUserDTO(UserDTO dto, ShoppingListDTO shoppingListDTO) throws DatabaseException {

        List<IngredientDTO> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT meal.name, meal.mid, event.date FROM meal, eventmeals, event " +
                "WHERE meal.owner = ? AND meal.mid = eventmeals.mid AND event.eid = eventmeals.eid " +
                "AND event.date BETWEEN ? AND ?";
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
            statement.setInt(1, dto.getUserID());
            statement.setDate(2, Date.valueOf(shoppingListDTO.getStartDate()));
            statement.setDate(3, Date.valueOf(shoppingListDTO.getEndDate()));
            result = statement.executeQuery();
            MealDAO mealDAO = new MealDAO();
            while (result.next()) {
                MealDTO toAdd = new MealDTO();

                toAdd.setmID(result.getInt(2));
                toAdd.setMealIngredients(mealDAO.getIngredientByMealDTO(toAdd));
                for (IngredientDTO ingredientDTO : toAdd.getMealIngredients()) {
                    toReturn.add(ingredientDTO);
                }


            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return toReturn;
    }

    public void addToShoppingList(UserDTO userDTO, IngredientDTO ingredientDTO) {

        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO shoppinglist (uid, iid, amount) VALUES (?,?,?)";

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


    public List<ShoppingListIngredientDTO> getShoppingListIngredients(UserDTO dto) throws DatabaseException {

        List<ShoppingListIngredientDTO> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT shoppinglist.amount,ingredient.name,ingredient.iid FROM shoppinglist,ingredient WHERE " +
                "shoppinglist.uid = ? AND shoppinglist.iid = ingredient.iid";
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
            statement.setInt(1, dto.getUserID());

            result = statement.executeQuery();

            while (result.next()) {
                ShoppingListIngredientDTO toAdd = new ShoppingListIngredientDTO();
                toAdd.setAmount(result.getFloat(1));
                toAdd.setName(result.getString(2));
                toAdd.setIID(result.getInt(3));
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

    public void removeFromShoppingList(UserDTO userDTO, IngredientDTO ingredientDTO) {

        Connection conn = pool.getConnection();
        String query = "DELETE FROM shoppinglist WHERE shoppinglist.uid = ? AND shoppinglist.iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userDTO.getUserID());
            statement.setInt(2, ingredientDTO.getIID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


    }


    public boolean updateShoppingListAmount(UserDTO userDTO, IngredientDTO ingredientDTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE shoppinglist SET amount = ? WHERE uid = ? AND iid = ?";


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

}
