package snaketrainer.evolution;

import snaketrainer.agent.WeightedAgent;
import snaketrainer.evolution.evaluation.EndCause;

public class Individual implements Comparable<Individual> {
    private final WeightedAgent agent;
    private final int apples;
    private final int steps;
    private final EndCause endCause;

    public Individual(WeightedAgent agent, int apples, int steps, EndCause endCause) {
        this.agent = agent;
        this.apples = apples;
        this.steps = steps;
        this.endCause = endCause;
    }

    public WeightedAgent getAgent() {
        return agent;
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

    @Override
    public int compareTo(Individual other) {
        int appleComparison = Integer.compare(other.apples, this.apples);

        if (appleComparison != 0) {
            return appleComparison;
        }

        return Integer.compare(this.steps, other.steps);
    }
}