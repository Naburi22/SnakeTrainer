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
import snaketrainer.evolution.reproduction.FeatureGenomeMutation;
import snaketrainer.evolution.reproduction.ReproductionEngine;
import snaketrainer.evolution.reproduction.UniformMutation;
import snaketrainer.evolution.selection.TournamentSelection;

public class EvolutionEngine {
    private static final int BOARD_ROWS = 20;
    private static final int BOARD_COLS = 20;
    // TODO: Ajustar estos parámetros: equilibrio entre exploración y tiempo de entrenamiento.
    private static final int BASE_STEPS_WITHOUT_APPLE = 200;
    private static final int EXTRA_STEPS_PER_APPLE = 40;
    private static final int MAX_STEPS_WITHOUT_APPLE = 600;

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

    public EvolutionEngine(EvolutionConfig config) {
        this(config, new Random(), null);
    }

    public EvolutionEngine(EvolutionConfig config, Random random) {
        this(config, random, null);
    }

    public EvolutionEngine(EvolutionConfig config, Random random, EvolutionProgressListener progressListener) {
        this.config = config;
        this.random = random;
        this.progressListener = progressListener;
        this.logger = new EvolutionLogger("./logs/evolution_log.md");
    }

    public GenerationResult run() {
        logger.clear();

        Population population = Population.random(config.getAgentsPerGeneration(), random);

        GenerationResult bestGenerationResult = null;

        for (int generation = 1; generation <= config.getGenerations(); generation++) {
            if (progressListener != null) {
                progressListener.onGenerationStarted(generation, config.getGenerations());
            }
            
            List<Individual> individuals = evaluatePopulation(population);
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

    private List<Individual> evaluatePopulation(Population population) {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

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
        } finally {
            executor.shutdown();
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

    private ReproductionEngine createReproductionEngine() {
        return new ReproductionEngine(
                config,
                new TournamentSelection(config.getTournamentSize(), random),
                new ArithmeticCrossover(random),
                new UniformMutation(config.getMutationRate(), config.getMutationStrength(), random),
                new FeatureGenomeCrossover(random),
                new FeatureGenomeMutation(config.getFeatureMutationRate(), random),
                random
        );
    }
}