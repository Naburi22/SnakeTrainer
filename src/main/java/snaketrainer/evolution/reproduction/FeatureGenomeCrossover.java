package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.FeatureGenome;

public class FeatureGenomeCrossover {
    private final Random random;

    public FeatureGenomeCrossover(Random random) {
        this.random = random;
    }

    public FeatureGenome crossover(FeatureGenome first, FeatureGenome second) {
        boolean[] a = first.toArray();
        boolean[] b = second.toArray();
        boolean[] child = new boolean[a.length];

        for (int i = 0; i < child.length; i++) {
            child[i] = random.nextBoolean() ? a[i] : b[i];
        }

        return new FeatureGenome(child, random);
    }
}
