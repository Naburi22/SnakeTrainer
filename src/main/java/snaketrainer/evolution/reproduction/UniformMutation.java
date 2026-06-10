package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.WeightVector;

public class UniformMutation implements MutationStrategy {
    private final double mutationRate;
    private final double percentageMutationStrength;
    private final double minimumMutationStep;
    private final Random random;

    public UniformMutation(
            double mutationRate,
            double percentageMutationStrength,
            double minimumMutationStep,
            Random random
    ) {
        this.mutationRate = mutationRate;
        this.percentageMutationStrength = percentageMutationStrength;
        this.minimumMutationStep = minimumMutationStep;
        this.random = random;
    }

    @Override
    public WeightVector mutate(WeightVector weights) {
        double[] values = weights.toArray();

        for (int i = 0; i < values.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double variationLimit = Math.max(
                        minimumMutationStep,
                        Math.abs(values[i]) * percentageMutationStrength
                );

                double variation = -variationLimit + random.nextDouble() * 2.0 * variationLimit;
                values[i] = WeightVector.clamp(values[i] + variation);
            }
        }

        return new WeightVector(values);
    }
}
