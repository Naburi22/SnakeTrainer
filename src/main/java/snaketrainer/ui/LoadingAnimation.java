package snaketrainer.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import snaketrainer.model.Cell;
import snaketrainer.model.Position;

public class LoadingAnimation {
    private final int rows;
    private final int cols;
    private final List<Position> path;
    private final int snakeLength;

    private int frame;

    public LoadingAnimation(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.path = createSquarePath();
        this.snakeLength = 6;
        this.frame = 0;
    }

    public void nextFrame() {
        frame = (frame + 1) % path.size();
    }

    public Cell[][] getBoardMatrix() {
        Cell[][] board = new Cell[rows][cols];

        for (int row = 0; row < rows; row++) {
            Arrays.fill(board[row], Cell.EMPTY);
        }

        for (int i = 0; i < snakeLength; i++) {
            int index = mod(frame - i, path.size());
            Position position = path.get(index);

            if (i == 0) {
                board[position.row()][position.col()] = Cell.SNAKE_HEAD;
            } else if (i == snakeLength - 1) {
                board[position.row()][position.col()] = Cell.SNAKE_TAIL;
            } else {
                board[position.row()][position.col()] = Cell.SNAKE_BODY;
            }
        }

        Position apple = path.get((frame + 4) % path.size());
        board[apple.row()][apple.col()] = Cell.APPLE;

        return board;
    }

    private List<Position> createSquarePath() {
        List<Position> result = new ArrayList<>();

        int top = rows / 2 - 4;
        int bottom = rows / 2 + 4;
        int left = cols / 2 - 4;
        int right = cols / 2 + 4;

        for (int col = left; col <= right; col++) {
            result.add(new Position(top, col));
        }

        for (int row = top + 1; row <= bottom; row++) {
            result.add(new Position(row, right));
        }

        for (int col = right - 1; col >= left; col--) {
            result.add(new Position(bottom, col));
        }

        for (int row = bottom - 1; row > top; row--) {
            result.add(new Position(row, left));
        }

        return result;
    }

    private int mod(int value, int modulus) {
        return ((value % modulus) + modulus) % modulus;
    }
}