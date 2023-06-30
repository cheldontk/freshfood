package com.github.cheldontk.freshfood.marketplace.domain.model;

import java.math.BigDecimal;

public class Product {

    public Long id;
    public String name;
    public String description;
    public String img;
    public BigDecimal price;
    public Integer stock;
    public Category category;
    public Farm farm;

}
