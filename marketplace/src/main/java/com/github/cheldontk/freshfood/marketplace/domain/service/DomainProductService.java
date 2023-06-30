package com.github.cheldontk.freshfood.marketplace.domain.service;

import com.github.cheldontk.freshfood.marketplace.application.request.ProductUpdateRequest;
import com.github.cheldontk.freshfood.marketplace.application.response.FarmProductsResponse;
import com.github.cheldontk.freshfood.marketplace.application.response.ProductResponse;
import com.github.cheldontk.freshfood.marketplace.domain.DomainUtils;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.opentracing.Traced;

import java.util.*;
import java.util.stream.StreamSupport;

import static io.vertx.mutiny.sqlclient.Tuple.of;
import static io.vertx.mutiny.sqlclient.Tuple.wrap;

@ApplicationScoped
@Traced
public class DomainProductService implements ProductService {

    @Override
    public Multi<ProductResponse> findAll(PgPool pgPool) {
        Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery(
                        "SELECT p.id, p.name, p.description, p.img, p.price, p.stock, c.id as category_id , c.name as category_name, f.id as farm_id, f.name as farm_name, l.id as location_id, l.latitude as latitude, l.longitude as longitude FROM product p left join category c on c.id = p.category_id left join farm f on f.id = c.farm_id left join location l on l.id = f.location_id")
                .execute();

        return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
            return StreamSupport.stream(rowSet.spliterator(), false);
        })).onItem().transform(ProductMapper::from);
    }

    @Override
    public Multi<FarmProductsResponse> findProductsByFarmId(PgPool pgPool, Long id) {
        Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery(
                        "SELECT p.id as product_id, p.name as product_name, p.img as product_image,p.description as product_description, p.price as product_price, p.stock as product_stock, c.id as category_id , c.name as category_name, f.id, f.name, l.id as location_id, l.latitude as latitude, l.longitude as longitude FROM farm f left join location l on l.id = f.location_id left join product p on p.farm_id = f.id left join category c on c.id = p.category_id WHERE f.id = $1")
                .execute(of(id));

        return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
            return StreamSupport.stream(rowSet.spliterator(), false);
        })).onItem().transform(ProductMapper::fromFarm);
    }

    @Override
    public Multi<FarmProductsResponse> getProductsByCategory(PgPool pgPool, String categoryName) {
        Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery(
                        "SELECT p.id as product_id, p.name as product_name, p.img as product_image, p.description as product_description, p.price as product_price, p.stock as product_stock, c.id as category_id , c.name as category_name, f.id, f.name, l.id as location_id, l.latitude as latitude, l.longitude as longitude FROM farm f left join location l on l.id = f.location_id left join product p on p.farm_id = f.id left join category c on c.id = p.category_id WHERE c.name = $1")
                .execute(of(categoryName));

        return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
            return StreamSupport.stream(rowSet.spliterator(), false);
        })).onItem().transform(ProductMapper::fromFarm);
    }

    @Override
    public Multi<ProductResponse> findProductsById(PgPool pgPool, Set<Long> ids) {
        Uni<RowSet<Row>> preparedQuery = pgPool.preparedQuery(
                        "SELECT p.id, p.name, p.description, p.price, p.stock, c.id as category_id , c.name as category_name, f.id as farm_id, f.name as farm_name, l.id as location_id, l.latitude as latitude, l.longitude as longitude FROM product p left join farm f on p.farm_id = f.id left join location l on l.id = f.location_id left join category c on c.id = p.category_id WHERE p.id = ANY($1)")
                .execute(of(ids.toArray()));
        return preparedQuery.onItem().transformToMulti(rowSet -> Multi.createFrom().items(() -> {
            return StreamSupport.stream(rowSet.spliterator(), false);
        })).onItem().transform(ProductMapper::from);
    }

    @Override
    public void persist(PgPool pgPool, String productString) {
        Map<String, Object> request = DomainUtils.parseStringToMap(productString);

        Jsonb create = JsonbBuilder.create();
        ProductUpdateRequest product = create.fromJson((String) request.get("message"), ProductUpdateRequest.class);
        Set<Long> locationIds = new HashSet<Long>();
        Set<Long> farmIds = new HashSet<Long>();
        Set<Long> categoryIds = new HashSet<Long>();
        Set<Long> productIds = new HashSet<Long>();

        findProductsByFarmId(pgPool, product.farm.id).subscribe().asStream().forEach(f -> {
            locationIds.add(f.location_id);
            farmIds.add(f.id);
            categoryIds.add(f.product.category_id);
            productIds.add(f.product.id);
        });
        System.out.println(request.get("event") + ", farmName: " + product.farm.name + ", productName: " + product.name + ", stock: " + product.stock);
        if (!locationIds.contains(product.farm.location.id)) {
            pgPool.preparedQuery("insert into location(id, latitude, longitude) values ($1, $2, $3)")
                    .execute(of(product.farm.location.id,
                            product.farm.location.latitude,
                            product.farm.location.longitude))
                    .await().indefinitely();
        }

        if (!farmIds.contains(product.farm.id)) {
            pgPool.preparedQuery("insert into farm(id, name, location_id) values ($1, $2, $3)").execute(
                            of(product.farm.id, product.farm.name, product.farm.location.id))
                    .await().indefinitely();
        }

        if (!categoryIds.contains(product.category.id)) {
            pgPool.preparedQuery("insert into category(id, name, farm_id) values ($1, $2, $3)").execute(
                            of(product.category.id, product.category.name, product.farm.id))
                    .await().indefinitely();

        }

        List<Object> columns = new ArrayList<>();
        columns.add(product.id);
        columns.add(product.name);
        columns.add(product.description);
        columns.add(product.img);
        columns.add(product.price);
        columns.add(product.stock);
        columns.add(product.category.id);

        if (!productIds.contains(product.id)) {
            columns.add(product.farm.id);
            pgPool.preparedQuery(
                            "insert into product(id, name, description, img, price, stock, category_id, farm_id) values ($1, $2, $3, $4, $5, $6, $7, $8)")
                    .execute(wrap(columns))
                    .await().indefinitely();
        } else {
            pgPool.preparedQuery(
                            "update product set name = $2, description = $3, img = $4, price = $5, stock = $6, category_id = $7 WHERE id = $1")
                    .execute(wrap(columns))
                    .await().indefinitely();
        }
    }

    @Override
    public Map<Long, Integer> updateStock(PgPool pgPool, Map<Long, Integer> products) {
        Map<Long, Integer> toStock = new HashMap<>();
        findProductsById(pgPool, products.keySet()).subscribe().asStream().forEach(p -> {
            System.out.println("p.id --> " + p.id);
            System.out.println("p.stock --> " + p.stock);
            System.out.println("p.quantity --> " + products.get(p.id));
            if (p.stock >= products.get(p.id)) {
                pgPool.preparedQuery("update product set stock = $2 where id = $1")
                        .execute(of(p.id, p.stock - products.get(p.id))).await().indefinitely();
                toStock.put(p.id, products.get(p.id));
            }
        });
        return toStock;
    }

}
