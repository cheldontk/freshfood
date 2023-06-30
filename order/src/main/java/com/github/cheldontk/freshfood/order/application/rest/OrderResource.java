package com.github.cheldontk.freshfood.order.application.rest;

import com.github.cheldontk.freshfood.order.application.request.DeliveryStatusRequest;
import com.github.cheldontk.freshfood.order.domain.model.Location;
import com.github.cheldontk.freshfood.order.domain.model.Order;
import com.github.cheldontk.freshfood.order.domain.service.DomainOrderService;
import com.github.cheldontk.freshfood.order.domain.service.OrderStatus;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

import java.util.List;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("delivery")
@SecurityScheme(securitySchemeName = "freshfood-oauth", type = SecuritySchemeType.OAUTH2, flows = @OAuthFlows(password = @OAuthFlow(tokenUrl = "http://localhost:8180/auth/realms/freshfood/protocol/openid-connect/token")))
@ApplicationScoped
public class OrderResource {

    @Inject
    DomainOrderService orderService;

    @GET
    @Path("/status/{status}")
    public List<Order> getOrdersByStatus(@PathParam("status") OrderStatus status){
        return orderService.getOrdersByStatus(status);
    }


    @GET
    @Path("/{id}")
    public Order getOrder(@PathParam("id") String id){
        return orderService.getOrder(id);
    }

    @POST
    @Path("/{id}/delivery/{delivery_id}/location")
    public void addLocation(@PathParam("id") String orderId, @PathParam("delivery_id") String deliveryId, Location location){
        orderService.addLocation(orderId, deliveryId, location);
    }

    @PUT
    @Path("/{id}/delivery/{delivery_id}/status")
    public void addOrderStatus(@PathParam("id") String orderId, @PathParam("delivery_id") String deliveryId, DeliveryStatusRequest delivery){
        orderService.addOrderStatus(orderId, deliveryId, delivery);
    }
}


