package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.FeatureGenome;

public class FeatureGenomeMutation {
    private final double mutationRate;
    private final Random random;

    public FeatureGenomeMutation(double mutationRate, Random random) {
        this.mutationRate = mutationRate;
        this.random = random;
    }

    public FeatureGenome mutate(FeatureGenome genome) {
        boolean[] enabled = genome.toArray();

        for (int i = 0; i < enabled.length; i++) {
            if (random.nextDouble() < mutationRate) {
                enabled[i] = !enabled[i];
            }
        }

        return new FeatureGenome(enabled);
    }
}
