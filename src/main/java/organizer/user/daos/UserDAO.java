package organizer.user.daos;

import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.dtos.UserDTO;
import organizer.system.ConnectionPool;

import javax.servlet.http.Cookie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by cord on 07.06.16.
 */
public class UserDAO {

    ConnectionPool pool = ConnectionPool.getInstance();


    public void insert(UserDTO dto)
            throws DatabaseException, DuplicateException {
        String email = dto.getEmail();
        String password = dto.getPasswordHash();
        String firstname = dto.getFirstname();
        String surname = dto.getSurname();
        String address = dto.getAddress();
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO users (email, password, firstname, surname, "
                        + " address,verificationhash,accountstatus"
                        + ") VALUES (?, "
                        + "?, ?, ?, ?,?,?);";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, firstname);
            statement.setString(4, surname);
            statement.setString(5, address);
            statement.setString(6, dto.getVerificationHash());
            statement.setBoolean(7, false);
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }

    public void updateVerificationHash(String email, String verificationHash)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "UPDATE users SET verificationhash = ? WHERE email = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, verificationHash);
            statement.setString(2, email);
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }

    public void updatePassword(String email, String password)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "UPDATE users SET password = ? WHERE email = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, password);
            statement.setString(2, email);
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public void updateUserCookie(UserDTO u_DTO, Cookie cookie)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "UPDATE postgres.public.users SET sessioncookie = ? WHERE postgres.public.users.userid = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, cookie.getValue());
            statement.setInt(2, u_DTO.getUserID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public void updateUserLanuage(UserDTO u_DTO)
            throws DatabaseException, DuplicateException {


        Connection conn = pool.getConnection();
        String query =
                "UPDATE usersettings SET language = ? WHERE uid = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, u_DTO.getUserSettingsDTO().getLocale());
            statement.setInt(2, u_DTO.getUserID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    public void updateStatus(String email)
            throws DatabaseException, DuplicateException {

        Connection conn = pool.getConnection();

        String query =
                "UPDATE users SET accountstatus = TRUE WHERE email = ?;";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, email);
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }

    public String getVerificationHash(UserDTO dto) {
        ConnectionPool pool = ConnectionPool.getInstance();
        List<String> hash = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT verificationhash FROM users WHERE email = ?";
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
            statement.setString(1, dto.getEmail());
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }

        try {
            result = statement.executeQuery();
            while (result.next()) {
                hash.add(result.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }


        pool.releaseConnection(conn);
        return hash.get(0);
    }

    public UserDTO selectByDto(UserDTO u_DTO) throws DatabaseException {

        UserDTO t_R = null;

        Connection conn = pool.getConnection();
        String query = "SELECT userid, email, password, firstname, surname, "
                + "address,verificationhash,accountstatus"
                + " FROM users WHERE " +
                "(? IS NULL OR userid=?) " +
                "AND (? IS NULL OR LOWER(email) LIKE LOWER(?)) " +
                "AND (? IS NULL OR password = ?) " +
                "AND (? IS NULL OR LOWER(firstname) LIKE LOWER(?)) " +
                "AND (? IS NULL OR LOWER(surname) LIKE LOWER(?)) " +
                "AND (? IS NULL OR LOWER(address) LIKE LOWER(?)) " +
                "AND (? IS NULL OR verificationhash LIKE ?)" +
                "AND (? IS NULL OR accountstatus = ?::boolean)";
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
            if (u_DTO.getUserID() != null && !u_DTO.getUserID().equals("")) {
                statement.setInt(1, u_DTO.getUserID());
                statement.setInt(2, u_DTO.getUserID());
            } else {
                statement.setNull(1, Types.VARCHAR);
                statement.setNull(2, 0);
            }
            if (u_DTO.getEmail() != null && !u_DTO.getEmail().equals("")) {
                statement.setString(3, "" + u_DTO.getEmail() + "%");
                statement.setString(4, "" + u_DTO.getEmail() + "%");
            } else {
                statement.setNull(3, Types.VARCHAR);
                statement.setNull(4, Types.VARCHAR);
            }
            if (u_DTO.getPasswordHash() != null) {
                statement.setString(5, "" + u_DTO.getPasswordHash() + "");
                statement.setString(6, "" + u_DTO.getPasswordHash() + "");
            } else {
                statement.setNull(5, Types.VARCHAR);
                statement.setNull(6, Types.VARCHAR);
            }
            if (u_DTO.getFirstname() != null) {
                statement.setString(7, "%" + u_DTO.getFirstname() + "%");
                statement.setString(8, "%" + u_DTO.getFirstname() + "%");
            } else {
                statement.setNull(7, Types.VARCHAR);
                statement.setNull(8, Types.VARCHAR);
            }
            if (u_DTO.getSurname() != null) {
                statement.setString(9, "%" + u_DTO.getSurname() + "%");
                statement.setString(10, "%" + u_DTO.getSurname() + "%");
            } else {
                statement.setNull(9, Types.VARCHAR);
                statement.setNull(10, Types.VARCHAR);
            }
            if (u_DTO.getAddress() != null) {
                statement.setString(11, "%" + u_DTO.getAddress() + "%");
                statement.setString(12, "%" + u_DTO.getAddress() + "%");
            } else {
                statement.setNull(11, Types.VARCHAR);
                statement.setNull(12, Types.VARCHAR);
            }
            if (u_DTO.getVerificationHash() != null) {
                statement.setString(13, "%" + u_DTO.getVerificationHash() + "%");
                statement.setString(14, "%" + u_DTO.getVerificationHash() + "%");
            } else {
                statement.setNull(13, Types.VARCHAR);
                statement.setNull(14, Types.VARCHAR);
            }
            if (u_DTO.isStatus() != null) {
                statement.setString(15, u_DTO.isStatus().toString());
                statement.setString(16, u_DTO.isStatus().toString());
            } else {
                statement.setNull(15, Types.VARCHAR);
                statement.setNull(16, Types.VARCHAR);
            }

            result = statement.executeQuery();
            while (result.next()) {
                t_R = new UserDTO();
                t_R.setUserID(Integer.parseInt(result.getString(1)));
                t_R.setEmail(result.getString(2));
                t_R.setPasswordHash(result.getString(3));
                t_R.setFirstname(result.getString(4));
                t_R.setSurname(result.getString(5));
                t_R.setAddress(result.getString(6));
                t_R.setVerificationHash(result.getString(7));
                t_R.setStatus(result.getBoolean(8));


            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return t_R;
    }


    public UserDTO selectByCookie(Cookie cookie) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        UserDTO t_R = null;

        Connection conn = pool.getConnection();
        String query = "SELECT userid, email, password, firstname, surname, "
                + "address,verificationhash,accountstatus,sessioncookie"
                + " FROM users WHERE sessioncookie = ?";
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
            statement.setString(1, cookie.getValue());

            result = statement.executeQuery();
            while (result.next()) {
                t_R = new UserDTO();
                t_R.setUserID(Integer.parseInt(result.getString(1)));
                t_R.setEmail(result.getString(2));
                t_R.setPasswordHash(result.getString(3));
                t_R.setFirstname(result.getString(4));
                t_R.setSurname(result.getString(5));
                t_R.setAddress(result.getString(6));
                t_R.setVerificationHash(result.getString(7));
                t_R.setStatus(result.getBoolean(8));
                t_R.setSessioncookie(result.getString(9));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return t_R;
    }



    public UserDTO testselectByDto(UserDTO DTO) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        UserDTO dto = new UserDTO();

        Connection conn = pool.getConnection();
        String query = "SELECT userid, email, password, firstname, surname, "
                + "address,verificationhash,accountstatus"
                + " FROM users WHERE userid = ?";
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
            statement.setInt(1, DTO.getUserID());
            result = statement.executeQuery();
            while (result.next()) {

                dto.setUserID(Integer.parseInt(result.getString(1)));
                dto.setEmail(result.getString(2));
                dto.setPasswordHash(result.getString(3));
                dto.setFirstname(result.getString(4));
                dto.setSurname(result.getString(5));
                dto.setAddress(result.getString(6));
                dto.setVerificationHash(result.getString(7));
                dto.setStatus(result.getBoolean(8));


            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

        return dto;
    }

}
