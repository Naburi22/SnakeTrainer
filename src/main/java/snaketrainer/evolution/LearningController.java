package snaketrainer.evolution;

import java.util.List;
import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.FeatureName;

/**
 * Adaptive learning function for the evolutionary algorithm.
 *
 * It adjusts only mutation-related parameters. Crossover, elitism and direct
 * parent-copy probabilities remain fixed to avoid changing too many forces at
 * the same time.
 */
public class LearningController {
    private static final double MUTATION_RATE_MIN = 0.05;
    private static final double MUTATION_RATE_MAX = 0.25;

    private static final double WEIGHT_MUTATION_PERCENTAGE_MIN = 0.05;
    private static final double WEIGHT_MUTATION_PERCENTAGE_MAX = 0.20;

    private static final double FEATURE_MUTATION_RATE_MIN = 0.02;
    private static final double FEATURE_MUTATION_RATE_MAX = 0.12;

    private static final double IMPROVEMENT_COOLING_FACTOR = 0.95;
    private static final double SOLUTION_CONSOLIDATION_FACTOR = 0.90;

    private static final double STAGNATION_MUTATION_RATE_FACTOR = 1.20;
    private static final double STAGNATION_FEATURE_MUTATION_FACTOR = 1.30;
    private static final double STAGNATION_WEIGHT_MUTATION_FACTOR = 1.15;

    private static final double LOW_GENOME_DIVERSITY_THRESHOLD = 0.25;
    private static final double LOW_DIVERSITY_FEATURE_MUTATION_FACTOR = 1.25;

    private final EvolutionParameters parameters;
    private final int stagnationWindow;
    private final int maximumScore;

    private Individual bestSoFar;
    private int generationsWithoutImprovement;

    public LearningController(EvolutionConfig config, int maximumScore) {
        this.parameters = new EvolutionParameters(config);
        this.stagnationWindow = Math.max(2, (int) Math.round(config.getGenerations() * 0.20));
        this.maximumScore = maximumScore;
    }

    public EvolutionParameters getParameters() {
        return parameters;
    }

    public LearningState update(int generation, List<Individual> orderedIndividuals) {
        Individual currentBest = orderedIndividuals.get(0);
        double genomeDiversity = calculateGenomeDiversity(orderedIndividuals);
        StringBuilder decision = new StringBuilder();

        if (bestSoFar == null) {
            bestSoFar = currentBest;

            if (isMaximumSolutionReached(bestSoFar)) {
                generationsWithoutImprovement = 0;
                consolidateSolutionMode();
                decision.append("Inicialización con solución máxima: se entra directamente en modo consolidación.");
                clampParameters();
                return createState(generation, genomeDiversity, decision.toString());
            }

            decision.append("Inicialización: se fija el primer mejor global y se mantienen parámetros base.");
            clampParameters();
            return createState(generation, genomeDiversity, decision.toString());
        }

        if (isMaximumSolutionReached(currentBest) || isMaximumSolutionReached(bestSoFar)) {
            if (currentBest.compareTo(bestSoFar) < 0) {
                bestSoFar = currentBest;
                decision.append("Solución máxima alcanzada con mejor criterio secundario: se actualiza el mejor global.");
            } else {
                decision.append("Solución máxima ya alcanzada: no se considera estancamiento.");
            }

            generationsWithoutImprovement = 0;
            consolidateSolutionMode();
            decision.append(" Modo consolidación: se reducen mutaciones hacia valores mínimos y se bloquea el aumento de exploración.");

            clampParameters();
            return createState(generation, genomeDiversity, decision.toString());
        }

        if (currentBest.compareTo(bestSoFar) < 0) {
            bestSoFar = currentBest;
            generationsWithoutImprovement = 0;
            coolDownAfterImprovement();
            decision.append("Mejora global: enfriamiento suave de mutaciones.");
        } else {
            generationsWithoutImprovement++;
            decision.append("Sin mejora global: contador de estancamiento = ")
                    .append(generationsWithoutImprovement)
                    .append(".");

            if (generationsWithoutImprovement % stagnationWindow == 0) {
                increaseExplorationAfterStagnation();
                decision.append(" Ventana de estancamiento alcanzada: aumento de exploración.");
            }
        }

        if (genomeDiversity < LOW_GENOME_DIVERSITY_THRESHOLD) {
            increaseStructuralExploration();
            decision.append(" Diversidad de genomas baja: aumento de mutación de features.");
        }

        clampParameters();
        return createState(generation, genomeDiversity, decision.toString());
    }

    private void coolDownAfterImprovement() {
        parameters.setMutationRate(parameters.getMutationRate() * IMPROVEMENT_COOLING_FACTOR);
        parameters.setFeatureMutationRate(parameters.getFeatureMutationRate() * IMPROVEMENT_COOLING_FACTOR);
        parameters.setWeightMutationPercentage(parameters.getWeightMutationPercentage() * IMPROVEMENT_COOLING_FACTOR);
    }

    private void increaseExplorationAfterStagnation() {
        parameters.setMutationRate(parameters.getMutationRate() * STAGNATION_MUTATION_RATE_FACTOR);
        parameters.setFeatureMutationRate(parameters.getFeatureMutationRate() * STAGNATION_FEATURE_MUTATION_FACTOR);
        parameters.setWeightMutationPercentage(parameters.getWeightMutationPercentage() * STAGNATION_WEIGHT_MUTATION_FACTOR);
    }

    private void increaseStructuralExploration() {
        parameters.setFeatureMutationRate(parameters.getFeatureMutationRate() * LOW_DIVERSITY_FEATURE_MUTATION_FACTOR);
    }

    private void clampParameters() {
        parameters.setMutationRate(clamp(parameters.getMutationRate(), MUTATION_RATE_MIN, MUTATION_RATE_MAX));
        parameters.setFeatureMutationRate(clamp(parameters.getFeatureMutationRate(), FEATURE_MUTATION_RATE_MIN, FEATURE_MUTATION_RATE_MAX));
        parameters.setWeightMutationPercentage(clamp(
                parameters.getWeightMutationPercentage(),
                WEIGHT_MUTATION_PERCENTAGE_MIN,
                WEIGHT_MUTATION_PERCENTAGE_MAX
        ));
    }

    private boolean isMaximumSolutionReached(Individual individual) {
        return individual.getApples() >= maximumScore;
    }

    private void consolidateSolutionMode() {
        parameters.setMutationRate(parameters.getMutationRate() * SOLUTION_CONSOLIDATION_FACTOR);
        parameters.setFeatureMutationRate(parameters.getFeatureMutationRate() * SOLUTION_CONSOLIDATION_FACTOR);
        parameters.setWeightMutationPercentage(parameters.getWeightMutationPercentage() * SOLUTION_CONSOLIDATION_FACTOR);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private LearningState createState(int generation, double genomeDiversity, String decision) {
        return new LearningState(
                generation,
                stagnationWindow,
                generationsWithoutImprovement,
                genomeDiversity,
                parameters.getMutationRate(),
                parameters.getWeightMutationPercentage(),
                parameters.getFeatureMutationRate(),
                decision
        );
    }

    private double calculateGenomeDiversity(List<Individual> individuals) {
        if (individuals.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        int comparisons = 0;

        for (int i = 0; i < individuals.size(); i++) {
            FeatureGenome firstGenome = individuals.get(i).getAgent().getGenome();

            for (int j = i + 1; j < individuals.size(); j++) {
                FeatureGenome secondGenome = individuals.get(j).getAgent().getGenome();
                totalDistance += calculateNormalizedHammingDistance(firstGenome, secondGenome);
                comparisons++;
            }
        }

        return totalDistance / comparisons;
    }

    private double calculateNormalizedHammingDistance(FeatureGenome firstGenome, FeatureGenome secondGenome) {
        int differences = 0;

        for (FeatureName featureName : FeatureName.values()) {
            if (firstGenome.isEnabled(featureName) != secondGenome.isEnabled(featureName)) {
                differences++;
            }
        }

        return (double) differences / FeatureName.size();
    }
}
