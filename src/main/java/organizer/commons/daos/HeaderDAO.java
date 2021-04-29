package organizer.commons.daos;

import organizer.commons.dtos.HeaderDTO;
import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.dtos.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeaderDAO {


    public List<Integer> selectByUser(HeaderDTO headerDTO) throws DatabaseException {


        ConnectionPool pool = ConnectionPool.getInstance();
        List<Integer> gIDs = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT * FROM postgres.public.memberofgroup WHERE uid = ? AND\n" +
                "accepted = TRUE";
        PreparedStatement statement = null;
        ResultSet result;
        try {

            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {
            assert statement != null;
            statement.setInt(1, headerDTO.getUserID());
            result = statement.executeQuery();
            while (result.next()) {
                int toAdd = result.getInt(2);
                gIDs.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return gIDs;
    }
}
