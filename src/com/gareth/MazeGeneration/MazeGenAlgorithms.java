package com.gareth.MazeGeneration;

import com.gareth.MazeGeneration.Algorithms.DepthFirst;
import com.gareth.MazeGeneration.Algorithms.Ellers;
import com.gareth.MazeGeneration.Algorithms.Prims;
import com.gareth.MazeGeneration.Algorithms.RecursiveDivision;
import com.gareth.Pathfinding.Algorithms.AStar;
import com.gareth.Pathfinding.Algorithms.Dijkstra;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.mainFrame;

import java.util.ArrayList;

public class MazeGenAlgorithms {
    public ArrayList<MazeGenerator> mazeGenerationAlgorithms = new ArrayList<>();

    public MazeGenAlgorithms(mainFrame.DrawPanel drawPanel) {
        mazeGenerationAlgorithms.add(new DepthFirst(drawPanel));
        mazeGenerationAlgorithms.add(new Prims(drawPanel));
        mazeGenerationAlgorithms.add(new RecursiveDivision(drawPanel));
        mazeGenerationAlgorithms.add(new Ellers(drawPanel));

    }


    public MazeGenerator getAlgorithmByName(String name) {
        for (MazeGenerator p : mazeGenerationAlgorithms) {
            if (name.equalsIgnoreCase(p.getClass().getSimpleName())) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<MazeGenerator> getAlgorithms() {
        return mazeGenerationAlgorithms;
    }

    public ArrayList<String> getAlgorithmNames() {
        ArrayList<String> algorithmNames = new ArrayList<>();
        for (MazeGenerator p : mazeGenerationAlgorithms) {
            algorithmNames.add(p.getClass().getSimpleName());
        }
        return algorithmNames;
    }
}
