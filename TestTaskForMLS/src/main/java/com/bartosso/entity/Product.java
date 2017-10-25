package com.bartosso.entity;



import java.util.Date;

//Main entity for project, working like DTO
@SuppressWarnings("FieldCanBeLocal")
public class Product {
    private String  partName;
    private String  partNumber;
    private String  vendor;
    private Integer qty;
    private Date    shipped;
    private Date    receive;

    public Product(String partName, String partNumber, String vendor, Integer qty, Date shipped, Date receive) {
        this.partName = partName;
        this.partNumber = partNumber;
        this.vendor = vendor;
        this.qty = qty;
        this.shipped = shipped;
        this.receive = receive;
    }




}
