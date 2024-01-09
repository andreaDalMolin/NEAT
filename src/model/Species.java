package model;

import model.genes.Genome;

import java.util.*;

/**
 * @author Andrea Dal Molin
 * Represents a species in the NEAT algorithm.
 * A species groups similar Individuals based on a compatibility threshold.
 */
public class Species {

    private final HashSet<Individual> individualMembers = new HashSet<>();
    private Individual champion;
    private double score;

    /**
     * Constructs a Species with an initial representative.
     * @param champion The initial representative of the species.
     */
    public Species(Individual champion) {
        this.champion = champion;
        this.champion.setSpecies(this);
        individualMembers.add(champion);
        score = 0;
    }

    /**
     * Adds an individual to the species if it is compatible.
     * @param individual The individual to be added.
     * @return True if added, false otherwise.
     */
    public boolean addIndividualIfCompatible(Individual individual) {
        if (individual.distance(champion) < Neat.CP) {
            individual.setSpecies(this);
            individualMembers.add(individual);
            return true;
        }
        return false;
    }

    /**
     * Forces the addition of an individual to the species.
     * @param individual The individual to be added.
     */
    public void addIndividual(Individual individual) {
        individual.setSpecies(this);
        individualMembers.add(individual);
    }

    /**
     * Handles the extinction of the species.
     * Removes the species association from all members.
     */
    public void goExtinct() {
        for (Individual individual : individualMembers) {
            individual.setSpecies(null);
        }
    }

    /**
     * Calculates and updates the overall score of the species.
     */
    public void evaluateScore() {
        score = 0;
        for (Individual individual : individualMembers) {
            score += individual.getScore();
        }
        this.score = score / individualMembers.size();
    }

    /**
     * Gets the current score of the species.
     * The score is typically updated periodically based on the performance of its members.
     */
    public void reset() {
        champion = getRandomClient();
        for (Individual individual : individualMembers) {
            individual.setSpecies(null);
        }
        individualMembers.clear();
        individualMembers.add(champion);
        champion.setSpecies(this);
        score = 0;
    }

    /**
     * Removes a certain percentage of individuals from the species, based on their score.
     * The individuals with the lowest scores are removed first. This is one of the most
     * important parts of NEAT
     *
     * @param percentage The percentage of individuals to be removed from the species.
     *                   Must be a value between 0 and 100.
     */
    public void kill(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        int clientsToRemove = (int) Math.ceil(individualMembers.size() * (percentage / 100.0));
        List<Individual> sortedIndividuals = new ArrayList<>(individualMembers);
        sortedIndividuals.sort(Comparator.comparingDouble(Individual::getScore));

        for (int i = 0; i < clientsToRemove; i++) {
            Individual removedIndividual = sortedIndividuals.get(i);
            removedIndividual.setSpecies(null);
            individualMembers.remove(removedIndividual);
        }
    }

    /**
     * Performs breeding of two randomly selected individuals within the species to produce a new genome.
     * The method selects two individuals and crosses their genomes. The individual with the higher score
     * is chosen as the first parent in the crossover.
     *
     * @return A new Genome resulting from the crossover of two selected individuals' genomes.
     */
    public Genome breed () {
        Individual individual1 = getRandomClient();
        Individual individual2 = getRandomClient();

        if (individual1.getScore() > individual2.getScore()) return Genome.crossover(individual1.getGenome(), individual2.getGenome());
        return Genome.crossover(individual2.getGenome(), individual1.getGenome());
    }

    public int size () {
        return individualMembers.size();
    }

    public double getScore() {
        return score;
    }

    private Individual getRandomClient() {
        Random random = new Random();
        int randomIndex = random.nextInt(individualMembers.size());
        return new ArrayList<>(individualMembers).get(randomIndex);
    }
}
