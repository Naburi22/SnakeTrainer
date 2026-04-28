package snaketrainer.trainer;

import snaketrainer.agent.GreedyAgent;
import snaketrainer.agent.SnakeAgent;
import snaketrainer.game.SnakeGame;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;

public class SnakeTrainer {
    private static final int BOARD_ROWS = 20;
    private static final int BOARD_COLS = 20;
    private static final int MAX_STEPS_PER_GAME = 1000;

    public TrainingResult train(int generations, int agentsPerGeneration) {
        SnakeAgent bestAgent = null;
        int bestScore = -1;

        for (int generation = 1; generation <= generations; generation++) {
            for (int agentIndex = 0; agentIndex < agentsPerGeneration; agentIndex++) {
                SnakeAgent agent = createAgentPlaceholder();
                int score = simulate(agent);

                if (score > bestScore) {
                    bestScore = score;
                    bestAgent = agent;
                }
            }

            /*
             * Sustituir esta zona por la parte evolutiva:
             *
             * 1. Guardar población de agentes.
             * 2. Evaluar fitness de cada agente.
             * 3. Seleccionar mejores agentes.
             * 4. Cruzar vectores de pesos.
             * 5. Mutar pesos.
             * 6. Crear siguiente generación.
             */
        }

        if (bestAgent == null) {
            bestAgent = createAgentPlaceholder();
            bestScore = 0;
        }

        return new TrainingResult(bestAgent, bestScore, generations, agentsPerGeneration);
    }

    private SnakeAgent createAgentPlaceholder() {
        return new GreedyAgent();
    }

    private int simulate(SnakeAgent agent) {
        SnakeGame game = new SnakeGame(BOARD_ROWS, BOARD_COLS);

        while (!game.isGameOver() && game.getSteps() < MAX_STEPS_PER_GAME) {
            Cell[][] board = game.getBoardMatrix();
            Direction decision = agent.decideMove(board, game.getDirection(), game.getScore());
            game.step(decision);
        }

        return game.getScore();
    }
}
