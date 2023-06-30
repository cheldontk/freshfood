package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.CategoryCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.ProductUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.CategoryResponse;
import com.github.cheldontk.freshfood.catalog.application.response.ProductResponse;

import com.github.cheldontk.freshfood.catalog.domain.model.Category;
import com.github.cheldontk.freshfood.catalog.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    Category toCategory(CategoryCreateRequest categoryRequest);

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toProduct(ProductCreateRequest productRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(source = "img", target = "img")
    void toProduct(ProductUpdateRequest productRequest, @MappingTarget Product product);

    @Mapping(target = "created_at", dateFormat = "dd/MM/yyyy HH:mm:ss")
    @Mapping(target = "updated_at", dateFormat = "dd/MM/yyyy HH:mm:ss")
    @Mapping(target = "farm.created_at", ignore = true)
    @Mapping(target = "farm.updated_at", ignore = true)
    ProductResponse toProductResponse(Product product);

}
