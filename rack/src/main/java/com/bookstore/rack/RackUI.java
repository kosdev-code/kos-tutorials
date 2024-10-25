package com.bookstore.rack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

public class RackUI extends JFrame {
    private JLabel imageLabel;
    private int currentIndex = -1;
    private final String[] imagePaths = {
        "/images/all.png",
        "/images/book1.png",
        "/images/book2.png",
        "/images/book3.png"
    };

    public RackUI() {
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

        // timer to rotate the image on the display
        Timer timer = new Timer(5000, (e) -> displayNextImage());
        timer.start();

        // show the first image
        displayNextImage();
    }

    private void displayNextImage() {
        currentIndex++;
        URL imageUrl = getClass().getResource(imagePaths[currentIndex % imagePaths.length]);
        ImageIcon icon = new ImageIcon(imageUrl);
        imageLabel.setIcon(icon);
    }
}