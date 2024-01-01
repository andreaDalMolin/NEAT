package model;

import model.genes.ConnectionGene;
import model.genes.Genome;
import model.genes.NodeGene;
import ui.Frame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Neat {

    public static final int MAX_NODES = 1000;
    public static final double MUTATE_LINK_RATE = 0.4;
    public static final double MUTATE_NODE_RATE = 0.4;
    public static final double MUTATE_WEIGHT_SHIFT_RATE = 0.4;
    public static final double MUTATE_WEIGHT_RANDOM_RATE = 0.4;
    public static final double MUTATE_TOGGLE_RATE = 0.4;
    public static final double WEIGHT_SHIFT_STRENGTH = 0.3;
    public static final double WEIGHT_RANDOM_STRENGTH = 1;
    public static final double C1 = 1.0;
    public static final double C2 = 1;
    public static final double C3 = 1;

    private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>();
    private List<NodeGene> allNodes = new ArrayList<>();
    private int inputSize;
    private int outputSize;
    private int maxClients;

    public Neat(int inputSize, int outputSize, int clients) {
         this.reset(inputSize, outputSize, clients);
    }

    public Genome emptyGenome () {
        Genome g = new Genome(this);
        for(int i = 0; i < inputSize + outputSize; i++){
            g.getNodes().put(i+1, getNode(i+1));
        }
        return g;
    }

    private void reset(int inputSize, int outputSize, int clients) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.maxClients = clients;

        allConnections.clear();
        allNodes.clear();

        for(int i = 0;i < inputSize; i++){
            NodeGene n = getNode();
            n.setX(0.1);
            n.setY((i + 1) / (double)(inputSize + 1));
        }

        for(int i = 0; i < outputSize; i++){
            NodeGene n = getNode();
            n.setX(0.9);
            n.setY((i + 1) / (double)(outputSize + 1));
        }
    }

    /**
     *  Copies a model.genes.ConnectionGene
     *
     * @param connectionGene
     * @return
     */
    public static ConnectionGene getConnection (ConnectionGene connectionGene) {
        ConnectionGene c = new ConnectionGene(connectionGene.getFrom(), connectionGene.getTo());
        c.setEnabled(connectionGene.isEnabled());
        c.setInnovation_number(connectionGene.getInnovation_number());
        c.setWeight(connectionGene.getWeight());

        return c;
    }

    public ConnectionGene getConnection (NodeGene node1, NodeGene node2) {
        ConnectionGene connectionGene = new ConnectionGene(node1, node2);

        if (allConnections.containsKey(connectionGene)) {
             connectionGene.setInnovation_number(allConnections.get(connectionGene).getInnovation_number());
        } else {
            connectionGene.setInnovation_number(allConnections.size() + 1);
            allConnections.put(connectionGene, connectionGene);
        }

        return connectionGene;
    }

    /**
     * Creates a totally new node
     *
     * @return a new model.genes.NodeGene
     */
    public NodeGene getNode() {
        NodeGene n = new NodeGene(allNodes.size() + 1);
        allNodes.add(n);
        return n;
    }

    public NodeGene getNode(int id) {
        if (id <= allNodes.size()) return allNodes.get(id - 1);
        return getNode();
    }

    public static void main(String[] args) {
        Neat neat = new Neat(3,2,10);

        new Frame((neat.emptyGenome()));

    }
}
