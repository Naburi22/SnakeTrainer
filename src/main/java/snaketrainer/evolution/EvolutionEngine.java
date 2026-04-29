package snaketrainer.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.WeightedAgent;
import snaketrainer.evolution.evaluation.FitnessEvaluator;
import snaketrainer.evolution.evaluation.FitnessResult;
import snaketrainer.evolution.reproduction.ArithmeticCrossover;
import snaketrainer.evolution.reproduction.ReproductionEngine;
import snaketrainer.evolution.reproduction.UniformMutation;
import snaketrainer.evolution.selection.TournamentSelection;

public class EvolutionEngine {
    private static final int BOARD_ROWS = 20;
    private static final int BOARD_COLS = 20;
    private static final int MAX_STEPS_WITHOUT_APPLE = 200;

    private final EvolutionConfig config;
    private final Random random;
    private final EvolutionLogger logger;

    public EvolutionEngine(EvolutionConfig config) {
        this(config, new Random());
    }

    public EvolutionEngine(EvolutionConfig config, Random random) {
        this.config = config;
        this.random = random;
        this.logger = new EvolutionLogger("evolution_log.txt");
    }

    public GenerationResult run() {
        logger.clear();

        Population population = Population.random(config.getAgentsPerGeneration(), random);
        FitnessEvaluator evaluator = new FitnessEvaluator(BOARD_ROWS, BOARD_COLS, MAX_STEPS_WITHOUT_APPLE);

        GenerationResult bestGenerationResult = null;

        for (int generation = 1; generation <= config.getGenerations(); generation++) {
            List<Individual> individuals = evaluatePopulation(population, evaluator);
            Collections.sort(individuals);
            logger.logGeneration(generation, individuals);

            GenerationResult currentResult = new GenerationResult(generation, individuals);

            if (bestGenerationResult == null
                    || currentResult.getBestIndividual().compareTo(bestGenerationResult.getBestIndividual()) < 0) {
                bestGenerationResult = currentResult;
            }

            if (generation < config.getGenerations()) {
                population = createReproductionEngine().reproduce(individuals);
            }
        }

        logger.logBestIndividual(bestGenerationResult.getBestIndividual(), config.getGenerations());

        return bestGenerationResult;
    }

    private List<Individual> evaluatePopulation(Population population, FitnessEvaluator evaluator) {
        List<Individual> individuals = new ArrayList<>();

        for (WeightedAgent agent : population.getAgents()) {
            FitnessResult result = evaluator.evaluate(agent);
            individuals.add(new Individual(
                        agent,
                        result.getApples(),
                        result.getSteps(),
                        result.getEndCause()
                        ));
        }

        return individuals;
    }

    private ReproductionEngine createReproductionEngine() {
        return new ReproductionEngine(
                config,
                new TournamentSelection(config.getTournamentSize(), random),
                new ArithmeticCrossover(random),
                new UniformMutation(config.getMutationRate(), config.getMutationStrength(), random),
                random
        );
    }
}