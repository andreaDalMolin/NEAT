package XOR;

import model.genes.Genome;

import java.util.ArrayList;

public interface FitnessEvaluator {
    void evaluateFitness(ArrayList<Genome> population);
}
