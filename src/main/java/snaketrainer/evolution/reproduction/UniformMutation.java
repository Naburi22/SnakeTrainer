package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.WeightVector;

public class UniformMutation implements MutationStrategy {
    private final double mutationRate;
    private final double mutationStrength;
    private final Random random;

    public UniformMutation(double mutationRate, double mutationStrength, Random random) {
        this.mutationRate = mutationRate;
        this.mutationStrength = mutationStrength;
        this.random = random;
    }

    @Override
    public WeightVector mutate(WeightVector weights) {
        double[] values = weights.toArray();

        for (int i = 0; i < values.length; i++) {
            if (random.nextDouble() < mutationRate) {
                double variation = -mutationStrength + random.nextDouble() * 2.0 * mutationStrength;
                values[i] = WeightVector.clamp(values[i] + variation);
            }
        }

        return new WeightVector(values);
    }
}