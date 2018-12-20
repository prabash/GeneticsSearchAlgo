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
            + "	\"id\" : 10,\n"
            + "	\"generatedOn\" : 1,\n"
            + "	\"data\" :[[7,1],[5,5],[8,2],[1,2],[0,0]]\n"
            + "} ";

    public static void main(String[] args)
    {
        try
        {
            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray stationsData = obj.getJSONArray("data");
            for (int i = 0; i < stationsData.length(); i++)
            {
                JSONArray jsonStation = stationsData.getJSONArray(i);
                stationsList.add(new Station(jsonStation.getInt(0), jsonStation.getInt(1)));
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        HashMap<Integer, ArrayList<Integer>> stationsWithAccess = new HashMap<>();
        for (int i = 0; i < stationsList.size(); i++)
        {
            stationsWithAccess.put(i, getAccessibleStations(i));
        }

        for (Map.Entry<Integer, ArrayList<Integer>> entrySet : stationsWithAccess.entrySet())
        {
            System.out.println("\n Station : " + entrySet.getKey());
            System.out.print("Accessible Stations : ");
            for (Integer value : entrySet.getValue())
            {
                System.out.print(value + ", ");
            }
            System.out.print("\n");
            System.out.print("____________________________________________________");
        }
    }

    private static ArrayList<Integer> getAccessibleStations(int stationIndex)
    {
        ArrayList<Integer> accessibleStations = new ArrayList<>();
        Station originalStation = stationsList.get(stationIndex);

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
                break;
            }

            // accumulate distances
            distanceToCurrentStation = distanceToCurrentStation + previousStation.getDistanceToNextStation();

            // check if the original station can access the current station with its battery capacity with the accumulated distance
            // and if so add it to the accessibleStationsList
            if (distanceToCurrentStation <= originalStation.getBatteryAtStation())
            {
                accessibleStations.add(currentStationIndex);
                // set the current station as the previous station for taking the distance to the next station
                previousStation = stationsList.get(currentStationIndex);
            }
            else
            {
                currentStationIndex = -1;
            }
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
