package snaketrainer.evolution.evaluation;

public class FitnessResult {
    private final int apples;
    private final int steps;
    private final EndCause endCause;

    public FitnessResult(int apples, int steps, EndCause endCause) {
        this.apples = apples;
        this.steps = steps;
        this.endCause = endCause;
    }

    public int getApples() {
        return apples;
    }

    public int getSteps() {
        return steps;
    }

    public EndCause getEndCause() {
        return endCause;
    }
}