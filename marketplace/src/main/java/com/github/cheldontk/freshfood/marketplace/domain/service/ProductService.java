package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.response.FarmProductsResponse;
import com.github.cheldontk.freshfood.marketplace.application.response.ProductResponse;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;

import java.util.Map;
import java.util.Set;

public interface ProductService {
    Multi<ProductResponse> findAll(PgPool pgPool);
    Multi<FarmProductsResponse> findProductsByFarmId(PgPool pgPool, Long id);

    Multi<FarmProductsResponse> getProductsByCategory(PgPool pgPool, String categoryName);

    Multi<ProductResponse> findProductsById(PgPool pgPool, Set<Long> ids);

    void persist(PgPool pgPool, String productString);

    Map<Long, Integer> updateStock(PgPool pgPool, Map<Long, Integer> products);
}
