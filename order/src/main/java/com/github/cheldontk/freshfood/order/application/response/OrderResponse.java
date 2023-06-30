package com.github.cheldontk.freshfood.order.application.response;

import com.github.cheldontk.freshfood.order.domain.model.*;

import java.util.List;

public class OrderResponse {
    public Customer customer;
    public Delivery delivery;
    public Payment payment;
    public List<Product> products;
    public Farm farm;
}
