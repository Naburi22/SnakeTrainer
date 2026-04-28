package snaketrainer.agent;

import snaketrainer.model.Cell;
import snaketrainer.model.Direction;

public interface SnakeAgent {
    Direction decideMove(Cell[][] board, Direction currentDirection, int score);
    String getName();
}
