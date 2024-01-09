package model.genes;

public enum NodeType {
    INPUT(0.1),
    OUTPUT(0.9),
    HIDDEN(0.5), // Example value, adjust as needed
    BIAS(0.0);   // Example value, adjust as needed

    private final double value;

    NodeType(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
