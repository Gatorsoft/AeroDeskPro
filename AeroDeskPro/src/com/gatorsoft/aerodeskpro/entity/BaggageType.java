/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.gatorsoft.aerodeskpro.entity;

   // Enums to match database ENUM fields exactly
    public enum BaggageType {
        CARRY_ON("carry_on"),
        CHECKED("checked"),
        OVERSIZED("oversized"),
        FRAGILE("fragile");
        
        private final String value;
        
        BaggageType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static BaggageType fromValue(String value) {
            if (value == null) return CHECKED; // Default value
            for (BaggageType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown baggage type: " + value);
        }
    }