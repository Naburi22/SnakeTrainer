package snaketrainer.evolution.reproduction;

import snaketrainer.agent.WeightVector;

public interface MutationStrategy {
    WeightVector mutate(WeightVector weights);
}