package services; /**
 * Created by Eshu on 04.02.2017.
 */
import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * Created by Eshu on 03.02.2017.
 */
//Класс нашего продукта который мы будем получать из БД
public class ProductPrice {
    private final String productName;
    private final BigDecimal productPrice;
    private final LocalDate validFrom;
    private final LocalDate validTo;
    public ProductPrice(String productName, BigDecimal productPrice, LocalDate validFrom, LocalDate validTo) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }
    public String getProductName() { return productName; }
    public BigDecimal getProductPrice() { return productPrice; }
    public LocalDate getValidFrom() {return validFrom; }
    public LocalDate getValidTo() {return validTo; }


}