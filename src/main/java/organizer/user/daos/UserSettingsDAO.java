package organizer.user.daos;

import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.dtos.UserDTO;
import organizer.user.dtos.UserSettingsDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserSettingsDAO {
    ConnectionPool pool = ConnectionPool.getInstance();

    public UserSettingsDTO getUserSettingsByDTO(UserDTO u_DTO) throws DatabaseException {

        UserSettingsDTO t_R = new UserSettingsDTO();


        Connection conn = pool.getConnection();
        String query = "SELECT usersettings.language FROM usersettings WHERE uid = ?";
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
              t_R.setLocale(result.getString(1));

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
