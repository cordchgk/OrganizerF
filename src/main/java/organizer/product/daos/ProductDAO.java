package organizer.product.daos;

import organizer.group.daos.GroupDAO;
import organizer.group.dtos.GroupDTO;
import organizer.product.dtos.ProductDTO;
import organizer.system.ConnectionPool;

import organizer.system.exceptions.DatabaseException;
import organizer.system.exceptions.DuplicateException;
import organizer.user.Creator.NotificationCreator;
import organizer.user.beans.UserBean;
import organizer.user.daos.NotifcationDAO;
import organizer.user.daos.UserDAO;
import organizer.user.dtos.NotificationDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ProductDAO {


    private static ProductDAO instance = new ProductDAO();

    public static ProductDAO getInstance() {
        return instance;
    }





    public List<ProductDTO> selectByDTO(GroupDTO groupDTO) throws DatabaseException {
        ConnectionPool pool = ConnectionPool.getInstance();
        List<ProductDTO> productDTOS;
        productDTOS = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT productof.pid,productof.gid,product.name,product.description,productof.count," +
                "productof.ordered\n" +
                "FROM productof,product WHERE productof.gid = ? AND productid = pid";
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
            statement.setInt(1, groupDTO.getgID());
            result = statement.executeQuery();
            while (result.next()) {
                ProductDTO pDTO = new ProductDTO();
                pDTO.setpID(result.getInt(1));
                pDTO.setgID(result.getInt(2));
                pDTO.setName(result.getString(3));
                pDTO.setDescription(result.getString(4));
                pDTO.setCount(result.getInt(5));
                pDTO.setOrdered(result.getBoolean(6));

                productDTOS.add(pDTO);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return productDTOS;
    }





    public boolean update(ProductDTO dto)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "UPDATE postgres.public.productof SET count = ?,ordered = ? WHERE " +
                        "pid = ? AND gid = ? ";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, dto.getCount());
            statement.setBoolean(2, dto.getOrdered());
            statement.setInt(3, dto.getpID());
            statement.setInt(4, dto.getgID());
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


    public void insert(ProductDTO dto, UserBean userBean)
            throws DatabaseException, DuplicateException {

        ConnectionPool pool = ConnectionPool.getInstance();
        Connection conn = pool.getConnection();
        String query =
                "INSERT INTO postgres.public.product(name, description) VALUES (?,?)";

        try {
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, dto.getName());
            statement.setString(2, dto.getDescription());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        dto.setpID(this.getProductID(dto.getName()));
        String queryTWO =
                "INSERT INTO postgres.public.productof(gid, pid, count) VALUES (?,?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(queryTWO);
            statement.setInt(1, dto.getgID());
            statement.setInt(2, dto.getpID());
            statement.setInt(3, dto.getCount());
            statement.execute();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            NotificationDTO nDTO = new NotificationDTO();
            nDTO.setName(dto.getName());
            nDTO.setgID(dto.getgID());
            nDTO.setTime(formatter.format(date));
            nDTO.setMessage(new NotificationCreator().newProductNotification(nDTO));
            NotifcationDAO dao = new NotifcationDAO();
            dao.insertByDTO(nDTO);
            userBean.send(this.getUserIds(dto.getgID()));

        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
    }


    private int getProductID(String name) {
        ConnectionPool pool = ConnectionPool.getInstance();
        int toReturn = 0;

        Connection conn = pool.getConnection();
        String query = "SELECT productid FROM postgres.public.product WHERE name = ?";
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
            statement.setString(1, name);
            result = statement.executeQuery();
            while (result.next()) {
                toReturn = result.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return toReturn;
    }


    public void deleteByDTO(ProductDTO dto) {
        ConnectionPool pool = ConnectionPool.getInstance();
        int toReturn = 0;

        Connection conn = pool.getConnection();
        String query = "DELETE FROM postgres.public.productof WHERE postgres.public.productof.gid = ? AND postgres.public.productof.pid = ?";
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
            statement.setInt(2, dto.getpID());
            statement.execute();

        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);

    }

    private List<Integer> getUserIds(int gID) {
        ConnectionPool pool = ConnectionPool.getInstance();
        ArrayList<Integer> users = new ArrayList<>();

        Connection conn = pool.getConnection();
        String query = "SELECT memberofgroup.uid FROM postgres.public.memberofgroup WHERE memberofgroup.gid = ?";
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
            statement.setInt(1, gID);
            result = statement.executeQuery();
            while (result.next()) {
                users.add(result.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroupDAO.class.getName())
                    .log(Level.SEVERE, null, ex);
            pool.releaseConnection(conn);
        }
        pool.releaseConnection(conn);
        return users;
    }


}
