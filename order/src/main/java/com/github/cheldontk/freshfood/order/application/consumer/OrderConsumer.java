package com.github.cheldontk.freshfood.order.application.consumer;

import com.github.cheldontk.freshfood.order.domain.service.DomainOrderService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class OrderConsumer {

    @Inject
    DomainOrderService orderService;

    @Incoming("order")
    @Counted(
            name = "counted by checkout"
    )
    @SimplyTimed(
            name = "Simple time to checkout"
    )
    @Timed(
            name = "Total time to checkout"
    )
    public void consumer(String jsonCheckout) {
        orderService.createOrder(jsonCheckout);
    }
}
