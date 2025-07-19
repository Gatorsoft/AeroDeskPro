/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.gatorsoft.aerodeskpro.entity;

public enum Priority {
        NORMAL("normal"),
        PRIORITY("priority"),
        RUSH("rush");
        
        private final String value;
        
        Priority(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Priority fromValue(String value) {
            if (value == null) return NORMAL; // Default value
            for (Priority priority : values()) {
                if (priority.value.equals(value)) {
                    return priority;
                }
            }
            throw new IllegalArgumentException("Unknown priority: " + value);
        }
    }
    