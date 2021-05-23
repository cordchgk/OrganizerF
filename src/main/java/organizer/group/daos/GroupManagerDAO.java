package organizer.group.daos;

import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.Creator.NotificationCreator;
import organizer.user.daos.NotifcationDAO;
import organizer.user.daos.UserDAO;
import organizer.group.dtos.GroupUserDTO;
import organizer.user.dtos.NotificationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupManagerDAO extends GroupDAO{





    public GroupDTO getDtoByID(GroupDTO dto) throws DatabaseException {


        ConnectionPool pool = ConnectionPool.getInstance();
        GroupDTO toReturn = new GroupDTO();


        Connection conn = pool.getConnection();
        String query = "SELECT * FROM postgres.public.groupo WHERE groupid = ?";
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
            statement.setInt(1, dto.getgID());
            result = statement.executeQuery();
            while (result.next()) {
                toReturn.setgID(result.getInt(1));
                toReturn.setName(result.getString(2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return toReturn;
    }


    public boolean makeAdmin(GroupUserDTO dto) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "UPDATE postgres.public.memberofgroup SET admin = TRUE WHERE postgres.public.memberofgroup.uid = ? AND " +
                        "postgres.public.memberofgroup.gid = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, dto.getUserID());
            statement.setInt(2, dto.getgID());
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


    public boolean addToGroup(GroupUserDTO dto) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO postgres.public.memberofgroup(uid, gid, admin, accepted) VALUES (?,?,FALSE,FALSE)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            int uID = this.getUserID(dto.getEmail());
            if (uID == 0 || this.alredyInGroup(uID, dto.getgID())) {
                pool.releaseConnection(conn);
                return false;
            }
            statement.setInt(1, this.getUserID(dto.getEmail()));
            statement.setInt(2, dto.getgID());
            statement.execute();


        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return true;
    }


    private int getUserID(String email) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        ResultSet result;
        String query = "SELECT userid FROM postgres.public.users WHERE email = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            result = statement.executeQuery();
            while (result.next()) {
                pool.releaseConnection(conn);
                return result.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return 0;
    }


    private boolean alredyInGroup(int userID, int groupID) {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        ResultSet result;
        String query = "SELECT * FROM postgres.public.memberofgroup WHERE uid = ? AND gid = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, userID);
            statement.setInt(2, groupID);
            result = statement.executeQuery();
            while (result.next()) {
                pool.releaseConnection(conn);
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return false;
    }


}
