package snaketrainer.evolution.reproduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.FeatureName;

/**
 * Genome mutation operator used after an individual has already been selected
 * for mutation. It performs one structural swap: one active feature is disabled
 * and one inactive feature is enabled.
 */
public class FeatureGenomeMutation {
    private final Random random;

    public FeatureGenomeMutation(Random random) {
        this.random = random;
    }

    public FeatureGenome mutate(FeatureGenome genome) {
        boolean[] enabled = genome.toArray();
        List<Integer> activeIndices = new ArrayList<>();
        List<Integer> inactiveIndices = new ArrayList<>();

        for (FeatureName featureName : FeatureName.values()) {
            int index = featureName.ordinal();
            if (enabled[index]) {
                activeIndices.add(index);
            } else {
                inactiveIndices.add(index);
            }
        }

        if (activeIndices.isEmpty() || inactiveIndices.isEmpty()) {
            return new FeatureGenome(enabled);
        }

        int activeIndex = activeIndices.get(random.nextInt(activeIndices.size()));
        int inactiveIndex = inactiveIndices.get(random.nextInt(inactiveIndices.size()));

        enabled[activeIndex] = false;
        enabled[inactiveIndex] = true;

        return new FeatureGenome(enabled);
    }
}
