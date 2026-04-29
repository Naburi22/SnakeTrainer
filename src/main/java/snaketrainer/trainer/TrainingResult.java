package snaketrainer.trainer;

import snaketrainer.agent.SnakeAgent;
import snaketrainer.agent.WeightVector;

public class TrainingResult {
    private final SnakeAgent bestAgent;
    private final WeightVector bestWeights;
    private final int bestScore;
    private final int bestSteps;
    private final int generations;
    private final int agentsPerGeneration;

    public TrainingResult(
            SnakeAgent bestAgent,
            WeightVector bestWeights,
            int bestScore,
            int bestSteps,
            int generations,
            int agentsPerGeneration
    ) {
        this.bestAgent = bestAgent;
        this.bestWeights = bestWeights;
        this.bestScore = bestScore;
        this.bestSteps = bestSteps;
        this.generations = generations;
        this.agentsPerGeneration = agentsPerGeneration;
    }

    public SnakeAgent getBestAgent() {
        return bestAgent;
    }

    public WeightVector getBestWeights() {
        return bestWeights;
    }

    public int getBestScore() {
        return bestScore;
    }

    public int getBestSteps() {
        return bestSteps;
    }

    public int getGenerations() {
        return generations;
    }

    public int getAgentsPerGeneration() {
        return agentsPerGeneration;
    }
}