package snaketrainer.evolution.evaluation;

import snaketrainer.agent.WeightedAgent;
import snaketrainer.game.SnakeGame;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;

public class FitnessEvaluator {
    private final int rows;
    private final int cols;
    private final int baseStepsWithoutApple;
    private final int extraStepsPerApple;
    private final int maxStepsWithoutApple;

    public FitnessEvaluator(
        int rows,
        int cols,
        int baseStepsWithoutApple,
        int extraStepsPerApple,
        int maxStepsWithoutApple
    ) {
        this.rows = rows;
        this.cols = cols;
        this.baseStepsWithoutApple = baseStepsWithoutApple;
        this.extraStepsPerApple = extraStepsPerApple;
        this.maxStepsWithoutApple = maxStepsWithoutApple;
    }

    public FitnessResult evaluate(WeightedAgent agent) {
        SnakeGame game = new SnakeGame(rows, cols);

        int stepsWithoutApple = 0;
        int previousScore = game.getScore();

        while (!game.isGameOver()
                && stepsWithoutApple < calculateCurrentLimit(game.getScore())) {

            Cell[][] board = game.getBoardMatrix();
            Direction decision = agent.decideMove(board, game.getDirection(), game.getScore());

            game.step(decision);

            if (game.getScore() > previousScore) {
                previousScore = game.getScore();
                stepsWithoutApple = 0;
            } else {
                stepsWithoutApple++;
            }
        }

        EndCause endCause = game.isGameOver()
                ? EndCause.DEATH
                : EndCause.NO_APPLE_TIMEOUT;

        return new FitnessResult(game.getScore(), game.getSteps(), endCause);
    }

    private int calculateCurrentLimit(int score) {
        int limit = baseStepsWithoutApple + score * extraStepsPerApple;
        return Math.min(limit, maxStepsWithoutApple);
    }
}