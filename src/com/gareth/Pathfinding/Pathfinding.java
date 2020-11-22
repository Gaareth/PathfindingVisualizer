package com.gareth.Pathfinding;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.mainFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;
import java.util.List;

public class Pathfinding {

    public int sleepTime = 50;
    public int pathDrawTime = 50;

    private Graph graph;
    public Node activeNode;
    public ArrayList<Node> visitedNodes;
    public ArrayList<Node> foundPath;

    public mainFrame.DrawPanel drawPanel;


    public Pathfinding(mainFrame.DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    public void solve(Node startNode, Node endNode, Graph graph, boolean diagonalSearch) throws InterruptedException {
        this.graph = graph;
        mainFrame.menuBarStatusBar.setText("Status: Solving Path");
    }

    public void handleFoundPath() throws InterruptedException {
        System.out.println("Found path with distance: " + activeNode.getDistanceToParent() +  " blocks");
        System.out.println(getNodeInfo(activeNode));
        System.out.println(activeNode.getNodeType());

        graph.updateNode(activeNode, Node.NodeType.EndNode);
        drawPanel.repaint();

        mainFrame.menuBarStatusBar.setText(String.format("Status: Found Path (%s blocks distance)", activeNode.getDistanceToParent()));

        mainFrame.endTime = System.currentTimeMillis();
        mainFrame.updateStatus(String.format("(%dms)", mainFrame.endTime-mainFrame.startTime));

        reconstructPath(activeNode, graph);
    }

    public void calcDistanceToNeighbours(AbstractCollection<Node> collection) {
    }

    // Initializes the Queue with start values to their parents
    public void initPriorityQueue(PriorityQueue<Node> priorityQueue, Node startNode) {
        // Init nodes to Queue
        for (Node node: graph.getGraph()) {
            if (node.getNodeType() != Node.NodeType.WallNode) {
                Double initialValue = node.sharesSameLocation(startNode) ? 0.0 : Double.POSITIVE_INFINITY;
                node.setDistanceToParent(initialValue);
                node.setParentNode(null);
                priorityQueue.offer(node);
            }
        }
    }

    // yoinked
    public static HashMap<Node, Double> sortMapByValue(HashMap<Node, Double> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<Node, Double> > list =
                new LinkedList<Map.Entry<Node, Double> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Node, Double> >() {
            public int compare(Map.Entry<Node, Double> o1,
                               Map.Entry<Node, Double> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Node, Double> temp = new LinkedHashMap<Node, Double>();
        for (Map.Entry<Node, Double> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public void printNodeMap(HashMap<Node, Double> map) {
        System.out.println("\n\n================================");
        System.out.println("Size:" + map.size());
        for (Map.Entry<Node, Double> mapEntry : map.entrySet()) {
            String infoString = String.format("(%d,%d)", mapEntry.getKey().getX(), mapEntry.getKey().getY());
            System.out.println(infoString + ":" + mapEntry.getValue().toString());
        }
        System.out.println("================================\n\n");
    }

    public String getNodeInfo(Node n) {
        System.out.printf("(%d,%d)", n.getX(), n.getY());
        Node parentNode = n.getParentNode();
        String parentInformation = "";
        if (parentNode != null) {
            parentInformation = "<= " + getNodeInfo(parentNode);
        }

        return String.format("(%d,%d) %s", n.getX(), n.getY(), parentInformation);
    }

    public boolean containsShare(ArrayList<Node> blockNodes, Node n) {
        for (Node blockN : blockNodes) {
            if (blockN.sharesSameLocation(n)){
                return true;
            }
        }
        return false;
    }

    public Set<Node> initNodes(Set<Node> graph) {
        Set<Node> initializedNotes = new HashSet<>();
        for (Node n : graph) {
            if (n.getNodeType() != Node.NodeType.WallNode) {
                initializedNotes.add(n);
            }
        }
        return initializedNotes;
    }
    public Node getNodeByCoord(int x, int y, Set<Node> nodes) {
        for (Node node : nodes) {
            if(x == node.getX() && y == node.getY()) {
                return node;
            }
        }
        return null;
    }

    public void reconstructPath(Node endNode, Graph graph) throws InterruptedException {
        Node parentNode = endNode.getParentNode();
        while (parentNode != null) {
            Node node = parentNode;

            parentNode = node.getParentNode();

            if (parentNode == null)
                break;

            Thread.sleep(pathDrawTime);

            graph.updateNode(node, Node.NodeType.FoundPath);
        }
        System.out.println("Constructed Solved Path");
    }

    /*
    public int getQueuePosition(Node in) {
        int c = 0;
        for (Node n : priorityQueue) {
            if (n == in)
                return c;
            c++;
        }
        return -1;
    }
     */





}
