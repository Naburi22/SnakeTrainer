package snaketrainer.evolution;

/**
 * Mutable evolutionary parameters used by the learning controller.
 * EvolutionConfig keeps the fixed run configuration, while this class contains
 * the values that may change during training.
 */
public class EvolutionParameters {
    private final int tournamentSize;
    private final double minimumMutationStep;
    private final double mixedMutationTypeRate;
    private final int mutatedWeightsPerMutation;
    private final double crossoverRate;
    private final double directCopySuperiorRate;
    private final double featureSuperiorInheritanceRate;

    private double individualMutationRate;
    private double weightMutationPercentage;
    private double genomeMutationTypeRate;

    public EvolutionParameters(EvolutionConfig config) {
        this.tournamentSize = config.getTournamentSize();
        this.individualMutationRate = config.getIndividualMutationRate();
        this.weightMutationPercentage = config.getWeightMutationPercentage();
        this.minimumMutationStep = config.getMinimumMutationStep();
        this.genomeMutationTypeRate = config.getGenomeMutationTypeRate();
        this.mixedMutationTypeRate = config.getMixedMutationTypeRate();
        this.mutatedWeightsPerMutation = config.getMutatedWeightsPerMutation();
        this.crossoverRate = config.getCrossoverRate();
        this.directCopySuperiorRate = config.getDirectCopySuperiorRate();
        this.featureSuperiorInheritanceRate = config.getFeatureSuperiorInheritanceRate();
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public double getIndividualMutationRate() {
        return individualMutationRate;
    }

    public void setIndividualMutationRate(double individualMutationRate) {
        this.individualMutationRate = individualMutationRate;
    }

    public double getWeightMutationPercentage() {
        return weightMutationPercentage;
    }

    public void setWeightMutationPercentage(double weightMutationPercentage) {
        this.weightMutationPercentage = weightMutationPercentage;
    }

    public double getMinimumMutationStep() {
        return minimumMutationStep;
    }

    public double getGenomeMutationTypeRate() {
        return genomeMutationTypeRate;
    }

    public void setGenomeMutationTypeRate(double genomeMutationTypeRate) {
        this.genomeMutationTypeRate = genomeMutationTypeRate;
    }

    public double getMixedMutationTypeRate() {
        return mixedMutationTypeRate;
    }

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
