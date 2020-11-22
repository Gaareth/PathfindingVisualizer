package com.gareth.Utils;

import java.awt.*;
import java.util.ArrayList;

public class ColorUtils {
    // almost everything yoinked from stackoverflow


    // yoinked from: https://stackoverflow.com/a/33571241/9748362
    public static Color getOppositeColor(Color c) {
        float[] hsv = new float[3];
        hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
        hsv[0] = (float) ((hsv[0] + 0.5) % 1.0);

        // black or white? return opposite
        if (hsv[2] == 0) return new Color(255, 255, 255);
        else if (hsv[2] == 1.0) return new Color(0, 0, 0);

        // low value? otherwise, adjust that too
        if (hsv[2] < 0.5) {
            hsv[2] = (float) ((hsv[2] + 0.5) % 1.0);
        }

        Color oppColor = new Color(Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));

        // if the opposite color is the same as the input => just make it white for now
        if (oppColor.getRGB() == c.getRGB())
            oppColor = Color.WHITE;

        return oppColor;
    }


    // yoinked
    // Returns a single rgb color interpolation between given rgb color
    // based on the factor given; via https://codepen.io/njmcode/pen/axoyD?editors=0010
    public static Color interpolateColor(Color color1, Color color2, float factor) {
        int r = Math.round(color1.getRed() + factor * (color2.getRed()- color1.getRed()));
        int g = Math.round(color1.getGreen() + factor * (color2.getGreen()- color1.getGreen()));
        int b = Math.round(color1.getBlue() + factor * (color2.getBlue()- color1.getBlue()));

        return new Color(r,g,b);
    }

    // yoinked from some fiddle
    // My function to interpolate between two colors completely, returning an array
    public static ArrayList<Color> interpolateColors(Color color1, Color color2, float steps) {
        float stepFactor = 1 / (steps - 1);

        ArrayList<Color> interpolatedColorArray = new ArrayList<>();

        for(var i = 0; i < steps; i++) {
            interpolatedColorArray.add(interpolateColor(color1, color2, stepFactor * i));
        }

        return interpolatedColorArray;
    }

    public static float calcColor(double current, double max) {
        return (float) ((current*255/max)/255);
    }

}
