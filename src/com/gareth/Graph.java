package com.gareth;

import java.util.*;

public class Graph {

    private Node startNode, endNode;
    private Set<Node> nodes;
    private Set<Node> visitedNodes;
    private Set<Node> wallNodes;
    private Set<Node> cellNodes;

    private int width;
    private int height;

    private int gridSize;

    private final mainFrame.DrawPanel drawPanel;

    public Graph(mainFrame.DrawPanel drawPanel, int gridSize) {
        this.drawPanel = drawPanel;
        this.gridSize = gridSize;

        nodes = new HashSet<>();
    }

    public Set<Node> getGraph() {
        return nodes;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void getEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public void clear() {
        nodes.clear();
    }

    public int getWidth() {
        Node maxNodeX = this.getGraph().stream().max(Comparator.comparingInt(Node::getX)).get();
        this.width = maxNodeX.getX();
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        Node maxNodeY = this.getGraph().stream().max(Comparator.comparingInt(Node::getY)).get();
        this.height = maxNodeY.getY();
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void resetConnections() {
        for (Node n : nodes) {
            n.setDistanceToParent(Double.POSITIVE_INFINITY);
            n.setParentNode(null);
        }
    }

    public void updateNode(Node n, Node.NodeType newType) {
        //Remove nodes
        if(newType != Node.NodeType.StartNode && newType != Node.NodeType.EndNode) {
            if (n.sharesSameLocation(startNode)) {
                nodes.remove(startNode);
                startNode = null;
            }
            else if (n.sharesSameLocation(endNode)) {
                nodes.remove(endNode);
                endNode = null;
            }
        }

        // If a new wall gets placed all known distances should basically treated as useless
        if ((newType == Node.NodeType.WallNode || newType == Node.NodeType.CellNode)) {
            if(n.getNodeType() == Node.NodeType.VisitedNode)
                resetConnections();
        }

        n.setNodeType(newType);
        nodes.add(n);
        drawPanel.repaint();
    }

    public void addStartNode(Node newStartNode) {
        if (startNode != null) {
            Node startNodeRef = startNode.getNodeByCoord(nodes);
            if (startNodeRef != null)
                updateNode(startNodeRef, Node.NodeType.CellNode);
        }
        startNode = newStartNode;
        updateNode(startNode.getNodeByCoord(nodes), Node.NodeType.StartNode);
    }


    public void addEndNode(Node newEndNode) {
        if (endNode != null) {
            Node endNodeRef = endNode.getNodeByCoord(nodes);
            if (endNodeRef != null)
                updateNode(endNodeRef, Node.NodeType.CellNode);
        }
        endNode = newEndNode;
        updateNode(endNode.getNodeByCoord(nodes), Node.NodeType.EndNode);
    }

    public void addNode(Node n) {
        // Check if graph already contains nodes
        Node containingNode = n.getNodeByCoord(nodes);

        // Remove old node if already exists
        if (containingNode != null) {
            nodes.remove(containingNode);
        }
        nodes.add(n);
        drawPanel.repaint();
    }

    public void printGraph() {
        System.out.println("\n\n================================");
        System.out.println("Size:" + getGraph().size());
        for (Node n : getGraph()) {
            System.out.println(n.getNodeInfo());
        }
        System.out.println("================================\n\n");
    }

    public Node getNodeByCoord(int x, int y) {
        for (Node node : nodes) {
            if(x == node.getX() && y == node.getY()) {
                return node;
            }
        }
        return null;
    }

    public Set<Node> getNodesByType(Node.NodeType type) {
        Set<Node> nodesByType = new HashSet<Node>();
        for (Node n : nodes) {
            if (n.getNodeType() == type)
                nodesByType.add(n);
        }
        return nodesByType;
    }

    public void clearPathRemains() {
        // Remove nodes created by solving a path
        clearNodesByType(new Node.NodeType[]
                {
                        Node.NodeType.FoundPath,
                        Node.NodeType.CurrentNode,
                        Node.NodeType.VisitedNode
                });
    }

    public void clearNodesByType(Node.NodeType[] nodeTypesToClear) {
        // Remove nodes by chosen type
        updateNodesByType(nodeTypesToClear, Node.NodeType.CellNode);
    }

    public Node getRandomNode() {
        return new ArrayList<>(getGraph()).get(new Random().nextInt(getGraph().size()));
    }

    public void updateNodesByType(Node.NodeType[] nodeTypesToClear, Node.NodeType updatedNodeType) {
        ArrayList<Node.NodeType> pathSolvingNodes = new ArrayList<>(Arrays.asList(
                nodeTypesToClear));
        for (Node n : this.getGraph()) {
            if (pathSolvingNodes.contains(n.getNodeType())) {
                n.setNodeType(updatedNodeType);
            }
        }
        drawPanel.repaint();
    }

}
