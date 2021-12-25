package organizer.diet.meal.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.diet.nutrients.macronutrient.dtos.CarbDTO;
import organizer.diet.nutrients.macronutrient.dtos.FatDTO;
import organizer.diet.nutrients.macronutrient.dtos.MacroDTO;
import organizer.diet.nutrients.macronutrient.dtos.ProteinDTO;
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

    public static MealDAO getInstance() {
        return instance;
    }


    public List<MealDTO> selectByDto(UserDTO dto) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        List<MealDTO> dtos = new ArrayList<>();

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
                MealDTO mealDTO = new MealDTO();
                mealDTO.setName(result.getString(1));
                mealDTO.setmID(result.getInt(2));
                mealDTO.setMealIngredients(this.selectByMealDto(mealDTO));
                dtos.add(mealDTO);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return dtos;
    }


    public MealDTO returnMealDTO(MealDTO dto) {

        ConnectionPool pool = ConnectionPool.getInstance();
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
                toReturn.setMealIngredients(this.selectByMealDto(dto));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);


        return toReturn;
    }

    public List<IngredientDTO> selectByMealDto(MealDTO dto) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        List<IngredientDTO> dtos = new ArrayList<>();

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
                IngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setAmount(result.getFloat(1));
                FatDTO fatDTO = new FatDTO();
                fatDTO.setAmount(result.getFloat(2));
                ProteinDTO proteinDTO = new ProteinDTO();
                proteinDTO.setAmount(result.getFloat(3));

                CarbDTO carbDTO = new CarbDTO();
                carbDTO.setAmount(result.getFloat(4));
                MacroDTO macroDTO = new MacroDTO();
                macroDTO.setFatDTO(fatDTO);
                macroDTO.setCarbDTO(carbDTO);
                macroDTO.setProteinDTO(proteinDTO);
                ingredientDTO.setMacroDTO(macroDTO);
                ingredientDTO.setCalories(result.getFloat(5));
                ingredientDTO.setName(result.getString(6));


                ingredientDTO.setiID(result.getInt(7));


                dtos.add(ingredientDTO);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return dtos;
    }


    public boolean createMeal(MealDTO mealDTO, UserDTO userDTO) {

        ConnectionPool pool = ConnectionPool.getInstance();


        Connection conn = pool.getConnection();
        String query = "INSERT INTO meal (name, owner) VALUES (?,?)";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
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

        ConnectionPool pool = ConnectionPool.getInstance();


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


}
