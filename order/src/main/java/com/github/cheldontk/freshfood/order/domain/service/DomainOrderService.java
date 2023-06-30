package com.github.cheldontk.freshfood.order.domain.service;

import com.github.cheldontk.freshfood.order.application.request.DeliveryStatusRequest;
import com.github.cheldontk.freshfood.order.application.request.OrderRequest;
import com.github.cheldontk.freshfood.order.domain.model.Location;
import com.github.cheldontk.freshfood.order.domain.model.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.opentracing.Traced;

import java.util.List;


@ApplicationScoped
@Traced
public class DomainOrderService implements OrderService{

    @Inject
    OrderMapper orderMapper;

    @Override
    public void createOrder(String jsonCheckout) {
        try{
            System.out.println("checkout: " + jsonCheckout);
            System.out.println("------------\n");
            Jsonb create = JsonbBuilder.create();
            OrderRequest orderRequest = create.fromJson(jsonCheckout, OrderRequest.class);
            System.out.println("orderRequest: " + orderRequest.customer_name);
            System.out.println("------------\n");
            Order order = orderMapper.toOrder(orderRequest);
            order.delivery.status = OrderStatus.NEW.toString();
            order.persist();
        }catch (Exception e){
            System.out.println("mongoPersist -> " + e.getMessage());
        }
    }

    @Override
    public void addLocation(String orderId, String deliveryId, Location location) {
        Order order = Order.findById(new ObjectId(orderId));
        order.delivery.id = deliveryId;
        order.delivery.current_location.add(location);
        order.persistOrUpdate();
    }

    @Override
    public void addOrderStatus(String orderId, String deliveryId, DeliveryStatusRequest delivery) {
        Order order = Order.findById(new ObjectId(orderId));
        order.delivery.id = deliveryId;
        order.delivery.status = delivery.status.toString();
        order.delivery.description = delivery.description;
        order.persistOrUpdate();
    }

    @Override
    public Order getOrder(String orderId) {
        return Order.findById(new ObjectId(orderId));
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return Order.list("delivery.status", status.toString());
    }
}

