package com.github.cheldontk.freshfood.marketplace.application.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.github.cheldontk.freshfood.marketplace.domain.service.DomainProductService;

import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductConsumer {

    @Inject
    PgPool pgPool;

    @Inject
    DomainProductService productservice;

    @Incoming("products")
    @Blocking
    public void consumer(String productString) {
        productservice.persist(pgPool, productString);
    }

}
