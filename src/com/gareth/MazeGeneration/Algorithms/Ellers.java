package com.gareth.MazeGeneration.Algorithms;

import com.gareth.Graph;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Node;
import com.gareth.mainFrame;

import java.util.*;

public class Ellers extends MazeGenerator {
    public Ellers(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }


    public Set<Node> getSetFromNode(Node n, HashMap<Set<Node>, Integer> sets) {
        for (Map.Entry<Set<Node>, Integer> subSet : sets.entrySet()) {
           for (Node n2 : subSet.getKey()) {
               if (n == n2)
                   return subSet.getKey();
           }
        }
        return null;
    }

    @Override
    public void generateMaze(Graph graph, boolean diagonal) throws InterruptedException {
        super.generateMaze(graph, diagonal);
        super.initMaze();

        for (int row = 0; row < graph.getGridSize(); row+=graph.getGridSize()) {
            HashMap<Set<Node>, Integer> sets = new HashMap();
            ArrayList<Node> nodes = new ArrayList<>();

            // only every second cell
            for(int col=0; col < graph.getWidth(); col+=(graph.getGridSize()*2)) {
                Set<Node> set = new HashSet<>();

                Node n = graph.getNodeByCoord(col, row);

                n.setInfo(String.valueOf(col));
                super.drawPanel.repaint();

                set.add(n);
                sets.put(set, col);
                nodes.add(n);

            }

            Thread.sleep(5000);
            Random r = new Random();
            Node prevNode = null;
            Node currNode = null;

            for (int i=0; i < nodes.size(); i++) {
                currNode = nodes.get(i);

                System.out.println( "prevNode: " + prevNode + " curr: "+ currNode);

                if (prevNode != null) {
                    Set<Node> currSet = getSetFromNode(currNode, sets);
                    Set<Node> prevSet = getSetFromNode(prevNode, sets);

                    if (currSet != prevSet && r.nextBoolean()) {
                        System.out.println("merging sets: " + sets.get(prevSet) + " => "+ sets.get(currSet));
                        int v = sets.get(currSet);
                        currSet.addAll(prevSet);
                        sets.put(currSet, v);
                        
                        sets.remove(prevSet);
                        currNode.setInfo(String.valueOf(sets.get(prevSet)));
                        prevNode.setInfo(String.valueOf(sets.get(currSet)));

                        carvePath(prevNode, currNode);
                        System.out.println(prevNode.getNodeInfoRelative(false) + " => " + currNode.getNodeInfoRelative(false));
                    }

                }
                prevNode = currNode;
            }
        }
    }
}
