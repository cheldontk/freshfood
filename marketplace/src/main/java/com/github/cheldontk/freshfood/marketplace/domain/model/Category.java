package com.github.cheldontk.freshfood.marketplace.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;


public class Category extends PanacheEntityBase {
    public Long id;
    public String name;
    public Farm farm;

}

