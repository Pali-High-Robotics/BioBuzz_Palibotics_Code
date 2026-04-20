package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.List;

public class LightGrouper {
    private final List<Servo> lights;

    // Private constructor: The Builder hands over the list of servos
    private LightGrouper(Builder builder) {
        this.lights = new ArrayList<>(builder.servos);
    }

    /**
     * Set all registered lights to a specific hex color string
     */
    public void setAll(String hex) {
        double position = hexInPosition(hex);
        for (Servo s : lights) {
            s.setPosition(position);
        }
    }

    /**
     * Set all registered lights to a raw servo position
     */
    public void setAll(double pos) {
        for (Servo s : lights) {
            s.setPosition(pos);
        }
    }

    private double hexInPosition(String hex) {
        // 1. Clean the string and parse RGB
        if (hex.startsWith("#")) hex = hex.substring(1);
        // Handle 3-digit hex (like #F00) or 6-digit (like #FF0000)

        int r, g, b;
        if (hex.length() == 3) {
            r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
            g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
            b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
        } else {
            r = Integer.parseInt(hex.substring(0, 2), 16);
            g = Integer.parseInt(hex.substring(2, 4), 16);
            b = Integer.parseInt(hex.substring(4, 6), 16);
        }

        // 2. Convert RGB to 0.0-1.0 range for math
        double rf = r / 255.0;
        double gf = g / 255.0;
        double bf = b / 255.0;

        double max = Math.max(rf, Math.max(gf, bf));
        double min = Math.min(rf, Math.min(gf, bf));
        double delta = max - min;

        // 3. Calculate Hue
        double hue = 0;
        if (delta != 0) {
            if (max == rf) {
                hue = ((gf - bf) / delta) % 6;
            } else if (max == gf) {
                hue = ((bf - rf) / delta) + 2;
            } else {
                hue = ((rf - gf) / delta) + 4;
            }
            hue *= 60; // Convert to degrees
            if (hue < 0) hue += 360;
        }

        // 4. Map to goBILDA 0.277 - 0.722 range
        // If the color is essentially black (max < 0.1), return 0 (Off)
        if (max < 0.1) return 0.0;
         // If the color is essentially white (delta < 0.1), return 1.0 (White)
        if (delta < 0.1 && max > 0.9) return 1.0;

        double startPos = 0.277;
        double endPos = 0.722;
        return startPos + ((hue / 360.0) * (endPos - startPos));

    }

    // --- The Builder ---
    public static class Builder {
        private final List<Servo> servos = new ArrayList<>();

        public Builder addLight(Servo servo) {
            this.servos.add(servo);
            return this;
        }

        public LightGrouper build() {
            if (servos.isEmpty()) {
                throw new IllegalStateException("You can't group zero lights!");
            }
            return new LightGrouper(this);
        }
    }
}