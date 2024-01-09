package model.genes;

/**
 * @author Andrea Dal Molin
 * Represents a connection gene in the NEAT algorithm.
 * A connection gene signifies a connection between two nodes (from and to) in the neural network,
 * along with the weight of the connection and its enabled status.
 */
public class ConnectionGene extends Gene {

    private final NodeGene from;
    private final NodeGene to;
    private double weight;
    private boolean enabled = true;

    /**
     * Constructs a ConnectionGene with specified source and destination nodes.
     * @param from The source node of the connection.
     * @param to The destination node of the connection.
     */
    public ConnectionGene(NodeGene from, NodeGene to) {
        this.from = from;
        this.to = to;
    }

    public NodeGene getFrom() {
        return from;
    }

    public NodeGene getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Compares this ConnectionGene with another object for equality.
     * @param otherConnectionGene The object to compare with.
     * @return True if the given object is a ConnectionGene with the same from and to nodes, false otherwise.
     */
    public boolean equals(Object otherConnectionGene){
        if(!(otherConnectionGene instanceof ConnectionGene connectionGene)) return false;
        return (from.equals(connectionGene.from) && to.equals(connectionGene.to));
    }

    @Override
    public String toString() {
        return "model.genes.ConnectionGene{" +
                "from=" + from.getInnovationNumber() +
                ", to=" + to.getInnovationNumber() +
                ", weight=" + weight +
                ", enabled=" + enabled +
                ", innovation_number=" + innovationNumber +
                '}';
    }

    public int hashCode() {
        return from.getInnovationNumber() + to.getInnovationNumber();
    }
}
