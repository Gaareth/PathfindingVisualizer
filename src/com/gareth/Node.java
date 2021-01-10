package com.gareth;


import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Node implements Comparable<Node> {


	public enum NodeType{
		StartNode,
		EndNode,
		CellNode,
		WallNode,
		VisitedNode,
		UnvisitedNeighbour,
		CurrentNode,
		FoundPath
	}

	private Double distanceToParent = Double.POSITIVE_INFINITY;
	private int x, y, size;
	private Node parentNode; // last visited by this node?
	private Color color;
	private Set<Node> neigbours;

	private NodeType nodeType;

	private String info;


	public static Map<NodeType, Color> nodeColorMap = Map.of(
			NodeType.StartNode, new Color(0, 255, 0),
			NodeType.EndNode, new Color(255, 0, 0),
			NodeType.WallNode, new Color(0, 0, 0),
			NodeType.CellNode, Color.LIGHT_GRAY,
			NodeType.VisitedNode, Color.LIGHT_GRAY,
			NodeType.UnvisitedNeighbour, Color.RED,
			NodeType.CurrentNode, Color.CYAN,
			NodeType.FoundPath, Color.MAGENTA
			);


	public Node(int x, int y, int size, Node parentNode, Color color, NodeType nodeType) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.parentNode = parentNode;
		this.color = color;
		this.nodeType = nodeType;
	}

	public Node(int x, int y, int size, NodeType nodeType) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.parentNode = null;
		if (nodeType != null) {
			this.color = nodeColorMap.get(nodeType);
		}
		this.nodeType = nodeType;
		this.info = nodeType.toString();
	}

	public Node(int x, int y, int size) {
		this(x,y, size, null, new Color(0,0,0), NodeType.CellNode);
	}

	public Node(int x, int y, int size, Color color) {
		this(x,y, size, null, color, NodeType.CellNode);
	}

	public double getDistanceToNode(Node n2) {
		return Math.sqrt(Math.pow(n2.getX()-this.getX(), 2) + Math.pow((n2.getY()-this.getY()), 2));
	}

	public boolean sharesSameLocation(Node n2) {
		if (n2 == null)
			return false;
		return (n2.getX() == this.getX() && n2.getY() == this.getY());
	}

	public boolean containsShare(List<Node> list) {
		for (Node n : list) {
			if (n.sharesSameLocation(this)){
				return true;
			}
		}
		return false;
	}

	public Node getNodeBetween(Node n2) {
		int maxX = Collections.max(Arrays.asList(n2.getX(), this.x));
		int maxY = Collections.max(Arrays.asList(n2.getY(), this.y));

		int newX = n2.getX() == this.x ? maxX : maxX-size;
		int newY = n2.getY() == this.y ? maxY : maxY-size;

		return new Node(newX, newY, this.size);
	}

	public Node getNodeBetweenA(Node n) {
		Set<Node> sharedNeighbourNodes = new HashSet<>();
		for (Node nn : this.getNeighbourNodes()) {
			for (Node nn2 : n.getNeighbourNodes()) {
				if(nn2.sharesSameLocation(nn)) {
					sharedNeighbourNodes.add(nn2);
				}
			}
		}

		if (sharedNeighbourNodes.size() == 1) {
			return sharedNeighbourNodes.iterator().next();
		}else {
			return null;
		}

	}

	public void addNeighbourNode(Node neighbourNode) {
		this.neigbours.add(neighbourNode);
	}

	public Node getNodeByCoord(Set<Node> nodes) {
		return getNodeByCoord(this.x,this.y,nodes);
	}

	public Node getNodeByCoord(int x, int y, Set<Node> nodes) {
		for (Node node : nodes) {
			if(x == node.getX() && y == node.getY()) {
				return node;
			}
		}
		return null;
	}

	public Set<Node> calcNeighbourNodes(Set<Node> graph, int sizeMultiplier, boolean diagonal) {
		Set<Node> neighbours = new HashSet<>();

		Node topNeighbour = getNodeByCoord(x, y-(size*sizeMultiplier), graph);
		Node bottomNeighbour = getNodeByCoord(x, y+(size*sizeMultiplier), graph);
		Node leftNeighbour = getNodeByCoord(x-(size*sizeMultiplier), y, graph);
		Node rightNeighbour = getNodeByCoord(x+(size*sizeMultiplier), y, graph);

		neighbours.add(topNeighbour);
		neighbours.add(leftNeighbour);
		neighbours.add(bottomNeighbour);
		neighbours.add(rightNeighbour);

		if (diagonal) {
			Node bottomRightNeighbour = getNodeByCoord(x+(size*sizeMultiplier), y+(size*sizeMultiplier), graph);
			Node bottomLeftNeighbour = getNodeByCoord(x-(size*sizeMultiplier), y+(size*sizeMultiplier), graph);

			Node topRightNeighbour = getNodeByCoord(x+(size*sizeMultiplier), y-(size*sizeMultiplier), graph);
			Node topLeftNeighbour = getNodeByCoord(x-(size*sizeMultiplier), y-(size*sizeMultiplier), graph);

			neighbours.add(bottomLeftNeighbour);
			neighbours.add(bottomRightNeighbour);
			neighbours.add(topLeftNeighbour);
			neighbours.add(topRightNeighbour);
		}

		return neighbours;
	}
	public Set<Node> getNeighbourNodes() {
		return this.neigbours;
	}

	public Double getDistanceToParent() {
		return distanceToParent;
	}

	public void setDistanceToParent(Double distanceToParent) {
		this.distanceToParent = distanceToParent;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
		this.parentNode = parentNode;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
		this.color = getNodeColorByType(nodeType);
	}

	public static Color getNodeColorByType(NodeType nodeType) {
		return nodeColorMap.get(nodeType);
	}

	public String getNodeInfoRelative(boolean type) {
		return String.format("(%d,%d) %s", x/size, y/size, type ? System.lineSeparator() + this.nodeType : "");
	}

	public String getNodeInfoAbsolute(boolean type) {
		return String.format("(%d,%d) %s", x, y, type ? System.lineSeparator() + this.nodeType : "");
	}

	public String getNodeInfo() {
		return getNodeInfo(false, true);
	}

	public String getNodeInfo(boolean recursive, boolean type) {
		Node parentNode = getParentNode();
		String parentInformation = "";

		if (recursive) {
			if (parentNode != null) {
				parentInformation = "<= " + getNodeInfo(true, type);
			}
		}

		return String.format("(%d,%d) %s %s", x, y, parentInformation, type ? System.lineSeparator() + this.nodeType : "");
	}


	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void resetConnection() {
		this.parentNode = null;
		this.distanceToParent = Double.POSITIVE_INFINITY;
	}

	@Override
	public String toString() {
		return getNodeInfoRelative(false);
	}

	@Override
	public int compareTo(Node n2) {
		return this.getDistanceToParent().compareTo(n2.getDistanceToParent());

	}
	
}
