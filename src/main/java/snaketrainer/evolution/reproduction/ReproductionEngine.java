package snaketrainer.evolution.reproduction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.WeightVector;
import snaketrainer.agent.WeightedAgent;
import snaketrainer.evolution.EvolutionConfig;
import snaketrainer.evolution.EvolutionParameters;
import snaketrainer.evolution.Individual;
import snaketrainer.evolution.Population;
import snaketrainer.evolution.selection.SelectionStrategy;

public class ReproductionEngine {
    private final EvolutionConfig config;
    private final EvolutionParameters parameters;
    private final SelectionStrategy selectionStrategy;
    private final CrossoverStrategy crossoverStrategy;
    private final FeatureGenomeCrossover genomeCrossover;
    private final IndividualMutationOperator mutationOperator;
    private final Random random;

    public ReproductionEngine(
            EvolutionConfig config,
            EvolutionParameters parameters,
            SelectionStrategy selectionStrategy,
            CrossoverStrategy crossoverStrategy,
            FeatureGenomeCrossover genomeCrossover,
            IndividualMutationOperator mutationOperator,
            Random random
    ) {
        this.config = config;
        this.parameters = parameters;
        this.selectionStrategy = selectionStrategy;
        this.crossoverStrategy = crossoverStrategy;
        this.genomeCrossover = genomeCrossover;
        this.mutationOperator = mutationOperator;
        this.random = random;
    }

    public Population reproduce(List<Individual> orderedIndividuals) {
        List<WeightedAgent> newAgents = new ArrayList<>();

        for (int i = 0; i < config.getEliteCount() && i < orderedIndividuals.size(); i++) {
            WeightedAgent elite = orderedIndividuals.get(i).getAgent();
            WeightVector eliteWeights = new WeightVector(elite.getWeights().toArray());
            FeatureGenome eliteGenome = FeatureGenome.copyOf(elite.getGenome());

            newAgents.add(new WeightedAgent(eliteWeights, eliteGenome, random));
        }

        while (newAgents.size() < config.getAgentsPerGeneration()) {
            Individual firstParent = selectionStrategy.select(orderedIndividuals);
            Individual secondParent = selectionStrategy.select(orderedIndividuals);

            Individual superiorParent = getSuperiorParent(firstParent, secondParent);
            Individual inferiorParent = superiorParent == firstParent ? secondParent : firstParent;

            WeightedAgent childAgent = random.nextDouble() < parameters.getCrossoverRate()
                    ? createCrossoverChild(superiorParent, inferiorParent)
                    : copyOneParent(superiorParent, inferiorParent);

            newAgents.add(childAgent);
        }

        return new Population(newAgents);
    }

    private WeightedAgent createCrossoverChild(Individual superiorParent, Individual inferiorParent) {
        WeightVector childWeights = crossoverStrategy.crossover(
                superiorParent.getAgent().getWeights(),
                inferiorParent.getAgent().getWeights()
        );

        FeatureGenome childGenome = genomeCrossover.crossover(
                superiorParent.getAgent().getGenome(),
                inferiorParent.getAgent().getGenome()
        );

        MutationResult mutationResult = mutationOperator.mutate(childWeights, childGenome);

        return new WeightedAgent(
                mutationResult.getWeights(),
                mutationResult.getGenome(),
                random
        );
    }

    private WeightedAgent copyOneParent(Individual superiorParent, Individual inferiorParent) {
        WeightedAgent selectedParent = random.nextDouble() < parameters.getDirectCopySuperiorRate()
                ? superiorParent.getAgent()
                : inferiorParent.getAgent();

        WeightVector copiedWeights = new WeightVector(selectedParent.getWeights().toArray());
        FeatureGenome copiedGenome = FeatureGenome.copyOf(selectedParent.getGenome());

        MutationResult mutationResult = mutationOperator.mutate(copiedWeights, copiedGenome);

        return new WeightedAgent(
                mutationResult.getWeights(),
                mutationResult.getGenome(),
                random
        );
    }

    private Individual getSuperiorParent(Individual firstParent, Individual secondParent) {
        return firstParent.compareTo(secondParent) <= 0 ? firstParent : secondParent;
    }
}
