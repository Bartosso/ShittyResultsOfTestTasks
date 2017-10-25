package com.bartosso.dbService;


import com.bartosso.dbService.dao.ProductsDAO;
import com.bartosso.entity.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

//Because that is test task it's using only one db class, without dao factory etc.
public class DBService {

    private final Connection connection;

    public DBService() {
        this.connection = getPostgreSQLConnection();
    }

    private Connection getPostgreSQLConnection(){
        try {

            Class.forName("org.postgresql.Driver");

            String url = "jdbc:postgresql://localhost:5432/TestTask"; // Full url to database host, example "jdbc:postgresql://localhost:5432/TestTask"
            String username = "postgres";                             // Username for database
            String password = "test";                                 // Password for given username to database

            return DriverManager.getConnection(url,username,password);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error: PostgreSql driver is missed");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Something wrong with DB properties, check DBService class");
        }
        return null;
    }

    //Returning all products
    public List<Product> getAllProducts() throws DBException {

        List<Product> products;
        ProductsDAO dao = new ProductsDAO(connection);
        products = dao.getAll();
        return products;

    }

    //Returning chosen products as you see
    public List<Product> getProductsByParameters(Map<String, String> clearParameterMap) throws  DBException{

        List<Product> products;
        ProductsDAO dao = new ProductsDAO(connection);
        products = dao.getProductsByParameters(clearParameterMap);
        return products;

    }

}
