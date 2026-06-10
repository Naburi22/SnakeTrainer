package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.FeatureGenome;

public class FeatureGenomeCrossover {
    private final double superiorInheritanceRate;
    private final Random random;

    public FeatureGenomeCrossover(double superiorInheritanceRate, Random random) {
        this.superiorInheritanceRate = superiorInheritanceRate;
        this.random = random;
    }

    /**
     * The first parent is expected to be the better classified parent.
     */
    public FeatureGenome crossover(FeatureGenome superiorParent, FeatureGenome inferiorParent) {
        boolean[] superior = superiorParent.toArray();
        boolean[] inferior = inferiorParent.toArray();
        boolean[] child = new boolean[superior.length];

        for (int i = 0; i < child.length; i++) {
            child[i] = random.nextDouble() < superiorInheritanceRate
                    ? superior[i]
                    : inferior[i];
        }

        return new FeatureGenome(child);
    }
}
