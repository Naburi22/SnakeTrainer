package snaketrainer.evolution;

/**
 * Mutable evolutionary parameters used by the learning controller.
 * EvolutionConfig keeps the fixed run configuration, while this class contains
 * the values that may change during training.
 */
public class EvolutionParameters {
    private final int tournamentSize;
    private final double minimumMutationStep;
    private final double crossoverRate;
    private final double directCopySuperiorRate;
    private final double featureSuperiorInheritanceRate;

    private double mutationRate;
    private double weightMutationPercentage;
    private double featureMutationRate;

    public EvolutionParameters(EvolutionConfig config) {
        this.tournamentSize = config.getTournamentSize();
        this.mutationRate = config.getMutationRate();
        this.weightMutationPercentage = config.getMutationStrength();
        this.minimumMutationStep = config.getMinimumMutationStep();
        this.featureMutationRate = config.getFeatureMutationRate();
        this.crossoverRate = config.getCrossoverRate();
        this.directCopySuperiorRate = config.getDirectCopySuperiorRate();
        this.featureSuperiorInheritanceRate = config.getFeatureSuperiorInheritanceRate();
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
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

    public double getFeatureMutationRate() {
        return featureMutationRate;
    }

    public void setFeatureMutationRate(double featureMutationRate) {
        this.featureMutationRate = featureMutationRate;
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
