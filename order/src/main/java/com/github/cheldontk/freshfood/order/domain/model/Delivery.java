package com.github.cheldontk.freshfood.order.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Delivery {
    public String id;
    public String deliveryman;
    public Address origin_address;
    public Address delivery_address;
    public List<Location> current_location = new ArrayList<>();
    public String status;
    public String description;

}
