package com.github.cheldontk.freshfood.catalog.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Table(name = "category")
public class Category extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    @ManyToOne
    public Farm farm;
    @CreationTimestamp
    public Date created_at;
    @UpdateTimestamp
    public Date updated_at;

    public Category build(Farm farm, String name) {
        this.farm = farm;
        this.name = name;
        return this;
    }
}

