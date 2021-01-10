package com.gareth;

import com.gareth.Listeners.MouseInputHandler;
import com.gareth.MazeGeneration.MazeGenAlgorithms;
import com.gareth.MazeGeneration.MazeGenerator;
import com.gareth.Pathfinding.Pathfinding;
import com.gareth.Pathfinding.PathfindingAlgorithms;
import com.gareth.Utils.ColorUtils;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static com.gareth.Utils.ColorUtils.interpolateColors;

public class mainFrame extends JFrame {

    private JCheckBox diagonalSearchCheck;
    private JCheckBox nodeInfoCheck;
    private JCheckBox gradientColorCheck;
    private JCheckBox infoCheckBoxNodeType;


    private JRadioButton infoRadioButtonDistParent;
    private JRadioButton infoRadioButtonPosAbs;
    private JRadioButton infoRadioButtonPosRel;

    public static JLabel labelPriorityQueue;

    public static JLabel menuBarStatusBar;
    private final JMenuBar menuBar;

    private static JComboBox<String> pathAlgorithmsBox;
    private static JComboBox<String> mazeGenBox;

    public static JPanel contentPane;
    public static DrawPanel drawPanel;
    private final GridBagLayout gbl_contentPane;

    private  JPanel sidePanel;
    private JPanel queuePanel;
    private  JPanel settingsPanel;

    private CardLayout cardLayout = new CardLayout();
    private JPanel activeCard;



    private Double currentMaxDistance;
    private ArrayList<Color> gradientColors = new ArrayList<>();

    public static int gridSize = 75;
    public static Graph graph;

    public static boolean blockNodeMode = false;

    MazeGenerator gen;
    static PathfindingAlgorithms pathfindingAlgorithms;
    static MazeGenAlgorithms mazeGenAlgorithms;

    Thread pathfindingThread;
    Thread mazeGenThread;

    public static long startTime;
    public static long endTime;

    public static Pathfinding getSelectedPathfindingAlgorithm() {
        return pathfindingAlgorithms.getAlgorithmByName(Objects.requireNonNull(pathAlgorithmsBox.getSelectedItem()).toString());
    }

    public static MazeGenerator getSelectedMazeGenerationAlgorithm() {
        return mazeGenAlgorithms.getAlgorithmByName(Objects.requireNonNull(mazeGenBox.getSelectedItem()).toString());
    }

    public void solvePath() {
        startTime = System.currentTimeMillis();

        // Remove nodes created by solving a path
        graph.clearPathRemains();

        pathfindingThread = new Thread(() -> {

            try {
                getSelectedPathfindingAlgorithm().solve(graph.getStartNode(), graph.getEndNode(), graph, diagonalSearchCheck.isSelected());
            }catch (InterruptedException e) {
                menuBarStatusBar.setText("Status: Error while finding a path");
            }

        });

        /*
        int temp1 = getSelectedPathfindingAlgorithm().sleepTime;
        int temp2 = getSelectedPathfindingAlgorithm().pathDrawTime;

        //very hacky TODO: refactor
        getSelectedPathfindingAlgorithm().sleepTime = 0;
        getSelectedPathfindingAlgorithm().pathDrawTime = 0;

        pathfindingThread.run();

        try {
            pathfindingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        graph.clearPathRemains();
        repaint();

        System.out.println("Current distance: " +graph.getEndNode().getDistanceToParent());
        currentMaxDistance = graph.getEndNode().getDistanceToParent();
        gradientColors = ColorUtils.interpolateColors(new Color(255, 0, 0), new Color(0, 0, 255), currentMaxDistance.intValue());

        pathfindingThread.interrupt();


        getSelectedPathfindingAlgorithm().sleepTime = temp1;
        getSelectedPathfindingAlgorithm().pathDrawTime = temp2;
        */



        pathfindingThread.start();

    }

    public void stopSolvePath() {
        pathfindingThread.interrupt();
    }

    public void generateMaze() {


        mazeGenThread = new Thread() {
            public void run() {

                try {
                    //gen.depthFirst(graph, diagonalSearchCheck.isSelected());
                    getSelectedMazeGenerationAlgorithm().generateMaze(graph, diagonalSearchCheck.isSelected());

                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

            }
        };
        mazeGenThread.start();
    }

    public void stopGenerateMaze() {
        mazeGenThread.interrupt();
    }

    class KeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println("AAAAAAaaa");

            super.keyReleased(e);

            if (e.getKeyCode()  == KeyEvent.VK_F5) {
                System.out.println("AAAH");
                cellify();
                repaint();
            }


            if (e.getKeyCode()  == KeyEvent.VK_F1) {
                if (pathfindingThread != null) {
                    if (pathfindingThread.isAlive())
                        pathfindingThread.interrupt();
                }
                solvePath();
            }

            if (e.getKeyCode()  == KeyEvent.VK_F2) {
                if (mazeGenThread != null) {
                    if (mazeGenThread.isAlive())
                        mazeGenThread.interrupt();
                }
                generateMaze();
            }

        }
    }


    public void initNodes() {
        for (int y = 0; y < this.getHeight() - gridSize; y += gridSize) {
            for (int x = 0; x < this.getWidth() - gridSize; x += gridSize) {
                graph.addNode(new Node(x, y, gridSize, Node.NodeType.CellNode));
            }
        }
    }

    public void clearNodes() {
        graph.getGraph().clear();
        repaint();
    }

    // TODO: find better name
    public void cellify() {
        for (Node n : graph.getGraph()) {
            n.setNodeType(Node.NodeType.CellNode);
            graph.resetConnections();
        }
        repaint();
    }


    public void changeCellSize(int size) {
        int column = 1;
        int row = 0;
        for (int y = 0; y < this.getHeight() - gridSize; y += gridSize) {
            // start at first column
            column = 1;
            for (int x = 0; x < this.getWidth() - gridSize; x += gridSize) {
                Node n = graph.getNodeByCoord(x, y);
                // node out of bounds ==> inefficient
                if (n == null)
                    break;

                if (n.getX() != 0) {
                    n.setX(column * size);
                    column++;
                }

                if (n.getY() != 0) {
                    n.setY(row * size);
                }

                n.setSize(size);
            }
            // after first row increase it
            row++;
        }

        gridSize = size;
        graph.setGridSize(gridSize);
        repaint();
    }

    public void refreshCells() {
        clearNodes();
        initNodes();
        repaint();
    }


    public void expandCells() {
        for (int y = 0; y < this.getHeight() - gridSize; y += gridSize) {
            for (int x = 0; x < this.getWidth() - gridSize; x += gridSize) {
                Node n = graph.getNodeByCoord(x, y);
                // Only add new nodes
                if (n == null)
                    graph.addNode(new Node(x,y,gridSize, Node.NodeType.CellNode));
            }
        }
        repaint();
    }

    public mainFrame() {
        this.addKeyListener(new KeyListener());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1080, 720);
        setTitle("Path-finding Visualizer");

        contentPane = new JPanel();

        gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0,0,0};
        gbl_contentPane.rowHeights = new int[]{0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 0.1, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        setContentPane(contentPane);

        drawPanel = new DrawPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        //gbc_panel_1.ipadx = (int) (this.getSize().getWidth()*0.65);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 0;
        contentPane.add(drawPanel, gbc_panel_1);

        graph = new Graph(drawPanel, gridSize);
        pathfindingAlgorithms = new PathfindingAlgorithms(drawPanel);
        mazeGenAlgorithms = new MazeGenAlgorithms(drawPanel);
        gen = new MazeGenerator(drawPanel);

        initQueuePanel();
        activeCard = queuePanel;
        initSettingsPanel();

        initSidePanel();
        hideSidePanel();

        menuBar = new JMenuBar();
        initMenubar();
        this.setJMenuBar(menuBar);
        initNodes();

        contentPane.addMouseListener(new MouseInputHandler(drawPanel));
        contentPane.addMouseMotionListener(new MouseInputHandler(drawPanel));
    }

    public void initSidePanel() {
        sidePanel = new JPanel(cardLayout);
        sidePanel.setVisible(false);

        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.insets = new Insets(0, 0, 0, 0);
        gbc_panel_2.fill = GridBagConstraints.VERTICAL;
        gbc_panel_2.gridx = 1;
        gbc_panel_2.gridy = 0;

        sidePanel.add(queuePanel, queuePanel.getName());
        sidePanel.add(settingsPanel, settingsPanel.getName());

        //cardLayout.show(sidePanel,"queuePanel");
        contentPane.add(sidePanel, gbc_panel_2);
    }

    public void initSettingsPanel() {
        settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.setName("settingsPanel");
        settingsPanel.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));

        JPanel backPanel = new JPanel(new BorderLayout());
        backPanel.add(new JLabel("Settings"), BorderLayout.CENTER);
        JButton backButton = new JButton("< Back");
        backPanel.add(backButton, BorderLayout.NORTH);
        backButton.addActionListener(actionEvent -> hideSidePanel());

        settingsPanel.add(backPanel, BorderLayout.NORTH);

        JPanel settingsPanelSettings = new JPanel(new GridLayout(0, 2, 0, 0));


        JLabel pathTimeLabel = new JLabel("Pathfinding time:");
        settingsPanelSettings.add(pathTimeLabel);

        JSlider pathTimeSlider = new JSlider();
        pathTimeSlider.setMajorTickSpacing(500);
        pathTimeSlider.setMinorTickSpacing(10);
        pathTimeSlider.setSnapToTicks(true);
        pathTimeSlider.setPaintLabels(true);
        pathTimeSlider.setPaintTicks(true);
        pathTimeSlider.setMaximum(2000);

        pathTimeSlider.addChangeListener(changeEvent -> {
            getSelectedPathfindingAlgorithm().sleepTime = pathTimeSlider.getValue();
            pathTimeLabel.setText("Pathfinding time:" + getSelectedPathfindingAlgorithm().sleepTime);
        });
        settingsPanelSettings.add(pathTimeSlider);


        JLabel mazeTimeLabel = new JLabel("Maze generation time:");
        settingsPanelSettings.add(mazeTimeLabel);

        JSlider mazeTimeSlider = new JSlider();
        mazeTimeSlider.setMajorTickSpacing(500);
        mazeTimeSlider.setMinorTickSpacing(50);
        mazeTimeSlider.setSnapToTicks(true);
        mazeTimeSlider.setPaintLabels(true);
        mazeTimeSlider.setPaintTicks(true);
        mazeTimeSlider.setMaximum(2000);

        mazeTimeSlider.addChangeListener(changeEvent -> {
            gen.sleepTime = mazeTimeSlider.getValue();
            mazeTimeLabel.setText("Maze generation time:" + gen.sleepTime);
        });

        settingsPanelSettings.add(mazeTimeSlider);


        JSlider gridSizeSlider = new JSlider();
        gridSizeSlider.setValue(gridSize);
        gridSizeSlider.setMajorTickSpacing(100);
        gridSizeSlider.setMinorTickSpacing(5);
        gridSizeSlider.setSnapToTicks(true);
        gridSizeSlider.setPaintLabels(true);
        gridSizeSlider.setPaintTicks(true);
        gridSizeSlider.setMaximum(500);
        gridSizeSlider.setMinimum(5);


        JLabel gridSizeLabel = new JLabel(String.format("Grid Size: %d", gridSizeSlider.getValue()));
        settingsPanelSettings.add(gridSizeLabel);

        gridSizeSlider.addChangeListener(changeEvent -> {
            changeCellSize(gridSizeSlider.getValue());
            gridSizeLabel.setText(String.format("Grid Size: %d", gridSizeSlider.getValue()));
        });


        settingsPanelSettings.add(gridSizeSlider);


        JLabel diagonalSearchLabel = new JLabel( "Search direction");
        settingsPanelSettings.add(diagonalSearchLabel);

        diagonalSearchCheck = new JCheckBox("Diagonal Search");
        diagonalSearchCheck.setSelected(false);
        settingsPanelSettings.add(diagonalSearchCheck);

        // TODO: diagonal maze gen

        nodeInfoCheck = new JCheckBox("Node Info");
        nodeInfoCheck.setSelected(true);
        nodeInfoCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                repaint();
            }
        });
        settingsPanelSettings.add(nodeInfoCheck);


        gradientColorCheck = new JCheckBox("Gradient Node color");
        gradientColorCheck.setSelected(true);
        gradientColorCheck.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                repaint();
            }
        });
        settingsPanelSettings.add(gradientColorCheck);


        ButtonGroup buttonGroup = new ButtonGroup();
        infoRadioButtonDistParent = new JRadioButton("Distance to Parent");
        infoRadioButtonDistParent.addActionListener((e) -> drawPanel.repaint());

        infoRadioButtonPosAbs = new JRadioButton("Absolute Position");
        infoRadioButtonPosAbs.addActionListener((e) -> drawPanel.repaint());

        infoRadioButtonPosRel = new JRadioButton("Relative Position");
        infoRadioButtonPosRel.addActionListener((e) -> drawPanel.repaint());

        infoRadioButtonPosRel.setSelected(true);

        infoCheckBoxNodeType = new JCheckBox("Node Type");
        infoCheckBoxNodeType.addActionListener((e) -> drawPanel.repaint());


        buttonGroup.add(infoRadioButtonDistParent);
        buttonGroup.add(infoRadioButtonPosAbs);
        buttonGroup.add(infoRadioButtonPosRel);

        settingsPanelSettings.add(infoRadioButtonDistParent);
        settingsPanelSettings.add(infoRadioButtonPosAbs);
        settingsPanelSettings.add(infoRadioButtonPosRel);
        settingsPanelSettings.add(infoCheckBoxNodeType);

        settingsPanel.add(settingsPanelSettings);
    }

    public void initQueuePanel() {
        queuePanel = new JPanel(new BorderLayout());
        queuePanel.setName("queuePanel");
        queuePanel.setBorder(new MatteBorder(0, 1, 0, 1, new Color(0, 0, 0)));

        JPanel backPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("< Back");
        backPanel.add(backButton, BorderLayout.NORTH);
        backButton.addActionListener(actionEvent -> hideSidePanel());

        queuePanel.add(backPanel, BorderLayout.NORTH);

        JPanel queuePanelQueue = new JPanel(new BorderLayout());

        JLabel lblNewLabel_1 = new JLabel("Priority Queue");
        queuePanelQueue.add(lblNewLabel_1, BorderLayout.NORTH);

        labelPriorityQueue = new JLabel("New label");
        queuePanelQueue.add(labelPriorityQueue, BorderLayout.CENTER);

        queuePanel.add(queuePanelQueue, BorderLayout.CENTER);
        //contentPane.add(queuePanel, gbc_panel_2);
    }

    public static void changeStatus(String status) {
        menuBarStatusBar.setText("Status: " + status);
    }

    public static void updateStatus(String status) {
        menuBarStatusBar.setText(menuBarStatusBar.getText() + " " + status);
    }

    public void initMenubar() {
        JMenu pathfindingAction = new JMenu("Solve path");
        JMenu mazeGenAction = new JMenu("Generate Maze");



        // Path action
        JMenuItem startPath = new JMenuItem("Start");
        startPath.addActionListener((event) -> solvePath());
        JMenuItem stopPath = new JMenuItem("Stop");
        stopPath.addActionListener((event) -> stopSolvePath());

        pathfindingAction.add(startPath);
        pathfindingAction.add(stopPath);

        // Maze action
        JMenuItem startMaze = new JMenuItem("Start");
        startMaze.addActionListener((actionEvent -> generateMaze()));
        JMenuItem stopMaze = new JMenuItem("Stop");
        stopMaze.addActionListener((actionEvent -> stopGenerateMaze()));

        mazeGenAction.add(startMaze);
        mazeGenAction.add(stopMaze);

        menuBar.add(pathfindingAction);
        menuBar.add(mazeGenAction);



        JMenu menuItemSeparator1 = new JMenu("|");
        menuItemSeparator1.setEnabled(false);
        menuItemSeparator1.setBorder(new EmptyBorder(0, 4, 0, 6));
        menuBar.add(menuItemSeparator1);

        menuBarStatusBar = new JLabel("Status: ");
        menuBar.add(menuBarStatusBar);


        JMenu menuItemSeparator = new JMenu("|");
        menuItemSeparator.setEnabled(false);
        menuItemSeparator.setBorder(new EmptyBorder(0, 6, 0, 6));
        menuBar.add(menuItemSeparator);

        JLabel pathAlgorithmLabel = new JLabel("Pathfinding Algorithm:");
        pathAlgorithmLabel.setBorder(new EmptyBorder(0, 0, 0, 6));
        menuBar.add(pathAlgorithmLabel);

        pathAlgorithmsBox = new JComboBox<String>();
        pathAlgorithmsBox.setModel(new DefaultComboBoxModel<String>(pathfindingAlgorithms.getAlgorithmNames().toArray(new String[0])));
        //pathAlgorithmsBox.setMaximumSize(new Dimension(10 , 20));
        menuBar.add(pathAlgorithmsBox);

        JLabel mazeGenLabel = new JLabel("MazeGeneration Algorithm:");
        mazeGenLabel.setBorder(new EmptyBorder(0, 4, 0, 6));
        menuBar.add(mazeGenLabel);

        mazeGenBox = new JComboBox<String>();
        mazeGenBox.setModel(new DefaultComboBoxModel<String>(mazeGenAlgorithms.getAlgorithmNames().toArray(new String[0])));
        menuBar.add(mazeGenBox);




        JMenu cellMenu = new JMenu("Cells");
        JMenu sideMenu = new JMenu("Side Menu");



        // Reset
        JMenuItem resetItem = new JMenuItem("Reset");
        resetItem.addActionListener(actionEvent -> refreshCells());
        cellMenu.add(resetItem);

        // Clear
        JMenuItem clearItem = new JMenuItem("Clear");
        clearItem.addActionListener(actionEvent -> cellify());
        cellMenu.add(clearItem);

        // Expand
        // TODO: implement shrink
        JMenuItem expandItem = new JMenuItem("Expand");
        expandItem.addActionListener(actionEvent -> expandCells());
        cellMenu.add(expandItem);



        // Settings
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (activeCard != null) {
                    // Settingspanel not active card = > set it to active else toggle visibility of sidePanel
                    if (!activeCard.getName().equals(settingsPanel.getName())) {
                        showSidePanel();
                        cardLayout.show(sidePanel, settingsPanel.getName());
                        activeCard = settingsPanel;
                    }else {
                        toggleVisibilityOfSidePanel();
                    }
                }
            }
        });
        sideMenu.add(settingsItem);
        // Queue
        JMenuItem queueItem = new JMenuItem("Queue");
        queueItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (activeCard != null) {
                    if (!activeCard.getName().equals(queuePanel.getName()) || !sidePanel.isVisible()) {
                        showSidePanel();
                        cardLayout.show(sidePanel, queuePanel.getName());
                        activeCard = queuePanel;
                    }else {
                        toggleVisibilityOfSidePanel();
                    }
                }
            }
        });
        sideMenu.add(queueItem);


        menuBar.add(cellMenu);
        menuBar.add(sideMenu);
    }

    public void showSidePanel() {
        sidePanel.setVisible(true);
        gbl_contentPane.columnWeights = new double[]{1.0, 0.1, Double.MIN_VALUE};
    }

    public void hideSidePanel() {
        sidePanel.setVisible(false);
        gbl_contentPane.columnWeights = new double[]{1.0, 0, Double.MIN_VALUE};
    }

    public void toggleVisibilityOfSidePanel() {
        if (sidePanel.isVisible())
            hideSidePanel();
        else
            showSidePanel();
    }


    public static Node getSelectedNode(int x, int y) {
        if (x < drawPanel.getWidth() && x > 0 && y < drawPanel.getHeight() && y > 0)
            return new Node((x/mainFrame.gridSize)*mainFrame.gridSize, (y/mainFrame.gridSize)*mainFrame.gridSize, mainFrame.gridSize);
        else
            return null;
    }


    public class DrawPanel extends JPanel {

        // partly yoinked
        public void centerMultilineString(Graphics g, Rectangle r, String s) {
            int numOfLines= s.split("\n").length;
            int c = 0;
            for (String s1 : s.split("\n")) {
                FontRenderContext frc =
                        new FontRenderContext(null, true, true);

                g.setFont(new Font(g.getFont().getFontName(), g.getFont().getStyle(), gridSize/6));

                Font font = g.getFont();
                Rectangle2D r2D = font.getStringBounds(s1, frc);

                int stringWidth = (int) Math.round(r2D.getWidth());
                int stringHeight = (int) Math.round(r2D.getHeight()) * numOfLines;

                int rX = (int) Math.round(r2D.getX());
                int rY = (int) Math.round(r2D.getY());

                int a = (r.width / 2) - (stringWidth / 2) - rX;
                int b = (r.height / 2) - (stringHeight / 2) - rY;

                g.setFont(font);
                g.drawString(s1, r.x + a, r.y + b + c*(stringHeight/numOfLines));
                c = c +1;
            }

        }

        public double calculateMaxDistance() {
            Node maxNodeX = graph.getGraph().stream().max(Comparator.comparingInt(Node::getX)).get();
            Node maxNodeY = graph.getGraph().stream().max(Comparator.comparingInt(Node::getY)).get();

            return (maxNodeX.getX() + maxNodeY.getY()) / gridSize;
        }

        public double calculateMaxDistanceToEnd() {
            Node maxNode = graph.getGraph().stream().max(new Comparator<Node>() {
                @Override
                public int compare(Node node, Node t1) {
                    if (node.getDistanceToParent() == Double.POSITIVE_INFINITY && t1.getDistanceToParent() == Double.POSITIVE_INFINITY)
                        return 0;

                    // Otherwise the infinity value will be chosen which we don't want
                    if (node.getDistanceToParent() == Double.POSITIVE_INFINITY)
                        return -1;

                    if (t1.getDistanceToParent() == Double.POSITIVE_INFINITY)
                        return 1;


                    return node.getDistanceToParent().compareTo(t1.getDistanceToParent());
                }
            }).get();

            double maxDistance = maxNode.getDistanceToParent() == Double.POSITIVE_INFINITY ? graph.getGraph().size() : maxNode.getDistanceToParent();
            return maxDistance;
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);



            //System.out.println("Gradients colors: "+ calculateMaxDistance());
            if (gradientColorCheck.isSelected() && currentMaxDistance == null) {
                currentMaxDistance = calculateMaxDistance();
                gradientColors = interpolateColors(new Color(255, 0, 0), new Color(0, 0, 255), currentMaxDistance.floatValue());
            }
                //very inefficient ? yes

            //System.out.println(maxNode.getNodeInfo());


            // Draw Cell nodes
            for (Node cellNode : graph.getNodesByType(Node.NodeType.CellNode)) {
                g.setColor(cellNode.getColor());
                g.drawRect(cellNode.getX(), cellNode.getY(), cellNode.getSize(), cellNode.getSize());
                g.setColor(Color.LIGHT_GRAY);

                if (nodeInfoCheck.isSelected()) {
                    Rectangle rect = new Rectangle(cellNode.getX(), cellNode.getY(), cellNode.getSize(), cellNode.getSize());
                    //centerMultilineString(g, rect, String.format("%.2f", n.getDistanceToParent()));
                    String info = infoRadioButtonDistParent.isSelected() ? String.format("%.2f %s", cellNode.getDistanceToParent(),
                                    infoCheckBoxNodeType.isSelected() ? System.lineSeparator() + cellNode.getNodeType() : "")
                            : infoRadioButtonPosAbs.isSelected() ? cellNode.getNodeInfoAbsolute(infoCheckBoxNodeType.isSelected())
                            : infoRadioButtonPosRel.isSelected() ? cellNode.getNodeInfoRelative(infoCheckBoxNodeType.isSelected()) : "";

                    //info = String.format("%s \n %s", cellNode.getInfo(), cellNode.getNodeInfoRelative(false));
                    centerMultilineString(g, rect, info);
                }
            }

            // Draws all nodes except cell nodes
            for (Node n : graph.getGraph()) {
                if (n.getNodeType() == Node.NodeType.CellNode)
                    continue;

                g.setColor(n.getColor());

                if (gradientColorCheck.isSelected()) {

                    if (n.getDistanceToParent() >= gradientColors.size()) {
                        currentMaxDistance = calculateMaxDistanceToEnd();
                        gradientColors = interpolateColors(new Color(255, 0, 0), new Color(0, 0, 255), currentMaxDistance.floatValue());
                    }

                    if (n.getDistanceToParent() < gradientColors.size()) {
                        if (n.getNodeType() == Node.NodeType.VisitedNode) {
                            Color c = new Color(ColorUtils.calcColor(n.getDistanceToParent(), currentMaxDistance), 0.5F, 1F);
                            g.setColor(c);
                        } else if (n.getNodeType() == Node.NodeType.FoundPath) {
                            g.setColor(gradientColors.get((int) Math.round(n.getDistanceToParent())));
                        }
                    }
                }

                g.fillRect(n.getX(), n.getY(), n.getSize(), n.getSize());

                if (nodeInfoCheck.isSelected()) {
                    g.setColor(ColorUtils.getOppositeColor(n.getColor()));


                    Rectangle rect = new Rectangle(n.getX(), n.getY(), n.getSize(), n.getSize());
                    String info = infoRadioButtonDistParent.isSelected() ? String.format("%.2f %s", n.getDistanceToParent(),
                                    infoCheckBoxNodeType.isSelected() ? System.lineSeparator() + n.getNodeType() : "")
                            : infoRadioButtonPosAbs.isSelected() ? n.getNodeInfoAbsolute(infoCheckBoxNodeType.isSelected())
                            : infoRadioButtonPosRel.isSelected() ? n.getNodeInfoRelative(infoCheckBoxNodeType.isSelected()) : "";
                    ;

                    //info = String.format("%s \n %s", n.getInfo(), n.getNodeInfoRelative(false));

                    centerMultilineString(g, rect, info);
                }

            }



            //drawNodes(gen.notWall, g, Node.nodeColorMap.get(Node.NodeType.CellNode), "not wall");


        }


    }


}

