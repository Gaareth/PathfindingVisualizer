package com.gareth;

import java.awt.*;

public class pathFindingVisualizer {



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {

                mainFrame frame = new mainFrame();
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
