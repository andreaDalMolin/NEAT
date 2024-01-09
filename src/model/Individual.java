package model;

import model.genes.Genome;

/**
 * @author Andrea Dal Molin
 * Represents an individual in the NEAT algorithm.
 * An individual is characterized by its genome, which encodes the structure and parameters of a neural network.
 * The individual's performance is evaluated through a fitness score, and it can belong to a species.
 */
public class Individual {

    private Genome genome;
    private double score;
    private Species species;

    /**
     * Calculates the output of the neural network represented by the individual's genome given specific inputs.
     * @param inputs The inputs to the neural network.
     * @return The output from the neural network.
     */
    public double[] calculateOutput(double[] inputs) {
        return this.genome.calculateOutput(inputs);
    }

    /**
     * Computes the genetic distance between this individual and another. This is necessary for species categorization.
     * @param other The other individual to compare with.
     * @return The genetic distance.
     */
    public double distance(Individual other) {
        return this.getGenome().distance(other.getGenome());
    }

    /**
     * Performs mutation on the individual's genome, potentially altering its structure and parameters.
     */
    public void mutate() {
        genome.mutate();
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Species getSpecies() {
        return species;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }
}
