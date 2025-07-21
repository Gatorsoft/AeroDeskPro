package com.gatorsoft.aerodeskpro.api;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherChartFrame extends JFrame {

    private JComboBox<String> airportDropdown;
    private JPanel chartPanel;

  public WeatherChartFrame(String title) {
    super(title);

    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    airportDropdown = new JComboBox<>(new String[]{
            "John F. Kennedy International Airport (JFK) - USA",
            "London Heathrow Airport (LHR) - UK",
            "Tokyo International Airport (Haneda) - Japan",
            "Dubai International Airport (DXB) - UAE",
            "Paris Charles de Gaulle Airport (CDG) - France",
            "Los Angeles International Airport (LAX) - USA"
    });

    airportDropdown.addActionListener(e -> {
        String selectedAirport = (String) airportDropdown.getSelectedItem();
        showWeatherForAirport(selectedAirport);
    });

    add(airportDropdown);

    chartPanel = new JPanel();
    add(chartPanel);

    showWeatherForAirport((String) airportDropdown.getSelectedItem());


    this.pack();
    this.setLocationRelativeTo(null); // center
  
}

    private void showWeatherForAirport(String selectedAirport) {
        // Coordinates for the airports (Latitude, Longitude)
        double latitude = 40.6413, longitude = -73.7781;  // Default to JFK (USA)

        // Update coordinates based on selected airport
        switch (selectedAirport) {
            case "London Heathrow Airport (LHR) - UK":
                latitude = 51.4700;
                longitude = -0.4543;
                break;
            case "Tokyo International Airport (Haneda) - Japan":
                latitude = 35.5494;
                longitude = 139.7798;
                break;
            case "Dubai International Airport (DXB) - UAE":
                latitude = 25.2532;
                longitude = 55.3657;
                break;
            case "Paris Charles de Gaulle Airport (CDG) - France":
                latitude = 49.0097;
                longitude = 2.5477;
                break;
            case "Los Angeles International Airport (LAX) - USA":
                latitude = 33.9416;
                longitude = -118.4085;
                break;
        }

        // Fetch the weather data for the selected coordinates
        try {
            // Fetch the data from Open-Meteo API
            String jsonResponse = WeatherDataFetcher.fetchWeatherData(latitude, longitude);

            // Parse the temperature data from the JSON response
            List<Double> temperatures = WeatherDataFetcher.parseTemperatureData(jsonResponse);

            // Create a time series for the temperature data
            TimeSeries timeSeries = new TimeSeries("Temperature (°C)");

            // Set a start date (e.g., today's date at midnight)
            Date startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2025-07-20 00:00:00");

            // Populate the time series with hourly temperature data
            for (int i = 0; i < temperatures.size(); i++) {
                // Create a date object by adding i hours to the start date
                Date date = new Date(startDate.getTime() + i * 60 * 60 * 1000L); // Adding i hours to the start date
                timeSeries.addOrUpdate(new Second(date), temperatures.get(i));
            }

            // Create a dataset for the chart
            TimeSeriesCollection dataset = new TimeSeriesCollection(timeSeries);

            // Create the JFreeChart
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Temperature Over Time at " + selectedAirport,    // Title of the chart
                    "Time",                     // X-Axis label
                    "Temperature (°C)",         // Y-Axis label
                    dataset,                    // Data for the chart
                    false,                      // Legend visibility
                    true,                       // Tooltips visibility
                    false                       // URL generation
            );

            // Clear the previous chart panel and add the new chart
            chartPanel.removeAll();
            chartPanel.add(new ChartPanel(chart));
            chartPanel.revalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create and display the JFrame
        SwingUtilities.invokeLater(() -> {
            WeatherChartFrame frame = new WeatherChartFrame("Weather Data Visualization");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    // Inner class for fetching and parsing weather data
    public static class WeatherDataFetcher {

        // Fetches weather data from Open-Meteo API
        public static String fetchWeatherData(double latitude, double longitude) throws Exception {
            String apiUrl = String.format("https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m", latitude, longitude);

            // Create a URL object
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }

        // Parses the temperature data from the API response (manual JSON parsing)
        public static List<Double> parseTemperatureData(String jsonResponse) {
            List<Double> tempList = new ArrayList<>();

            // Basic JSON parsing without any library
            try {
                // Look for the part of the JSON that contains the "temperature_2m" array
                String target = "\"temperature_2m\":[";  // this indicates where the array starts
                int startIndex = jsonResponse.indexOf(target) + target.length();
                int endIndex = jsonResponse.indexOf("]", startIndex);

                // Extract the temperatures part and split them by commas
                String tempData = jsonResponse.substring(startIndex, endIndex);

                // Now split the temperature data into individual temperature values
                String[] tempValues = tempData.split(",");

                // Add each temperature value to the list
                for (String tempValue : tempValues) {
                    tempList.add(Double.parseDouble(tempValue.trim()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tempList;
        }
    }
}

