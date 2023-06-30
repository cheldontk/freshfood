package com.github.cheldontk.freshfood.order.domain.model;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.util.List;

@MongoEntity(collection = "orders", database = "freshfood")
public class Order extends PanacheMongoEntity {
    public Customer customer;
    public Delivery delivery;
    public Payment payment;
    public List<Product> products;
    public Farm farm;

}
