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

    ConnectionPool pool = ConnectionPool.getInstance();


    public List<MealDTO> getMealsByUserDTO(UserDTO u_DTO) throws DatabaseException {

        List<MealDTO> to_Return = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT meal.name,meal.mid"
                + " FROM meal WHERE meal.owner = ?";
        PreparedStatement statement = null;
        ResultSet r_Set;
        try {
            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {
            statement.setInt(1, u_DTO.getUserID());
            r_Set = statement.executeQuery();
            while (r_Set.next()) {
                MealDTO to_Add = new MealDTO();
                to_Add.setName(r_Set.getString(1));
                to_Add.setMID(r_Set.getInt(2));
                to_Add.setMealIngredients(this.getIngredientByMealDTO(to_Add));
                to_Add.calculateCalories();
                to_Return.add(to_Add);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return to_Return;
    }


    public MealDTO getMealDTO(MealDTO m_DTO) {


        MealDTO t_R = new MealDTO();
        t_R.setMID(m_DTO.getMID());

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
            statement.setInt(1, m_DTO.getMID());
            result = statement.executeQuery();
            while (result.next()) {
                t_R.setName(result.getString(1));
                t_R.setMealIngredients(this.getIngredientByMealDTO(m_DTO));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);


        return t_R;
    }

    public List<IngredientDTO> getIngredientByMealDTO(MealDTO m_DTO) throws DatabaseException {

        List<IngredientDTO> t_R = new ArrayList<>();

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
            statement.setInt(1, m_DTO.getMID());
            result = statement.executeQuery();

            while (result.next()) {
                IngredientDTO t_A = new IngredientDTO();

                t_A.setAmount(result.getFloat(1));
                t_A.setFats(result.getFloat(2));
                t_A.setProtein(result.getFloat(3));
                t_A.setCarbs(result.getFloat(4));
                t_A.setCalories(result.getFloat(5));
                t_A.setName(result.getString(6));
                t_A.setIID(result.getInt(7));


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


    public boolean createMeal(MealDTO m_DTO, UserDTO u_DTO) {


        Connection conn = pool.getConnection();
        String query = "INSERT INTO meal (name, owner) VALUES (?,?)";


        try {
            PreparedStatement statement;
            statement = conn.prepareStatement(query);
            statement.setString(1, m_DTO.getName());
            statement.setInt(2, u_DTO.getUserID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
            return false;
        }

        pool.releaseConnection(conn);


        return true;
    }


    public boolean deleteMeal(MealDTO m_DTO) {


        Connection conn = pool.getConnection();
        String query = "DELETE FROM meal WHERE meal.mid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setInt(1, m_DTO.getMID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }

    public boolean deleteIngredientFromMeal(MealDTO m_DTO, IngredientDTO i_DTO) {
        Connection conn = pool.getConnection();
        String query = "DELETE FROM mealingredients WHERE mealingredients.iid = ? AND mealingredients.mid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, i_DTO.getIID());
            statement.setInt(2, m_DTO.getMID());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        pool.releaseConnection(conn);


        return true;
    }

    public boolean updateIngredientAmount(MealDTO m_DTO, IngredientDTO i_DTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE mealingredients SET amount = ? WHERE mid = ? AND iid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setFloat(1, i_DTO.getAmount());
            statement.setInt(2, m_DTO.getMID());
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
