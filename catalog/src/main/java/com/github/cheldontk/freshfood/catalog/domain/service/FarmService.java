package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.FarmCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.FarmUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.FarmResponse;

import java.util.List;

public interface FarmService {
    FarmResponse createFarm(FarmCreateRequest farm, String owner);

    FarmResponse deleteFarm(String sub, Long id);

    List<FarmResponse> getAllFarms();

    FarmResponse updateFarm(String sub, Long id, FarmUpdateRequest farm);

    List<FarmResponse> getMyFarms(String sub);

    FarmResponse getFarmById(Long id);
}
