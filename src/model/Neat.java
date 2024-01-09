package model;

import model.genes.ConnectionGene;
import model.genes.Genome;
import model.genes.NodeGene;
import model.genes.NodeType;
import ui.Frame;

import java.util.*;

public class Neat {

    public static final int MAX_NODES = 1000;
    public static final double MUTATE_LINK_RATE = 0.3;
    public static final double MUTATE_NODE_RATE = 0.03;
    public static final double MUTATE_WEIGHT_SHIFT_RATE = 0.02;
    public static final double MUTATE_WEIGHT_RANDOM_RATE = 0.02;
    public static final double MUTATE_TOGGLE_RATE = 0;
    public static final double WEIGHT_SHIFT_STRENGTH = 0.3;
    public static final double WEIGHT_RANDOM_STRENGTH = 1;
    public static final double SURVIVORS = 80;
    public static final double C1 = 1;
    public static final double C2 = 1;
    public static final double C3 = 0.4;
    public static final double CP = 4;

    private HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>();
    private List<NodeGene> allNodes = new ArrayList<>();
    private int inputSize;
    private int outputSize;
    private int maxClients;
    private ArrayList<Individual> individuals = new ArrayList<>();
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
        this.individuals.clear();

        for(int i = 0;i < inputSize; i++){
            NodeGene n = getNode();
            n.setType(NodeType.INPUT);
            n.setY((i + 1) / (double)(inputSize + 1));
        }

        for(int i = 0; i < outputSize; i++){
            NodeGene n = getNode();
            n.setType(NodeType.OUTPUT);
            n.setY((i + 1) / (double)(outputSize + 1));
        }

        for (int i = 0; i < maxClients; i++) {
            Individual individual = new Individual();
            individual.setGenome(emptyGenome());
            this.individuals.add(individual);
        }
    }

    public Individual getClient (int index) {
        return individuals.get(index);
    }

    public void evolve () {

        generateSpecies();
        kill();
        prune();
        reproduce();
        mutate();

    }

    private void mutate() {
        for (Individual individual : individuals) {
            individual.mutate();
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

        for(Individual c: individuals){
            if(c.getSpecies() == null){
                Species s = selector.random();
                c.setGenome(s.breed());
                s.addIndividual(c);
            }
        }
    }

    private void prune() {
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

        for (Individual individual : individuals) {
            if (individual.getSpecies() != null) continue;

            boolean found = false;

            for(Species species : species) {
                if (species.addIndividualIfCompatible(individual)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                species.add(new Species(individual));
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
        c.setInnovationNumber(connectionGene.getInnovationNumber());
        c.setWeight(connectionGene.getWeight());

        return c;
    }

    public ConnectionGene getConnection (NodeGene node1, NodeGene node2) {
        ConnectionGene connectionGene = new ConnectionGene(node1, node2);

        if (allConnections.containsKey(connectionGene)) {
             connectionGene.setInnovationNumber(allConnections.get(connectionGene).getInnovationNumber());
        } else {
            connectionGene.setInnovationNumber(allConnections.size() + 1);
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

        Neat neat = new Neat(3, 1, 1000); // 2 inputs, 1 output, 1000 clients

        // XOR input and output pairs
        double[][] inputs = {{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        double[] expectedOutputs = {0, 1, 1, 0};

        // Evolve over several generations
        for (int generation = 0; generation < 300; generation++) {
            for (Individual individual : neat.individuals) {
                double fitness = 0;
                for (int i = 0; i < inputs.length; i++) {
                    double[] output = individual.calculateOutput(inputs[i]);
                    double error = Math.abs(expectedOutputs[i] - output[0]);
                    fitness += 1 - error; // Fitness based on closeness to expected XOR output
                }
                individual.setScore(fitness);
            }

            neat.evolve(); // Evolve the population
        }

        // Finding the best performing client
        Individual bestIndividual = null;
        double bestFitness = -1;
        for (Individual individual : neat.individuals) {
            if (individual.getScore() > bestFitness) {
                bestFitness = individual.getScore();
                bestIndividual = individual;
            }
        }

        if (bestIndividual != null) {
            System.out.println("Demonstrating XOR with the best model:");
            System.out.println("Input1, Input2 -> Predicted Output : Actual Output");

            for (int i = 0; i < inputs.length; i++) {
                double[] output = bestIndividual.calculateOutput(inputs[i]);
                System.out.println(inputs[i][0] + ", " + inputs[i][1] + " -> " + output[0] + " : " + expectedOutputs[i]);
            }
        } else {
            System.out.println("No suitable model found for demonstrating XOR bordel.");
        }

        // After the evolution, you can examine the fittest networks to see if they solve XOR
        new Frame((neat.getClient(0).getGenome()));

    }
}
