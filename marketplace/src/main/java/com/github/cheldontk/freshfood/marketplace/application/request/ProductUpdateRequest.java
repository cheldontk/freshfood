package com.github.cheldontk.freshfood.marketplace.application.request;


import com.github.cheldontk.freshfood.marketplace.domain.model.Category;
import com.github.cheldontk.freshfood.marketplace.domain.model.Farm;

import java.math.BigDecimal;

public class ProductUpdateRequest {
    public Long id;
    public String name;
    public String description;
    public String img;
    public BigDecimal price;
    public Integer stock;
    public Category category;
    public Farm farm;
}
