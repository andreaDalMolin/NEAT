public class InnovationCounter {
    private int globalInnovationNumber = 0;

    public int getInnovation() {
        return globalInnovationNumber++;
    }
}
