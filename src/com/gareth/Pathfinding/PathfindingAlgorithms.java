package com.gareth.Pathfinding;

import java.util.ArrayList;

import com.gareth.Pathfinding.Algorithms.AStar;
import com.gareth.Pathfinding.Algorithms.BFS;
import com.gareth.Pathfinding.Algorithms.DFS;
import com.gareth.Pathfinding.Algorithms.Dijkstra;
import com.gareth.mainFrame;

public class PathfindingAlgorithms {

    public ArrayList<Pathfinding> pathfindingAlgorithms = new ArrayList<>();

    public PathfindingAlgorithms(mainFrame.DrawPanel drawPanel) {
        pathfindingAlgorithms.add(new Dijkstra(drawPanel, "Dijkstra"));
        pathfindingAlgorithms.add(new AStar(drawPanel, "A*"));
        pathfindingAlgorithms.add(new DFS(drawPanel, "DFS (Depth-First-Search"));
        pathfindingAlgorithms.add(new BFS(drawPanel, "BFS (Breadth-First-Search"));

    }


    public Pathfinding getAlgorithmByName(String name) {
        for (Pathfinding p : pathfindingAlgorithms) {
            if (name.equalsIgnoreCase(p.getName())) {
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
            algorithmNames.add(p.getName());
        }
        return algorithmNames;
    }

}
