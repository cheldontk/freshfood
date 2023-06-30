package com.github.cheldontk.freshfood.order.domain.model;

import jakarta.persistence.ManyToOne;
import org.bson.types.Decimal128;

public class Product {

    public Long id;
    public String name;
    public String description;
    public Integer quantity;
    public Decimal128 price;

}
