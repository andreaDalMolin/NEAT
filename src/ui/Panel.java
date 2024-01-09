package ui;

import model.genes.ConnectionGene;
import model.genes.Genome;
import model.genes.NodeGene;
import model.genes.NodeType;

import javax.swing.*;
import java.awt.*;

public class Panel extends JPanel {
    private Genome genome;

    public Panel() {
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Call to the superclass method to ensure proper painting
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, this.getWidth(), this.getHeight()); // Fill with white background

        for (ConnectionGene c : genome.getConnections().values()) {
            paintConnection(c, (Graphics2D) g);
        }

        for (NodeGene n : genome.getNodes().values()) {
            paintNode(n, (Graphics2D) g);
        }
    }

    private void paintNode(NodeGene n, Graphics2D g) {
        if (n.getType() == NodeType.INPUT) {
            g.setColor(Color.BLUE);
        } else if (n.getType() == NodeType.OUTPUT) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.GRAY);
        }

        int x = (int) (this.getWidth() * n.getX()) - 10;
        int y = (int) (this.getHeight() * n.getY()) - 10;
        g.fillOval(x, y, 20, 20); // Draw filled circles for nodes
    }

    private void paintConnection(ConnectionGene c, Graphics2D g) {
        if (!c.isEnabled()) return;

        g.setColor(Color.GREEN);
        g.setStroke(new BasicStroke(2)); // Thinner lines for connections

        int fromX = (int) (this.getWidth() * c.getFrom().getX());
        int fromY = (int) (this.getHeight() * c.getFrom().getY());
        int toX = (int) (this.getWidth() * c.getTo().getX());
        int toY = (int) (this.getHeight() * c.getTo().getY());

        g.drawLine(fromX, fromY, toX, toY);

        // Draw weight as a string near the middle of the connection
        String weightStr = String.format("%.2f", c.getWeight());
        int textX = (fromX + toX) / 2;
        int textY = (fromY + toY) / 2;
        g.drawString(weightStr, textX, textY);
    }
}
