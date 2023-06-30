package com.github.cheldontk.freshfood.order.domain.service;

import com.github.cheldontk.freshfood.order.application.request.OrderRequest;
import com.github.cheldontk.freshfood.order.application.request.ProductRequest;
import com.github.cheldontk.freshfood.order.domain.model.Location;
import com.github.cheldontk.freshfood.order.domain.model.Order;
import com.github.cheldontk.freshfood.order.domain.model.Product;
import org.bson.types.Decimal128;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "cdi")
public interface OrderMapper {

    @Mapping(source = "customer_id", target = "customer.id")
    @Mapping(source = "customer_name", target = "customer.name")
    @Mapping(source = "customer_email", target = "customer.email")
    @Mapping(source = "payment_method", target = "payment.method")
    @Mapping(source = "payment_status", target = "payment.status")
    @Mapping(source = "payment_token", target = "payment.token")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "products", target = "products", qualifiedByName = "mapProducts")
    @Mapping(source = "address", target = "delivery.delivery_address")
    @Mapping(source = "farm.location.latitude", target = "delivery.delivery_address.location.latitude")
    @Mapping(source = "farm.location.longitude", target = "delivery.delivery_address.location.longitude")
    @Mapping(source = "farm.location", target = "delivery.current_location", qualifiedByName = "mapDelivery")
    Order toOrder(OrderRequest orderRequest);

    @BigDecimalToDecimal128
    public static Decimal128 convertBigDecimalToDecimal128(BigDecimal value) {
        return new Decimal128(value);
    }
    @Named("mapProducts")
    default List<Product> mapProducts(List<ProductRequest> productsRequest){
        List<Product> products = new ArrayList<>();
        for (ProductRequest pr : productsRequest) {
            Product p = new Product();
            p.id = pr.id;
            p.name = pr.product_name;
            p.description = pr.description;
            p.quantity = pr.quantity;
            p.price = new Decimal128(pr.price);
            products.add(p);
        }
        return products;
    }
    @Named("mapDelivery")
    default List<Location> mapDelivery(Location location){
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        return locations;
    }
}
