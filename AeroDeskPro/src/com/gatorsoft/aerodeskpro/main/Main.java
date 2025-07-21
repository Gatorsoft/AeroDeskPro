package com.gatorsoft.aerodeskpro.main;

import com.gatorsoft.aerodeskpro.utils.PassengerTableFrame;
import com.gatorsoft.aerodeskpro.api.WeatherChartFrame;
import com.gatorsoft.aerodeskpro.gui.AirportManagementSystem;
import com.gatorsoft.aerodeskpro.utils.FlightTableFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        //StaffDAO dao = new StaffDAO();
       /* new PassengerTableFrame().setVisible(true);
        new WeatherChartFrame("Weather Chart").setVisible(true);
        new AirportManagementSystem().setVisible(true);
        new FlightTableFrame().setVisible(true);*/
mm();
    }
    
   public static void mm(){
         SwingUtilities.invokeLater(() -> {
            openPassengerTableFrame();
        });
   }


   
    private static void openPassengerTableFrame() {
        PassengerTableFrame ptf = new PassengerTableFrame();
        ptf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ptf.setVisible(true);

        ptf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Called when user initiates close (before disposal)
                // Schedule next frame to open after current frame disposes
                SwingUtilities.invokeLater(() -> openWeatherChartFrame());
            }
        });
    }

    private static void openWeatherChartFrame() {
        WeatherChartFrame wcf = new WeatherChartFrame("Weather Chart");
        wcf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        wcf.setVisible(true);

        wcf.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> openAirportManagementSystem());
            }
        });
    }

    private static void openAirportManagementSystem() {
        AirportManagementSystem ams = new AirportManagementSystem();
        ams.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ams.setVisible(true);

        ams.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> openFlightTableFrame());
            }
        });
    }

    private static void openFlightTableFrame() {
        FlightTableFrame ftf = new FlightTableFrame();
        ftf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ftf.setVisible(true);

        // No next window here
    }

}


