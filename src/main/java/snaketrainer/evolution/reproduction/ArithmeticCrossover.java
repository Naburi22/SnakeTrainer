package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.WeightVector;

public class ArithmeticCrossover implements CrossoverStrategy {
    private final Random random;

    public ArithmeticCrossover(Random random) {
        this.random = random;
    }

    @Override
    public WeightVector crossover(WeightVector parent1, WeightVector parent2) {
        double[] p1 = parent1.toArray();
        double[] p2 = parent2.toArray();
        double[] child = new double[p1.length];

        for (int i = 0; i < child.length; i++) {
            double alpha = random.nextDouble();
            child[i] = alpha * p1[i] + (1.0 - alpha) * p2[i];
        }

        return new WeightVector(child);
    }
}