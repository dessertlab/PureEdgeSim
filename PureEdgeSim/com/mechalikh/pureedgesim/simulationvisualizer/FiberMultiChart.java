package com.mechalikh.pureedgesim.simulationvisualizer;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList; 
import java.util.Map;
import java.util.HashMap;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters; 
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;

/**
 * A chart that displays the Fiber up and down utilization over time.
 */
public class FiberMultiChart extends Chart {
    
    // We can use an ArrayList instead of a LinkedList for better random access performance.
    protected ArrayList<Map<String, Double>> fiberUpUsageData = new ArrayList<>();
    protected ArrayList<Map<String, Double>> fiberDownUsageData = new ArrayList<>();

    /**
     * Constructs a new Fiber chart with the given title, X and Y axis titles, and simulation manager.
     *
     * @param title             the title of the chart
     * @param xAxisTitle        the title of the X axis
     * @param yAxisTitle        the title of the Y axis
     * @param simulationManager the simulation manager
     */
    public FiberMultiChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
        super(title, xAxisTitle, yAxisTitle, simulationManager);
        getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        // We can use the constant directly instead of computing it every time.
        updateSize(0.0, 0.0, 0.0, SimulationParameters.FiberBandwidthBitsPerSecond / 1000000.0);
    }

    /**
     * Updates the Fiber utilization data for the chart.
     */
    public void update() {
    	
    	//System.out.println("Time: " + Arrays.toString(time));
    	//System.out.println("Data for FiberUp: " + getDataFor("FiberUp"));
    	//System.out.println("Data for FiberDown: " + getDataFor("FiberDown"));
        Map<String, Double> fiberUpUsage = new HashMap<>();
        Map<String, Double> fiberDownUsage = new HashMap<>();

        // Get Fiber usage in Mbps for each element in the map.
        for (Map.Entry<String, Double> entry : simulationManager.getNetworkModel().getFiberUpUtilization().entrySet()) {
            fiberUpUsage.put(entry.getKey(), (entry.getValue() / 1000000.0));
        }
        
        for (Map.Entry<String, Double> entry : simulationManager.getNetworkModel().getFiberDownUtilization().entrySet()) {
            fiberDownUsage.put(entry.getKey(), (entry.getValue() / 1000000.0));
        }

        fiberUpUsageData.add(fiberUpUsage);
        fiberDownUsageData.add(fiberDownUsage);
        
        //printValues(fiberUpUsage, "Up");
        //printValues(fiberDownUsage, "Down");

        // Remove old data points.
        int maxDataPoints = (int) (300 / SimulationParameters.chartsUpdateInterval);
        while (fiberUpUsageData.size() > maxDataPoints) {
            fiberUpUsageData.remove(0);
            fiberDownUsageData.remove(0);
        }

        // Compute the time values for the data points.
        double[] time = new double[fiberUpUsageData.size()];
        double currentTime = simulationManager.getSimulation().clock();
        for (int i = fiberUpUsageData.size() - 1; i >= 0; i--) {
            time[i] = currentTime - ((fiberUpUsageData.size() - i) * SimulationParameters.chartsUpdateInterval);
        }

        // Update the chart with the new data.
        updateSize(currentTime - 200, currentTime, 0.0, SimulationParameters.FiberBandwidthBitsPerSecond / 1000000.0);

        // Update the series for each element in the map.
        for (Map.Entry<String, Double> entry : fiberUpUsage.entrySet()) {
            updateSeries(getChart(), entry.getKey() + " Up", time, toArray(getDataUpFor(entry.getKey())), SeriesMarkers.NONE, Color.BLACK);
        }
        
        for (Map.Entry<String, Double> entry : fiberDownUsage.entrySet()) {
            updateSeries(getChart(), entry.getKey() + " Down", time, toArray(getDataDownFor(entry.getKey())), SeriesMarkers.NONE, Color.BLACK);
        }
        
    }

    // Utility method to retrieve UpData for a specific key in the map.
    private List<Double> getDataUpFor(String key) {
        List<Double> data = new ArrayList<>();
        for (Map<String, Double> fiberUsage : fiberUpUsageData) {
            double value = fiberUsage.getOrDefault(key, 0.0);
            //System.out.println("Data for " + key + ": " + value);
            data.add(value);
        }
        return data;
    }
    
    // Utility method to retrieve DownData for a specific key in the map.
    private List<Double> getDataDownFor(String key) {
        List<Double> data = new ArrayList<>();
        for (Map<String, Double> fiberUsage : fiberDownUsageData) {
            double value = fiberUsage.getOrDefault(key, 0.0);
            //System.out.println("Data for " + key + ": " + value);
            data.add(value);
        }
        return data;
    }
    
    private void printValues(Map<String, Double> fiberUsage, String direction) {
        System.out.println("Values for Fiber " + direction + ":");
        for (Map.Entry<String, Double> entry : fiberUsage.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
    }
    
    
}