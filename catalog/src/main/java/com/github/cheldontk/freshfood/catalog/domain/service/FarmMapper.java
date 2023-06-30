package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.FarmCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.FarmUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.FarmResponse;
import com.github.cheldontk.freshfood.catalog.domain.model.Farm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "cdi")
public interface FarmMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    Farm toFarm(FarmCreateRequest farmRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registered_number", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "location.id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    void toFarm(FarmUpdateRequest farmRequest, @MappingTarget Farm farm);

    @Mapping(target = "created_at", dateFormat = "dd/MM/yyyy HH:mm:ss")
    @Mapping(target = "updated_at", dateFormat = "dd/MM/yyyy HH:mm:ss")
    FarmResponse toFarmResponse(Farm farm);

}
