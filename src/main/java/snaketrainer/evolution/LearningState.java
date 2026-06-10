package snaketrainer.evolution;

/**
 * Snapshot of the learning controller after processing one generation.
 * It is mainly used for logging and debugging training behaviour.
 */
public class LearningState {
    private final int generation;
    private final int stagnationWindow;
    private final int generationsWithoutImprovement;
    private final double genomeDiversity;
    private final double mutationRate;
    private final double weightMutationPercentage;
    private final double featureMutationRate;
    private final String decision;

    public LearningState(
            int generation,
            int stagnationWindow,
            int generationsWithoutImprovement,
            double genomeDiversity,
            double mutationRate,
            double weightMutationPercentage,
            double featureMutationRate,
            String decision
    ) {
        this.generation = generation;
        this.stagnationWindow = stagnationWindow;
        this.generationsWithoutImprovement = generationsWithoutImprovement;
        this.genomeDiversity = genomeDiversity;
        this.mutationRate = mutationRate;
        this.weightMutationPercentage = weightMutationPercentage;
        this.featureMutationRate = featureMutationRate;
        this.decision = decision;
    }

    public int getGeneration() {
        return generation;
    }

    public int getStagnationWindow() {
        return stagnationWindow;
    }

    public int getGenerationsWithoutImprovement() {
        return generationsWithoutImprovement;
    }

    public double getGenomeDiversity() {
        return genomeDiversity;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getWeightMutationPercentage() {
        return weightMutationPercentage;
    }

    public double getFeatureMutationRate() {
        return featureMutationRate;
    }

    public String getDecision() {
        return decision;
    }
}
