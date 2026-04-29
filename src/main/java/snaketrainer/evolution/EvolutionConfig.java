package snaketrainer.evolution;

public class EvolutionConfig {
    private final int generations;
    private final int agentsPerGeneration;
    private final int eliteCount;
    private final int tournamentSize;
    private final double mutationRate;
    private final double mutationStrength;

    public EvolutionConfig(int generations, int agentsPerGeneration) {
        this(generations, agentsPerGeneration, 1, 3, 0.10, 0.20);
    }

    public EvolutionConfig(
            int generations,
            int agentsPerGeneration,
            int eliteCount,
            int tournamentSize,
            double mutationRate,
            double mutationStrength
    ) {
        this.generations = generations;
        this.agentsPerGeneration = agentsPerGeneration;
        this.eliteCount = eliteCount;
        this.tournamentSize = tournamentSize;
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
    }

    public int getGenerations() {
        return generations;
    }

    public int getAgentsPerGeneration() {
        return agentsPerGeneration;
    }

    public int getEliteCount() {
        return eliteCount;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getMutationStrength() {
        return mutationStrength;
    }
}