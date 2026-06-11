package snaketrainer.evolution;

public class EvolutionConfig {
    private final int generations;
    private final int agentsPerGeneration;
    private final int eliteCount;
    private final int tournamentSize;
    private final double individualMutationRate;
    private final double weightMutationPercentage;
    private final double minimumMutationStep;
    private final double genomeMutationTypeRate;
    private final double mixedMutationTypeRate;
    private final int mutatedWeightsPerMutation;
    private final double crossoverRate;
    private final double directCopySuperiorRate;
    private final double featureSuperiorInheritanceRate;

    public EvolutionConfig(int generations, int agentsPerGeneration) {
        this(
                generations,
                agentsPerGeneration,
                1,
                3,
                0.25,
                0.10,
                0.05,
                0.25,
                0.15,
                2,
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
            double individualMutationRate,
            double weightMutationPercentage,
            double minimumMutationStep,
            double genomeMutationTypeRate,
            double mixedMutationTypeRate,
            int mutatedWeightsPerMutation,
            double crossoverRate,
            double directCopySuperiorRate,
            double featureSuperiorInheritanceRate
    ) {
        if (individualMutationRate < 0.0 || individualMutationRate > 1.0) {
            throw new IllegalArgumentException("La probabilidad de mutación individual debe estar en [0, 1].");
        }
        if (genomeMutationTypeRate < 0.0 || mixedMutationTypeRate < 0.0
                || genomeMutationTypeRate + mixedMutationTypeRate > 1.0) {
            throw new IllegalArgumentException("Las probabilidades de tipo de mutación no son válidas.");
        }
        if (mutatedWeightsPerMutation <= 0) {
            throw new IllegalArgumentException("Debe mutarse al menos un peso cuando se aplica mutación de pesos.");
        }

        this.generations = generations;
        this.agentsPerGeneration = agentsPerGeneration;
        this.eliteCount = eliteCount;
        this.tournamentSize = tournamentSize;
        this.individualMutationRate = individualMutationRate;
        this.weightMutationPercentage = weightMutationPercentage;
        this.minimumMutationStep = minimumMutationStep;
        this.genomeMutationTypeRate = genomeMutationTypeRate;
        this.mixedMutationTypeRate = mixedMutationTypeRate;
        this.mutatedWeightsPerMutation = mutatedWeightsPerMutation;
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

    /**
     * Probability that a non-elite descendant suffers any mutation.
     */
    public double getIndividualMutationRate() {
        return individualMutationRate;
    }

    /**
     * Percentage strength for weight mutation.
     * Example: 0.10 means +/-10% of the absolute value of the weight.
     */
    public double getWeightMutationPercentage() {
        return weightMutationPercentage;
    }

    public double getMinimumMutationStep() {
        return minimumMutationStep;
    }

    /**
     * Probability that a mutated individual receives a genome-only mutation.
     */
    public double getGenomeMutationTypeRate() {
        return genomeMutationTypeRate;
    }

    /**
     * Probability that a mutated individual receives a mixed mutation.
     */
    public double getMixedMutationTypeRate() {
        return mixedMutationTypeRate;
    }

    /**
     * Probability that a mutated individual receives a weight-only mutation.
     */
    public double getWeightMutationTypeRate() {
        return 1.0 - genomeMutationTypeRate - mixedMutationTypeRate;
    }

    public int getMutatedWeightsPerMutation() {
        return mutatedWeightsPerMutation;
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
