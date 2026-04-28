package snaketrainer.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.model.Position;

public class GreedyAgent implements SnakeAgent {
    private final Random random = new Random();

    @Override
    public Direction decideMove(Cell[][] board, Direction currentDirection, int score) {
        Position head = findCell(board, Cell.SNAKE_HEAD);
        Position apple = findCell(board, Cell.APPLE);

        if (head == null || apple == null) {
            return currentDirection;
        }

        List<Direction> preferredDirections = new ArrayList<>();

        if (apple.row() < head.row()) {
            preferredDirections.add(Direction.UP);
        }
        if (apple.row() > head.row()) {
            preferredDirections.add(Direction.DOWN);
        }
        if (apple.col() < head.col()) {
            preferredDirections.add(Direction.LEFT);
        }
        if (apple.col() > head.col()) {
            preferredDirections.add(Direction.RIGHT);
        }

        Collections.shuffle(preferredDirections, random);

        for (Direction direction : preferredDirections) {
            if (!direction.isOpposite(currentDirection) && isSafe(board, head, direction)) {
                return direction;
            }
        }

        List<Direction> fallbackDirections = new ArrayList<>(List.of(
                Direction.UP,
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT
        ));

        Collections.shuffle(fallbackDirections, random);

        for (Direction direction : fallbackDirections) {
            if (!direction.isOpposite(currentDirection) && isSafe(board, head, direction)) {
                return direction;
            }
        }

        return currentDirection;
    }

    @Override
    public String getName() {
        return "GreedyAgent";
    }

    private Position findCell(Cell[][] board, Cell target) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] == target) {
                    return new Position(row, col);
                }
            }
        }

        return null;
    }

    private boolean isSafe(Cell[][] board, Position head, Direction direction) {
        Position next = nextPosition(head, direction);

        if (next.row() < 0
                || next.row() >= board.length
                || next.col() < 0
                || next.col() >= board[0].length) {
            return false;
        }

        Cell target = board[next.row()][next.col()];
        return target != Cell.SNAKE_BODY && target != Cell.SNAKE_HEAD;
    }

    private Position nextPosition(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.row() - 1, position.col());
            case DOWN -> new Position(position.row() + 1, position.col());
            case LEFT -> new Position(position.row(), position.col() - 1);
            case RIGHT -> new Position(position.row(), position.col() + 1);
        };
    }
}
