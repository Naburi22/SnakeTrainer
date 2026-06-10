package snaketrainer.trainer;

import snaketrainer.agent.WeightedAgent;
import snaketrainer.evolution.EvolutionConfig;
import snaketrainer.evolution.EvolutionEngine;
import snaketrainer.evolution.EvolutionProgressListener;
import snaketrainer.evolution.GenerationResult;
import snaketrainer.evolution.Individual;

public class SnakeTrainer {
    public TrainingResult train(
            int generations,
            int agentsPerGeneration,
            EvolutionProgressListener progressListener
    ) {
        return train(generations, agentsPerGeneration, progressListener, true);
    }

    public TrainingResult train(
            int generations,
            int agentsPerGeneration,
            EvolutionProgressListener progressListener,
            boolean detailedLogging
    ) {
        EvolutionConfig config = new EvolutionConfig(generations, agentsPerGeneration);
        EvolutionEngine engine = new EvolutionEngine(config, new java.util.Random(), progressListener, detailedLogging);

        GenerationResult result = engine.run();
        Individual best = result.getBestIndividual();
        WeightedAgent bestAgent = best.getAgent();

        return new TrainingResult(
                bestAgent,
                bestAgent.getWeights(),
                bestAgent.getGenome(),
                best.getApples(),
                best.getSteps(),
                result.getGenerationNumber(),
                generations,
                agentsPerGeneration
        );
    }

    public TrainingResult train(int generations, int agentsPerGeneration) {
        return train(generations, agentsPerGeneration, null);
    }
}