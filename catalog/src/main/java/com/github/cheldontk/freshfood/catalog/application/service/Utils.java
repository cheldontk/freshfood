package com.github.cheldontk.freshfood.catalog.application.service;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static void sendToQueue(Emitter<String> emitter, String eventName, Object message ){
        Jsonb create = JsonbBuilder.create();

        Map<String, String> payload = new HashMap<>();
        payload.put("event", eventName);
        payload.put("message", create.toJson(message));

        emitter.send(create.toJson(payload));
    }
    public static Map<Long, Integer> parseStringToMap(String json){
        Jsonb create = JsonbBuilder.create();
        return (Map<Long, Integer>) create.fromJson(json, new HashMap<Long, Integer>().getClass());

    }
}
