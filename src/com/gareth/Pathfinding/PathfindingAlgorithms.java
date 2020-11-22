package com.gareth.Pathfinding;

import java.util.ArrayList;

import com.gareth.Pathfinding.Algorithms.AStar;
import com.gareth.Pathfinding.Algorithms.Dijkstra;
import com.gareth.mainFrame;

public class PathfindingAlgorithms {

    public ArrayList<Pathfinding> pathfindingAlgorithms = new ArrayList<>();

    public PathfindingAlgorithms(mainFrame.DrawPanel drawPanel) {
        pathfindingAlgorithms.add(new Dijkstra(drawPanel));
        pathfindingAlgorithms.add(new AStar(drawPanel));

    }


    public Pathfinding getAlgorithmByName(String name) {
        for (Pathfinding p : pathfindingAlgorithms) {
            if (name.equalsIgnoreCase(p.getClass().getSimpleName())) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Pathfinding> getAlgorithms() {
        return pathfindingAlgorithms;
    }

    public ArrayList<String> getAlgorithmNames() {
        ArrayList<String> algorithmNames = new ArrayList<>();
        for (Pathfinding p : pathfindingAlgorithms) {
            algorithmNames.add(p.getClass().getSimpleName());
        }
        return algorithmNames;
    }

}
