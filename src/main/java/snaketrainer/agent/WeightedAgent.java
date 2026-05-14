package snaketrainer.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import snaketrainer.analysis.FeatureExtractor;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.model.Position;

public class WeightedAgent implements SnakeAgent {
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
        Position head = FeatureExtractor.findCell(board, Cell.SNAKE_HEAD);

        if (head == null) {
            return currentDirection;
        }

        List<Direction> safeMoves = getSafeMoves(board, head, currentDirection);

        if (safeMoves.isEmpty()) {
            return currentDirection;
        }

        Direction bestDirection = safeMoves.get(0);
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Direction direction : safeMoves) {
            FeatureVector features = FeatureExtractor.extract(board, currentDirection, direction, score);
            double value = weights.dot(features, genome);

            if (value > bestValue || value == bestValue && random.nextBoolean()) {
                bestValue = value;
                bestDirection = direction;
            }
        }

        return bestDirection;
    }

    private List<Direction> getSafeMoves(Cell[][] board, Position head, Direction currentDirection) {
        List<Direction> safeMoves = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            if (direction.isOpposite(currentDirection)) {
                continue;
            }

            Position next = FeatureExtractor.nextPosition(head, direction);

            if (FeatureExtractor.isSafeImmediateMove(board, next)) {
                safeMoves.add(direction);
            }
        }

        return safeMoves;
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
