package organizer.group.daos;

import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.dtos.UserDTO;
import organizer.group.dtos.GroupUserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupUserDAO {


    public boolean isGroupAdminByDTO(GroupDTO groupDTO, UserDTO userDTO) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();


        Connection conn = pool.getConnection();
        String query = "SELECT * FROM postgres.public.memberofgroup WHERE\n" +
                "memberofgroup.gid = ? AND memberofgroup.uid = ? AND memberofgroup.admin = TRUE";
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
            statement.setInt(1, groupDTO.getgID());
            statement.setInt(2, userDTO.getUserID());
            result = statement.executeQuery();
            while (result.next()) {
                pool.releaseConnection(conn);
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return false;
    }



    public void removeUserFromGroup(GroupUserDTO dto){
        ConnectionPool pool = ConnectionPool.getInstance();
        int toReturn = 0;

        Connection conn = pool.getConnection();
        String query = "DELETE FROM postgres.public.memberofgroup WHERE postgres.public.memberofgroup.uid = ? AND postgres.public.memberofgroup.gid = ?";
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
            statement.setInt(1,dto.getUserID());
            statement.setInt(2,dto.getgID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }
}
