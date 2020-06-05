package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Paint extends JPanel {

        private BufferedImage image;

        public Paint() {

            setLayout(new BorderLayout());
            try {
                // Use this if the image exists within the file system
                image = ImageIO.read(new File("background2.jpg"));
                // Use this if the image is an embedded resource
//                image = ImageIO.read(getClass().getResource("/path/to/resource/imageName.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public Dimension getPreferredSize() {
            return image == null ? super.getPreferredSize() : new Dimension (image.getWidth(), image.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int x = (getWidth() - image.getWidth()) / 2;
                int y = (getHeight()- image.getHeight()) / 2;
                g.drawImage(image, x, y, this);
            }
        }

    }