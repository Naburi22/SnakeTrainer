package snaketrainer.agent;

import java.util.Random;

public class FeatureGenome {
    public static final int MIN_ACTIVE_FEATURES = 5;
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

        return new FeatureGenome(enabled, random);
    }

    public boolean isEnabled(FeatureName featureName) {
        return enabled[featureName.ordinal()];
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
}
