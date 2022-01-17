package organizer.diet.meal.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.shopping.list.dtos.ShoppingListDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
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

public class MealDAO {
    private static final MealDAO instance = new MealDAO();
    ConnectionPool pool = ConnectionPool.getInstance();

    public static MealDAO getInstance() {
        return instance;
    }


    public List<MealDTO> getMealsByUserDTO(UserDTO dto) throws DatabaseException {

        List<MealDTO> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT meal.name,meal.mid"
                + " FROM meal WHERE meal.owner = ?";
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
                MealDTO toAdd = new MealDTO();
                toAdd.setName(result.getString(1));
                toAdd.setmID(result.getInt(2));
                toAdd.setMealIngredients(this.getIngredientByMealDTO(toAdd));
                toAdd.calculateCalories();
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


    public List<MealDTO> getMealsByUserDTOForShoppingList(UserDTO dto, ShoppingListDTO shoppingListDTO) throws DatabaseException {

        List<MealDTO> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT meal.name,meal.mid"
                + " FROM meal,eventmeals,event WHERE meal.owner = ?";
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
                MealDTO toAdd = new MealDTO();
                toAdd.setName(result.getString(1));
                toAdd.setmID(result.getInt(2));
                toAdd.setMealIngredients(this.getIngredientByMealDTO(toAdd));
                toAdd.calculateCalories();
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


    public List<Integer> getIDsForAccess(UserDTO dto) throws DatabaseException {

        List<Integer> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT meal.mid"
                + " FROM meal WHERE meal.owner = ?";
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
                toReturn.add(result.getInt(1));

            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return toReturn;
    }


    public MealDTO getMealDTO(MealDTO dto) {


        MealDTO toReturn = new MealDTO();
        toReturn.setmID(dto.getmID());

        Connection conn = pool.getConnection();
        String query = "SELECT name FROM meal WHERE mid = ? ";
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
            statement.setInt(1, dto.getmID());
            result = statement.executeQuery();
            while (result.next()) {
                toReturn.setName(result.getString(1));
                toReturn.setMealIngredients(this.getIngredientByMealDTO(dto));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);


        return toReturn;
    }

    public List<IngredientDTO> getIngredientByMealDTO(MealDTO dto) throws DatabaseException {

        List<IngredientDTO> toReturn = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT amount, fat, protein, carbs, calories,ingredient.name,ingredient.iid FROM meal," +
                "                ingredient," +
                "                mealingredients" +
                "        WHERE ingredient.iid = mealingredients.iid" +
                "        AND meal.mid = mealingredients.mid" +
                "        AND meal.mid = ?";
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
            statement.setInt(1, dto.getmID());
            result = statement.executeQuery();

            while (result.next()) {
                IngredientDTO toAdd = new IngredientDTO();

                toAdd.setAmount(result.getFloat(1));
                toAdd.setFats(result.getFloat(2));
                toAdd.setProtein(result.getFloat(3));
                toAdd.setCarbs(result.getFloat(4));
                toAdd.setCalories(result.getFloat(5));
                toAdd.setName(result.getString(6));
                toAdd.setIID(result.getInt(7));


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


    public boolean createMeal(MealDTO mealDTO, UserDTO userDTO) {


        Connection conn = pool.getConnection();
        String query = "INSERT INTO meal (name, owner) VALUES (?,?)";


        try {
            PreparedStatement statement;
            statement = conn.prepareStatement(query);
            statement.setString(1, mealDTO.getName());
            statement.setInt(2, userDTO.getUserID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }


    public boolean deleteMeal(MealDTO mealDTO) {


        Connection conn = pool.getConnection();
        String query = "DELETE FROM meal WHERE meal.mid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setInt(1, mealDTO.getmID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }

    public boolean deleteIngredientFromMeal(MealDTO mealDTO, IngredientDTO ingredientDTO) {
        Connection conn = pool.getConnection();
        String query = "DELETE FROM mealingredients WHERE mealingredients.iid = ? AND mealingredients.mid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, ingredientDTO.getIID());
            statement.setInt(2, mealDTO.getmID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }

    public boolean updateIngredientAmount(MealDTO mealDTO, IngredientDTO ingredientDTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE mealingredients SET amount = ? WHERE mid = ? AND iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setFloat(1, ingredientDTO.getAmount());
            statement.setInt(2, mealDTO.getmID());
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
