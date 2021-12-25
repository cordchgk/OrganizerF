package organizer.TRIE;

import organizer.system.ConnectionPool;
import organizer.system.ObjectCloner;
import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.daos.UserDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrieDAO {


    public void insert(Trie trie)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query = "INSERT INTO trie VALUES (?)";


        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, ObjectCloner.toString(trie));

            statement.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.releaseConnection(conn);
    }
}
