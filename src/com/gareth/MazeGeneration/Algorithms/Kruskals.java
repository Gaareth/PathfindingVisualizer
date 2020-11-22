package com.gareth.MazeGeneration.Algorithms;

import com.gareth.Graph;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Node;
import com.gareth.mainFrame;

import java.util.HashSet;
import java.util.Set;

public class Kruskals extends MazeGenerator {
    public Kruskals(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }

    @Override
    public void generateMaze(Graph graph, boolean diagonal) throws InterruptedException {
        super.generateMaze(graph, diagonal);
        initMaze();

        Set<Node> edges = new HashSet<>();
        Set<Set> superSet = new HashSet<>();
        for (Node n : graph.getGraph()) {
            Set<Node> set = new HashSet<>();
            set.add(n);
            superSet.add(set);
        }

        while (!edges.isEmpty()) {
            Node n = edges.iterator().next();
        }
    }
}
