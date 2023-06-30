package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CartProductMapper {

    public static CartProductResponse from(Row row) {
        CartProductResponse dto = new CartProductResponse();
        dto.cart_id = row.getLong("id");
        dto.customer_id = row.getString("customer_id");
        dto.customer_email = row.getString("customer_email");
        dto.customer_name = row.getString("customer_name");
        if(row.getLong("item_id") != null){
            dto.id = row.getLong("item_id");
            dto.product_id = row.getLong("product_id");
            dto.product_name = row.getString("product_name");
            dto.quantity = row.getInteger("quantity");
            dto.price = row.getBigDecimal("price");
            dto.stock = row.getInteger("stock");
        }
        if (row.getString("payment_method") != null) {
            dto.payment_method = row.getString("payment_method");
            dto.payment_status = row.getString("payment_status");
            dto.payment_token = row.getString("payment_token");
        }
        if(row.getLong("farm_id") != null){
            dto.farm_id = row.getLong("farm_id");
            dto.farm_name = row.getString("farm_name");
            dto.farm_latitude = row.getDouble("farm_latitude");
            dto.farm_longitude = row.getDouble("farm_longitude");
        }
        if(row.getDouble("ca_longitude") != null){
            dto.ca_city = row.getString("ca_city");
            dto.ca_district = row.getString("ca_district");
            dto.ca_street = row.getString("ca_street");
            dto.ca_number = row.getString("ca_number");
            dto.ca_description = row.getString("ca_description");
            dto.ca_latitude = row.getDouble("ca_latitude");
            dto.ca_longitude = row.getDouble("ca_longitude");
        }
        return dto;
    }
}
