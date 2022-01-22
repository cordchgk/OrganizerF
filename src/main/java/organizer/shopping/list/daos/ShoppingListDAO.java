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


    public List<IngredientDTO> getIngredientssByUserDTO(UserDTO u_DTO, ShoppingListDTO s_L_DTO) throws DatabaseException {

        List<IngredientDTO> t_R = new ArrayList<>();

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
            statement.setInt(1, u_DTO.getUserID());
            statement.setDate(2, Date.valueOf(s_L_DTO.getS_D()));
            statement.setDate(3, Date.valueOf(s_L_DTO.getE_D()));
            result = statement.executeQuery();
            MealDAO m_DAO = new MealDAO();
            while (result.next()) {
                MealDTO t_A = new MealDTO();
                t_A.setMID(result.getInt(2));
                t_A.setMealIngredients(m_DAO.getIngredientByMealDTO(t_A));
                for (IngredientDTO ingredientDTO : t_A.getMealIngredients()) {
                    ingredientDTO.createImageUrlList();
                    t_R.add(ingredientDTO);

                }


            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return t_R;
    }

    public void addToShoppingList(UserDTO u_DTO, IngredientDTO i_DTO) {

        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO shoppinglist (uid, iid, amount) VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, u_DTO.getUserID());
            statement.setInt(2, i_DTO.getIID());
            statement.setFloat(3, i_DTO.getAmount());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public List<ShoppingListIngredientDTO> getShoppingListIngredients(UserDTO u_DTO) throws DatabaseException {

        List<ShoppingListIngredientDTO> t_R = new ArrayList<>();

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
            statement.setInt(1, u_DTO.getUserID());

            result = statement.executeQuery();

            while (result.next()) {
                ShoppingListIngredientDTO t_A = new ShoppingListIngredientDTO();
                t_A.setAmount(result.getFloat(1));
                t_A.setName(result.getString(2));
                t_A.setIID(result.getInt(3));
                t_R.add(t_A);

            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return t_R;
    }

    public void removeFromShoppingList(UserDTO u_DTO, IngredientDTO i_DTO) {

        Connection conn = pool.getConnection();
        String query = "DELETE FROM shoppinglist WHERE shoppinglist.uid = ? AND shoppinglist.iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, u_DTO.getUserID());
            statement.setInt(2, i_DTO.getIID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


    }


    public boolean updateShoppingListAmount(UserDTO u_DTO, IngredientDTO i_DTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE shoppinglist SET amount = ? WHERE uid = ? AND iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setFloat(1, i_DTO.getAmount());
            statement.setInt(2, u_DTO.getUserID());
            statement.setInt(3, i_DTO.getIID());

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
