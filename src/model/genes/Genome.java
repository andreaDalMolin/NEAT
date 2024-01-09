package model.genes;

import model.Neat;

import java.util.*;

/**
 * Represents a genome in the NEAT algorithm.
 * A genome consists of a collection of nodes and connections, representing a neural network.
 */
public class Genome {

    private final Neat neat;
    private final Map<Integer, NodeGene> nodes = new TreeMap<>();
    private final Map<Integer, ConnectionGene> connections = new TreeMap<>();

    public Genome(Neat neat) {
        this.neat = neat;
    }

    /**
     * Calculates the output of the neural network represented by this genome.
     * The method performs forward propagation through the network.
     * @param inputs The input values to the network.
     * @return The output values from the network.
     */
    public double[] calculateOutput(double[] inputs) {
        // Step 1: Set input values
        setInputs(inputs);

        // Step 1.5: Initialize connections for each node
        initializeNodeConnections();

        // Step 2: Forward propagation
        forwardPropagation();

        // Step 3: Collect and return output values
        return getOutputs();
    }

    private void setInputs(double[] inputs) {
        int inputIndex = 0;
        for (NodeGene node : nodes.values()) {
            if (node.getType() == NodeType.INPUT && inputIndex < inputs.length) {
                node.setOutput(inputs[inputIndex++]);
            }
        }
    }

    private void initializeNodeConnections() {
        // Clear existing connections in each node
        for (NodeGene node : nodes.values()) {
            node.clearConnections();
        }

        // Add each connection to the corresponding nodes
        for (ConnectionGene connection : connections.values()) {
            NodeGene from = connection.getFrom();
            NodeGene to = connection.getTo();

            ConnectionGene con = new ConnectionGene(from, to);
            con.setWeight(connection.getWeight());
            con.setEnabled(connection.isEnabled());

            to.addConnection(con);
        }
    }

    private void forwardPropagation() {
        for (NodeGene node : nodes.values()) {
            if (node.getType() != NodeType.INPUT) { // Skip input nodes
                node.calculateOutput();
            }
        }
    }

    private double[] getOutputs() {
        List<Double> outputsList = new ArrayList<>();
        for (NodeGene node : nodes.values()) {
            if (node.getType() == NodeType.OUTPUT) {
                outputsList.add(node.getOutput());
            }
        }

        double[] outputs = new double[outputsList.size()];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = outputsList.get(i);
        }
        return outputs;
    }

    public void mutate() {
        if (Math.random() < Neat.MUTATE_LINK_RATE) {
            mutateLink();
        }
        if (Math.random() < Neat.MUTATE_NODE_RATE) {
            mutateNode();
        }
        if (Math.random() < Neat.MUTATE_WEIGHT_SHIFT_RATE) {
            mutateWeightShift();
        }
        if (Math.random() < Neat.MUTATE_WEIGHT_RANDOM_RATE) {
            mutateWeightRandom();
        }
        if (Math.random() < Neat.MUTATE_TOGGLE_RATE) {
            mutateLinkToggle();
        }
    }

    public void mutateLink() {
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

            connections.put(connectionGene.getInnovationNumber(), connectionGene);
            return;
        }
    }

    public void mutateNode() {
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

        connections.remove(connectionGene.getInnovationNumber());
        connections.put(connection1.getInnovationNumber(), connection1);
        connections.put(connection2.getInnovationNumber(), connection2);

        nodes.put(middle.getInnovationNumber(), middle);
    }

    public void mutateWeightShift() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setWeight(randomConnection.getWeight() + (Math.random() * 2 - 1) * Neat.WEIGHT_SHIFT_STRENGTH);
        }
    }

    public void mutateWeightRandom() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setWeight((Math.random() * 2 - 1) * Neat.WEIGHT_RANDOM_STRENGTH);
        }
    }

    public void mutateLinkToggle() {
        ConnectionGene randomConnection = getRandomConnection();

        if(randomConnection != null) {
            randomConnection.setEnabled(!randomConnection.isEnabled());
        }
    }

    public double distance(Genome g2) {
        Genome g1 = this;

        int highestInnovationGene1 = 0;
        if(!g1.getConnections().isEmpty()){
            highestInnovationGene1 = g1.getConnections().keySet().stream().max(Integer::compare).get();
        }

        int highestInnovationGene2 = 0;
        if(!g2.getConnections().isEmpty()){
            highestInnovationGene2 = g2.getConnections().keySet().stream().max(Integer::compare).get();
        }

        if (highestInnovationGene1 < highestInnovationGene2) {
            Genome g = g1;
            g1 = g2;
            g2 = g;
        }

        // Convert TreeMap values to ArrayLists for indexed access
        List<ConnectionGene> g1Connections = new ArrayList<>(g1.getConnections().values());
        List<ConnectionGene> g2Connections = new ArrayList<>(g2.getConnections().values());

        int indexG1 = 0;
        int indexG2 = 0;

        double disjoint = 0;
        int excess;
        double weightDiff = 0;
        int similar = 0;

        while (indexG1 < g1.getConnections().size() && indexG2 < g2.getConnections().size()) {
            ConnectionGene gene1 = g1Connections.get(indexG1);
            ConnectionGene gene2 = g2Connections.get(indexG2);

            int in1 = gene1.getInnovationNumber();
            int in2 = gene2.getInnovationNumber();

            if (in1 == in2) {
                // Similar gene
                indexG1++;
                indexG2++;

                similar++;
                weightDiff += Math.abs(gene1.getWeight() - gene2.getWeight());

            } else if (in1 > in2) {
                //Disjoint gene of 2
                indexG2++;
                disjoint++;
            } else {
                //Disjoint gene of 1
                indexG1++;
                disjoint++;
            }
        }

        excess = g1.getConnections().size() - indexG1;

        if (weightDiff > 0) {
           weightDiff = weightDiff / similar;
        }

        double N = Math.max(g1.getConnections().size(), g2.getConnections().size());
        if (N<20) {
            N = 1;
        }

        return ((Neat.C1*excess) / N) + ((Neat.C2*disjoint) / N) + (Neat.C3*weightDiff);
    }

    public static Genome crossover(Genome g1, Genome g2) {
        Genome genome = g1.getNeat().emptyGenome();

        int indexG1 = 0;
        int indexG2 = 0;

        // Convert TreeMap values to ArrayLists for indexed access
        List<ConnectionGene> g1Connections = new ArrayList<>(g1.getConnections().values());
        List<ConnectionGene> g2Connections = new ArrayList<>(g2.getConnections().values());

        while (indexG1 < g1.getConnections().size() && indexG2 < g2.getConnections().size()) {
            ConnectionGene gene1 = g1Connections.get(indexG1);
            ConnectionGene gene2 = g2Connections.get(indexG2);

            int in1 = gene1.getInnovationNumber();
            int in2 = gene2.getInnovationNumber();

            if (in1 == in2) {
                // Similar gene

                if (Math.random() > 0.5) {
                    genome.getConnections().put(Neat.getConnection(gene1).getInnovationNumber(), Neat.getConnection(gene1));
                } else {
                    genome.getConnections().put(Neat.getConnection(gene2).getInnovationNumber(), Neat.getConnection(gene2));
                }

                indexG1++;
                indexG2++;
            } else if (in1 > in2) {
                //Disjoint gene of 2
                indexG2++;
            } else {
                //Disjoint gene of 1
                genome.getConnections().put(Neat.getConnection(gene1).getInnovationNumber(), Neat.getConnection(gene1));

                indexG1++;
            }
        }

        while (indexG1 < g1.getConnections().size()) {

            ConnectionGene gene1 = g1Connections.get(indexG1);
            genome.getConnections().put(Neat.getConnection(gene1).getInnovationNumber(), Neat.getConnection(gene1));

            indexG1++;

        }

        for (ConnectionGene c : genome.getConnections().values()) {
             genome.getNodes().put(c.getFrom().getInnovationNumber(), c.getFrom());
             genome.getNodes().put(c.getTo().getInnovationNumber(), c.getTo());
        }

        return genome;
    }

    public Neat getNeat() {
        return neat;
    }

    private ConnectionGene getRandomConnection() {
        if (connections.isEmpty()) return null;

        List<Map.Entry<Integer, ConnectionGene>> entryList = new ArrayList<>(connections.entrySet());

        Random random = new Random();
        int randomIndex = random.nextInt(entryList.size());

        Map.Entry<Integer, ConnectionGene> randomEntry = entryList.get(randomIndex);
        return randomEntry.getValue();
    }

    private NodeGene getRandomNode() {
        List<Map.Entry<Integer, NodeGene>> entryList = new ArrayList<>(nodes.entrySet());

        Random random = new Random();
        int randomIndex = random.nextInt(entryList.size());

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
