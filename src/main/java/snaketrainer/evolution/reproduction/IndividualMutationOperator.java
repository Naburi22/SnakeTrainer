package snaketrainer.evolution.reproduction;

import java.util.Random;

import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.WeightVector;
import snaketrainer.evolution.EvolutionParameters;

/**
 * Individual-level mutation operator.
 *
 * A non-elite descendant first has a probability of mutating. If it mutates,
 * one mutation type is selected: weight-only, genome-only or mixed.
 */
public class IndividualMutationOperator {
    private final EvolutionParameters parameters;
    private final UniformMutation weightMutation;
    private final FeatureGenomeMutation genomeMutation;
    private final Random random;

    public IndividualMutationOperator(EvolutionParameters parameters, Random random) {
        this.parameters = parameters;
        this.random = random;
        this.weightMutation = new UniformMutation(
                parameters.getMutatedWeightsPerMutation(),
                parameters.getWeightMutationPercentage(),
                parameters.getMinimumMutationStep(),
                random
        );
        this.genomeMutation = new FeatureGenomeMutation(random);
    }

    public MutationResult mutate(WeightVector weights, FeatureGenome genome) {
        WeightVector mutatedWeights = new WeightVector(weights.toArray());
        FeatureGenome mutatedGenome = FeatureGenome.copyOf(genome);

        if (random.nextDouble() < parameters.getIndividualMutationRate()) {
            double mutationType = random.nextDouble();
            double weightOnlyLimit = parameters.getWeightMutationTypeRate();
            double genomeOnlyLimit = weightOnlyLimit + parameters.getGenomeMutationTypeRate();

            if (mutationType < weightOnlyLimit) {
                mutatedWeights = weightMutation.mutateActiveWeights(mutatedWeights, mutatedGenome);
            } else if (mutationType < genomeOnlyLimit) {
                mutatedGenome = genomeMutation.mutate(mutatedGenome);
            } else {
                mutatedGenome = genomeMutation.mutate(mutatedGenome);
                mutatedWeights = weightMutation.mutateActiveWeights(mutatedWeights, mutatedGenome);
            }
        }

        mutatedGenome.repairByWeightMagnitude(mutatedWeights);
        return new MutationResult(mutatedWeights, mutatedGenome);
    }
}
