package ui;

import model.genes.Genome;

import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    private final Panel panel;

    public Frame(Genome genome) {
        this();
        setGenome(genome);
        this.repaint();
    }

    public void setGenome(Genome genome){
        panel.setGenome(genome);
    }

    public Frame() throws HeadlessException {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setTitle("NEAT Best Genome Visualization");
        this.setMinimumSize(new Dimension(1280,1000));
        this.setPreferredSize(new Dimension(1280,1000));

        this.setLayout(new BorderLayout());

        this.panel = new Panel();
        this.add(panel, BorderLayout.CENTER);

        this.setVisible(true);
    }
}
