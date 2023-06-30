package com.github.cheldontk.freshfood.catalog.domain;

public class DomainUtils {
    public static Boolean isValidRegisteredNumber(String registered_number){
        return !registered_number.isEmpty();
    }
}
