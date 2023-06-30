package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.ProductCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.CategoryResponse;
import com.github.cheldontk.freshfood.catalog.application.response.ProductResponse;

import java.util.List;

public interface ProductService {

    CategoryResponse createCategory(Long farmId, String sub, String name);

    List<CategoryResponse> getAllCategoriesByFarmId(Long farmId, String sub);

    ProductResponse createProduct(Long farmId, String sub, ProductCreateRequest productRequest);

    ProductResponse updateProduct(Long farmId, String sub, Long id, ProductUpdateRequest productRequest);

    ProductResponse deleteProduct(Long farmId, String sub, Long id);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long id);

    List<ProductResponse> getProductsByFarmId(Long id);
}
