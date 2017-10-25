package services;


import services.dbService.DBException;
import services.dbService.DBService;
import util.LocalDateParamConverterProvider;

import javax.ejb.Stateless;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NoContentException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;

/**
 * Created by Eshu on 04.02.2017.
 */
@Stateless
@Path("/priceList")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PriceListWebServiceI implements PriceListWebService {

    //Создаем сервис запросов к БД
    private DBService dbService = new DBService();
    //Метод получения цены на заданный день
    @Override
    @Path("/getPrice")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public BigDecimal getPrice(@NotNull @QueryParam("productName") String productName, @NotNull @QueryParam("date") LocalDate date ) {
    //Создаем BigDecimal который вернем при успехе, а так же ProductPrice с которого мы возьмем цену в случае успеха
        BigDecimal price = null;
        ProductPrice product = null;
        // Так же создаем boolean который скажет нам есть ли продут в базе если он будет не доступен на запрошенное число
        boolean productInBase;
        //Запрашиваем у БД сервиса product
        try {product = dbService.productPriceGet(productName, date);} catch (DBException e) {e.printStackTrace();} catch (SQLException e) {e.printStackTrace();}
        //Если ответ оказался пустым то проверяем есть продукт в базе вообще
        if (product == null) {
            try {
                productInBase = dbService.checkProduct(productName);
                //Если продукт есть в базе то кидаем NoContent клиенту
                if (productInBase) {
                    try {throw new NoContentException("Not available");} catch (NoContentException e) {e.printStackTrace();}}
                //Если ответ есть кидаем NotFound клиенту
                else { throw new NotFoundException();  }} catch (DBException e) {e.printStackTrace();} catch (SQLException e) {e.printStackTrace();}}

         //Возвращаем цену в случае успеха
         else
        {price = product.getProductPrice();}return price;}

    //Метод получения цены на все дни
    @Override
    @Path("/getPrices")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArrayList<ProductPrice> getPrices(@NotNull @QueryParam("productName") String productName) {
        //Создаем ArrayList который вернем при успехе
        ArrayList productPrice = null;
        //Запрашиваем у БД список всех цен на продукт
        try {
            productPrice = dbService.getProductPrices(productName);
        } catch (DBException e) {e.printStackTrace();}
        //Если БД вернула нам ноль то отдаем клиенту Not Found
        if (productPrice==null) {throw new NotFoundException();}
        //Возвращаем список цен сортированный по дате действия цены в случае успеха
        return productPrice;
    }
    //Метод установки цены на заданный интервал
    @Override
    @Path("/setPrice")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setPrice(@NotNull @QueryParam("productName") String productName, @NotNull @QueryParam("price") @DecimalMin("0") BigDecimal price, @QueryParam("fromDate") LocalDate fromDate, @QueryParam("toDate") LocalDate toDate)  {
        //Проверяем входные данные, если fromDate либо toDate не пусты то проверяем не находится ли toDate раньше fromDate
        if (fromDate != null && toDate != null) {
          if (toDate.isBefore(fromDate)) {
              // Если результат нашей проверки печален то кидаем InternalServer клиенту
           throw new InternalServerErrorException();}}
        try {
            // В случае если данные пережили проверку отдаем их ДБ сервису
            dbService.setProductPrice(productName, price, fromDate, toDate);
        } catch (DBException e) {e.printStackTrace();}}
    //Метод остановки продажи на данный интервал
    @Override
    @Path("/stopSelling")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("*/*")
    public void stopSelling(@NotNull @QueryParam("productName") String productName, @QueryParam("fromDate") LocalDate fromDate, @QueryParam("toDate") LocalDate toDate)  {
        //Проверяем входные данные, если fromDate либо toDate не пусты то проверяем не находится ли toDate раньше fromDate
        if (fromDate != null && toDate != null)
       {
           // Если результат нашей проверки печален то кидаем InternalServer клиенту
           if (toDate.isBefore(fromDate)) {throw new InternalServerErrorException();}}
       else
           try {
            // Проверяем есть ли данный продукт у нас в базе, если нет то кидаем NotFound клиенту
            if (!dbService.checkProduct(productName)) {throw new NotFoundException();}
            else
                // Если данный продукт есть в базе то передаем его данные в ДБ сервис
            dbService.stopSel(productName, fromDate, toDate);
        } catch (DBException e) {throw new InternalServerErrorException();} catch (SQLException e) {throw new InternalServerErrorException();
        }

    }


    }

