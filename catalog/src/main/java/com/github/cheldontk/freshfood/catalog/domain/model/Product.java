package com.github.cheldontk.freshfood.catalog.domain.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

/*
table( product ) {
  primary_key( id ):
  column( name ): CHARACTER VARYING
  column( price ): NUMBER
  column( costprice ): NUMBER
  column( isPublished ): BOOLEAN
  column( stock ): INTEGER
  foreign_key( farmId ): INTEGER <<FK>>
  foreign_key( categoryId ): INTEGER <<FK>>
}
 */
@Entity
@Table(name = "product")
public class Product extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public BigDecimal costprice;
    public Boolean isPublished;
    public Integer stock;
    public String img;
    @ManyToOne
    public Category category;
    @ManyToOne
    public Farm farm;
    @CreationTimestamp
    public Date created_at;
    @UpdateTimestamp
    public Date updated_at;

}
