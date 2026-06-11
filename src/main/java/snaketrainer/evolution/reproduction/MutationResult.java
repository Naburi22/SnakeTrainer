package snaketrainer.evolution.reproduction;

import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.WeightVector;

public class MutationResult {
    private final WeightVector weights;
    private final FeatureGenome genome;

    public MutationResult(WeightVector weights, FeatureGenome genome) {
        this.weights = weights;
        this.genome = genome;
    }

    public WeightVector getWeights() {
        return weights;
    }

    public FeatureGenome getGenome() {
        return genome;
    }
}
