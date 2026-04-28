package snaketrainer.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.model.Position;

public class SnakeGame {
    private final int rows;
    private final int cols;
    private final Random random;

    private final Deque<Position> snake;
    private Position apple;
    private Direction direction;
    private boolean gameOver;
    private int score;
    private int steps;

    public SnakeGame(int rows, int cols) {
        this(rows, cols, new Random());
    }

    public SnakeGame(int rows, int cols, Random random) {
        if (rows < 5 || cols < 5) {
            throw new IllegalArgumentException("El tablero debe ser al menos de 5x5.");
        }

        this.rows = rows;
        this.cols = cols;
        this.random = random;
        this.snake = new ArrayDeque<>();
        reset();
    }

    public void reset() {
        snake.clear();

        int startRow = rows / 2;
        int startCol = cols / 2;

        snake.addFirst(new Position(startRow, startCol));
        snake.addLast(new Position(startRow, startCol - 1));
        snake.addLast(new Position(startRow, startCol - 2));

        apple = null;
        direction = Direction.RIGHT;
        gameOver = false;
        score = 0;
        steps = 0;

        spawnApple();
    }

    public void step(Direction requestedDirection) {
        if (gameOver) {
            return;
        }

        if (requestedDirection != null && !requestedDirection.isOpposite(direction)) {
            direction = requestedDirection;
        }

        Position currentHead = snake.peekFirst();
        Position newHead = nextPosition(currentHead, direction);
        steps++;

        boolean eatingApple = newHead.equals(apple);

        if (hitsWall(newHead)) {
            gameOver = true;
            return;
        }

        /*
         * Se permite avanzar sobre la cola si no se está comiendo manzana,
         * porque la cola se moverá en este mismo frame.
         */
        Position tail = snake.peekLast();
        boolean movingIntoTail = newHead.equals(tail) && !eatingApple;

        if (snake.contains(newHead) && !movingIntoTail) {
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        if (eatingApple) {
            score++;
            spawnApple();
        } else {
            snake.removeLast();
        }
    }

    private boolean hitsWall(Position position) {
        return position.row() < 0
                || position.row() >= rows
                || position.col() < 0
                || position.col() >= cols;
    }

    private Position nextPosition(Position position, Direction direction) {
        return switch (direction) {
            case UP -> new Position(position.row() - 1, position.col());
            case DOWN -> new Position(position.row() + 1, position.col());
            case LEFT -> new Position(position.row(), position.col() - 1);
            case RIGHT -> new Position(position.row(), position.col() + 1);
        };
    }

    private void spawnApple() {
        List<Position> freePositions = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position candidate = new Position(row, col);
                if (!snake.contains(candidate)) {
                    freePositions.add(candidate);
                }
            }
        }

        if (freePositions.isEmpty()) {
            gameOver = true;
            return;
        }

        apple = freePositions.get(random.nextInt(freePositions.size()));
    }

    public Cell[][] getBoardMatrix() {
        Cell[][] board = new Cell[rows][cols];

        for (int row = 0; row < rows; row++) {
            Arrays.fill(board[row], Cell.EMPTY);
        }

        if (apple != null) {
            board[apple.row()][apple.col()] = Cell.APPLE;
        }

        int index = 0;
        int lastIndex = snake.size() - 1;

        for (Position position : snake) {
            if (index == 0) {
                board[position.row()][position.col()] = Cell.SNAKE_HEAD;
            } else if (index == lastIndex) {
                board[position.row()][position.col()] = Cell.SNAKE_TAIL;
            } else {
                board[position.row()][position.col()] = Cell.SNAKE_BODY;
            }

            index++;
        }

        return board;
    }

    public int[][] getNumericBoardMatrix() {
        Cell[][] board = getBoardMatrix();
        int[][] numericBoard = new int[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                numericBoard[row][col] = switch (board[row][col]) {
                    case EMPTY -> 0;
                    case SNAKE_HEAD -> 1;
                    case SNAKE_BODY -> 2;
                    case SNAKE_TAIL -> 3;
                    case APPLE -> 4;
                };
            }
        }

        return numericBoard;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getScore() {
        return score;
    }

    public int getSteps() {
        return steps;
    }

    public Direction getDirection() {
        return direction;
    }

    public Position getHead() {
        return snake.peekFirst();
    }

    public Position getTail() {
        return snake.peekLast();
    }

    public Position getApple() {
        return apple;
    }
}
