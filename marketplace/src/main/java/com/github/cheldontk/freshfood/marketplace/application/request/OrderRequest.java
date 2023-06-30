package com.github.cheldontk.freshfood.marketplace.application.request;


import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.model.Address;
import com.github.cheldontk.freshfood.marketplace.domain.model.Farm;

import java.util.List;

public class OrderRequest {
    public String customer_id;
    public String customer_name;
    public String customer_email;
    public List<CartProductResponse> products;
    public Address address;
    public Farm farm;
    public String payment_method;
    public String payment_status;
    public String payment_token;
}
