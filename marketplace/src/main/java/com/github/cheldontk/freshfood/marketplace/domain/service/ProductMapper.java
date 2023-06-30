package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.response.FarmProductsResponse;
import com.github.cheldontk.freshfood.marketplace.application.response.FarmResponse;
import com.github.cheldontk.freshfood.marketplace.application.response.ProductResponse;

import io.vertx.mutiny.sqlclient.Row;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMapper {

    public static ProductResponse from(Row row) {
        ProductResponse dto = new ProductResponse();
        dto.id = row.getLong("id");
        dto.name = row.getString("name");
        dto.description = row.getString("description");
        dto.img = row.getString("img");
        dto.price = row.getBigDecimal("price");
        dto.stock = row.getInteger("stock");
        dto.category_id = row.getLong("category_id");
        dto.category_name = row.getString("category_name");
        dto.farm = new FarmResponse();
        dto.farm.id = row.getLong("farm_id");
        dto.farm.name = row.getString("farm_name");
        dto.farm.latitude = row.getDouble("latitude");
        dto.farm.longitude = row.getDouble("longitude");
        return dto;
    }

    public static FarmProductsResponse fromFarm(Row row){
        FarmProductsResponse dto = new FarmProductsResponse();
        dto.id = row.getLong("id");
        dto.name = row.getString("name");
        dto.location_id = row.getLong("location_id");
        dto.latitude = row.getDouble("latitude");
        dto.longitude = row.getDouble("longitude");
        dto.product = new ProductResponse();
        dto.product.id = row.getLong("product_id");
        dto.product.name = row.getString("product_name");
        dto.product.img = row.getString("product_image");
        dto.product.description = row.getString("product_description");
        dto.product.stock = row.getInteger("product_stock");
        dto.product.price = row.getBigDecimal("product_price");
        dto.product.category_id = row.getLong("category_id");
        dto.product.category_name = row.getString("category_name");

        return dto;
    }

}
