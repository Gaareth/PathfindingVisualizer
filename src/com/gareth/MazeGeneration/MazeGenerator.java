package com.gareth.MazeGeneration;

import com.gareth.Graph;
import com.gareth.Node;
import com.gareth.mainFrame;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class MazeGenerator {

    public enum Direction {
        HORIZONTAL,
        VERTICAL,
        RANDOM
    }


    public Graph graph;

    public int sleepTime = 50;

    public mainFrame.DrawPanel drawPanel;
    public MazeGenerator(mainFrame.DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    public void drawMaze() {
        Graphics g = drawPanel.getGraphics();
        if (graph != null) {
            for (Node node : graph.getGraph()) {
                g.setColor(node.getColor());
                g.fillRect(node.getX(), node.getY(), node.getSize(), node.getSize());
                g.setColor(Color.WHITE);

                drawPanel.centerMultilineString(g, new Rectangle(node.getX(), node.getY(), node.getSize(), node.getSize()), node.getNodeInfo());
            }
        }
    }

    public void initMaze() {
        // Set all nodes as wallnodes
        for (Node n : graph.getGraph()) {
            n.setNodeType(Node.NodeType.WallNode);
            n.resetConnection();
            // TODO: this overwrite start and end :(
        }
    }

    public void generateMaze(Graph graph, boolean diagonal) throws InterruptedException {
        this.graph = graph;
        mainFrame.menuBarStatusBar.setText("Status: Generating Maze");
    }

    public void carvePath(Node n1, Node n2) {
        Node betweenNode = n1.getNodeBetween(n2);
        graph.updateNode(n1, Node.NodeType.CellNode);
        graph.updateNode(betweenNode, Node.NodeType.CellNode);
        graph.updateNode(n2, Node.NodeType.CellNode);

    }

    public int getRandomBetween(int min, int max) {
        if (min > max) {
            System.out.println(min + " " + max);
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }







}
