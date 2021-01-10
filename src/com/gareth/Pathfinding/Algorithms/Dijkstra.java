package com.gareth.Pathfinding.Algorithms;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.mainFrame;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra extends Pathfinding {

    PriorityQueue<Node> priorityQueue;
    Boolean diagonalSearch;
    Graph graph;

    public Dijkstra(mainFrame.DrawPanel drawPanel, String name) {
        super(drawPanel, name);
    }


    // Updates values of nodes
    public void calculateNodeValues(Node currentNode) {
        Set<Node> neighbours = currentNode.calcNeighbourNodes(this.graph.getGraph(), 1, this.diagonalSearch);

        int validNeighbours = 0;
        for (Node neighbourNode: neighbours) {
            if (priorityQueue.contains(neighbourNode) && !visitedNodes.contains(neighbourNode)) {
                validNeighbours+=1;

                //System.out.println("Neighbour Node: " + getNodeInfo(neighbourNode));

                Double distanceToNeighbourNode = activeNode.getDistanceToParent() + activeNode.getDistanceToNode(neighbourNode)/mainFrame.gridSize;

                //System.out.println("Distance: " + distanceToNeighbourNode.toString());
                if (distanceToNeighbourNode < neighbourNode.getDistanceToParent()) {
                    //System.out.println("Updating: " + getNodeInfo(neighbourNode));
                    //System.out.println(neighbourNode.getDistanceToParent() + " => " + distanceToNeighbourNode.toString());

                    priorityQueue.remove(neighbourNode);
                    neighbourNode.setParentNode(activeNode);
                    neighbourNode.setDistanceToParent(distanceToNeighbourNode);
                    priorityQueue.offer(neighbourNode);
                }
            }
        }
    }

    @Override
    public void solve(Node startNode, Node endNode, Graph graph, boolean diagonalSearch) throws InterruptedException {
        super.solve(startNode, endNode, graph, diagonalSearch);
        //HashMap<Node, Double> priorityQueue = new HashMap(); // Node: distance

        // Create a Priority Queue with a custom Comparator
        priorityQueue = new PriorityQueue<Node>();

        visitedNodes = new ArrayList<>();
        foundPath = new ArrayList<>();

        // Init nodes to Queue
        super.initPriorityQueue(priorityQueue, startNode);
        this.graph = graph;
        this.diagonalSearch = diagonalSearch;
        // Set active node to first node in queue
        activeNode = priorityQueue.iterator().next();

        System.out.println("Nodes in Maze: " + priorityQueue.size());

        while (!priorityQueue.isEmpty()) {
            //priorityQueue = sortMapByValue(priorityQueue);
            //printNodeMap(priorityQueue);

            //mainFrame.labelPriorityQueue.setText(str);

            activeNode = priorityQueue.poll();
            //System.out.println("Current Node: " + getNodeInfo(activeNode) + ":" + activeNode.getDistanceToParent());


            if (activeNode.sharesSameLocation(endNode)) {
                super.handleFoundPath();
                return;
            }else if (activeNode.getDistanceToParent() == Double.POSITIVE_INFINITY) {
                // a new node should always have a distance to its predecessor lower than inf
                break;
            }

            // Render purposes
            if (!activeNode.sharesSameLocation(startNode)) {
                graph.updateNode(activeNode, Node.NodeType.CurrentNode);
            }
            Thread.sleep(sleepTime);


           calculateNodeValues(activeNode);


            //priorityQueue.remove(activeNode);
            visitedNodes.add(activeNode);

            // Dont set start node to visited for visual purposes
            if (!activeNode.sharesSameLocation(startNode)) {
                graph.updateNode(activeNode, Node.NodeType.VisitedNode);
            }
        }

        // for rendering purposes
        activeNode = null;
        System.out.println("No path found");
        mainFrame.changeStatus("No valid path found");
        mainFrame.endTime = System.currentTimeMillis();
        mainFrame.updateStatus(String.format("(%dms)", mainFrame.endTime-mainFrame.startTime));
    }

    public void Dijkstra(Node startNode, Node endNode, ArrayList<Node> nodes) {

    }


}
