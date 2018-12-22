package com.search.algo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// adjacency list representation 
public class SearchGraph
{

    // Total No. of in the search 
    private int totalStations;

    // an arry of integer Arraylists for stations
    // Integer arraylist holds the accessible stations
    private ArrayList<Integer>[] stations;

    private ArrayList<List<Integer>> setOfPaths;

    //Constructor 
    public SearchGraph(int noOfStations)
    {
        //initialise total number of stations
        this.totalStations = noOfStations;
        setOfPaths = new ArrayList<>();
        
        // initialise adjacency list 
        initStations();
    }

    // utility method to initialise 
    // adjacency list 
    @SuppressWarnings("unchecked")
    private void initStations()
    {
        stations = new ArrayList[totalStations];

        for (int i = 0; i < totalStations; i++)
        {
            stations[i] = new ArrayList<>();
        }
    }

    // add edge from currentStation to accessibleStation
    public void addEdge(int currentStation, int accessibleStation)
    {
        // Add v to u's list. 
        stations[currentStation].add(accessibleStation);
    }

    // Prints all paths from 
    // sourceStation to the destinationStation
    public void findAllPaths(int sourceStation, int destinationStation)
    {
        // a boolean array to store if the station has been visited with reference to the current path
        boolean[] isVisited = new boolean[totalStations];
        ArrayList<Integer> pathList = new ArrayList<>();

        //add sourceStation to the path list as the starting point 
        pathList.add(sourceStation);

        //Call recursive function 
        SearchGraph.this.findAllPaths(sourceStation, destinationStation, isVisited, pathList);
    }

    // A recursive function to find all the paths from the sourceStation to destinationStation
    // isVisited[] keeps track of vertices in current path. 
    // localPathList<> stores actual vertices in the current path 
    private void findAllPaths(Integer sourceStation, Integer destinationStation,
            boolean[] isVisited,
            List<Integer> localPathList)
    {

        // Mark the current node 
        isVisited[sourceStation] = true;

        // When the sourceStation equals to the destinationStation (reached end of the path)
        if (sourceStation.equals(destinationStation))
        {
            // Add the current path to the set of path
            setOfPaths.add(localPathList.stream().collect(Collectors.toList()));

            // if match found then no need to traverse more till depth 
            isVisited[sourceStation] = false;
            return;
        }

        // Recur for all the vertices 
        // adjacent to current vertex 
        for (Integer i : stations[sourceStation])
        {
            if (!isVisited[i])
            {
                // store current node  
                // in path[] 
                localPathList.add(i);
                SearchGraph.this.findAllPaths(i, destinationStation, isVisited, localPathList);

                // remove current node 
                // in path[] 
                localPathList.remove(i);
            }
        }

        // Mark the current node 
        isVisited[sourceStation] = false;
    }

    public List<Integer> getBestPath()
    {
        System.out.println("Total no. of Paths : " + setOfPaths.size());
        List<Integer> bestPath = setOfPaths.stream().min(Comparator.comparing(List::size)).get();
        return bestPath;
    }
}
