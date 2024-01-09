package model;

import model.genes.ConnectionGene;
import model.genes.Genome;
import model.genes.NodeGene;
import model.genes.NodeType;
import ui.Frame;

import java.util.*;

public class Neat {

    public static final double MUTATE_LINK_RATE = 0.3;
    public static final double MUTATE_NODE_RATE = 0.03;
    public static final double MUTATE_WEIGHT_SHIFT_RATE = 0.02;
    public static final double MUTATE_WEIGHT_RANDOM_RATE = 0.02;
    public static final double MUTATE_TOGGLE_RATE = 0.2;
    public static final double WEIGHT_SHIFT_STRENGTH = 0.3;
    public static final double WEIGHT_RANDOM_STRENGTH = 1;
    public static final double SURVIVAL_PERCENTAGE = 80;
    public static final double C1 = 1;
    public static final double C2 = 1;
    public static final double C3 = 0.4;
    public static final double CP = 4;

    private final HashMap<ConnectionGene, ConnectionGene> allConnections = new HashMap<>();
    private final List<NodeGene> allNodes = new ArrayList<>();
    private int inputSize;
    private int outputSize;
    private final ArrayList<Individual> individuals = new ArrayList<>();
    private final ArrayList<Species> species = new ArrayList<>();

    public Neat(int inputSize, int outputSize, int individuals) {
         this.initialize(inputSize, outputSize, individuals);
    }

    public Genome emptyGenome () {
        Genome g = new Genome(this);
        for(int i = 0; i < inputSize + outputSize; i++){
            g.getNodes().put(i+1, getNode(i+1));
        }
        return g;
    }

    private void initialize(int inputSize, int outputSize, int individuals) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;

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

        for (int i = 0; i < individuals; i++) {
            Individual individual = new Individual();
            individual.setGenome(emptyGenome());
            this.individuals.add(individual);
        }
    }

    public void evolvePopulation() {
        generateSpecies();
        eliminateWeakIndividuals();
        removeEmptySpecies();
        reproducePopulation();
        mutatePopulation();
    }

    private void mutatePopulation() {
        for (Individual individual : individuals) {
            individual.mutate();
        }
    }

    private void reproducePopulation() {
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

    private void removeEmptySpecies() {
        for(int i = species.size()-1; i>= 0; i--){
            if(species.get(i).size() <= 1){
                species.get(i).goExtinct();
                species.remove(i);
            }
        }
    }

    private void eliminateWeakIndividuals() {
        for (Species species : species) {
            species.kill(100 - SURVIVAL_PERCENTAGE);
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

    public static void main(String[] args) {

        Neat neat = new Neat(3, 1, 250);

        double[][] inputs = {{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        double[] expectedOutputs = {0, 1, 1, 0};

        double fitnessThreshold = 3.9;
        double bestFitness = 0;
        int generation = 0;

        while (bestFitness < fitnessThreshold) {
            bestFitness = 0;
            for (Individual individual : neat.individuals) {
                double fitness = 0;
                for (int i = 0; i < inputs.length; i++) {
                    double[] output = individual.calculateOutput(inputs[i]);
                    double error = Math.abs(expectedOutputs[i] - output[0]);
                    fitness += 1 - error;
                }
                individual.setScore(fitness);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                }
            }

            System.out.println("Generation " + generation + " - Best Fitness: " + bestFitness);
            neat.evolvePopulation();

            if (bestFitness >= fitnessThreshold) {
                System.out.println("Satisfactory fitness level reached at Generation " + generation);
                break;
            }

            generation++;
        }

        Individual bestIndividual = null;
        for (Individual individual : neat.individuals) {
            if (bestIndividual == null || individual.getScore() > bestIndividual.getScore()) {
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

            new Frame(bestIndividual.getGenome());
        } else {
            System.out.println("No suitable model found for demonstrating XOR.");
        }
    }
}
