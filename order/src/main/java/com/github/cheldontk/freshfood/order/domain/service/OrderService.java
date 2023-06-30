package com.github.cheldontk.freshfood.order.domain.service;

import com.github.cheldontk.freshfood.order.application.request.DeliveryStatusRequest;
import com.github.cheldontk.freshfood.order.application.response.OrderResponse;
import com.github.cheldontk.freshfood.order.domain.model.Location;
import com.github.cheldontk.freshfood.order.domain.model.Order;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.PanacheQuery;

import java.util.List;

public interface OrderService {

    void createOrder(String jsonCheckout);

    void addLocation(String orderId, String deliveryId, Location location);

    void addOrderStatus(String orderId, String deliveryId, DeliveryStatusRequest delivery);

    Order getOrder(String orderId);

    List<Order> getOrdersByStatus(OrderStatus status);
}
