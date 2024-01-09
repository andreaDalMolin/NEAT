package model.genes;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrea Dal Molin
 * Represents a node gene in the NEAT algorithm.
 * This class is responsible for managing the connections and output calculation of a node in the neural network.
 */
public class NodeGene extends Gene implements Comparable<NodeGene> {
    private double x, y;
    private double output;
    private NodeType type;

    private final List<ConnectionGene> connections = new ArrayList<>();

    public NodeGene(int innovation_number) {
        super(innovation_number);
    }

    /**
     * Calculates the output of this node based on its incoming connections.
     * The output is determined by summing the weighted outputs from all enabled connections
     * and then applying a sigmoid activation function to this sum.
     */
    public void calculateOutput() {
        double sum = 0;

        for (ConnectionGene gene : connections) {
            if (gene.isEnabled()) {
                sum += gene.getWeight() * gene.getFrom().getOutput();
            }
        }
        output = sigmoid(sum);
    }

    /**
     * The sigmoid activation function. It transforms the input value into an output value
     * between 0 and 1, which is useful for binary classification problems. We could have chosen
     * different functions but this is the one used in the paper. As per indication of the paper
     * we have indicated a factor of -4.9
     * @param value The input value to the sigmoid function.
     * @return The output of the sigmoid function.
     */
    private double sigmoid (double value) {
        return 1d / (1 + Math.exp(-4.9*value));
    }

    public void addConnection(ConnectionGene connection) {
        connections.add(connection);
    }

    public void clearConnections() {
        connections.clear();
    }

    @Override
    public int compareTo(NodeGene o) {
        return Double.compare(o.x, this.x);
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

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
        if (type == NodeType.INPUT) {
            this.x = 0.1;
        } else {
            this.x = 0.9;
        }
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "model.genes.NodeGene{" +
                "innovation_number=" + innovationNumber +
                '}';
    }

    public boolean equals(Object o){
        if(!(o instanceof NodeGene)) return false;
        return innovationNumber == ((NodeGene) o).getInnovationNumber();
    }

    public int hashCode(){
        return innovationNumber;
    }
}
