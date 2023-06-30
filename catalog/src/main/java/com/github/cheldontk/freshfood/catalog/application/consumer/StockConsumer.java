package com.github.cheldontk.freshfood.catalog.application.consumer;

import com.github.cheldontk.freshfood.catalog.domain.service.DomainProductService;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class StockConsumer {

    @Inject
    DomainProductService productservice;

    @Incoming("stock")
    @Blocking
    @Transactional
    public void consumer(String productStock) {
        productservice.updateStock(productStock);
    }
}
