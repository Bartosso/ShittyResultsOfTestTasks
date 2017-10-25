package com.bartosso.dbService.dao;

import com.bartosso.dbService.DBException;
import com.bartosso.dbService.executor.Executor;
import com.bartosso.entity.Product;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;

public class ProductsDAO {

    private Executor executor;

    public ProductsDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    //Method will return all products from database
    public List<Product> getAll(){
        try {
            return executor.execQuery("select * from products", result -> {

                ArrayList<Product> products = new ArrayList<>();
                if (!result.isBeforeFirst()) return null;
                else

                while (result.next()) {
                    products.add(new Product(
                            result.getString("part_name"),
                            result.getString("part_number"),
                            result.getString("vendor"),
                            result.getInt("qty"),
                            Date.from(result.getDate("shipped").toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            Date.from(result.getDate("receive").toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                    );
                }
                return products;});
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: something wrong with ProductsDAO class");
        }
        return null;
    }

    //Method will return all products which meet the criteria from parameters
    public List<Product> getProductsByParameters(Map<String,String> parameters) throws DBException {
        StringBuilder query = new StringBuilder("select * from products where ");

        boolean started = false;
        for (String s : parameters.keySet()) {
            if (started) {
                query.append(" and ");
            }
            switch (s) {
                case "PN":
                    query.append("part_number like '").append(parameters.get(s)).append("'");
                    break;
                case "PartName":
                    query.append("part_name like '").append(parameters.get(s)).append("'");
                    break;
                case "Vendor":
                    query.append("vendor like '").append(parameters.get(s)).append("'");
                    break;
                case "Qty":
                    query.append("qty >= ").append(parameters.get(s));
                    break;
                case "ShippedAfter":
                    query.append("shipped > to_date( '").append(parameters.get(s)).append("','MON dd, yyyy')");
                    break;
                case "ShippedBefore":
                    query.append("shipped < to_date( '").append(parameters.get(s)).append("','MON dd, yyyy')");
                    break;
                case "ReceivedAfter":
                    query.append("receive > to_date( '").append(parameters.get(s)).append("','MON dd, yyyy')");
                    break;
                case "ReceivedBefore":
                    query.append("receive < to_date( '").append(parameters.get(s)).append("','MON dd, yyyy')");
                    break;
            }
            started = true;
        }

        try {
            return executor.execQuery(query.toString(), result -> {
                ArrayList<Product> products = new ArrayList<>();
                if (!result.isBeforeFirst()) return null;
                else
                    while (result.next()) {
                        products.add(new Product(
                                result.getString("part_name"),
                                result.getString("part_number"),
                                result.getString("vendor"),
                                result.getInt("qty"),
                                Date.from(result.getDate("shipped").toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                Date.from(result.getDate("receive").toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        );
                    }
                return products;});
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

}
