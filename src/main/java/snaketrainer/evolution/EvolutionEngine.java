package snaketrainer.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import snaketrainer.agent.WeightedAgent;
import snaketrainer.evolution.evaluation.FitnessEvaluator;
import snaketrainer.evolution.evaluation.FitnessResult;
import snaketrainer.evolution.reproduction.ArithmeticCrossover;
import snaketrainer.evolution.reproduction.FeatureGenomeCrossover;
import snaketrainer.evolution.reproduction.IndividualMutationOperator;
import snaketrainer.evolution.reproduction.ReproductionEngine;
import snaketrainer.evolution.selection.TournamentSelection;

public class EvolutionEngine {
    private static final int BOARD_ROWS = 20;
    private static final int BOARD_COLS = 20;
    // TODO: Ajustar estos parámetros: equilibrio entre exploración y tiempo de entrenamiento.
    private static final int BASE_STEPS_WITHOUT_APPLE = 200;
    private static final int EXTRA_STEPS_PER_APPLE = 100;
    private static final int MAX_STEPS_WITHOUT_APPLE = 1000;

    /*
     * Dejamos un procesador libre para la interfaz gráfica y el sistema operativo.
     * Así evitamos que el entrenamiento sature completamente el equipo.
     */
    private static final int THREAD_COUNT =
            Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    private final EvolutionConfig config;
    private final Random random;
    private final EvolutionLogger logger;
    private final EvolutionProgressListener progressListener;
    private final boolean detailedLogging;

    public EvolutionEngine(EvolutionConfig config) {
        this(config, new Random(), null, true);
    }

    public EvolutionEngine(EvolutionConfig config, Random random) {
        this(config, random, null, true);
    }

    public EvolutionEngine(EvolutionConfig config, Random random, EvolutionProgressListener progressListener) {
        this(config, random, progressListener, true);
    }

    public EvolutionEngine(
            EvolutionConfig config,
            Random random,
            EvolutionProgressListener progressListener,
            boolean detailedLogging
    ) {
        this.config = config;
        this.random = random;
        this.progressListener = progressListener;
        this.detailedLogging = detailedLogging;
        this.logger = new EvolutionLogger("./logs/evolution_log.md");
    }

    public GenerationResult run() {
        if (detailedLogging) {
            logger.clear();
        }

        Population population = Population.random(config.getAgentsPerGeneration(), random);
        LearningController learningController = new LearningController(config, BOARD_COLS * BOARD_ROWS);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        GenerationResult bestGenerationResult = null;

        try {
            for (int generation = 1; generation <= config.getGenerations(); generation++) {
                if (progressListener != null) {
                    progressListener.onGenerationStarted(generation, config.getGenerations());
                }

                List<Individual> individuals = evaluatePopulation(population, executor);
                Collections.sort(individuals);

                if (detailedLogging) {
                    logger.logGeneration(generation, individuals);
                }

                GenerationResult currentResult = new GenerationResult(generation, individuals);
                LearningState learningState = learningController.update(generation, individuals);

                if (detailedLogging) {
                    logger.logLearningState(learningState);
                }

                if (bestGenerationResult == null
                        || currentResult.getBestIndividual().compareTo(bestGenerationResult.getBestIndividual()) < 0) {
                    bestGenerationResult = currentResult;
                }

                if (generation < config.getGenerations()) {
                    population = createReproductionEngine(learningController.getParameters()).reproduce(individuals);
                }
            }
        } finally {
            executor.shutdown();
        }

        if (detailedLogging) {
            logger.logBestIndividual(
                bestGenerationResult.getBestIndividual(),
                bestGenerationResult.getGenerationNumber(),
                config.getGenerations()
            );
        }

        return bestGenerationResult;
    }

    private List<Individual> evaluatePopulation(Population population, ExecutorService executor) {
        try {
            List<Callable<Individual>> tasks = new ArrayList<>();

            for (WeightedAgent agent : population.getAgents()) {
                tasks.add(() -> evaluateAgent(agent));
            }

            List<Future<Individual>> futures = executor.invokeAll(tasks);
            List<Individual> individuals = new ArrayList<>();

            for (Future<Individual> future : futures) {
                individuals.add(future.get());
            }

            return individuals;
        } catch (Exception exception) {
            throw new RuntimeException("Error durante la evaluación concurrente de la población.", exception);
        }
    }

    private Individual evaluateAgent(WeightedAgent agent) {
        /*
         * Cada tarea crea su propio FitnessEvaluator.
         * Así evitamos compartir estado entre hilos.
         */
        FitnessEvaluator evaluator = new FitnessEvaluator(
            BOARD_ROWS,
            BOARD_COLS,
            BASE_STEPS_WITHOUT_APPLE,
            EXTRA_STEPS_PER_APPLE,
            MAX_STEPS_WITHOUT_APPLE
        );

        FitnessResult result = evaluator.evaluate(agent);

        return new Individual(
                agent,
                result.getApples(),
                result.getSteps(),
                result.getEndCause()
        );
    }

    private ReproductionEngine createReproductionEngine(EvolutionParameters parameters) {
        return new ReproductionEngine(
                config,
                parameters,
                new TournamentSelection(parameters.getTournamentSize(), random),
                new ArithmeticCrossover(random),
                new FeatureGenomeCrossover(parameters.getFeatureSuperiorInheritanceRate(), random),
                new IndividualMutationOperator(parameters, random),
                random
        );
    }
}