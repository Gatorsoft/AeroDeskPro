package com.gatorsoft.aerodeskpro.models;

import java.math.BigDecimal;

public class BaggageReport {

    // Fields for the report
    private int totalBaggage;          // Total number of baggage items
    private BigDecimal totalWeight;    // Total weight of all baggage
    private int lostBaggage;           // Number of lost baggage items
    private int delayedBaggage;        // Number of delayed baggage items

    // Constructor to initialize the baggage report with required data
    public BaggageReport(int totalBaggage, BigDecimal totalWeight, int lostBaggage, int delayedBaggage) {
        this.totalBaggage = totalBaggage;
        this.totalWeight = totalWeight;
        this.lostBaggage = lostBaggage;
        this.delayedBaggage = delayedBaggage;
    }

    // Getters and setters for each field
    public int getTotalBaggage() {
        return totalBaggage;
    }

    public void setTotalBaggage(int totalBaggage) {
        this.totalBaggage = totalBaggage;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getLostBaggage() {
        return lostBaggage;
    }

    public void setLostBaggage(int lostBaggage) {
        this.lostBaggage = lostBaggage;
    }

    public int getDelayedBaggage() {
        return delayedBaggage;
    }

    public void setDelayedBaggage(int delayedBaggage) {
        this.delayedBaggage = delayedBaggage;
    }

    // Method to generate a formatted string summary of the baggage report
    public String generateReportSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Baggage Report Summary:\n")
               .append("Total Baggage: ").append(totalBaggage).append("\n")
               .append("Total Weight: ").append(totalWeight != null ? totalWeight : "N/A").append("\n")
               .append("Lost Baggage: ").append(lostBaggage).append("\n")
               .append("Delayed Baggage: ").append(delayedBaggage).append("\n");
        return summary.toString();
    }

    // Override the toString() method to return the report summary
    @Override
    public String toString() {
        return generateReportSummary();
    }
}
