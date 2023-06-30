package com.github.cheldontk.freshfood.catalog.application.response;

import java.math.BigDecimal;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public BigDecimal costprice;
    public Boolean isPublished;
    public Integer stock;
    public String img;
    public CategoryResponse category;
    public String created_at;
    public String updated_at;
    public FarmResponse farm;
    public ProductResponse() {

    }
}
