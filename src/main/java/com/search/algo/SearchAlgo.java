/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.search.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author prabash.dharshanapri
 */
public class SearchAlgo
{

    static ArrayList<Station> stationsList = new ArrayList<>();
    static String JSONString = "{\n"
            + "  \"id\":\"0812492a-6a08-4d55-97e5-5dc06780016c\",\n"
            + "  \"generatedOn\":\"2018-12-22T07:02:27.3917913Z\",\n"
            + "  \"data\":[[17,4],[16,3],[23,7],[26,9],[17,5],[16,4],[24,5],[33,2],[27,7],[23,1],[32,4],[26,9],[28,6],[25,2],[32,9],[22,3],[33,1],[26,8],[18,3],[30,8],[29,8],[30,7],[31,2],[28,8],[25,3],[32,4],[18,4],[15,7],[30,1],[27,8],[18,4],[15,3],[16,7],[25,4],[33,1],[33,3],[32,5],[22,1],[27,8],[17,4],[32,6],[28,6],[15,9],[17,5],[15,8],[16,1],[29,9],[28,4],[30,5],[28,1],[29,3],[33,4],[22,6],[25,6],[15,9],[17,9],[34,2],[34,5],[28,5],[18,1],[28,1],[23,1],[15,8],[26,9],[31,1],[20,1],[18,1],[29,6],[29,3],[19,5],[23,5],[24,1],[29,2],[16,5],[34,2],[28,3],[21,6],[23,7],[31,8],[22,5],[34,6],[31,1],[28,1],[29,9],[23,4],[31,4],[20,7],[28,2],[25,4],[22,9],[31,1],[34,7],[22,9],[29,6],[27,1],[31,8],[32,7],[24,8],[28,5],[22,7],[23,1],[16,6],[21,3],[16,6],[33,3],[18,5],[24,5],[28,9],[26,2],[27,7],[20,1],[22,4],[25,9],[25,5],[19,8],[25,1],[34,6],[23,7],[33,4],[22,2],[33,1],[29,4],[32,4],[22,4],[16,5],[20,5],[31,7],[23,4],[30,7],[17,4],[29,5],[23,1],[29,1],[24,8],[15,9],[15,2],[29,5],[30,2],[22,9],[25,2],[26,2],[24,7],[25,9],[19,3],[19,1],[34,3],[20,9],[23,4],[28,6],[28,8],[32,6],[22,1],[24,1],[30,4],[33,3],[34,8],[18,9],[30,6],[15,7],[26,7],[32,2],[26,9],[34,8],[33,7],[15,4],[24,7],[20,7],[19,1],[29,4],[19,5],[18,8],[21,9],[17,2],[16,3],[18,8],[22,9],[15,9],[33,7],[34,4],[17,7],[17,1],[21,6],[30,2],[24,4],[29,8],[32,6],[25,2],[25,1],[31,6],[31,8],[29,6],[25,2],[34,5],[29,4],[31,5],[15,3],[20,5],[21,2],[25,8],[0,0]]\n"
            + "};";

    public static void main(String[] args)
    {
        try
        {
            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray stationsData = obj.getJSONArray("data");

            System.out.println("No. of Stations : " + stationsData.length());

            for (int i = 0; i < stationsData.length(); i++)
            {
                JSONArray jsonStation = stationsData.getJSONArray(i);
                // Add each station dataObject to the stations List
                stationsList.add(new Station(jsonStation.getInt(0), jsonStation.getInt(1)));
            }
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        // for each station find the accessible stations
        HashMap<Integer, ArrayList<AccessibleStation>> stationsWithAccess = new HashMap<>();
        for (int i = 0; i < stationsList.size(); i++)
        {
            stationsWithAccess.put(i, getBestAccessibleStation(i));
        }

        int count = 0;
        for (Map.Entry<Integer, ArrayList<AccessibleStation>> entrySet : stationsWithAccess.entrySet())
        {
            System.out.println("\n Station : " + entrySet.getKey());
            System.out.println("Battery At station : " + stationsList.get(count).getBatteryAtStation());
            System.out.print("Accessible Stations : ");
            for (AccessibleStation accessibleStation : entrySet.getValue())
            {
                if (accessibleStation != null)
                {
                    System.out.print(accessibleStation.getStationIndex() + "(" + accessibleStation.getBatteryUsed() + "), ");
                }
            }
            System.out.print("\n");
            System.out.print("____________________________________________________");

            count++;
        }

        // Create a search graph 
        SearchGraph graph = new SearchGraph(stationsList.size());
        for (Map.Entry<Integer, ArrayList<AccessibleStation>> entrySet : stationsWithAccess.entrySet())
        {
            // Add edges for each station by getting the accessible stations
            for (AccessibleStation accessibleStation : entrySet.getValue())
            {
                if (accessibleStation != null)
                {
                    graph.addEdge(entrySet.getKey(), accessibleStation.getStationIndex());
                }
            }
        }

        int sourceStation = 0;
        int destinationStation = stationsList.size() - 1;
        System.out.println("\nFollowing are all different paths from " + sourceStation + " to " + destinationStation);
        graph.findAllPaths(sourceStation, destinationStation);

        System.out.println("++++++++++++++++++++++++++++++++++++");
        System.out.println("BEST PATH : " + graph.getBestPath());
    }

    private static ArrayList<AccessibleStation> getBestAccessibleStation(int stationIndex)
    {
        ArrayList<AccessibleStation> accessibleStations = new ArrayList<>();
        // Get the zeroth station
        Station originalStation = stationsList.get(stationIndex);
        AccessibleStation bestAccessibleStation = null;

        try
        {
            // current station index is equal to the original station index at first
            int currentStationIndex = stationIndex;
            Station previousStation = originalStation;
            int distanceToCurrentStation = 0;

            if (originalStation.getBatteryAtStation() == 0)
            {
                return accessibleStations;
            }

            while (currentStationIndex != -1)
            {
                currentStationIndex++;

                if (currentStationIndex == stationsList.size())
                {
                    accessibleStations.add(bestAccessibleStation);
                    break;
                }

                // accumulate distances
                distanceToCurrentStation = distanceToCurrentStation + previousStation.getDistanceToNextStation();

                // check if the original station can access the current station with its battery capacity with the accumulated distance
                // and if so add it to the accessibleStationsList
                if (distanceToCurrentStation <= originalStation.getBatteryAtStation())
                {
                    // set the current station as the previous station for taking the distance to the next station
                    previousStation = stationsList.get(currentStationIndex);
                    if (previousStation.getBatteryAtStation() >= previousStation.getDistanceToNextStation())
                    {
                        bestAccessibleStation = new AccessibleStation(currentStationIndex, distanceToCurrentStation);
                    }

                } else
                {
                    accessibleStations.add(bestAccessibleStation);
                    currentStationIndex = -1;
                }
            }
        } catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return accessibleStations;
    }
}

class Station
{

    private int batteryAtStation;
    private int distanceToNextStation;

    public Station(int batteryAtStation, int distanceToNextStation)
    {
        this.batteryAtStation = batteryAtStation;
        this.distanceToNextStation = distanceToNextStation;
    }

    public int getBatteryAtStation()
    {
        return batteryAtStation;
    }

    public void setBatteryAtStation(int batteryAtStation)
    {
        this.batteryAtStation = batteryAtStation;
    }

    public int getDistanceToNextStation()
    {
        return distanceToNextStation;
    }

    public void setDistanceToNextStation(int distanceToNextStation)
    {
        this.distanceToNextStation = distanceToNextStation;
    }
}

class AccessibleStation
{

    private int stationIndex;
    private int batteryUsed;

    public AccessibleStation(int stationIndex, int batteryUsed)
    {
        this.stationIndex = stationIndex;
        this.batteryUsed = batteryUsed;
    }

    public int getStationIndex()
    {
        return stationIndex;
    }

    public void setStationIndex(int stationIndex)
    {
        this.stationIndex = stationIndex;
    }

    public int getBatteryUsed()
    {
        return batteryUsed;
    }

    public void setBatteryUsed(int batteryUsed)
    {
        this.batteryUsed = batteryUsed;
    }
}
