package model;

import model.genes.ConnectionGene;
import model.genes.Genome;
import model.genes.NodeGene;
import ui.Frame;

import java.util.*;

public class Neat {

    public static final int MAX_NODES = 1000;
    public static final double MUTATE_LINK_RATE = 0.1;
    public static final double MUTATE_NODE_RATE = 0.03;
    public static final double MUTATE_WEIGHT_SHIFT_RATE = 0.02;
    public static final double MUTATE_WEIGHT_RANDOM_RATE = 0.02;
    public static final double MUTATE_TOGGLE_RATE = 0;
    public static final double WEIGHT_SHIFT_STRENGTH = 0.3;
    public static final double WEIGHT_RANDOM_STRENGTH = 1;
    public static final double SURVIVORS = 80;
    public static final double C1 = 1.0;
    public static final double C2 = 1;
    public static final double C3 = 1;
    public static final double CP = 4;

    private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>();
    private List<NodeGene> allNodes = new ArrayList<>();
    private int inputSize;
    private int outputSize;
    private int maxClients;
    private ArrayList<Client> clients = new ArrayList<>();
    private ArrayList<Species> species = new ArrayList<>();

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
        this.clients.clear();

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

        for (int i = 0; i < maxClients; i++) {
            Client client = new Client();
            client.setGenome(emptyGenome());
            this.clients.add(client);
        }
    }

    public Client getClient (int index) {
        return clients.get(index);
    }

    public void evolve (double[] input) {

        generateSpecies();
        kill();
        removeExtinct();
        reproduce();
        mutate();

        for(Client client : clients) {
            client.getGenome().calculateOutput(input);
        }
    }

    private void mutate() {
        for (Client client : clients) {
            client.mutate();
        }
    }

    private void reproduce() {
//        for (Client client : clients) {
//            if (client.getSpecies() == null) {
//                Species randomSpecies = getRandomSpecies();
//                client.setGenome(randomSpecies.breed());
//                randomSpecies.forcePut(client);
//            }
//        }

        RandomSelector<Species> selector = new RandomSelector<>();
        for(Species s:species){
            selector.add(s, s.getScore());
        }

        for(Client c:clients){
            if(c.getSpecies() == null){
                Species s = selector.random();
                c.setGenome(s.breed());
                s.forcePut(c);
            }
        }
    }

    private void removeExtinct() {
        for(int i = species.size()-1; i>= 0; i--){
            if(species.get(i).size() <= 1){
                species.get(i).goExtinct();
                species.remove(i);
            }
        }
    }

    private void kill() {
        for (Species species : species) {
            species.kill(100 - SURVIVORS);
        }
    }

    private void generateSpecies() {
        for(Species species : species) {
            species.reset();
        }

        for (Client client : clients) {
            if (client.getSpecies() != null) continue;

            boolean found = false;

            for(Species species : species) {
                if (species.putClient(client)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                species.add(new Species(client));
            }
        }

        for (Species species : species) {
            species.evaluateScore();
        }
    }

    public Species getRandomSpecies() {
        if (species.isEmpty()) return null;

        Random random = new Random();
        int randomIndex = random.nextInt(species.size());
        return species.get(randomIndex);
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

    public void printSpecies () {
        System.out.println("################################");
        for (Species species : species) {
            System.out.println(species + " " + species.getScore() + " Individuals " + species.size());
        }
    }

    public static void main(String[] args) {
        Neat neat = new Neat(10,1,1000);

        double[] in = new double[10];
        for (int i = 0; i < 10; i++) {
            in[i] = Math.random();
        }

        for (int i = 0; i < 100; i++) {
            for (Client client : neat.clients) {
                double score = client.calculate(in)[0];
                client.setScore(score);
            }
            neat.evolve(in);
            neat.printSpecies();
        }

//        new Frame((neat.emptyGenome()));

    }
}
