package com.bartosso;

import com.bartosso.dbService.DBException;
import com.bartosso.entity.Product;
import com.bartosso.dbService.DBService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet("/mainservlet/*")
public class MainServlet extends HttpServlet {

    private DBService dbService = new DBService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> parametersMap = req.getParameterMap();

        List<Product> products = new ArrayList<>();

        try {

        if (parametersMap.values().stream().allMatch(strings -> strings[0].equals(""))) {
            products = dbService.getAllProducts();
        } else
            {

            Map<String, String> clearParameterMap = new HashMap<>();
            parametersMap.forEach((s, strings) -> clearParameterMap.put(s,strings[0]));
            // Complete making our parameter map is clear
            clearParameterMap.values().removeIf(s -> s.equals(""));

            products = dbService.getProductsByParameters(clearParameterMap);

            }

        } catch (DBException e) {
            //if input value is invalid (such a wrong date format) we returning status code 400
            if (e.toString().contains("invalid value")) {
                resp.setStatus(400);
                System.out.println("Error: Wrong parameters input \n"+ e.toString());
            } else {
                e.printStackTrace();
            }
        }
        Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy").create();
        String json = gson.toJson(products);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
