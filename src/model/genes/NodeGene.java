package model.genes;

import java.util.ArrayList;
import java.util.List;

public class NodeGene extends Gene implements Comparable<NodeGene> {
    private double x,y;
    private double output;
    private List<ConnectionGene> connections = new ArrayList<>();

    public NodeGene(int innovation_number) {
        super(innovation_number);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public List<ConnectionGene> getConnections() {
        return connections;
    }

    public void setConnections(List<ConnectionGene> connections) {
        this.connections = connections;
    }

    public void addConnection(ConnectionGene connection) {
        connections.add(connection);
    }

    public void clearConnections() {
        connections.clear();
    }

    public boolean equals(Object o){
        if(!(o instanceof NodeGene)) return false;
        return innovation_number == ((NodeGene) o).getInnovation_number();
    }

    @Override
    public String toString() {
        return "model.genes.NodeGene{" +
                "innovation_number=" + innovation_number +
                '}';
    }

    public int hashCode(){
        return innovation_number;
    }

    public void calculate() {
        double sum = 0;

        for (ConnectionGene gene : connections) {
            if (gene.isEnabled()) {
                sum += gene.getWeight() * gene.getFrom().getOutput();
            }
        }
        output = sigmoid(sum);
    }

    private double sigmoid (double value) {
        return 1d / (1 + Math.exp(-4.9*value));
    }

    @Override
    public int compareTo(NodeGene o) {
        if (this.x > o.x) return -1;
        if (this.x < o.x) return 1;

        return 0;
    }
}
