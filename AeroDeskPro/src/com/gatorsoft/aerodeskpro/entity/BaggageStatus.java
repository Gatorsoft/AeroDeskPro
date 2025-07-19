/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.gatorsoft.aerodeskpro.entity;

   public enum BaggageStatus {
        REGISTERED("registered"),
        SECURITY_CHECK("security_check"),
        LOADED("loaded"),
        IN_TRANSIT("in_transit"),
        ARRIVED("arrived"),
        DELIVERED("delivered"),
        LOST("lost"),
        DELAYED("delayed"),
        UNKNOWN("unknown");
        
        private final String value;
        
        BaggageStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static BaggageStatus fromValue(String value) {
            if (value == null) return REGISTERED; // Default value
            for (BaggageStatus status : values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown baggage status: " + value);
        }
    }