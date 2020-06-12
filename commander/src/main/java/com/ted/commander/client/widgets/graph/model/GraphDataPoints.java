package com.ted.commander.client.widgets.graph.model;

import com.ted.commander.client.widgets.graph.formatter.GraphFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * An object used to represent graph data points for a set of data.
 */
public class GraphDataPoints implements Iterable<GraphDataPoint>{

    final String color;
    final String description;
    final GraphFormatter formatter;
    final List<GraphDataPoint> graphDataPointsList = new ArrayList<GraphDataPoint>();
    double minValue = Double.MAX_VALUE;
    double maxValue = Double.MIN_VALUE;

    public GraphDataPoints(String color, String description, GraphFormatter formatter) {
        this.color = color;
        this.description = description;
        this.formatter = formatter;
    }

    //For now we assume this will always be called in order
    public void add(GraphDataPoint graphDataPoint){
        graphDataPointsList.add(graphDataPoint);
        if (minValue > graphDataPoint.getValue()) minValue = graphDataPoint.getValue();
        if (maxValue < graphDataPoint.getValue()) maxValue = graphDataPoint.getValue();
    }

    public void addAll(List<GraphDataPoint> graphDataPointsList){
        for (GraphDataPoint graphDataPoint: graphDataPointsList){
            add(graphDataPoint);
        }
    }

    /**
     * Returns the closest but not over graph data point for the specified timestamp.
     * This uses a slightly modified binary search.
     * @param epochTime
     * @return
     */

    //TODO: We may want to add a hash table as well for a little improvement in the lookup time.
    // We'll see how this flies on slower devices. In a lot of cases, these lists will be generally
    // small (minute graph being the only exception). Its will be a tradeoff between hash calculation vs lookup time.
    public GraphDataPoint findForTime(long epochTime){
        return findForTime(epochTime, false);
    }

    public GraphDataPoint findForTime(long epochTime, boolean exact){
        if (graphDataPointsList.size() == 0) return null; //No Data (Should I return an empty data point?)
        int lowIndex = 0;
        int highIndex = graphDataPointsList.size()-1;

        while (lowIndex <= highIndex){
            //Get the middle index.
            int midIndex = lowIndex + (highIndex - lowIndex) / 2;
            long lowValue = graphDataPointsList.get(lowIndex).getTimestamp();
            long midValue = graphDataPointsList.get(midIndex).getTimestamp();
            long highValue = graphDataPointsList.get(highIndex).getTimestamp();

       //     GWT.log("lowValue:" + lowValue +   " midValue:" + midValue  + " highValue: " + highValue + " epochTime:" + epochTime + " lowIndex: " + lowIndex + " midIndex :" + midIndex + " highIndex:" + highIndex);

            //If we have an exact match at one of the points (short circuit another loop)
            if (lowValue == epochTime) return graphDataPointsList.get(lowIndex);
            if (midValue == epochTime) return graphDataPointsList.get(midIndex);
            if (highValue == epochTime) return graphDataPointsList.get(highIndex);


            //Special case if we only have two values left. Check the high value to make sure its not a match
            //if not, always default to the low value because it can be assumed that the test timestamp does not
            //exist in the array.
            if (!exact && (highIndex - lowIndex) <= 2) {
//                GWT.log("-------------LAST TWO STANDING: FIGHT!");
                if (highValue == epochTime) graphDataPointsList.get(highIndex);
                return graphDataPointsList.get(lowIndex);
            } else {
                //Search the other half of the index.
                if (epochTime < midValue) {
                    highIndex = midIndex - 1;
                } else {
                    lowIndex = midIndex + 1;
                }
            }
        }

        //This code technically should never be reached.
        return null;

    };

    public String getDescription() {
        return description;
    }

    public GraphFormatter getFormatter() {
        return formatter;
    }

    public String getColor() {
        return color;
    }

    @Override
    public Iterator<GraphDataPoint> iterator() {
        return graphDataPointsList.iterator();
    }

    public GraphDataPoint get(int index) {
        return graphDataPointsList.get(index);
    }

    public int size() {
        return graphDataPointsList.size();
    }

    public double getMinValue() {
        return minValue;
    }


    public double getMaxValue() {
        return maxValue;
    }

    public List<GraphDataPoint> getGraphDataPointsList() {
        return graphDataPointsList;
    }

    public void addAt(int i, GraphDataPoint startPoint) {
        graphDataPointsList.add(i, startPoint);
    }

    @Override
    public String toString() {
        return "GraphDataPoints{" +
                "color='" + color + '\'' +
                ", description='" + description + '\'' +
                ", formatter=" + formatter +
                ", graphDataPointsList=" + graphDataPointsList +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
