package snaketrainer.evolution.reproduction;

import snaketrainer.agent.WeightVector;

public interface CrossoverStrategy {
    WeightVector crossover(WeightVector parent1, WeightVector parent2);
}