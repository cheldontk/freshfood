package com.github.cheldontk.freshfood.catalog.application.response;

import java.util.UUID;

public class FarmResponse {
    public Long id;
    public String name;
    public String description;
    public String registered_number;
    public String owner;
    public Boolean isActive;
    public LocationResponse location;
    public String created_at;
    public String updated_at;
}
