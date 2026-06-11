package snaketrainer.evolution.reproduction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.WeightVector;

/**
 * Weight mutation operator used after an individual has already been selected
 * for mutation. It no longer decides a probability per weight; it mutates a
 * fixed number of weights associated with active features.
 */
public class UniformMutation implements MutationStrategy {
    private final int mutatedWeightsPerMutation;
    private final double percentageMutationStrength;
    private final double minimumMutationStep;
    private final Random random;

    public UniformMutation(
            int mutatedWeightsPerMutation,
            double percentageMutationStrength,
            double minimumMutationStep,
            Random random
    ) {
        this.mutatedWeightsPerMutation = mutatedWeightsPerMutation;
        this.percentageMutationStrength = percentageMutationStrength;
        this.minimumMutationStep = minimumMutationStep;
        this.random = random;
    }

    @Override
    public WeightVector mutate(WeightVector weights) {
        double[] values = weights.toArray();
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < values.length; i++) {
            indices.add(i);
        }

        mutateSelectedIndices(values, indices);
        return new WeightVector(values);
    }

    public WeightVector mutateActiveWeights(WeightVector weights, FeatureGenome genome) {
        double[] values = weights.toArray();
        List<Integer> activeIndices = new ArrayList<>();

        for (FeatureName featureName : FeatureName.values()) {
            if (genome.isEnabled(featureName)) {
                activeIndices.add(featureName.ordinal());
            }
        }

        if (activeIndices.isEmpty()) {
            for (int i = 0; i < values.length; i++) {
                activeIndices.add(i);
            }
        }

        mutateSelectedIndices(values, activeIndices);
        return new WeightVector(values);
    }

    private void mutateSelectedIndices(double[] values, List<Integer> candidateIndices) {
        Collections.shuffle(candidateIndices, random);
        int mutations = Math.min(mutatedWeightsPerMutation, candidateIndices.size());

        for (int i = 0; i < mutations; i++) {
            int index = candidateIndices.get(i);
            double variationLimit = Math.max(
                    minimumMutationStep,
                    Math.abs(values[index]) * percentageMutationStrength
            );

            double variation = -variationLimit + random.nextDouble() * 2.0 * variationLimit;
            values[index] = WeightVector.clamp(values[index] + variation);
        }
    }
}
