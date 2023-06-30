package com.github.cheldontk.freshfood.catalog.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "farm")
public class Farm extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String registered_number;
    public String owner;
    public String description;
    public Boolean isActive;
    @OneToOne(cascade = CascadeType.ALL)
    public Location location;
    @CreationTimestamp
    public Date created_at;
    @UpdateTimestamp
    public Date updated_at;

    public Farm() {
    }

    public void addLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return this.id;
    }

}
