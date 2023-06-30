package com.github.cheldontk.freshfood.order.application.request;

import com.github.cheldontk.freshfood.order.domain.service.OrderStatus;

public class DeliveryStatusRequest {

    public OrderStatus status;
    public String description;
}
