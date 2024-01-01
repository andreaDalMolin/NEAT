import java.util.*;

public class Genome {


    public Neat getNeat() {
        return neat;
    }

    private Neat neat;
    private Map<Integer, NodeGene> nodes = new TreeMap<>();
    private Map<Integer, ConnectionGene> connections = new TreeMap<>();

    public Genome(Neat neat) {
        this.neat = neat;
    }

    public void mutate() {
        if (Math.random() < Neat.MUTATE_LINK_RATE) {
            mutate_link();
        }
        if (Math.random() < Neat.MUTATE_NODE_RATE) {
            mutate_node();
        }
        if (Math.random() < Neat.MUTATE_WEIGHT_SHIFT_RATE) {
            mutate_weight_shift();
        }
        if (Math.random() < Neat.MUTATE_WEIGHT_RANDOM_RATE) {
            mutate_weight_random();
        }
        if (Math.random() < Neat.MUTATE_TOGGLE_RATE) {
            mutate_link_toggle();
        }
    }

    public void mutate_link() {

        for (int i = 0; i < 100; i++) {

            NodeGene a = getRandomNode();
            NodeGene b = getRandomNode();

            if (a.getX() == b.getX()) {
                continue;
            }

            ConnectionGene connectionGene;

            if (a.getX() < b.getX()) {
                connectionGene = new ConnectionGene(a, b);
            } else {
                connectionGene = new ConnectionGene(b, a);
            }

            if (connections.containsValue(connectionGene)) {
                continue;
            }

            connectionGene = neat.getConnection(connectionGene.getFrom(), connectionGene.getTo());
            connectionGene.setWeight((Math.random() * 2 - 1) * Neat.WEIGHT_RANDOM_STRENGTH);

            connections.put(connectionGene.getInnovation_number(), connectionGene);
            return;
        }
    }

    public void mutate_node() {
        ConnectionGene connectionGene = getRandomConnection();

        if (connectionGene == null) return;

        NodeGene from = connectionGene.getFrom();
        NodeGene to = connectionGene.getTo();

        NodeGene middle = neat.getNode();
        middle.setX((from.getX() + to.getX()) / 2);
        middle.setY((from.getY() + to.getY()) / 2 + Math.random() * 0.1 - 0.05);

        ConnectionGene connection1 = neat.getConnection(from, middle);
        ConnectionGene connection2 = neat.getConnection(middle, to);

        connection1.setWeight(1);
        connection2.setWeight(connectionGene.getWeight());
        connection2.setEnabled(connection2.isEnabled());

        connections.remove(connectionGene.getInnovation_number());
        connections.put(connection1.getInnovation_number(), connection1);
        connections.put(connection2.getInnovation_number(), connection2);

        nodes.put(middle.getInnovation_number(), middle);
    }

    public void mutate_weight_shift() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setWeight(randomConnection.getWeight() + (Math.random() * 2 - 1) * Neat.WEIGHT_SHIFT_STRENGTH);
        }
    }

    public void mutate_weight_random() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setWeight((Math.random() * 2 - 1) * Neat.WEIGHT_RANDOM_STRENGTH);
        }
    }

    public void mutate_link_toggle() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setEnabled(!randomConnection.isEnabled());
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

    private ConnectionGene getRandomConnection() {
        if (connections.isEmpty()) return null;

        // Convert the TreeMap entries to a List
        List<Map.Entry<Integer, ConnectionGene>> entryList = new ArrayList<>(connections.entrySet());

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(entryList.size());

        // Retrieve the random entry and extract the ConnectionGene object
        Map.Entry<Integer, ConnectionGene> randomEntry = entryList.get(randomIndex);
        return randomEntry.getValue();
    }

    private NodeGene getRandomNode() {
        // Convert the TreeMap entries to a List
        List<Map.Entry<Integer, NodeGene>> entryList = new ArrayList<>(nodes.entrySet());

        // Generate a random index
        Random random = new Random();
        int randomIndex = random.nextInt(entryList.size());

        // Retrieve the random entry and extract the NodeGene object
        Map.Entry<Integer, NodeGene> randomEntry = entryList.get(randomIndex);
        return randomEntry.getValue();
    }

    public Map<Integer, NodeGene> getNodes() {
        return this.nodes;
    }

    public Map<Integer, ConnectionGene> getConnections() {
        return this.connections;
    }
}
