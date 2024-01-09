package model.genes;

public class Gene {

    protected int innovationNumber;

    public Gene(){}

    public Gene(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }
}
