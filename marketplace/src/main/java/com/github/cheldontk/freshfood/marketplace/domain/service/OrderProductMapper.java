package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.request.OrderRequest;
import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.model.Address;
import com.github.cheldontk.freshfood.marketplace.domain.model.Farm;
import com.github.cheldontk.freshfood.marketplace.domain.model.Location;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class OrderProductMapper {

    public static OrderRequest from(Map.Entry<Long, List<CartProductResponse>> entry) {
        OrderRequest order = new OrderRequest();
        order.farm = new Farm();
        order.address = new Address();
        order.address.location = new Location();
        order.farm.location = new Location();
        order.products = new ArrayList<>();
        entry.getValue().forEach(c -> {
            order.customer_id = c.customer_id;
            order.customer_name = c.customer_name;
            order.customer_email = c.customer_email;
            order.address.postal_code = c.ca_postal_code;
            order.address.city = c.ca_city;
            order.address.district = c.ca_district;
            order.address.street = c.ca_street;
            order.address.number = c.ca_number;
            order.address.description = c.ca_description;
            order.address.location.latitude = c.ca_latitude;
            order.address.location.longitude = c.ca_longitude;
            order.farm.id = c.farm_id;
            order.farm.name = c.farm_name;
            order.farm.location.latitude = c.farm_latitude;
            order.farm.location.longitude = c.farm_longitude;
            order.payment_method = c.payment_method;
            order.payment_status = c.payment_status;
            order.payment_token = c.payment_token;
            CartProductResponse product = new CartProductResponse();
            product.id = c.product_id;
            product.product_name = c.product_name;
            product.price = c.price;
            product.quantity = c.quantity;
            order.products.add(product);
        });
        return order;
    }
}
