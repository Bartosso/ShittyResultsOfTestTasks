package services.dbService;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;
import services.ProductPrice;
import services.dbService.DAO.ProductsDAO;

/**
 * Created by Eshu on 04.02.2017.
 */
public class DBService {
    private final Connection connection;
//Создаем соеденение к базе при вызове DBService
    public DBService() {
        this.connection = getH2Connection();
    }

    public void setProductPrice(String productPrice, BigDecimal price, LocalDate validFrom, LocalDate validTo) throws DBException {
       //Проверяем входные данные, если левая(правая) граница интервала пуста то делаем ее равной плюс (минус) бесконечности
        //P.S. значения выбраны как максимальные и минимальные для базы MySQL
        if (validFrom == null) {validFrom = LocalDate.of(1000,1,1);}
       if (validTo == null) {validTo = LocalDate.of(9999,12,31);}
        try {
            //Выключаем автокомит
            connection.setAutoCommit(false);
            //Передаем в DAO наше соединение
            ProductsDAO dao = new ProductsDAO(connection);
            //Создаем таблицу если она еще не была создана
            dao.createTable();
            //Удаляем цену на продукт если он попадает в наш промежуток включая границы
            dao.deletePrice(productPrice,validFrom,validTo);
            //Проверяем является ли наш интервал бесконечным в обе стороны
            if (validTo.toString().equals("9999-12-31")|| validFrom.toString().equals("1000-01-01")){
             //Если является то просто устанавливаем новую цену в заданном интервале
                dao.setPrice(productPrice,price, validFrom, validTo);
            }
            else {
                //В противном случае проверяем попадает ли наш интервал в границы большего интервала
                //пытась получить ProductPrice в чей интервал наш новый интервал попадает включая границы
                ProductPrice tempProduct = dao.getBiggerDate(productPrice,validFrom,validTo);
                if (tempProduct != null){
                    //Если наш новый интервал таки попал в границы большего интервала то
                    //Смещаем его границу на день минус начала нашего нового интервала
                    dao.changeBiggerDate(tempProduct, validFrom);
                    //Вставляем ранее полученный ProductPrice смещая значение validFrom  на значение
                    //окончания нашего нового интервала плюс день
                    dao.InsertBiggerDate(tempProduct,validTo);
                   //Устанавливаем новую цену в задананном интервале
                    dao.setPrice(productPrice, price, validFrom, validTo);
                }
                else {
                    //Если не попадает то просто смещаем границы интервала который заканчивается в новом интервале на день назад от нового
                    //и границы интервала который начинается в нашем интервале плюс день от нового интервала
                    dao.changeNext(productPrice, validFrom, validTo);
                dao.changePrev(productPrice, validFrom, validTo);
                //И устанавдиваем новую цену в заданном интервале
                dao.setPrice(productPrice, price, validFrom, validTo);

            }}
            //Если все прошло успешно то комитимся
            connection.commit();
            } catch (SQLException e) {
            try {
                //В случае ошибки пытаемся откатится
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                //В любом случае возвращаем автокомиту true
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
}

    public ArrayList getProductPrices(String productPrice) throws DBException {
        //Создаем ArrayList который вернем
        ArrayList<ProductPrice> productPrices;
        try {
            //Отключаем автокоммит
            connection.setAutoCommit(false);
            ProductsDAO dao = new ProductsDAO(connection);
            //Передаем соединение в DAO
            //Заполняем ArrayList значениями из базы данных
          productPrices = dao.getProductPrices(productPrice);
          //Коммитимся
            connection.commit();
        } catch (SQLException e) {
            try {
                //Откатываемся в случае ошибки от ДБ
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                //В любом случае включаем автокоммит
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {}
        }
        //Возвращаем ArrayList
   return productPrices; }

    public ProductPrice productPriceGet(String productName, LocalDate priceDate) throws DBException, SQLException {
        //Создаем продукт который вернем
       ProductPrice productPrice;
       try {
           // Передаем содинение в DAO
          ProductsDAO dao = new ProductsDAO(connection);
          //Заполняем продукт полученными данными
           productPrice = dao.getPrice(productName, priceDate);
       } catch (SQLException e) {
           throw new DBException(e);
       }
       //Возвращаем продукт
       return productPrice; }

        public boolean checkProduct(String productName) throws DBException, SQLException {
        //Создаем boolean который вернем
        boolean check;
        //Передаем в DAO наше соединение
            ProductsDAO dao = new ProductsDAO(connection);
            // Заполняем boolean check полученным boolean
            check = dao.checkProduct(productName);
            //Возвращаем boolean
            return  check;}

        public void stopSel(String productName, LocalDate dateFrom, LocalDate dateTo) throws DBException {
        try {
            //Проверяем входные данные, если левая(правая) граница интервала пуста то делаем ее равной плюс (минус) бесконечности
            //P.S. значения выбраны как максимальные и минимальные для базы MySQL
            if (dateFrom == null) {dateFrom = LocalDate.of(1000,1,1);}
            if (dateTo == null) {dateTo = LocalDate.of(9999,12,31);}
            //Отключаем автокоммит
            connection.setAutoCommit(false);
            //Передаем в DAO наше соединение
            ProductsDAO dao = new ProductsDAO(connection);
            //Удаляем цены которые попадают в рамки нашег интервала
            dao.deletePrice(productName, dateFrom, dateTo);
            //Проверяем попадает ли наш продукт в больший интервал
            //Получая ProductPrice по данным критериям
          ProductPrice tempProduct = dao.getBiggerDate(productName,dateFrom,dateTo);
            if (tempProduct != null){
                //Если попадает то смещаем границу этого интервала на начало заданного нами интервала минус один день
                dao.changeBiggerDate(tempProduct, dateFrom);
                //Вставляем ранее полученный productPrice смещая начало интервала на заданный нами конец интервала плюс один день
                dao.InsertBiggerDate(tempProduct, dateTo);

            }
            else
                //Если заданный интервал не попадает в больший интервал
                // то смещаем границы интервала который начинается в нашем интервала на конец нашего интервала плюс день
                dao.changeNext(productName, dateFrom, dateTo);
            //Так же смещаем конец интервала если он попадает в наш интервал на начало нашего интервала минус один день
            dao.changePrev(productName, dateFrom, dateTo);
            //Коммитимся
            connection.commit();
        } catch (SQLException e) {
            try {
                //В случае ошибки откатываемся
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw  new DBException(e);
        } finally {
            try {
                //В любом случае делаем автокоммит true
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {}
        }
        }

        //Метод для удаления таблицы, на всякий случай
    public void cleanUp() throws SQLException, DBException {
        try {
            connection.setAutoCommit(false);
        ProductsDAO dao = new ProductsDAO(connection);
        dao.dropTable();
        connection.commit();
    } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignoe) {}
            throw new DBException(e);
            }finally {
            try { connection.setAutoCommit(true);
        }catch (SQLException ignore) {}
        }}


//Настраиваем наше соединение с БД
    public static Connection getH2Connection() {
        try {
            String url = "jdbc:h2:./h2db";
            String name = "test";
            String pass = "test";

            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(name);
            ds.setPassword(pass);

            Connection connection = DriverManager.getConnection(url, name, pass);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
