package com.github.cheldontk.freshfood.marketplace.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;


public class Location extends PanacheEntityBase {
    public Long id;
    public Double longitude;
    public Double latitude;

}
