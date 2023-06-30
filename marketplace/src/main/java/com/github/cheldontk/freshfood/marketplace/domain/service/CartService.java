package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.request.AddressRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.CartRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.PaymentRequest;
import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;

public interface CartService {

    Multi<CartProductResponse> findCartProductsByCustomerId(PgPool pgPool, String id);

    void persist(PgPool pgPool, String customer_id, String customer_email, String customer_name, CartRequest cartProduct);

    void remove(PgPool pgPool, String customer_id, Long cartId, Long id);

    void payment(PgPool pgPool, String id, Long cartId, PaymentRequest payment);

    void address(PgPool pgPool, String id, Long cartId, AddressRequest payment);

    void checkout(PgPool pgPool, String customerId, Long cartId);
}
