/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.gatorsoft.aerodeskpro.entity;

   public enum BookingStatus {
        CONFIRMED, CHECKED_IN, CANCELLED, NO_SHOW;

        // Optional: Add a method to get BookingStatus by name
        public static BookingStatus fromString(String status) {
            try {
                return BookingStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return CONFIRMED; // default value if status is invalid
            }
        }
    }