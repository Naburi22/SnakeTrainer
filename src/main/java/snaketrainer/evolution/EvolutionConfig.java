package snaketrainer.evolution;

public class EvolutionConfig {
    private final int generations;
    private final int agentsPerGeneration;
    private final int eliteCount;
    private final int tournamentSize;
    private final double mutationRate;
    private final double mutationStrength;
    private final double minimumMutationStep;
    private final double featureMutationRate;
    private final double crossoverRate;
    private final double directCopySuperiorRate;
    private final double featureSuperiorInheritanceRate;

    public EvolutionConfig(int generations, int agentsPerGeneration) {
        this(
                generations,
                agentsPerGeneration,
                1,
                3,
                0.10,
                0.10,
                0.05,
                0.03,
                0.85,
                0.65,
                0.65
        );
    }

    public EvolutionConfig(
            int generations,
            int agentsPerGeneration,
            int eliteCount,
            int tournamentSize,
            double mutationRate,
            double mutationStrength,
            double minimumMutationStep,
            double featureMutationRate,
            double crossoverRate,
            double directCopySuperiorRate,
            double featureSuperiorInheritanceRate
    ) {
        this.generations = generations;
        this.agentsPerGeneration = agentsPerGeneration;
        this.eliteCount = eliteCount;
        this.tournamentSize = tournamentSize;
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
        this.minimumMutationStep = minimumMutationStep;
        this.featureMutationRate = featureMutationRate;
        this.crossoverRate = crossoverRate;
        this.directCopySuperiorRate = directCopySuperiorRate;
        this.featureSuperiorInheritanceRate = featureSuperiorInheritanceRate;
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

    /**
     * Interpreted as percentageMutationStrength.
     * Example: 0.10 means +/-10% of the absolute value of the weight.
     */
    public double getMutationStrength() {
        return mutationStrength;
    }

    public double getMinimumMutationStep() {
        return minimumMutationStep;
    }

    public double getFeatureMutationRate() {
        return featureMutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public double getDirectCopySuperiorRate() {
        return directCopySuperiorRate;
    }

    public double getFeatureSuperiorInheritanceRate() {
        return featureSuperiorInheritanceRate;
    }
}
