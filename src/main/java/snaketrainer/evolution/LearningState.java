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
    private final double individualMutationRate;
    private final double weightMutationPercentage;
    private final double weightMutationTypeRate;
    private final double genomeMutationTypeRate;
    private final double mixedMutationTypeRate;
    private final String decision;

    public LearningState(
            int generation,
            int stagnationWindow,
            int generationsWithoutImprovement,
            double genomeDiversity,
            double individualMutationRate,
            double weightMutationPercentage,
            double weightMutationTypeRate,
            double genomeMutationTypeRate,
            double mixedMutationTypeRate,
            String decision
    ) {
        this.generation = generation;
        this.stagnationWindow = stagnationWindow;
        this.generationsWithoutImprovement = generationsWithoutImprovement;
        this.genomeDiversity = genomeDiversity;
        this.individualMutationRate = individualMutationRate;
        this.weightMutationPercentage = weightMutationPercentage;
        this.weightMutationTypeRate = weightMutationTypeRate;
        this.genomeMutationTypeRate = genomeMutationTypeRate;
        this.mixedMutationTypeRate = mixedMutationTypeRate;
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

    public double getIndividualMutationRate() {
        return individualMutationRate;
    }

    public double getWeightMutationPercentage() {
        return weightMutationPercentage;
    }

    public double getWeightMutationTypeRate() {
        return weightMutationTypeRate;
    }

    public double getGenomeMutationTypeRate() {
        return genomeMutationTypeRate;
    }

    public double getMixedMutationTypeRate() {
        return mixedMutationTypeRate;
    }

    public String getDecision() {
        return decision;
    }
}
