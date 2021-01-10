package com.gareth.Pathfinding.Algorithms;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.mainFrame;

import java.util.*;

public class DFS extends Pathfinding {

    Boolean diagonalSearch;
    Graph graph;
    PriorityQueue<Node> priorityQueue;
    Node startNode, endNode;

    public DFS(mainFrame.DrawPanel drawPanel, String name) {
        super(drawPanel, name);
    }


    public void recursiveSearch(Node n) throws InterruptedException {
        activeNode = n;
        if (activeNode.sharesSameLocation(endNode) || n == endNode)
            return;

        graph.updateNode(activeNode, Node.NodeType.CurrentNode);

        visitedNodes.add(activeNode);

        Thread.sleep(sleepTime);
        graph.updateNode(activeNode, Node.NodeType.VisitedNode);

        Set<Node> adjacentNodes = activeNode.calcNeighbourNodes(this.graph.getGraph(), 1, this.diagonalSearch);

        /*
        for (Node adjacentNode : adjacentNodes) {
            if (adjacentNode != null)
                graph.updateNode(adjacentNode, Node.NodeType.UnvisitedNeighbour);
            Thread.sleep(sleepTime);
        }*/

        for (Node adjacentNode : adjacentNodes) {
            if (!visitedNodes.contains(adjacentNode) && adjacentNode != null && adjacentNode.getNodeType() != Node.NodeType.WallNode) {
                adjacentNode.setParentNode(activeNode);
                Double d = activeNode.getDistanceToParent() + activeNode.getDistanceToNode(adjacentNode)/graph.getGridSize();
                adjacentNode.setDistanceToParent(1D);

                if (!adjacentNode.sharesSameLocation(startNode) && !adjacentNode.sharesSameLocation(endNode)) {
                    this.graph.updateNode(adjacentNode, Node.NodeType.UnvisitedNeighbour);
                }
                recursiveSearch(adjacentNode);
            }
        }
    }



    public void depthFirstSearch(Node start, Node end) throws InterruptedException {
        Stack<Node> stack = new Stack<>();
        start.setDistanceToParent(0D);
        stack.push(start);

        while (!stack.isEmpty()) {
            activeNode = stack.pop();

            //if (activeNode.sharesSameLocation(end) || activeNode == end)
              //  return;

            if (!activeNode.sharesSameLocation(start) && !activeNode.sharesSameLocation(end)) {
                graph.updateNode(activeNode, Node.NodeType.CurrentNode);
            }

            Thread.sleep(sleepTime);

            if (!visitedNodes.contains(activeNode)) {
                visitedNodes.add(activeNode);

                if (!activeNode.sharesSameLocation(start) && !activeNode.sharesSameLocation(end)) {
                    graph.updateNode(activeNode, Node.NodeType.VisitedNode);
                }

                for (Node adjacentNode : activeNode.calcNeighbourNodes(this.graph.getGraph(), 1, this.diagonalSearch)) {
                    if (adjacentNode != null && !visitedNodes.contains(adjacentNode) && adjacentNode.getNodeType() != Node.NodeType.WallNode) {
                        adjacentNode.setParentNode(activeNode);
                        Double d = activeNode.getDistanceToParent() + activeNode.getDistanceToNode(adjacentNode)/graph.getGridSize();
                        adjacentNode.setDistanceToParent(d);
                        stack.push(adjacentNode);

                        if (!adjacentNode.sharesSameLocation(start) && !adjacentNode.sharesSameLocation(end)) {
                            graph.updateNode(adjacentNode, Node.NodeType.UnvisitedNeighbour);
                        }
                    }
                }
            }
            // Second time to visually update the currentNode to VisitedNode
            if (!activeNode.sharesSameLocation(start) && !activeNode.sharesSameLocation(end)) {
                graph.updateNode(activeNode, Node.NodeType.VisitedNode);
            }

        }
    }

    @Override
    public void solve(Node startNode, Node endNode, Graph graph, boolean diagonalSearch) throws InterruptedException {
        super.solve(startNode, endNode, graph, diagonalSearch);

        visitedNodes = new ArrayList<>();

        this.graph = graph;
        this.diagonalSearch = diagonalSearch;

        this.startNode = startNode;
        this.endNode = endNode;
        //startNode.setDistanceToParent(0D);
        //recursiveSearch(startNode);

        depthFirstSearch(startNode, endNode);
        activeNode = endNode;
        super.handleFoundPath();
    }
}
