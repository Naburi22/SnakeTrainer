package snaketrainer.agent;

import java.util.Arrays;
import java.util.Random;

public class WeightVector {
    public static final double MIN_WEIGHT = -1.0;
    public static final double MAX_WEIGHT = 1.0;

    private final double[] values;

    public WeightVector(double[] values) {
        if (values.length != FeatureName.size()) {
            throw new IllegalArgumentException("Número incorrecto de pesos.");
        }

        this.values = new double[values.length];

        for (FeatureName featureName : FeatureName.values()) {
            int index = featureName.ordinal();
            this.values[index] = discretize(featureName, values[index]);
        }
    }

    public static WeightVector random(Random random) {
        double[] values = new double[FeatureName.size()];

        for (FeatureName featureName : FeatureName.values()) {
            values[featureName.ordinal()] = randomDiscreteValue(featureName, random);
        }

        return new WeightVector(values);
    }

    public double dot(FeatureVector features) {
        double result = 0.0;
        double[] featureValues = features.toArray();

        for (int i = 0; i < values.length; i++) {
            result += values[i] * featureValues[i];
        }

        return result;
    }

    public double dot(FeatureVector features, FeatureGenome genome) {
        double result = 0.0;
        double[] featureValues = features.toArray();

        for (FeatureName featureName : FeatureName.values()) {
            if (genome.isEnabled(featureName)) {
                int i = featureName.ordinal();
                result += values[i] * featureValues[i];
            }
        }

        return result;
    }

    public double get(FeatureName featureName) {
        return values[featureName.ordinal()];
    }

    public double[] toArray() {
        return Arrays.copyOf(values, values.length);
    }

    public static double clamp(double value) {
        return Math.max(MIN_WEIGHT, Math.min(MAX_WEIGHT, value));
    }

    public static double discretize(FeatureName featureName, double value) {
        double clamped = clamp(value);
        int levels = featureName.getDiscretizationLevels();

        if (levels <= 1) {
            return 0.0;
        }

        double step = (MAX_WEIGHT - MIN_WEIGHT) / (levels - 1);
        int nearestLevel = (int) Math.round((clamped - MIN_WEIGHT) / step);
        double discretized = MIN_WEIGHT + nearestLevel * step;

        return cleanFloatingPointNoise(discretized);
    }

    private static double randomDiscreteValue(FeatureName featureName, Random random) {
        int levels = featureName.getDiscretizationLevels();
        int level = random.nextInt(levels);
        double step = (MAX_WEIGHT - MIN_WEIGHT) / (levels - 1);
        return cleanFloatingPointNoise(MIN_WEIGHT + level * step);
    }

    private static double cleanFloatingPointNoise(double value) {
        if (Math.abs(value) < 1e-12) {
            return 0.0;
        }

        return value;
    }

    public String toMultilineString() {
        StringBuilder builder = new StringBuilder();
        FeatureName[] features = FeatureName.values();

        for (int i = 0; i < features.length; i++) {
            FeatureName feature = features[i];

            builder.append(feature.getDisplayName())
                    .append(": ")
                    .append(String.format("%.4f", get(feature)));

            if (i < features.length - 1) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    public String toMultilineString(FeatureGenome genome) {
        StringBuilder builder = new StringBuilder();
        FeatureName[] features = FeatureName.values();
        int split = (features.length + 1) / 2;

        for (int i = 0; i < split; i++) {
            appendFeature(builder, features[i], genome);

            int rightIndex = i + split;
            if (rightIndex < features.length) {
                builder.append("    ");
                appendFeature(builder, features[rightIndex], genome);
            }

            if (i < split - 1) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    private void appendFeature(StringBuilder builder, FeatureName featureName, FeatureGenome genome) {
        builder.append(String.format(
                "%-22s %8.4f [%s]",
                featureName.getDisplayName() + ":",
                get(featureName),
                genome.isEnabled(featureName) ? "ON" : "OFF"
        ));
    }
}
