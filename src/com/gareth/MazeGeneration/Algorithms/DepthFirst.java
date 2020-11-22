package com.gareth.MazeGeneration.Algorithms;

import com.gareth.Graph;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Node;
import com.gareth.mainFrame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DepthFirst extends MazeGenerator {
    public DepthFirst(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }

    public void generateMaze(Graph graph, boolean diagonalGen) throws InterruptedException {
        super.generateMaze(graph, diagonalGen);
        initMaze();
        //graph = graph.getGraph();

        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> stack = new HashSet<>();

        Node currentNode = graph.getGraph().iterator().next();
        graph.updateNode(currentNode, Node.NodeType.CurrentNode);

        visitedNodes.add(currentNode);
        stack.add(currentNode);

        // while unvisited nodes left
        while (!stack.isEmpty()) {
            graph.updateNode(currentNode, Node.NodeType.CurrentNode);

            Set<Node> adjacentNodes = currentNode.calcNeighbourNodes(graph.getGraph(), 2, diagonalGen);

            Set<Node> unVisitedNeighbours = new HashSet<>();
            for(Node adjacentNode : adjacentNodes) {
                if (!visitedNodes.contains(adjacentNode) && adjacentNode != null) {
                    unVisitedNeighbours.add(adjacentNode);

                    graph.updateNode(adjacentNode, Node.NodeType.UnvisitedNeighbour);

                    Thread.sleep(sleepTime/3);
                }
            }

            if (unVisitedNeighbours.size() > 0) {
                // push current one to stack for backtracking
                stack.add(currentNode);

                // get a random neighbour cell
                Random r = new Random();
                Node randomNeighbour = new ArrayList<>(unVisitedNeighbours).get(r.nextInt(unVisitedNeighbours.size()));

                // remove wall between current and random neighbour cell
                // TODO: get diagonal between node
                Node inBetween = currentNode.getNodeBetween(randomNeighbour);
                //System.out.println(currentNode);
                //System.out.println(randomNeighbour);
                //System.out.println(inBetween);
                inBetween = inBetween.getNodeByCoord(graph.getGraph());


                // update current node to normal node
                graph.updateNode(currentNode, Node.NodeType.CellNode);

                Thread.sleep(sleepTime/3);

                currentNode = randomNeighbour;

                visitedNodes.add(randomNeighbour);

                // add node between and random node to graph as cellnode
                graph.updateNode(randomNeighbour, Node.NodeType.CellNode);

                Thread.sleep(sleepTime/3);


                // Is null if diagonal chooses a unvisitedNeighbour and a already visited Cell nodes lies inBetween
                // This node then is not in the graph?? idk why
                if (inBetween != null) {
                    graph.updateNode(inBetween, Node.NodeType.CellNode);
                }


            }else if (stack.iterator().hasNext()) {
                //reached a dead end
                graph.updateNode(currentNode, Node.NodeType.CellNode);

                currentNode = stack.iterator().next();
                stack.remove(currentNode);
            }
        }
        System.out.println("Generated Maze");

    }
}
