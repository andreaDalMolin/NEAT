import java.util.HashMap;
import java.util.Map;

public class Genome {


    public Neat getNeat() {
        return neat;
    }

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

        Genome g1 = this;

        int highestInnovationGene1 = g1.getConnections().get(g1.getConnections().size()-1).getInnovation_number();
        int highestInnovationGene2 = g2.getConnections().get(g2.getConnections().size()-1).getInnovation_number();

        if (highestInnovationGene1 > highestInnovationGene2) {
            Genome g = g1;
            g1 = g2;
            g2 = g;
        }

        int index_g_1 = 0;
        int index_g_2 = 0;

        double disjoint = 0;
        int excess = 0;
        double weight_diff = 0;
        int similar = 0;

        while (index_g_1 < g1.getConnections().size() && index_g_2 < g2.getConnections().size()) {
            ConnectionGene gene1 = g1.getConnections().get(index_g_1);
            ConnectionGene gene2 = g2.getConnections().get(index_g_2);
            
            int in1 = gene1.getInnovation_number();
            int in2 = gene2.getInnovation_number();
            
            if (in1 == in2) {
                // Similar gene
                index_g_1++;
                index_g_2++;

                similar++;
                weight_diff += Math.abs(gene1.getWeight() - gene2.getWeight());

            } else if (in1 > in2) {
                //Disjoint gene of 2
                index_g_2++;
                disjoint++;
            } else {
                //Disjoint gene of 1
                index_g_1++;
                disjoint++;
            }
        }

        excess = g1.getConnections().size() - index_g_1;
        weight_diff = weight_diff / similar;

        double N = Math.max(g1.getConnections().size(), g2.getConnections().size());
        if (N<20) {
            N = 1;
        }

        double delta = ((Neat.C1*excess) / N) + ((Neat.C2*disjoint) / N) + (Neat.C3*weight_diff);

        return delta;
    }

    public static Genome crossover(Genome g1, Genome g2) {

        Neat neat = g1.getNeat();
        Genome genome = g1.getNeat().emptyGenome();

        int index_g_1 = 0;
        int index_g_2 = 0;

        while (index_g_1 < g1.getConnections().size() && index_g_2 < g2.getConnections().size()) {

            ConnectionGene gene1 = g1.getConnections().get(index_g_1);
            ConnectionGene gene2 = g2.getConnections().get(index_g_2);

            int in1 = gene1.getInnovation_number();
            int in2 = gene2.getInnovation_number();

            if (in1 == in2) {
                // Similar gene

                if (Math.random() > 0.5) {
                    genome.getConnections().put(Neat.getConnection(gene1).getInnovation_number(), Neat.getConnection(gene1));
                } else {
                    genome.getConnections().put(Neat.getConnection(gene2).getInnovation_number(), Neat.getConnection(gene2));
                }

                index_g_1++;
                index_g_2++;
            } else if (in1 > in2) {
                //Disjoint gene of 2
                index_g_2++;
            } else {
                //Disjoint gene of 1
                genome.getConnections().put(Neat.getConnection(gene1).getInnovation_number(), Neat.getConnection(gene1));

                index_g_1++;
            }
        }

        while (index_g_1 < g1.getConnections().size()) {

            ConnectionGene gene1 = g1.getConnections().get(index_g_1);
            genome.getConnections().put(Neat.getConnection(gene1).getInnovation_number(), Neat.getConnection(gene1));

            index_g_1++;

        }

        for (ConnectionGene c : genome.getConnections().values()) {
             genome.getNodes().put(c.getFrom().getInnovation_number(), c.getFrom());
             genome.getNodes().put(c.getTo().getInnovation_number(), c.getTo());
        }

        return genome;
    }

    public Map<Integer, NodeGene> getNodes() {
        return this.nodes;
    }

    public Map<Integer, ConnectionGene> getConnections() {
        return this.connections;
    }
}
