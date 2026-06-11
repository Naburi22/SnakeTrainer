package snaketrainer.agent;

import java.util.Random;
import snaketrainer.analysis.FeatureExtractor;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;

public class WeightedAgent implements SnakeAgent {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int MAX_SAFE_MOVES = 4;

    private final WeightVector weights;
    private final FeatureGenome genome;
    private final Random random;

    public WeightedAgent(WeightVector weights) {
        this(weights, FeatureGenome.random(new Random()), new Random());
    }

    public WeightedAgent(WeightVector weights, Random random) {
        this(weights, FeatureGenome.random(random), random);
    }

    public WeightedAgent(WeightVector weights, FeatureGenome genome) {
        this(weights, genome, new Random());
    }

    public WeightedAgent(WeightVector weights, FeatureGenome genome, Random random) {
        this.weights = weights;
        this.genome = genome;
        this.random = random;
    }

    @Override
    public Direction decideMove(Cell[][] board, Direction currentDirection, int score) {
        FeatureExtractor.DecisionContext context = FeatureExtractor.createDecisionContext(
                board,
                currentDirection,
                score
        );

        if (!context.hasHead()) {
            return currentDirection;
        }

        Direction[] safeMoves = new Direction[MAX_SAFE_MOVES];
        int safeMoveCount = collectSafeMoves(context, currentDirection, safeMoves);

        if (safeMoveCount == 0) {
            return currentDirection;
        }

        Direction bestDirection = safeMoves[0];
        double bestValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < safeMoveCount; i++) {
            Direction direction = safeMoves[i];
            FeatureVector features = FeatureExtractor.extract(context, direction);
            double value = weights.dot(features, genome);

            if (value > bestValue || value == bestValue && random.nextBoolean()) {
                bestValue = value;
                bestDirection = direction;
            }
        }

        return bestDirection;
    }

    private int collectSafeMoves(
            FeatureExtractor.DecisionContext context,
            Direction currentDirection,
            Direction[] safeMoves
    ) {
        int count = 0;

        for (Direction direction : DIRECTIONS) {
            if (direction.isOpposite(currentDirection)) {
                continue;
            }

            if (context.isSafeImmediateMove(direction)) {
                safeMoves[count++] = direction;
            }
        }

        return count;
    }

    @Override
    public String getName() {
        return "WeightedAgent";
    }

    public WeightVector getWeights() {
        return weights;
    }

    public FeatureGenome getGenome() {
        return genome;
    }
}
