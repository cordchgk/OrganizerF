package organizer.shopping.list.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
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
    private ConnectionPool pool = ConnectionPool.getInstance();


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


}
