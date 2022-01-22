package organizer.system.image.daos;

import organizer.diet.ingredient.dtos.IngredientDTO;
import organizer.system.ConnectionPool;
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

public class ImageDAO {
    ConnectionPool pool = ConnectionPool.getInstance();


    public List<String> getIngredientImagesUrls(IngredientDTO i_DTO) {
        List<String> t_R = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT ingredientimages.url FROM ingredientimages WHERE iid = ?";
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
                String t_A = result.getString(1);
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

}
