package services.dbService.DAO;


import services.dbService.executor.Executor;

import java.lang.management.LockInfo;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import services.ProductPrice;
/**
 * Created by Eshu on 04.02.2017.
 */
public class ProductsDAO {

        private Executor executor;

        public ProductsDAO(Connection connection){this.executor = new Executor(connection);}


        //Запрашиваем у БД прайс на заданный продукт на заданное число
        public ProductPrice getPrice(String productName, LocalDate priceDate) throws SQLException {
            return executor.execQuery("select * from ProductsPrices where productName = '" + productName + "' and '" + priceDate + "' between validfrom and validto", result ->
           //Если результата дальше нет то вовзращаем ноль,в другом случае возвращаем новый ProductPrice
                    //с получеными значениями
            {if (!result.next()) {return null;}
            return new ProductPrice(result.getString(1), result.getBigDecimal(2),result.getDate(3).toLocalDate(), result.getDate(4).toLocalDate());
            })
        ;}
        //Получаем больший интервал заданного т.е. проверяем попадает ли вводимый интервал в больший интервал
        public ProductPrice getBiggerDate(String productName, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
            return executor.execQuery("select * from ProductsPrices where productName = '"+productName+"' and '"+dateFrom+"' between validFrom and validTo and '"+dateTo+"' between validFrom and validTo", result -> {
               //Если результат по запросу есть то возвращаем новый ProductPrice со значениями из таблицы
                if (result.next()) {
                    return new ProductPrice(result.getString(1), result.getBigDecimal(2), result.getDate(3).toLocalDate() , result.getDate(4).toLocalDate());
                }
                //Если нет то возвращаем ноль
                else {return null;}
            });
        }
        //Меняем значение большему интервалу в который попадает вводимый интервал
        public void changeBiggerDate(ProductPrice product, LocalDate validFromNew) throws SQLException {
        executor.execUpdate("update ProductsPrices set validTo = '"+validFromNew.minusDays(1)+"' where productName = '"+product.getProductName()+"' and validFrom = '"+product.getValidFrom()+"' and validTo = '"+product.getValidTo()+"'  ");}
        //Вводим больший интервал в ДБ со датой начала заданной нами плюс один день
        public void InsertBiggerDate(ProductPrice product, LocalDate validToNew) throws SQLException {
            executor.execUpdate("insert into ProductsPrices values ('"+product.getProductName()+"', "+product.getProductPrice()+", '"+validToNew.plusDays(1)+"', '"+product.getValidTo()+"') ");
        }
        //Создаем таблицу если она еще не была создана
        public void createTable() throws SQLException {executor.execUpdate("create table if not exists productsPrices (productName varchar(256), productPrice decimal, validFrom date, validTo date  )");}
        //Вводим указанные данные продукта в таблицу
        public void setPrice(String productName, BigDecimal price, LocalDate fromDate, LocalDate toDate) throws SQLException {
            executor.execUpdate(" insert into ProductsPrices values ('"+productName+"', "+price+", '"+fromDate+"', '"+toDate+"') ");}
        //Удаляем таблицу
        public void dropTable() throws SQLException { executor.execUpdate("drop table ProductsPrices");}
        //Удаляем цену в указанный нами срок на указанный продукт
        public void deletePrice(String productName,LocalDate dateFrom, LocalDate dateTo) throws SQLException {
            executor.execUpdate("delete from ProductsPrices where ProductName = '"+productName+"' and " +
                " validfrom >= '"+dateFrom+"' and validto <= '"+dateTo+"'");}
        //Переносим дату интервала который заканчивается в вводимый нами на начало нового интервала минус один день на указанный продукт
        public void changePrev(String productName, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
            executor.execUpdate("update ProductsPrices set validTo = '"+dateFrom.minusDays(1)+"' where productName = '"+productName+"' and validTo between '"+dateFrom+"' and '"+dateTo+"'");
        }
        //Переновим дату интервала который начинается в вводимый нами на конец нашего интервала плюс один день
        public void changeNext(String productName, LocalDate dateFrom, LocalDate dateTo) throws SQLException {
            executor.execUpdate("update ProductsPrices set validFrom = '"+dateTo.plusDays(1)+"' where productName = '"+productName+"' and validFrom between '"+dateFrom+"' and '"+dateTo+"'");
        }
        //Получаем все ценны на вводимый продукт сортироваными по дате действия цены
        public ArrayList getProductPrices(String productName) throws SQLException {
            //Создаем ArrayList который вернем при успехе
            ArrayList<ProductPrice> arrayList = new ArrayList<>();
            return executor.execQuery("SELECT * FROM productsprices WHERE productname = '"+productName+"'", result ->{
                //Если результата нет то возвращаем ноль
                if (!result.isBeforeFirst()) {return null;}
                else
                    //Если результат есть то создаем новый ProductPrice, и добавляем его в ArrayList пока не прочитаем
                //все полученные данные
                while(result.next()) {
                ProductPrice productPrice = new ProductPrice(result.getString(1), result.getBigDecimal(2), result.getDate(3).toLocalDate(), result.getDate(4).toLocalDate());
                arrayList.add(productPrice);}
                //Сортируем по дате после того как входных данных больше нет
                Comparator<ProductPrice> dateComparator = (Comparator.comparing(ProductPrice::getValidTo));
                arrayList.sort(dateComparator);
                //Возвращаем ArrayList
                return arrayList;});}
        //Проверяем есть ли данный продукт у нас в БД, если по запросу есть результат то возвращаем TRUE, иначе возвращаем FALSE
        public boolean checkProduct(String productName) throws SQLException {
           return executor.execQuery("select * from ProductsPrices where ProductName = '"+productName+"'", result-> {
       if (result.next())
       { return true;}
       return false;});



}}




