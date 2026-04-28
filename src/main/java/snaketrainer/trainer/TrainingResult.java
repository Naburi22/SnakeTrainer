package snaketrainer.trainer;

import snaketrainer.agent.SnakeAgent;

public class TrainingResult {
    private final SnakeAgent bestAgent;
    private final int bestScore;
    private final int generations;
    private final int agentsPerGeneration;

    public TrainingResult(SnakeAgent bestAgent, int bestScore, int generations, int agentsPerGeneration) {
        this.bestAgent = bestAgent;
        this.bestScore = bestScore;
        this.generations = generations;
        this.agentsPerGeneration = agentsPerGeneration;
    }

    public SnakeAgent getBestAgent() {
        return bestAgent;
    }

    public int getBestScore() {
        return bestScore;
    }

    public int getGenerations() {
        return generations;
    }

    public int getAgentsPerGeneration() {
        return agentsPerGeneration;
    }
}
