package com.gareth.MazeGeneration.Algorithms;

import com.gareth.Graph;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Node;
import com.gareth.mainFrame;

import java.util.*;
import java.util.stream.Collectors;

public class Prims extends MazeGenerator {

    public Prims(mainFrame.DrawPanel drawPanel) {
        super(drawPanel);
    }

    public Set<Node> calculateFrontierCells(Node n) {
        ArrayList<Node> neighbours = (ArrayList<Node>) n.calcNeighbourNodes(graph.getGraph(), 1, false).
                stream().filter(Objects::nonNull).collect(Collectors.toList());

        int dir = getRandomBetween(0, 3);
        int dir2;
        switch (dir) {
            case 0 : dir2 = 1; break;
            case 1 : dir2 = 0; break;
            case 2 : dir2 = 3; break;
            case 3 : dir2 = 2; break;
            default:
                throw new IllegalStateException("Unexpected value: " + dir);
        }

        Node firstNode = neighbours.get(dir);
        Node oppNode = neighbours.get(dir2);

        return new HashSet<>(Arrays.asList(firstNode, oppNode));
    }

    public void generateMaze(Graph graph, boolean diagonalGen) throws InterruptedException { super.generateMaze(graph, diagonalGen);
        super.generateMaze(graph, diagonalGen);
        initMaze();


        Node randomCell = graph.getRandomNode();
        graph.updateNode(randomCell, Node.NodeType.CurrentNode);
        Thread.sleep(sleepTime/4);
        graph.updateNode(randomCell, Node.NodeType.CellNode);

        // add frontier cells
        Set<Node> walls = randomCell.calcNeighbourNodes(graph.getGraph(), 2, false).
                stream().filter(Objects::nonNull).collect(Collectors.toSet());
        walls.forEach(node -> node.setNodeType(Node.NodeType.UnvisitedNeighbour));

        while (!walls.isEmpty()) {
            // get random frontier cell
            Node randomFrontierCell = walls.iterator().next();
            graph.updateNode(randomFrontierCell, Node.NodeType.CurrentNode);

            Thread.sleep(sleepTime/2);

            // calculate visited Neighbours
            Set<Node> visitedNeighbours = new HashSet<>();
            visitedNeighbours = randomFrontierCell.calcNeighbourNodes(graph.getGraph(), 2, false).stream().
                    filter(Objects::nonNull).
                    filter(n -> n.getNodeType() == Node.NodeType.CellNode).
                    collect(Collectors.toSet());


            if (visitedNeighbours.size() > 0) {
                Random r = new Random();
                // get random visited cell node
                Node randomVisitedCell = new ArrayList<>(visitedNeighbours).get(r.nextInt(visitedNeighbours.size()));

                // open wall between frontier and visited node
                Node inBetween = randomFrontierCell.getNodeBetween(randomVisitedCell);
                inBetween = inBetween.getNodeByCoord(graph.getGraph());
                graph.updateNode(inBetween, Node.NodeType.CellNode);

                Thread.sleep(sleepTime/3);

                // set frontier cell as visited
                graph.updateNode(randomFrontierCell, Node.NodeType.CellNode);

                // add all unvisited neighbours from old frontier cell to the frontier cells
                HashSet<Node> t = randomFrontierCell.calcNeighbourNodes(graph.getGraph(), 2, false).stream().
                        filter(Objects::nonNull).
                        filter(node -> node.getNodeType() == Node.NodeType.WallNode).collect(Collectors.toCollection(HashSet::new));
                t.forEach(node -> node.setNodeType(Node.NodeType.UnvisitedNeighbour));
                walls.addAll(t);
            }

            walls.remove(randomFrontierCell);
            Thread.sleep(sleepTime/3);
        }
    }

}
