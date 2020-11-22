package com.gareth.MazeGeneration.Algorithms;

import com.gareth.Graph;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Node;
import com.gareth.mainFrame;

public class RecursiveDivision extends MazeGenerator {
    public RecursiveDivision(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }

    public Direction chooseDirection(int width, int height) {
        if (width > height) {
            return Direction.VERTICAL;
        }else if(height > width) {
            return Direction.HORIZONTAL;
        }else {
            return Direction.RANDOM;
        }
    }

    public void horizontalBorder(int startX, int endX, int startY, int endY) throws InterruptedException {
        for (int i = startX; i <= endX; i+=graph.getGridSize()) {
            Node n = graph.getNodeByCoord(i, startY);
            graph.updateNode(n, Node.NodeType.WallNode);
        }

        // Upper
        //divide(minColumn, maxColumn, minRow, randomY-(2*graph.getGridSize()));
        //Lower
        //divide(minColumn, maxColumn, randomY+(2*graph.getGridSize()), maxRow);
    }

    public void verticalBorder(int startX, int endX, int startY, int endY) throws InterruptedException {
        // Vertical Walls
        for (int j = startY; j <= endY; j+=graph.getGridSize()) {
            Node n = graph.getNodeByCoord(startX, j);
            graph.updateNode(n, Node.NodeType.WallNode);
        }

        // Left
        //divide(minColumn, randomX-(2*graph.getGridSize()), minRow, maxRow);
        //Right
        //divide(randomX+(2*graph.getGridSize()), maxColumn, minRow, maxRow);
    }

    public void carveHoleHorizontal(int xStart, int xDivide, int yDivide) {
        int xEnd = xDivide;
        if (xStart > xEnd)
            return;
        int randomX = ((getRandomBetween(xStart, xEnd)) / graph.getGridSize()) * graph.getGridSize();
        Node randomHorizontalHole1 = graph.getNodeByCoord(randomX, yDivide);
        graph.updateNode(randomHorizontalHole1, Node.NodeType.CellNode);
    }

    public void carveHoleVertical(int yStart, int yDivide, int xDivide) {
        int yEnd = yDivide;
        if (yStart > yEnd)
            return;
        int randomY = ((getRandomBetween(yStart, yEnd)) / graph.getGridSize()) * graph.getGridSize();
        Node randomHorizontalHole1 = graph.getNodeByCoord(xDivide, randomY);
        graph.updateNode(randomHorizontalHole1, Node.NodeType.CellNode);
    }

    public void divide(int xStart, int xEnd, int yStart, int yEnd) throws InterruptedException {
        int width = xEnd - xStart;
        int height = yEnd - yStart;


        if (width >= (2*graph.getGridSize()) && height >= (2*graph.getGridSize())) {
            Thread.sleep(sleepTime);

            //System.out.println("xStart: " + xStart + "xEnd: " + xEnd + "width: " + width);
            //System.out.println("yStart: " + yStart + "yEnd: " + yEnd + " height: " + height);

            Direction orientation = chooseDirection(width, height);
            //orientation = Direction.HORIZONTAL;

            //int xDivide = getRandomBetween(minColumn+(2*graph.getGridSize()), maxColumn-(2*graph.getGridSize()));
            int xDivide = xStart + (width / 2);
            xDivide = (xDivide/graph.getGridSize())*graph.getGridSize();

            //int yDivide = getRandomBetween(minRow+(2*graph.getGridSize()), maxRow-(2*graph.getGridSize()));
            int yDivide = yStart + (height / 2);
            yDivide = (yDivide/graph.getGridSize())*graph.getGridSize();

            horizontalBorder(xStart, xEnd, yDivide, yEnd);
            verticalBorder(xDivide, xEnd, yStart, yEnd);

            // Random "Hole"s :^D

            //Left from Vertical on horizontal wall path
            carveHoleHorizontal(xStart, xDivide-graph.getGridSize(), yDivide);


            // Right from Vertical on horizontal wall path
            carveHoleHorizontal(xDivide+graph.getGridSize(), xEnd, yDivide);


            // Above Horizontal on Vertical
            carveHoleVertical(yStart, yDivide-graph.getGridSize(), xDivide);

            // Below Horizontal on Vertical
            carveHoleVertical(yDivide+graph.getGridSize(), yEnd, xDivide);



            //Above horizontal
            //Left
            divide(xStart, xDivide-graph.getGridSize(), yStart, yDivide-graph.getGridSize());
            //Right
            divide(xDivide+graph.getGridSize(), xEnd, yStart, yDivide-graph.getGridSize());

            //Below Horizontal
            divide(xStart, xDivide-graph.getGridSize(), yDivide+graph.getGridSize(), yEnd);
            divide(xDivide+graph.getGridSize(), xEnd, yDivide+graph.getGridSize(), yEnd);

            /*
            if (orientation == Direction.HORIZONTAL) {
                horizontalBorder(minColumn, maxColumn, minRow, maxRow);
            } else {
                verticalBorder(minColumn, maxColumn, minRow, maxRow);
            }*/
        }
        //divide(orientation == 0 ? 1 : 0);

    }

    public void generateMaze(Graph graph, boolean diagonalGen) throws InterruptedException {
        super.generateMaze(graph, diagonalGen);
        divide(0, graph.getWidth(), 0, graph.getHeight());
        System.out.println("end maze");
    }

}
