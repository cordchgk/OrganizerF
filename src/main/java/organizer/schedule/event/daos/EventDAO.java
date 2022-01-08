package organizer.schedule.event.daos;

import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.product.dtos.ProductDTO;
import organizer.schedule.event.dtos.EventDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import javax.xml.registry.infomodel.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDAO {

    ConnectionPool pool = ConnectionPool.getInstance();


    public List<EventDTO> selectByUserDto(UserDTO userDTO) throws DatabaseException {

        List<EventDTO> toReturn = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT event.name,event.starttime,event.endtime,event.date,event.eid FROM event,eventusers,users WHERE " +
                "event.eid = eventusers.eid AND eventusers.uid = users.userid AND userid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {

            statement = conn.prepareStatement(query);
            statement.setInt(1, userDTO.getUserID());
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {

            result = statement.executeQuery();
            while (result.next()) {
                EventDTO toADD = new EventDTO();
                toADD.setName(result.getString(1));
                toADD.setStart(result.getTime(2).toLocalTime().atDate(result.getDate(4).toLocalDate()));
                toADD.setEnd(result.getTime(3).toLocalTime().atDate(result.getDate(4).toLocalDate()));
                toADD.seteID(result.getInt(5));

                toReturn.add(toADD);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toReturn;
    }


    public boolean updateEvent(EventDTO eventDTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "UPDATE event SET name = ?,starttime = ?,endtime = ? WHERE eid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, eventDTO.getName());
            statement.setTimestamp(2, Timestamp.valueOf(eventDTO.getStart()));
            statement.setTimestamp(3, Timestamp.valueOf(eventDTO.getEnd()));
            statement.setInt(4, eventDTO.geteID());

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


    public boolean createNewEvent(EventDTO eventDTO, UserDTO userDTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "INSERT INTO event(name, starttime, endtime,date) VALUES (?,?,?,?)";
        PreparedStatement statement = null;
        ResultSet result;
        try {
            statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {

            statement.setString(1, eventDTO.getName());
            statement.setTimestamp(2, Timestamp.valueOf(eventDTO.getStart()));
            statement.setTimestamp(3, Timestamp.valueOf(eventDTO.getEnd()));
            statement.setDate(4, Date.valueOf(eventDTO.getDate()));


        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);

        }
        try {


            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            while (resultSet.next()) {
                eventDTO.seteID(resultSet.getInt(1));
                this.insertIntoEventUsers(eventDTO, userDTO);
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }


        pool.releaseConnection(conn);
        return true;
    }


    private boolean insertIntoEventUsers(EventDTO eventDTO, UserDTO userDTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "INSERT INTO eventusers(eid, uid) VALUES (?,?)";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, eventDTO.geteID());
            statement.setInt(2, userDTO.getUserID());
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


    private List<UserDTO> selectByEventDTO(EventDTO eventDTO) throws DatabaseException {

        List<UserDTO> toReturn = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT event.name,event.starttime,event.endtime FROM event,eventusers,users WHERE " +
                "event.eid = eventusers.eid AND eventusers.uid = users.userid AND userid = ?";
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


            result = statement.executeQuery();
            while (result.next()) {
                EventDTO toADD = new EventDTO();
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toReturn;
    }


    public List<MealDTO> selectMealsByEventDTO(EventDTO eventDTO) throws DatabaseException {

        List<MealDTO> toReturn = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT meal.mid,meal.name FROM event,eventmeals,meal WHERE event.eid = eventmeals.eid AND eventmeals.mid = meal.mid AND event.eid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {

            statement = conn.prepareStatement(query);
            statement.setInt(1, eventDTO.geteID());
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {

            result = statement.executeQuery();
            while (result.next()) {
                MealDTO toAdd = new MealDTO();
                toAdd.setmID(result.getInt(1));
                toAdd.setName(result.getString(2));
                toAdd.setMealIngredients(new MealDAO().getIngredientByMealDTO(toAdd));



                toReturn.add(toAdd);

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
