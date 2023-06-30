package com.github.cheldontk.freshfood.marketplace.domain.model;

import com.github.cheldontk.freshfood.marketplace.domain.service.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public class Cart {

    public Long id;
    public String customerUUID;
    public String customerEmail;
    public String customerName;
    public PaymentMethod paymentMethod;
    public String paymentStatus;
    public String paymentToken;
    public List<CartProduct> cartProducts;
}
