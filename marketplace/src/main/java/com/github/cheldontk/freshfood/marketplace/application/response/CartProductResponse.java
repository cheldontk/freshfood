package com.github.cheldontk.freshfood.marketplace.application.response;

import java.math.BigDecimal;

public class CartProductResponse {
    public Long id;
    public Long cart_id;
    public String customer_id;
    public String customer_email;
    public String customer_name;
    public Long product_id;
    public String product_name;
    public Integer stock;
    public Integer quantity;
    public BigDecimal price;
    public String payment_method;
    public String payment_status;
    public String payment_token;
    public Long farm_id;
    public String farm_name;
    public Double farm_latitude;
    public Double farm_longitude;
    public String ca_postal_code;
    public String ca_city;
    public String ca_district;
    public String ca_street;
    public String ca_number;
    public String ca_description;
    public Double ca_latitude;
    public Double ca_longitude;

}
