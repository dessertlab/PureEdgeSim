/**
 *     PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 *
 *     This file is part of PureEdgeSim Project.
 *
 *     PureEdgeSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PureEdgeSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 *     
 *     @author Charafeddine Mechalikh
 *     @since PureEdgeSim 2.0
 **/
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
public class FiberChart extends Chart {
    
    // We can use an ArrayList instead of a LinkedList for better random access performance.
    protected ArrayList<Double> FiberUpUsage = new ArrayList<>();
    protected ArrayList<Double> FiberDownUsage = new ArrayList<>();

    /**
     * Constructs a new Fiber chart with the given title, X and Y axis titles, and simulation manager.
     *
     * @param title             the title of the chart
     * @param xAxisTitle        the title of the X axis
     * @param yAxisTitle        the title of the Y axis
     * @param simulationManager the simulation manager
     */
    public FiberChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
        super(title, xAxisTitle, yAxisTitle, simulationManager);
        getChart().getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
        // We can use the constant directly instead of computing it every time.
        updateSize(0.0, 0.0, 0.0, SimulationParameters.FiberBandwidthBitsPerSecond / 1000000.0);
    }

    /**
     * Updates the Fiber up and down utilization data for the chart.
     */
    
    public void update() {/*
        // Get Fiber usage in Mbps.
        double FiberUp = simulationManager.getNetworkModel().getFiberUpUtilization() / 1000000.0;
        double FiberDown = simulationManager.getNetworkModel().getFiberDownUtilization() / 1000000.0;

        FiberUpUsage.add(FiberUp);
        FiberDownUsage.add(FiberDown);

        // Remove old data points.
        int maxDataPoints = (int) (300 / SimulationParameters.chartsUpdateInterval);
        while (FiberUpUsage.size() > maxDataPoints) {
            FiberUpUsage.remove(0);
            FiberDownUsage.remove(0);
        }

        // Compute the time values for the data points.
        double[] time = new double[FiberUpUsage.size()];
        double currentTime = simulationManager.getSimulation().clock();
        for (int i = FiberUpUsage.size() - 1; i >= 0; i--) {
            time[i] = currentTime - ((FiberUpUsage.size() - i) * SimulationParameters.chartsUpdateInterval);
        }

        // Update the chart with the new data.
        updateSize(currentTime - 200, currentTime, 0.0, SimulationParameters.FiberBandwidthBitsPerSecond / 1000000.0);
        updateSeries(getChart(), "FiberUp", time, toArray(FiberUpUsage), SeriesMarkers.NONE, Color.BLACK);
        updateSeries(getChart(), "FiberDown", time, toArray(FiberDownUsage), SeriesMarkers.NONE, Color.BLACK);*/
    }
}