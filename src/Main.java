import model.Neat;

public class Main {
    public static void main(String[] args) {
        double[][] xorInputs = {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        };

        double[] expectedOutputs = {0, 1, 1, 0};

        int inputSize = 2;
        int outputSize = 1;
        int populationSize = 100;

        Neat neat = new Neat(populationSize, inputSize, outputSize);
//        neat.initializePopulation();
//
//        XOR.FitnessEvaluator evaluator = new XOR.XORFitnessEvaluator();
//        evaluator.evaluateFitness(neat.population);

        // Evaluate fitness, perform evolution, etc. (These methods need to be implemented in NEAT class)
        // For example:
        // neat.evaluateFitness();
        // neat.evolve();

        // Further steps: Implement NEAT evaluation, evolution, and control flow to solve XOR
    }
}