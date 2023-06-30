package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.request.AddressRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.CartRequest;
import com.github.cheldontk.freshfood.marketplace.application.request.PaymentRequest;
import com.github.cheldontk.freshfood.marketplace.application.response.CartProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.DomainException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.BadRequestException;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import static io.vertx.mutiny.sqlclient.Tuple.of;
import static io.vertx.mutiny.sqlclient.Tuple.wrap;

@ApplicationScoped
@Traced
public class DomainCartService implements CartService {

    @Inject
    @Channel("stock")
    Emitter<String> emitter;

    @Inject
    @Channel("order")
    Emitter<String> emitterOrder;

    @Inject
    DomainProductService productService;

    @Override
    public Multi<CartProductResponse> findCartProductsByCustomerId(PgPool pgPool, String id) {
        Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery("SELECT c.id, c.customer_id, c.customer_email, c.customer_name, c.payment_method, c.payment_status, c.payment_token, cp.id as item_id, cp.product_id, cp.quantity, cp.price, p.stock, p.name as product_name, f.id as farm_id, f.name as farm_name, l.latitude  as farm_latitude, l.longitude as farm_longitude, ca.postal_code as ca_postal_code, ca.city ca_city, ca.district as ca_district, ca.street as ca_street, ca.number as ca_number, ca.description as ca_description, ca.latitude as ca_latitude, ca.longitude as ca_longitude FROM cart c left join cart_product cp on cp.cart_id = c.id left join product p on cp.product_id = p.id left join farm f on cp.farm_id = f.id left join location l on f.location_id = l.id left join cart_address ca on c.id = ca.cart_id WHERE c.customer_id = $1").execute(of(id));

        return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
            return StreamSupport.stream(rowSet.spliterator(), false);
        })).onItem().transform(CartProductMapper::from);
    }

    @Override
    public void persist(PgPool pgPool, String customer_id, String customer_email, String customer_name, CartRequest cartProduct) {

        Set<Long> cartId = new HashSet<>();
        Set<Long> productIds = new HashSet<>();

        findCartProductsByCustomerId(pgPool, customer_id).subscribe().asStream().forEach(c -> {
            if(c.product_id != null){
                cartId.add(c.id);
                productIds.add(c.product_id);
            }
        });

        if (cartId.isEmpty()) {
            pgPool.preparedQuery("insert into cart(customer_id, customer_email, customer_name) values ($1, $2, $3)").execute(of(customer_id, customer_email, customer_name)).await().indefinitely();
            findCartProductsByCustomerId(pgPool, customer_id).subscribe().asStream().forEach(c -> {
                cartId.add(c.cart_id);
            });
        }

        AtomicReference<Integer> stock = new AtomicReference<>(0);
        AtomicReference<BigDecimal> price = new AtomicReference<>();
        productService.findProductsByFarmId(pgPool, cartProduct.farm_id).subscribe().asStream().forEach(f -> {
            if (cartProduct.product_id.equals(f.product.id)) {
                stock.set(f.product.stock);
                price.set(f.product.price);
            }
        });

        if (cartProduct.quantity > stock.get()) {
            throw new DomainException("in Stock: " + stock.get());
        }

        BigDecimal qtd = new BigDecimal(cartProduct.quantity);
        BigDecimal calcPrice = price.get().multiply(qtd);

        Long currentCart = cartId.stream().findFirst().get();
        if (!productIds.contains(cartProduct.product_id)) {
            pgPool.preparedQuery("insert into cart_product(cart_id, product_id, farm_id, quantity, price) values ($1, $2, $3, $4, $5)").execute(of(currentCart, cartProduct.product_id, cartProduct.farm_id, cartProduct.quantity, calcPrice)).await().indefinitely();
        } else {
            pgPool.preparedQuery("update cart_product set quantity = $2, price = $3 WHERE id = $1").execute(of(currentCart, cartProduct.quantity, calcPrice)).await().indefinitely();
        }
    }

    @Override
    public void remove(PgPool pgPool, String customer_id, Long cartId, Long id) {

        AtomicReference<Long> itemId = new AtomicReference<Long>();

        findCartProductsByCustomerId(pgPool, customer_id).subscribe().asStream().forEach(c -> {
            if (id.equals(c.id) && cartId.equals(c.cart_id)) {
                itemId.set(id);
            }
        });
        if (itemId.get() != null) {
            pgPool.preparedQuery("delete from cart_product where id = $1 and cart_id = $2").execute(of(itemId.get(), cartId)).await().indefinitely();
        } else {
            throw new DomainException("Item not found");
        }
    }

    @Override
    public void payment(PgPool pgPool, String id, Long cartId, PaymentRequest payment) {
        AtomicReference<Long> cart = new AtomicReference<>();

        findCartProductsByCustomerId(pgPool, id).subscribe().asStream().forEach(c -> {
            if (c.cart_id.equals(cartId)) {
                cart.set(c.cart_id);
            }
        });

        if (cart.get() == null) throw new DomainException("cart not found");

        pgPool.preparedQuery("update cart set payment_method = $2, payment_status= $3, payment_token = $4 where id = $1")
                .execute(of(cartId, payment.method, payment.status, payment.token)).await().indefinitely();
        System.out.println("payment: " + payment.method);

    }

    @Override
    public void address(PgPool pgPool, String id, Long cartId, AddressRequest address) {
        AtomicReference<Long> cart = new AtomicReference<>();

        findCartProductsByCustomerId(pgPool, id).subscribe().asStream().forEach(c -> {
            if (c.cart_id.equals(cartId)) {
                cart.set(c.cart_id);
            }
        });

        if (cart.get() == null) throw new DomainException("cart not found");

        List<Object> columns = new ArrayList<>();
        columns.add(cartId);
        columns.add(address.postal_code);
        columns.add(address.city);
        columns.add(address.district);
        columns.add(address.street);
        columns.add(address.number);
        columns.add(address.description);
        columns.add(address.latitude);
        columns.add(address.longitude);

        pgPool.preparedQuery("insert into cart_address(cart_id, postal_code, city, district, street, number, description, latitude, longitude) values ($1,$2,$3,$4,$5,$6,$7,$8,$9)")
                .execute(wrap(columns)).await().indefinitely();
        System.out.println("address: " + address.postal_code);

    }

    @Override
    public void checkout(PgPool pgPool, String customerId, Long cartId) {
        AtomicReference<Long> cart = new AtomicReference<>();
        List<CartProductResponse> items = new ArrayList<>();
        Map<Long, List<CartProductResponse>> cartByFarm = new HashMap<>();

        findCartProductsByCustomerId(pgPool, customerId).subscribe().asStream().forEach(c -> {
            if (c.cart_id.equals(cartId)) {
                if(c.payment_token.isEmpty()) throw new DomainException("Payment Pending");
                if(c.ca_city.isEmpty()) throw new DomainException("Address Pending");
                cart.set(c.cart_id);
                items.add(c);
                if (cartByFarm.containsKey(c.farm_id)) {
                    cartByFarm.get(c.farm_id).add(c);
                } else {
                    List<CartProductResponse> r = new ArrayList<>();
                    r.add(c);
                    cartByFarm.put(c.farm_id, r);
                }
            }
        });

        if (cart.get() == null) throw new BadRequestException("cart not found");

        Map<Long, Integer> products = new HashMap<>();
        items.forEach(i -> {
            products.put(i.product_id, i.quantity);
        });

        Map<Long, Integer> updateStock = productService.updateStock(pgPool, products);
        Jsonb create = JsonbBuilder.create();

        emitter.send(create.toJson(updateStock));
        System.out.println("checkout :: send to queue STOCK");

        final Boolean[] success = {false};
        cartByFarm.entrySet().forEach(new Consumer<Map.Entry<Long, List<CartProductResponse>>>() {
            @Override
            public void accept(Map.Entry<Long, List<CartProductResponse>> entry) {
                try {
                    emitterOrder.send(create.toJson(OrderProductMapper.from(entry)));
                    success[0] = true;
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            }
        });
        if(success[0]) pgPool.preparedQuery("delete from cart where id = $1").execute(of(cart.get())).await().indefinitely();
    }

}
