package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.ProductCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.CategoryResponse;
import com.github.cheldontk.freshfood.catalog.application.response.ProductResponse;
import com.github.cheldontk.freshfood.catalog.application.service.Utils;
import com.github.cheldontk.freshfood.catalog.domain.DomainException;
import com.github.cheldontk.freshfood.catalog.domain.model.Category;
import com.github.cheldontk.freshfood.catalog.domain.model.Farm;
import com.github.cheldontk.freshfood.catalog.domain.model.Product;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.cheldontk.freshfood.catalog.application.service.Utils.parseStringToMap;

@ApplicationScoped
@Traced
public class DomainProductService implements ProductService {

    @Inject
    @Channel("products")
    Emitter<String> emitter;

    @Inject
    ProductMapper productMapper;

    @Override
    public CategoryResponse createCategory(Long farmId, String sub, String name) {
        Farm farm = Farm.findById(farmId);
        if (farm == null) throw new DomainException("Farm is required");
        if (!farm.owner.equals(sub)) throw new ForbiddenException();

        List<Category> category = Category.list("farm", farm);
        AtomicBoolean toPersist = new AtomicBoolean(true);
        if (!category.isEmpty()) {
            category.forEach(c -> {
                if (c.name.equalsIgnoreCase(name)) {
                    toPersist.set(false);
                }
            });
        }
        if (toPersist.get()) {
            Category newcategory = new Category().build(farm, name);
            newcategory.persist();
            return productMapper.toCategoryResponse(newcategory);
        }

        return productMapper.toCategoryResponse(category.get(0));
    }

    @Override
    public List<CategoryResponse> getAllCategoriesByFarmId(Long farmId, String sub) {
        Farm farm = Farm.findById(farmId);
        if (farm == null) throw new DomainException("Farm is required");
        if (!farm.owner.equals(sub)) throw new ForbiddenException();

        Stream<PanacheEntityBase> categories = Category.list("farm", farm).stream();
        return categories.map(c -> productMapper.toCategoryResponse((Category) c)).collect(Collectors.toList());
    }

    @Override
    public ProductResponse createProduct(Long farmId, String sub, ProductCreateRequest productRequest) {

        Optional<Farm> farm = Farm.findByIdOptional(farmId);
        if (farm.isEmpty()) {
            throw new DomainException("Farm is required");
        }
        if (!farm.get().owner.equals(sub)) {
            throw new ForbiddenException();
        }

        List<Product> existProduct = Product.list("farm", farm.get());
        existProduct.forEach(p -> {
            if (p.name.equalsIgnoreCase(productRequest.name)) {
                throw new DomainException("Product exists");
            }
        });

        AtomicReference<Category> onCategory = new AtomicReference<>();
        List<Category> categories = Category.list("farm", farm.get());

        categories.forEach(c -> {
            if (c.name.equals(productRequest.category)) {
                onCategory.set(c);
            }
        });

        if (onCategory.get() == null) {
            throw new DomainException("Category is required");
        }

        Product product = productMapper.toProduct(productRequest);
        product.category = onCategory.get();
        product.farm = farm.get();
        if (product.stock > 0) {
            product.isPublished = true;
        }
        product.persist();

        Utils.sendToQueue(emitter, "create_product", product);

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse updateProduct(Long farmId, String sub, Long id, ProductUpdateRequest productRequest) {
        Farm farm = Farm.findById(farmId);
        if (farm == null) throw new DomainException("Farm is required");
        if (!farm.owner.equals(sub)) throw new ForbiddenException();

        Category category = Category.findById(productRequest.categoryId);
        if (category == null) {
            throw new DomainException("Category is required");
        }

        Product product = Product.findById(id);
        if (product == null) {
            throw new DomainException("Product is invalid");
        }

        productMapper.toProduct(productRequest, product);
        product.isPublished = (product.stock > 0);
        product.persist();

        Utils.sendToQueue(emitter, "update_product", product);

        return productMapper.toProductResponse(product);
    }

    @Override
    public ProductResponse deleteProduct(Long farmId, String sub, Long id) {
        Farm farm = Farm.findById(farmId);

        if (farm == null) throw new DomainException("Farm is required");
        if (!farm.owner.equals(sub)) throw new ForbiddenException();

        Optional<Product> product = Product.findByIdOptional(id);
        if (product.get().farm.equals(farm)) {
            product.ifPresentOrElse(Product::delete, () -> {
                throw new NotFoundException("NotFound Product");
            });
            return productMapper.toProductResponse(product.get());
        }
        throw new BadRequestException("Invalid Farm to Product");
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        Stream<Product> products = Product.streamAll();
        return products.map(p -> productMapper.toProductResponse(p)).collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Optional<Product> product = Product.findByIdOptional(id);
        if (product.isEmpty()) {
            throw new NotFoundException("Product Not Found");
        }
        return productMapper.toProductResponse(product.get());
    }

    @Override
    public List<ProductResponse> getProductsByFarmId(Long farmId) {
        Optional<Farm> farm = Farm.findByIdOptional(farmId);
        if (farm.isEmpty()) {
            throw new DomainException("Farm is required");
        }

        Stream<PanacheEntityBase> products = Product.list("farm", farm.get()).stream();
        return products.map(p -> productMapper.toProductResponse((Product) p)).collect(Collectors.toList());
    }

    public void updateStock(String mapProductStock) {
        System.out.println("stock event -> " + mapProductStock);
        Map<Long, Integer> stringObjectMap = parseStringToMap(mapProductStock);
        stringObjectMap.entrySet().forEach(new Consumer<Map.Entry<Long, Integer>>() {
            @Override
            public void accept(Map.Entry<Long, Integer> longIntegerEntry) {

                try {
                    Optional<Product> product = Product.findByIdOptional(longIntegerEntry.getKey());
                    Object debit = longIntegerEntry.getValue();
                    Integer newStock = product.get().stock - Integer.valueOf(debit.toString());
                    Product.update("stock = ?2 where id = ?1", longIntegerEntry.getKey(), newStock);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }
}
