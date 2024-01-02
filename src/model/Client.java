package model;

import model.genes.Genome;

public class Client {

    private Genome genome;
    private double score;
    private Species species;

    public double[] calculate(double[] inputs) {
        return this.genome.calculateOutput(inputs);
    }

    public double distance (Client other) {
        return this.getGenome().distance(other.getGenome());
    }

    public void mutate () {
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
