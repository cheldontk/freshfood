package com.github.cheldontk.freshfood.marketplace.application.response;

import java.math.BigDecimal;

public class ProductResponse {
    public Long id;
    public String name;
    public String description;
    public String img;
    public BigDecimal price;
    public Integer stock;
    public Long category_id;
    public String category_name;
    public FarmResponse farm;

    @Override
    public String toString() {
        return "id=" + id + ", name=" + name + ", farm_id=" + farm.id + ", location_id=" + farm.location_id;
    }
}
