package snaketrainer.agent;

import java.util.Arrays;

public class FeatureVector {
    private final double[] values;

    public FeatureVector(double[] values) {
        if (values.length != FeatureName.size()) {
            throw new IllegalArgumentException("Número incorrecto de features.");
        }

        this.values = Arrays.copyOf(values, values.length);
    }

    public double get(FeatureName featureName) {
        return values[featureName.ordinal()];
    }

    public double[] toArray() {
        return Arrays.copyOf(values, values.length);
    }
}