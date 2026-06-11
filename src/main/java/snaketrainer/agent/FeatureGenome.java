package snaketrainer.agent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class FeatureGenome {
    public static final int MIN_ACTIVE_FEATURES = 8;
    public static final int MAX_ACTIVE_FEATURES = 10;

    private final boolean[] enabled;

    public FeatureGenome(boolean[] enabled, Random random) {
        this(enabled, true, random);
    }

    public FeatureGenome(boolean[] enabled) {
        this(enabled, false, null);
    }

    private FeatureGenome(boolean[] enabled, boolean repair, Random random) {
        if (enabled.length != FeatureName.size()) {
            throw new IllegalArgumentException("Número incorrecto de features.");
        }

        this.enabled = enabled.clone();

        if (repair) {
            repair(random);
        }
    }

    public static FeatureGenome copyOf(FeatureGenome genome) {
        return new FeatureGenome(genome.toArray(), false, null);
    }

    public static FeatureGenome random(Random random) {
        boolean[] enabled = new boolean[FeatureName.size()];
        int target = MIN_ACTIVE_FEATURES
                + random.nextInt(MAX_ACTIVE_FEATURES - MIN_ACTIVE_FEATURES + 1);

        int active = 0;
        while (active < target) {
            int index = random.nextInt(enabled.length);
            if (!enabled[index]) {
                enabled[index] = true;
                active++;
            }
        }

        return new FeatureGenome(enabled);
    }

    public boolean isEnabled(FeatureName featureName) {
        return enabled[featureName.ordinal()];
    }

    public boolean isEnabled(int index) {
        return enabled[index];
    }

    public boolean[] toArray() {
        return enabled.clone();
    }

    public int countEnabled() {
        int count = 0;

        for (boolean value : enabled) {
            if (value) {
                count++;
            }
        }

        return count;
    }

    /**
     * Fallback repair kept for random contexts. It only enforces the number of active features.
     */
    public void repair(Random random) {
        while (countEnabled() < MIN_ACTIVE_FEATURES) {
            enabled[random.nextInt(enabled.length)] = true;
        }

        while (countEnabled() > MAX_ACTIVE_FEATURES) {
            int index = random.nextInt(enabled.length);

            if (enabled[index]) {
                enabled[index] = false;
            }
        }
    }

    /**
     * Intelligent repair used after crossover/mutation.
     * If features are missing, activates the inactive features with highest absolute weight.
     * If there are too many features, disables the active features with lowest absolute weight.
     */
    public void repairByWeightMagnitude(WeightVector weights) {
        if (countEnabled() < MIN_ACTIVE_FEATURES) {
            List<FeatureName> inactiveFeatures = new ArrayList<>();

            for (FeatureName featureName : FeatureName.values()) {
                if (!isEnabled(featureName)) {
                    inactiveFeatures.add(featureName);
                }
            }

            inactiveFeatures.sort(Comparator.comparingDouble(
                    (FeatureName featureName) -> Math.abs(weights.get(featureName))
            ).reversed());

            int index = 0;
            while (countEnabled() < MIN_ACTIVE_FEATURES && index < inactiveFeatures.size()) {
                enabled[inactiveFeatures.get(index).ordinal()] = true;
                index++;
            }
        }

        if (countEnabled() > MAX_ACTIVE_FEATURES) {
            List<FeatureName> activeFeatures = new ArrayList<>();

            for (FeatureName featureName : FeatureName.values()) {
                if (isEnabled(featureName)) {
                    activeFeatures.add(featureName);
                }
            }

            activeFeatures.sort(Comparator.comparingDouble(
                    featureName -> Math.abs(weights.get(featureName))
            ));

            int index = 0;
            while (countEnabled() > MAX_ACTIVE_FEATURES && index < activeFeatures.size()) {
                enabled[activeFeatures.get(index).ordinal()] = false;
                index++;
            }
        }
    }
}
