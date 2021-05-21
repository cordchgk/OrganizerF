package organizer.system;

import organizer.system.Events.UserEventBean;
import organizer.user.beans.UserBean;

import organizer.system.exceptions.DatabaseException;

import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The ConnectionPool provides access to a certain number of database
 * connections. It locks deployed connections until they become released.
 *
 * @author
 */
@ApplicationScoped
public class ConnectionPool implements Serializable {

    private static final Logger LOGGER =
            Logger.getLogger(ConnectionPool.class.getName());

    private static ConnectionPool instance = new ConnectionPool();
    private final String dbUsername;
    private final String dbPassword;
    private final String dbDriver;
    private final Integer maxFailedConnections;
    private final List<Connection> availableConnections;
    private final List<Connection> blockedConnections;
    private Integer failedConnections;
    private Boolean closed;


    private ConnectionPool() {


        final Integer maxConnections = Config.getEntry("MAX_CONNECTIONS", 20);
        final String dbDriver =
                Config.getEntry("DB_DRIVER", "org.postgresql.Driver");
        this.maxFailedConnections =
                Config.getEntry("MAX_FAIlED_CONNECTIONS", 30);
        this.dbUsername = Config.getMandatoryEntry("DB_USERNAME");

        this.dbPassword = Config.getMandatoryEntry("DB_PASSWORD");

        this.availableConnections = new ArrayList<>(maxConnections);
        this.blockedConnections = new ArrayList<>(maxConnections);
        this.failedConnections = new Integer(0);
        this.closed = new Boolean(false);

        this.dbDriver = MessageFormat.format("jdbc:postgresql://{0}:{1}/{2}",
                Config.getMandatoryEntry("DB_HOST"),
                Config.getEntry("DB_PORT", "5432"),
                Config.getMandatoryEntry("DB_NAME"));

        try {
            Class.forName(dbDriver);
        } catch (final ClassNotFoundException ex) {
            throw new DatabaseException(
                    Messages.getFormattedString("DB.INVALID_DRIVER", dbDriver),
                    ex);
        }


        while (this.availableConnections.size() < maxConnections) {
            this.addConnection();
        }
        this.createDatabase();
        LOGGER.info("ConnectionPool initialized.");
    }


    public static ConnectionPool getInstance() {

        return instance;
    }

    private void addConnection() {

        try {
            final Connection connection = DriverManager
                    .getConnection(this.dbDriver, this.dbUsername,
                            this.dbPassword);
            this.availableConnections.add(connection);
        } catch (final SQLException ex) {
            checkMaxFails("DB.CONNECTION_FAIL", ex);
        }
    }


    /**
     * Releases the lock from the provided connection.
     *
     * @param connection the connection to release
     */
    public void releaseConnection(final Connection connection) {


        synchronized (this.closed) {
            if (this.closed) {
                return;
            } else {
                try {
                    if (connection.isClosed()) {
                        this.closeConnectionPool();
                        throw new DatabaseException(
                                Messages.getString("DB.CONNECTION_CLOSED"));
                    }
                } catch (SQLException e) {
                    LOGGER.warning("Releasing connection failed due to isClosed"
                            + "() exception.");
                    this.releaseClosedConnection(connection);
                    return;
                }
            }
        }

        synchronized (this.availableConnections) {
            if (this.checkConnection(connection)) {
                this.availableConnections.add(connection);
            }
            this.availableConnections.notifyAll();
        }
        synchronized (this.blockedConnections) {
            this.blockedConnections.remove(connection);
        }
    }

    /**
     * Releases a automatically closed invalid connection.
     *
     * @param connection the invalid connection
     */

    private void releaseClosedConnection(final Connection connection) {
        synchronized (this.closed) {
            if (this.closed) {
                return;
            }
        }
        synchronized (this.availableConnections) {
            this.addConnection();
            this.availableConnections.notifyAll();
        }
        synchronized (this.blockedConnections) {
            this.blockedConnections.remove(connection);
        }
    }

    private Boolean checkConnection(final Connection connection) {
        try {
            if (!connection.isValid(1)) {
                connection.close();
                checkMaxFails("DB.CONNECTION_INVALID", null);
                return false;

            } else {
                return true;
            }
        } catch (final SQLException ex) {
            checkMaxFails("DB.CONNECTION_INVALID", ex);
            return false;
        }
    }

    private void checkMaxFails(String messageKey, Throwable cause) {
        synchronized (this.failedConnections) {
            this.failedConnections++;
            LOGGER.log(Level.WARNING, Messages.getFormattedString(messageKey,
                    this.failedConnections, this.maxFailedConnections), cause);
            if (this.failedConnections >= this.maxFailedConnections) {
                this.closeConnectionPool();
                throw new DatabaseException(
                        Messages.getFormattedString("DB.MAX_FAIlED_CONNECTIONS",
                                this.maxFailedConnections), (Exception) cause);
            } else {
                this.addConnection();
            }
        }
    }

    /**
     * Provides and locks a connection from the connection pool. If no
     * connection is available, the request keeps blocked until there is a new
     * connection.
     *
     * @return the locked connection
     */
    public Connection getConnection() {

        synchronized (this.closed) {
            if (this.closed) {
                return null;
            }
        }
        Connection connection;
        synchronized (this.availableConnections) {
            while (this.availableConnections.isEmpty()) {
                try {
                    this.availableConnections.wait(100L);
                } catch (InterruptedException e) {
                    this.closeConnectionPool();
                    throw new DatabaseException(e.getMessage());
                }
            }
            // Get newest Element from List
            do {
                connection = this.availableConnections.get(0);
                this.availableConnections.remove(connection);
            } while (!this.checkConnection(connection));
        }
        synchronized (this.blockedConnections) {
            this.blockedConnections.add(connection);
        }
        return connection;

    }

    /**
     * Destroys the Connections in the Connection Pool. Checks for released
     * Connections and waits for their Release.
     */
    private void closeConnectionPool() {
        synchronized (this.closed) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        Integer availConNumber = 0;
        synchronized (this.availableConnections) {
            Integer totalAvailConNumber = this.availableConnections.size();
            for (final Connection connection : this.availableConnections) {
                availConNumber++;
                try {
                    connection.close();
                    LOGGER.finest(MessageFormat
                            .format("Closed connection number {0} of "
                                            + "{1} available connections.",
                                    availConNumber, totalAvailConNumber));
                } catch (SQLException e) {
                    LOGGER.warning("An available connection could not be closed"
                            + ".");
                }
            }
        }
        Integer blockedConNumber = 0;
        synchronized (this.blockedConnections) {
            Integer totalBlockedConNumber = this.blockedConnections.size();
            for (final Connection connection : this.blockedConnections) {
                blockedConNumber++;
                try {
                    connection.close();
                    LOGGER.finest(MessageFormat
                            .format("Closed connection number {0} of "
                                            + "{1} blocked connections.",
                                    blockedConNumber, totalBlockedConNumber));
                } catch (SQLException e) {
                    LOGGER.warning("A blocked connection could not be closed.");
                }
            }
        }
        LOGGER.log(Level.INFO, MessageFormat
                .format("ConnectionPool closed {0} connections.",
                        availConNumber + blockedConNumber));
    }

    public boolean createDatabase() {


        Connection connection = this.getConnection();

        boolean userExists = true;

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TYPE userRole AS ENUM ('USER', "
                    + "'MANAGER', 'ADMINISTRATOR');";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            LOGGER.log(Level.FINER, null, ex);
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TYPE encryptionType AS ENUM ('UNENCRYPTED', "
                    + "'SSL', 'STARTTLS');";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            LOGGER.log(Level.FINER, null, ex);
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS users ("
                    + "userID SERIAL PRIMARY KEY, "
                    + "eMail varchar(50) NOT NULL UNIQUE, "
                    + "password char(96) NOT NULL, "
                    + "firstname varchar(30) NOT NULL, "
                    + "surname varchar(30) NOT NULL, "
                    + "address varchar(100),"
                    + "verificationhash varchar(100),"
                    + "accountstatus BOOLEAN)";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }


        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS groupo ("
                    + "groupID SERIAL PRIMARY KEY UNIQUE , "
                    + "name varchar(50) UNIQUE )";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }


        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS product ("
                    + "productID SERIAL PRIMARY KEY, "
                    + "name varchar(50)," +
                    "description varchar)";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS memberofGroup ("
                    + "uID smallint, "
                    + "gID smallint," +
                    "admin BOOLEAN," +
                    "accepted BOOLEAN)";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS productof ("
                    + "gID smallint, "
                    + "pID smallint," +
                    "count smallint," +
                    "ordered BOOLEAN)";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS notificationsread (" +
                    "nID smallint," +
                    "uID smallint," +
                    "read boolean)";
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "nID SERIAL PRIMARY KEY," +
                    "gID smallint," +
                    "time varchar," +
                    "message varchar)";


            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Statement stmt = connection.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS groupmessages ("
                    + "mID SERIAL PRIMARY KEY," +
                    "gID smallint," +
                    "uID smallint," +
                    "message varchar," +
                    "time timestamp)";


            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String query =
                "INSERT INTO users (email, password, firstname, surname, "
                        + " address,verificationhash,accountstatus"
                        + ") VALUES ('cord.ch@hotmail.de'," +
                        "'7ca05b2d3980ea40ce4b717aa7161ca738e96704" +
                        "be17e98b5507da6c046b673edf1f1349377dfbc35" +
                        "b2e513d3fc63fc6','Cord','Göken','Nagelschmiedgasse 2',NULL ,TRUE);";

        try {
            assert stmt != null;
            stmt.executeUpdate(query);
            stmt.close();
        } catch (SQLException e) {
        }

        String query2 =
                "INSERT INTO users (email, password, firstname, surname, "
                        + " address,verificationhash,accountstatus"
                        + ") VALUES ('cordchgk@gmail.com'," +
                        "'7ca05b2d3980ea40ce4b717aa7161ca738e96704" +
                        "be17e98b5507da6c046b673edf1f1349377dfbc35" +
                        "b2e513d3fc63fc6','Cord','Göken','Nagelschmiedgasse 2',NULL ,TRUE);";

        try {
            assert stmt != null;
            stmt.executeUpdate(query2);
            stmt.close();
        } catch (SQLException e) {
        }


        this.releaseConnection(connection);
        return userExists;

    }


}


