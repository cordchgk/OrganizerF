package organizer.user.daos;

import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.dtos.UserDTO;
import organizer.system.ConnectionPool;

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
public class UserDAO {
    private static UserDAO instance = new UserDAO();

    public static UserDAO getInstance() {
        return instance;
    }


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


    public void updateStatus(String email)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
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

    public UserDTO selectById(Integer id) throws DatabaseException {
        UserDTO dto = new UserDTO();
        dto.setUserID(id);
        UserDTO result = selectByDto(dto).get(0);
        return result;
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

    public List<UserDTO> selectByDto(UserDTO DTO) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        List<UserDTO> userDTOs;
        userDTOs = new ArrayList<>();

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
            if (DTO.getUserID() != null && !DTO.getUserID().equals("")) {
                statement.setInt(1, DTO.getUserID());
                statement.setInt(2, DTO.getUserID());
            } else {
                statement.setNull(1, java.sql.Types.VARCHAR);
                statement.setNull(2, 0);
            }
            if (DTO.getEmail() != null && !DTO.getEmail().equals("")) {
                statement.setString(3, "" + DTO.getEmail() + "%");
                statement.setString(4, "" + DTO.getEmail() + "%");
            } else {
                statement.setNull(3, java.sql.Types.VARCHAR);
                statement.setNull(4, java.sql.Types.VARCHAR);
            }
            if (DTO.getPasswordHash() != null) {
                statement.setString(5, "" + DTO.getPasswordHash() + "");
                statement.setString(6, "" + DTO.getPasswordHash() + "");
            } else {
                statement.setNull(5, java.sql.Types.VARCHAR);
                statement.setNull(6, java.sql.Types.VARCHAR);
            }
            if (DTO.getFirstname() != null) {
                statement.setString(7, "%" + DTO.getFirstname() + "%");
                statement.setString(8, "%" + DTO.getFirstname() + "%");
            } else {
                statement.setNull(7, java.sql.Types.VARCHAR);
                statement.setNull(8, java.sql.Types.VARCHAR);
            }
            if (DTO.getSurname() != null) {
                statement.setString(9, "%" + DTO.getSurname() + "%");
                statement.setString(10, "%" + DTO.getSurname() + "%");
            } else {
                statement.setNull(9, java.sql.Types.VARCHAR);
                statement.setNull(10, java.sql.Types.VARCHAR);
            }
            if (DTO.getAddress() != null) {
                statement.setString(11, "%" + DTO.getAddress() + "%");
                statement.setString(12, "%" + DTO.getAddress() + "%");
            } else {
                statement.setNull(11, java.sql.Types.VARCHAR);
                statement.setNull(12, java.sql.Types.VARCHAR);
            }
            if (DTO.getVerificationHash() != null) {
                statement.setString(13, "%" + DTO.getVerificationHash() + "%");
                statement.setString(14, "%" + DTO.getVerificationHash() + "%");
            } else {
                statement.setNull(13, java.sql.Types.VARCHAR);
                statement.setNull(14, java.sql.Types.VARCHAR);
            }
            if (DTO.isStatus() != null) {
                statement.setString(15, DTO.isStatus().toString());
                statement.setString(16, DTO.isStatus().toString());
            } else {
                statement.setNull(15, java.sql.Types.VARCHAR);
                statement.setNull(16, java.sql.Types.VARCHAR);
            }

            result = statement.executeQuery();
            while (result.next()) {
                UserDTO dto = new UserDTO();
                dto.setUserID(Integer.parseInt(result.getString(1)));
                dto.setEmail(result.getString(2));
                dto.setPasswordHash(result.getString(3));
                dto.setFirstname(result.getString(4));
                dto.setSurname(result.getString(5));
                dto.setAddress(result.getString(6));
                dto.setVerificationHash(result.getString(7));
                dto.setStatus(result.getBoolean(8));

                userDTOs.add(dto);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return userDTOs;
    }

}
