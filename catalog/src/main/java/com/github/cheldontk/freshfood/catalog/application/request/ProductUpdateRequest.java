package com.github.cheldontk.freshfood.catalog.application.request;


import java.math.BigDecimal;

public class ProductUpdateRequest {
    public String name;
    public String description;
    public BigDecimal price;
    public BigDecimal costprice;
    public String img;
    public Boolean isPublished;
    public Integer stock;
    public Long categoryId;
}
