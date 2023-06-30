package com.github.cheldontk.freshfood.marketplace.application.request;

import com.github.cheldontk.freshfood.marketplace.domain.service.PaymentMethod;


public class PaymentRequest {
    public PaymentMethod method;
    public String status;
    public String token;
}
