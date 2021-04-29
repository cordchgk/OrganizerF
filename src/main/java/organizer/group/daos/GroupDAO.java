package organizer.group.daos;

import organizer.group.dtos.GroupDTO;
import organizer.product.dtos.ProductDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
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

/**
 * Created by cord on 07.06.16.
 */
public class GroupDAO {
    private static GroupDAO instance = new GroupDAO();

    public static GroupDAO getInstance() {
        return instance;
    }


    public List<GroupDTO> selectByUser(UserDTO userDTO) throws DatabaseException {


        ConnectionPool pool = ConnectionPool.getInstance();
        List<GroupDTO> groupDTOS;
        groupDTOS = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT groupo.name,memberofgroup.accepted,groupo.groupid,memberofgroup.admin\n" +
                "FROM memberofgroup,groupo\n" +
                "WHERE (memberofgroup.uid=?) AND memberofgroup.gid = groupo.groupid";
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
            statement.setInt(1, userDTO.getUserID());
            result = statement.executeQuery();
            while (result.next()) {
                GroupDTO dto = new GroupDTO();
                dto.setName(result.getString(1));
                dto.setAccepted(result.getBoolean(2));
                dto.setgID(result.getInt(3));
                dto.setGroupAdmin(result.getBoolean(4));
                groupDTOS.add(dto);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return groupDTOS;
    }


    public void update(GroupDTO dto, UserDTO userDTO)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "UPDATE postgres.public.memberofgroup SET accepted = ? WHERE postgres.public.memberofgroup.gid = ? " +
                        "AND postgres.public.memberofgroup.uid = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setBoolean(1,dto.isAccepted());
            statement.setInt(2, dto.getgID());
            statement.setInt(3, userDTO.getUserID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }
}

