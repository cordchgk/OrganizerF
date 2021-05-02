package organizer.group.daos;

import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.user.dtos.UserDTO;
import organizer.user.dtos.UserGroupDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GroupManagerDAO {


    public List<UserGroupDTO> getUsers(GroupDTO dto) throws DatabaseException {


        ConnectionPool pool = ConnectionPool.getInstance();
        List<UserGroupDTO> users = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT users.firstname, users.surname, memberofgroup.admin\n" +
                "FROM postgres.public.users,\n" +
                "     postgres.public.memberofgroup\n" +
                "WHERE memberofgroup.gid = ? AND memberofgroup.uid = userid;";
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
                UserGroupDTO toAdd = new UserGroupDTO();
                toAdd.setFirstname(result.getString(1));
                toAdd.setSurname(result.getString(2));
                toAdd.setGroupAdmin(result.getBoolean(3));
                users.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return users;
    }


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


}
