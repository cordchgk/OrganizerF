package organizer.group.daos;

import organizer.group.dtos.GroupDTO;
import organizer.group.dtos.GroupMessageDTO;

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

public class GroupMessageDAO {

    public List<GroupMessageDTO> getMessages(GroupDTO dto){
        ArrayList toReturn = new ArrayList();
        ConnectionPool pool = ConnectionPool.getInstance();


        Connection conn = pool.getConnection();
        String query = "SELECT time,users.firstname,message FROM groupmessages,users,memberofgroup WHERE\n" +
                "gid = ? AND groupmessages.gid = memberofgroup.gid AND memberofgroup.uid = users.userid";
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
            statement.setInt(1, dto.getgID());
            result = statement.executeQuery();
            while (result.next()) {
            GroupMessageDTO toAdd = new GroupMessageDTO();
            toAdd.setTime(result.getString(1));
            toAdd.setTime(result.getString(2));
            toAdd.setTime(result.getString(3));
            toReturn.add(toAdd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);






     return toReturn;
    }



    public void insertByDTOs(GroupDTO groupDTO, UserDTO userDTO,GroupMessageDTO groupMessageDTO) {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO groupmessages(gid, uid, message, time) VALUES (?,?,?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1,groupDTO.getgID());
            statement.setInt(2,userDTO.getUserID());
            statement.setString(3,groupMessageDTO.getMessage());
            statement.setString(4,groupMessageDTO.getTime());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


}
