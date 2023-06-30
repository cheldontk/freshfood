package com.github.cheldontk.freshfood.marketplace.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

public class Farm extends PanacheEntityBase {

    public Long id;
    public String name;
    public Location location;

}
