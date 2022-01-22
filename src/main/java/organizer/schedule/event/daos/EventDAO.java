package organizer.schedule.event.daos;

import organizer.diet.meal.daos.MealDAO;
import organizer.diet.meal.dtos.MealDTO;
import organizer.group.daos.GroupDAO;
import organizer.schedule.event.dtos.EventDTO;
import organizer.system.ConnectionPool;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDAO {

    ConnectionPool pool = ConnectionPool.getInstance();


    public List<EventDTO> selectByUserDto(UserDTO u_DTO) throws DatabaseException {

        List<EventDTO> t_R = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT event.name,event.starttime,event.endtime,event.date,event.eid FROM event,eventusers,users WHERE " +
                "event.eid = eventusers.eid AND eventusers.uid = users.userid AND userid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {

            statement = conn.prepareStatement(query);
            statement.setInt(1, u_DTO.getUserID());
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {

            result = statement.executeQuery();
            while (result.next()) {
                EventDTO t_A = new EventDTO();
                t_A.setName(result.getString(1));
                t_A.setStart(result.getTime(2).toLocalTime().atDate(result.getDate(4).toLocalDate()));
                t_A.setEnd(result.getTime(3).toLocalTime().atDate(result.getDate(4).toLocalDate()));
                t_A.setEID(result.getInt(5));
                t_A.setM_DTO_L(this.selectMealsByEventDTO(t_A));
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


    public boolean updateEvent(EventDTO e_DTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "UPDATE event SET name = ?,starttime = ?,endtime = ? WHERE eid = ?";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, e_DTO.getName());
            statement.setTimestamp(2, Timestamp.valueOf(e_DTO.getStart()));
            statement.setTimestamp(3, Timestamp.valueOf(e_DTO.getEnd()));
            statement.setInt(4, e_DTO.getEID());

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


    public boolean createNewEvent(EventDTO e_DTO, UserDTO u_DTO)
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

            statement.setString(1, e_DTO.getName());
            statement.setTimestamp(2, Timestamp.valueOf(e_DTO.getStart()));
            statement.setTimestamp(3, Timestamp.valueOf(e_DTO.getEnd()));
            statement.setDate(4, Date.valueOf(e_DTO.getDate()));


        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);

        }
        try {


            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            while (resultSet.next()) {
                e_DTO.setEID(resultSet.getInt(1));
                this.insertIntoEventUsers(e_DTO, u_DTO);
            }

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }


        pool.releaseConnection(conn);
        return true;
    }


    private boolean insertIntoEventUsers(EventDTO e_DTO, UserDTO u_DTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "INSERT INTO eventusers(eid, uid) VALUES (?,?)";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, e_DTO.getEID());
            statement.setInt(2, u_DTO.getUserID());
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





    public List<MealDTO> selectMealsByEventDTO(EventDTO e_DTO) throws DatabaseException {

        List<MealDTO> t_R = new ArrayList<>();


        Connection conn = pool.getConnection();
        String query = "SELECT meal.mid,meal.name FROM event,eventmeals,meal WHERE event.eid = eventmeals.eid AND eventmeals.mid = meal.mid AND event.eid = ?";
        PreparedStatement statement = null;
        ResultSet result;
        try {

            statement = conn.prepareStatement(query);
            statement.setInt(1, e_DTO.getEID());
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {

            result = statement.executeQuery();
            while (result.next()) {
                MealDTO t_A = new MealDTO();
                t_A.setMID(result.getInt(1));
                t_A.setName(result.getString(2));
                t_A.setMealIngredients(new MealDAO().getIngredientByMealDTO(t_A));
                t_A.calculateCalories();
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


    public void deleteMealFromEvent(EventDTO e_DTO, MealDTO m_DTO) {


        Connection conn = pool.getConnection();
        String query = "DELETE FROM eventmeals WHERE eventmeals.eid = ? AND eventmeals.mid = ?";
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(query);
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        try {
            statement.setInt(1, e_DTO.getEID());
            statement.setInt(2, m_DTO.getMID());
            statement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

    }


    public void addMealToEvent(EventDTO e_DTO, MealDTO m_DTO)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();
        String query = "INSERT INTO eventmeals (eid, mid) VALUES (?,?)";
        PreparedStatement statement = null;

        try {
            statement = conn.prepareStatement(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {

            statement.setInt(1, e_DTO.getEID());
            statement.setInt(2, m_DTO.getMID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);

        }


        pool.releaseConnection(conn);

    }
}
