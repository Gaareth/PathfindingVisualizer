package com.gareth.Listeners;

import com.gareth.Node;
import com.gareth.mainFrame;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInputHandler extends MouseAdapter {

    private mainFrame.DrawPanel drawPanel;

    public MouseInputHandler(mainFrame.DrawPanel drawPanel) {
        this.drawPanel = drawPanel;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        Node newNode = mainFrame.getSelectedNode(e.getX(), e.getY());
        if (newNode != null) {
            newNode = newNode.getNodeByCoord(mainFrame.graph.getGraph());

            if (mainFrame.blockNodeMode && newNode != null) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mainFrame.graph.updateNode(newNode, Node.NodeType.WallNode);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    mainFrame.graph.updateNode(newNode, Node.NodeType.CellNode);
                }
            }
        }
    }


    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 2) {
            mainFrame.blockNodeMode = !mainFrame.blockNodeMode;
            return;
        } // activated by middle click

        //printNodeMap(graph);

        Node newNode = mainFrame.getSelectedNode(mouseEvent.getX(), mouseEvent.getY()).getNodeByCoord(mainFrame.graph.getGraph());

        // outside maze
        if (newNode == null)
            return;

        //System.out.println("Selected node:" + newNode.getNodeInfo() + "\n");


        if (mouseEvent.getButton() == 1) { // left

            if (!mainFrame.blockNodeMode) {
                mainFrame.graph.addStartNode(newNode);
            }else {
                mainFrame.graph.updateNode(newNode, Node.NodeType.WallNode);
            }
        }else if (mouseEvent.getButton() == 3) { // right
            if (!mainFrame.blockNodeMode) {
                mainFrame.graph.addEndNode(newNode);

                // Update found path from known distances
                if (mainFrame.graph.getStartNode() != null) {
                    mainFrame.graph.updateNodesByType(new Node.NodeType[]{Node.NodeType.FoundPath}, Node.NodeType.VisitedNode);
                    Thread t1 = new Thread(() -> {
                        try {
                            mainFrame.getSelectedPathfindingAlgorithm().reconstructPath(newNode, mainFrame.graph);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    t1.start();
                }

            }else {
                mainFrame.graph.updateNode(newNode, Node.NodeType.CellNode);
            }
        }
    }


}


