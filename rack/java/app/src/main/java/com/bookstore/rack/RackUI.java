// @kdoc-ui@
package com.bookstore.rack;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.tccc.kos.commons.kab.KabFile;
import com.tccc.kos.commons.util.concurrent.AdjustableCallback;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RackUI extends JFrame {
    private JLabel imageLabel;
    private int currentIndex = -1;
    private final String[] imagePaths = {
            "/all.png",
            "/book1.png",
            "/book2.png",
            "/book3.png"
    };
    private KabFile assetsKab;

    public RackUI(KabFile assetsKab) {
        this.assetsKab = assetsKab;
        // run full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // select the layout manager
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(Color.BLACK);

        // Image label to display the image content
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.CENTER);

        if (assetsKab != null) {
            // timer to rotate the image on the display
            new AdjustableCallback(true, 5000, () -> displayNextImage());

            // show the first image
            displayNextImage();
        } else {
            log.error("Assets kab file is null ensure you placed the kab in the right section");
        }
    }

    private void displayNextImage() {
        currentIndex++;
        try {
            byte[] imageBytes = assetsKab.getEntry(imagePaths[currentIndex % 4]).getInputStream().readAllBytes();
            ImageIcon icon = new ImageIcon(imageBytes);
            imageLabel.setIcon(icon);
        } catch (Exception e) {
            log.error("Failed to load image, {} ", e);
        }
    }
}
// @kdoc-ui@
