import java.util.HashMap;
import java.util.Map;

public class Genome {


    private Neat neat;
    private Map<Integer, NodeGene> nodes = new HashMap<>();
    private Map<Integer, ConnectionGene> connections;

    public Genome(Neat neat) {
        this.neat = neat;
    }

    public void mutate() {
        if (Math.random() < Neat.CONNECTION_MUTATION_RATE) {
//            mutateAddConnection();
        }
        if (Math.random() < Neat.NODE_MUTATION_RATE) {
//            mutateAddNode();
        }
        if (Math.random() < Neat.ENABLE_MUTATION_RATE) {
//            mutateEnableConnection();
        }
        if (Math.random() < Neat.WEIGHT_MUTATION_RATE) {
//            mutateWeightRandomly();
        }
    }

    public double distance(Genome g2) {
        return 0;
    }

    public double crossover(Genome g1, Genome g2) {
        return 0;
    }

    public Map<Integer, NodeGene> getNodes() {
        return this.nodes;
    }

    public Map<Integer, ConnectionGene> getConnections() {
        return this.connections;
    }
}
