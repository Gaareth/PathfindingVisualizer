package com.gareth.Pathfinding.Algorithms;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.mainFrame;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class BFS extends Pathfinding {


    private Boolean diagonalSearch;

    public BFS(mainFrame.DrawPanel drawPanel, String name) {
        super(drawPanel, name);
    }

    public void breadthFirstSearch(Graph g, Node root, Node end) throws InterruptedException {
        Queue<Node> queue = new LinkedList<>();
        HashSet<Node> visitedNodes = new HashSet<>();

        root.setDistanceToParent(0D);

        visitedNodes.add(root);
        queue.add(root);

        while (!queue.isEmpty()) {
            activeNode = queue.poll();

            if (activeNode.sharesSameLocation(end))
                return;

            if (!activeNode.sharesSameLocation(root))
                g.updateNode(activeNode, Node.NodeType.CurrentNode);

            Thread.sleep(sleepTime);

            for (Node adjacentNode : activeNode.calcNeighbourNodes(g.getGraph(), 1, this.diagonalSearch)) {
                if (!visitedNodes.contains(adjacentNode) && adjacentNode != null && adjacentNode.getNodeType() != Node.NodeType.WallNode) {
                    adjacentNode.setParentNode(activeNode);
                    adjacentNode.setDistanceToParent(1D);  // Adjacent Node is always 1 Block away

                    visitedNodes.add(adjacentNode);
                    queue.add(adjacentNode);

                    if (!adjacentNode.sharesSameLocation(end))
                        g.updateNode(adjacentNode, Node.NodeType.VisitedNode);
                }
            }
            if (!activeNode.sharesSameLocation(root))
                g.updateNode(activeNode, Node.NodeType.VisitedNode);

        }
    }

    @Override
    public void solve(Node startNode, Node endNode, Graph graph, boolean diagonalSearch) throws InterruptedException {
        super.solve(startNode, endNode, graph, diagonalSearch);

        this.diagonalSearch = diagonalSearch;
        breadthFirstSearch(graph, startNode, endNode);

        activeNode = endNode;
        super.handleFoundPath();
    }
}
