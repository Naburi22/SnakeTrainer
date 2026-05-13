package snaketrainer.evolution.reproduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.FeatureGenome;
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
    private final FeatureGenomeCrossover genomeCrossover;
    private final FeatureGenomeMutation genomeMutation;
    private final Random random;

    public ReproductionEngine(
            EvolutionConfig config,
            SelectionStrategy selectionStrategy,
            CrossoverStrategy crossoverStrategy,
            MutationStrategy mutationStrategy,
            FeatureGenomeCrossover genomeCrossover,
            FeatureGenomeMutation genomeMutation,
            Random random
    ) {
        this.config = config;
        this.selectionStrategy = selectionStrategy;
        this.crossoverStrategy = crossoverStrategy;
        this.mutationStrategy = mutationStrategy;
        this.genomeCrossover = genomeCrossover;
        this.genomeMutation = genomeMutation;
        this.random = random;
    }

    public Population reproduce(List<Individual> orderedIndividuals) {
        List<WeightedAgent> newAgents = new ArrayList<>();

        for (int i = 0; i < config.getEliteCount() && i < orderedIndividuals.size(); i++) {
            WeightedAgent elite = orderedIndividuals.get(i).getAgent();
            WeightVector eliteWeights = new WeightVector(elite.getWeights().toArray());
            FeatureGenome eliteGenome = new FeatureGenome(elite.getGenome().toArray(), random);

            newAgents.add(new WeightedAgent(eliteWeights, eliteGenome, random));
        }

        while (newAgents.size() < config.getAgentsPerGeneration()) {
            Individual parent1 = selectionStrategy.select(orderedIndividuals);
            Individual parent2 = selectionStrategy.select(orderedIndividuals);

            WeightVector childWeights = crossoverStrategy.crossover(
                    parent1.getAgent().getWeights(),
                    parent2.getAgent().getWeights()
            );

            childWeights = mutationStrategy.mutate(childWeights);

            FeatureGenome childGenome = genomeCrossover.crossover(
                    parent1.getAgent().getGenome(),
                    parent2.getAgent().getGenome()
            );

            childGenome = genomeMutation.mutate(childGenome);

            newAgents.add(new WeightedAgent(childWeights, childGenome, random));
        }

        return new Population(newAgents);
    }
}
