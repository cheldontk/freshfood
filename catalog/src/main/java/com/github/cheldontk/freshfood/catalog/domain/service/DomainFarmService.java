package com.github.cheldontk.freshfood.catalog.domain.service;

import com.github.cheldontk.freshfood.catalog.application.request.FarmCreateRequest;
import com.github.cheldontk.freshfood.catalog.application.request.FarmUpdateRequest;
import com.github.cheldontk.freshfood.catalog.application.response.FarmResponse;
import com.github.cheldontk.freshfood.catalog.domain.DomainException;
import com.github.cheldontk.freshfood.catalog.domain.model.Farm;
import com.github.cheldontk.freshfood.catalog.domain.model.Location;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import org.eclipse.microprofile.opentracing.Traced;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
@Traced
public class DomainFarmService implements FarmService {
    @Inject
    FarmMapper farmMapper;

    @Override
    public FarmResponse createFarm(FarmCreateRequest farmRequest, String sub) {

        long farmCount = Farm.find("registered_number", farmRequest.registered_number).count();
        if (farmCount > 0) {
            throw new DomainException("Farm exists");
        }

        Farm farm = farmMapper.toFarm(farmRequest);
        farm.owner = sub;
        if (farm.location.longitude != 0 && farm.location.latitude != 0) {
            Location location = new Location(farm.location.longitude, farm.location.latitude);
            location.persist();
            farm.location = location;
        }

        farm.persist();

        return farmMapper.toFarmResponse(farm);
    }

    @Override
    public FarmResponse updateFarm(String sub, Long id, FarmUpdateRequest farm) {

        Optional<Farm> currentFarm = Farm.findByIdOptional(id);
        if (currentFarm.isEmpty()) {
            throw new DomainException("NotFound Farm");
        }
        if (!currentFarm.get().owner.equals(sub)) {
            throw new ForbiddenException();
        }

        Optional<Location> location = Location.findByIdOptional(currentFarm.get().location.id);

        Location newLocation = location.get();
        newLocation.latitude = farm.location.latitude;
        newLocation.longitude = farm.location.longitude;
        if (!farm.location.longitude.equals(location.get().longitude)
                || !farm.location.longitude.equals(location.get().latitude)) {
            newLocation.persist();
        }

        Farm farm1 = currentFarm.get();
        farmMapper.toFarm(farm, farm1);
        farm1.addLocation(newLocation);
        farm1.persist();

        return farmMapper.toFarmResponse(farm1);
    }

    @Override
    public FarmResponse deleteFarm(String sub, Long id) {
        Optional<Farm> currentFarm = Farm.findByIdOptional(id);
        if (currentFarm.isEmpty()) {
            throw new DomainException("NotFound Farm");
        }
        if (!currentFarm.get().owner.equals(sub)) {
            throw new ForbiddenException();
        }
        currentFarm.get().delete();

        return farmMapper.toFarmResponse(currentFarm.get());
    }

    @Override
    public List<FarmResponse> getAllFarms() {
        Stream<Farm> farms = Farm.streamAll();
        return farms.map(f -> farmMapper.toFarmResponse(f)).collect(Collectors.toList());
    }

    @Override
    public List<FarmResponse> getMyFarms(String sub) {
        List<Farm> farms = Farm.list("owner", sub);
        List<FarmResponse> response = new ArrayList<>();
        farms.forEach(f -> {
            response.add(farmMapper.toFarmResponse(f));
        });
        return response;
    }

    @Override
    public FarmResponse getFarmById(Long id) {
        Optional<Farm> farm = Farm.findByIdOptional(id);
        return farmMapper.toFarmResponse(farm.get());
    }

}
