package snaketrainer.evolution.reproduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.WeightedAgent;
import snaketrainer.agent.WeightVector;
import snaketrainer.evolution.EvolutionConfig;
import snaketrainer.evolution.Individual;
import snaketrainer.evolution.Population;
import snaketrainer.evolution.selection.SelectionStrategy;

public class ReproductionEngine {
    private final EvolutionConfig config;
    private final SelectionStrategy selectionStrategy;
    private final CrossoverStrategy crossoverStrategy;
    private final MutationStrategy mutationStrategy;
    private final Random random;

    public ReproductionEngine(
            EvolutionConfig config,
            SelectionStrategy selectionStrategy,
            CrossoverStrategy crossoverStrategy,
            MutationStrategy mutationStrategy,
            Random random
    ) {
        this.config = config;
        this.selectionStrategy = selectionStrategy;
        this.crossoverStrategy = crossoverStrategy;
        this.mutationStrategy = mutationStrategy;
        this.random = random;
    }

    public Population reproduce(List<Individual> orderedIndividuals) {
        List<WeightedAgent> newAgents = new ArrayList<>();

        for (int i = 0; i < config.getEliteCount() && i < orderedIndividuals.size(); i++) {
            WeightVector eliteWeights = new WeightVector(
                    orderedIndividuals.get(i).getAgent().getWeights().toArray()
            );

            newAgents.add(new WeightedAgent(eliteWeights, random));
        }

        while (newAgents.size() < config.getAgentsPerGeneration()) {
            Individual parent1 = selectionStrategy.select(orderedIndividuals);
            Individual parent2 = selectionStrategy.select(orderedIndividuals);

            WeightVector childWeights = crossoverStrategy.crossover(
                    parent1.getAgent().getWeights(),
                    parent2.getAgent().getWeights()
            );

            childWeights = mutationStrategy.mutate(childWeights);

            newAgents.add(new WeightedAgent(childWeights, random));
        }

        return new Population(newAgents);
    }
}