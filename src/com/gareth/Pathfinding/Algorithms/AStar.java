package com.gareth.Pathfinding.Algorithms;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.mainFrame;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar extends Pathfinding {
    public AStar(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }

    @Override
    public void solve(Node startNode, Node endNode, Graph graph, boolean diagonalSearch) throws InterruptedException {
        super.solve(startNode, endNode, graph, diagonalSearch);

        // Create a Priority Queue with a custom Comparator
        PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>();

        visitedNodes = new ArrayList<>();
        foundPath = new ArrayList<>();

        // Init nodes to Queue
        for (Node node: graph.getGraph()) {
            if (node.getNodeType() != Node.NodeType.WallNode) {
                Double initialValue = node.sharesSameLocation(startNode) ? 0.0 : Double.POSITIVE_INFINITY;
                node.setDistanceToParent(initialValue);
                node.setParentNode(null);
                priorityQueue.offer(node);
            }
        }

        // Set active node to first node in queue
        activeNode = priorityQueue.iterator().next();

        System.out.println("Nodes in Maze: " + priorityQueue.size());

        while (!priorityQueue.isEmpty()) {
            activeNode = priorityQueue.poll();

            // a new node should always have a distance to its predecessor lower than inf
            if (activeNode.getDistanceToParent() == Double.POSITIVE_INFINITY) {
                break;
            }

            if (activeNode.sharesSameLocation(endNode)) {
                super.handleFoundPath();
                return;
            }

            // Render purposes
            if (!activeNode.sharesSameLocation(startNode)) {
                graph.updateNode(activeNode, Node.NodeType.CurrentNode);
            }
            Thread.sleep(sleepTime);


            Set<Node> neighbours = activeNode.calcNeighbourNodes(graph.getGraph(), 1, diagonalSearch);

            int validNeighbours = 0;
            for (Node neighbourNode: neighbours) {
                if (priorityQueue.contains(neighbourNode) && !visitedNodes.contains(neighbourNode)) {
                    validNeighbours+=1;

                    Double distanceToNeighbourNode = activeNode.getDistanceToParent() + activeNode.getDistanceToNode(neighbourNode)/mainFrame.gridSize
                            + activeNode.getDistanceToNode(endNode);

                    if (distanceToNeighbourNode < neighbourNode.getDistanceToParent()) {
                        priorityQueue.remove(neighbourNode);
                        neighbourNode.setParentNode(activeNode);
                        neighbourNode.setDistanceToParent(distanceToNeighbourNode);
                        priorityQueue.offer(neighbourNode);
                    }
                }
            }

            // no neighbours no path; applies only for the first node
            if (validNeighbours <= 0 && activeNode.sharesSameLocation(startNode)) {
                System.out.println("No neighbours found");
                break;
            }

            //priorityQueue.remove(activeNode);
            visitedNodes.add(activeNode);

            // Dont set start node to visited for visual purposes
            if (!activeNode.sharesSameLocation(startNode)) {
                graph.updateNode(activeNode, Node.NodeType.VisitedNode);
                drawPanel.repaint();
            }
        }
        // End
        // for rendering purposes
        activeNode = null;
        System.out.println("No path found");

    }
}

