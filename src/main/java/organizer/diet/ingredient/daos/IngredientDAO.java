package organizer.diet.ingredient.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IngredientDAO {
    ConnectionPool pool = ConnectionPool.getInstance();

    public List<IngredientDTO> getIngredientsForTrie() {
        List<IngredientDTO> to_Return = new ArrayList<>();

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
                IngredientDTO to_Add = new IngredientDTO();
                to_Add.setIID(result.getInt(1));
                to_Add.setName(result.getString(2));
                to_Add.setBrand(result.getString(3));
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


    public void addIngredientToMeal(MealDTO m_DTO, IngredientDTO i_DTO)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO mealingredients (mid, iid, amount) VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, m_DTO.getMID());
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


    public int createNewIngredient(IngredientDTO i_DTO,UserDTO u_DTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();

        ResultSet r_Set;
        int iD = 0;

        String query =
                "INSERT INTO ingredient (name, fat, protein, carbs, calories, manufacturer, categorie, co,uid) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, i_DTO.getName());
            statement.setFloat(2, i_DTO.getFats());
            statement.setFloat(3, i_DTO.getProtein());
            statement.setFloat(4, i_DTO.getCarbs());
            statement.setFloat(5, i_DTO.getCalories());
            statement.setString(6, i_DTO.getBrand());
            statement.setString(7, "");
            statement.setString(8, "");
            statement.setInt(9,u_DTO.getUserID());
            statement.executeUpdate();
            r_Set = statement.getGeneratedKeys();
            while (r_Set.next()) {
                iD = r_Set.getInt(1);

            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return iD;
    }

    public List<IngredientDTO> getUserIngredients(UserDTO u_DTO) {
        List<IngredientDTO> to_Return = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredient.iid,ingredient.name,useringredients.amount,ingredient.manufacturer," +
                "ingredient.fat,ingredient.protein,ingredient.carbs,ingredient.calories FROM ingredient,useringredients WHERE " +
                "useringredients.uid = ? AND ingredient.iid = useringredients.iid";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query);

            statement.setInt(1, u_DTO.getUserID());

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {


            result = statement.executeQuery();
            while (result.next()) {
                IngredientDTO to_Add = new IngredientDTO();
                to_Add.setIID(result.getInt(1));
                to_Add.setName(result.getString(2));
                to_Add.setAmount(result.getFloat(3));
                to_Add.setBrand(result.getString(4));
                to_Add.setFats(result.getFloat(5));
                to_Add.setProtein(result.getFloat(6));
                to_Add.setCarbs(result.getFloat(7));
                to_Add.setCalories(result.getFloat(8));
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


    public void addToUserIngredients(UserDTO u_DTO, IngredientDTO i_DTO)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO useringredients (uid, iid, amount) VALUES (?,?,?)";

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


    public boolean updateUserIngredientAmount(UserDTO u_DTO, IngredientDTO i_DTO) {
        Connection conn = pool.getConnection();
        String query = "UPDATE useringredients SET amount = ? WHERE uid = ? AND iid = ?";


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

    public IngredientDTO getIngredientByDTO(IngredientDTO i_DTO) {
        IngredientDTO t_R = new IngredientDTO();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredient.iid,ingredient.name,ingredient.fat,ingredient.protein,ingredient.carbs," +
                "ingredient.calories,ingredient.manufacturer,ingredient.uid FROM ingredient " +
                "WHERE ingredient.iid = ? ";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query);

            statement.setInt(1, i_DTO.getIID());

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {


            result = statement.executeQuery();
            while (result.next()) {
                t_R.setIID(result.getInt(1));
                t_R.setName(result.getString(2));
                t_R.setFats(result.getFloat(3));
                t_R.setProtein(result.getFloat(4));
                t_R.setCarbs(result.getFloat(5));
                t_R.setCalories(result.getFloat(6));
                t_R.setBrand(result.getString(7));
                t_R.setU_ID(result.getInt(8));
                t_R.createImageUrlList();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return t_R;

    }


}
