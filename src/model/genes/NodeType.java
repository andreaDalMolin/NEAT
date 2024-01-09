package model.genes;

public enum NodeType {
    INPUT(0.1),
    OUTPUT(0.9),
    HIDDEN(0.5),
    BIAS(0.0);

    private final double value;

    NodeType(double value) {
        this.value = value;
    }

}
