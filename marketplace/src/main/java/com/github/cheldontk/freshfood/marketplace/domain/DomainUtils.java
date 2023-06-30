package com.github.cheldontk.freshfood.marketplace.domain;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.HashMap;
import java.util.Map;

public class DomainUtils {

    public static Map<String, Object> parseStringToMap(String json){
        Jsonb create = JsonbBuilder.create();
        return (Map<String, Object>) create.fromJson(json, new HashMap<String, Object>().getClass());

    }
}
