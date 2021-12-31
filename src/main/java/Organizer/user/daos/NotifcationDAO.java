package organizer.user.daos;

import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.system.ConnectionPool;
import organizer.user.dtos.NotificationDTO;
import organizer.user.dtos.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotifcationDAO {

    public void insertByDTO(NotificationDTO dto) {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO notifications (gid, time, message)  VALUES (?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, dto.getgID());
            statement.setString(2, dto.getTime());
            statement.setString(3, dto.getMessage());

            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }

    public List<NotificationDTO> selectByUserDTO(UserDTO userDTO) {
        ArrayList<NotificationDTO> dtos = new ArrayList<>();

        ConnectionPool pool = ConnectionPool.getInstance();


        Connection conn = pool.getConnection();
        String query = "SELECT notifications.gid,notifications.time,notifications.message,groupo.name FROM groupo,notifications,memberofgroup,users WHERE notifications.gid = memberofgroup.gid\n" +
                "AND memberofgroup.uid = users.userid AND userid = ? AND memberofgroup.accepted = TRUE AND groupo.groupid = memberofgroup.gid";
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

            statement.setInt(1, userDTO.getUserID());
            result = statement.executeQuery();
            while (result.next()) {
                NotificationDTO toAdd = new NotificationDTO();
                toAdd.setgID(result.getInt(1));
                toAdd.setTime(result.getString(2));
                toAdd.setMessage(result.getString(3));
                toAdd.setName(result.getString(4));
                dtos.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);


        return dtos;
    }


}
