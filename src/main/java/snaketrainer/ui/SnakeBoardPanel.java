package snaketrainer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import snaketrainer.game.SnakeGame;
import snaketrainer.model.Cell;

public class SnakeBoardPanel extends JPanel {
    private final SnakeGame game;
    private final int cellSize;

    public SnakeBoardPanel(SnakeGame game, int cellSize) {
        this.game = game;
        this.cellSize = cellSize;

        setPreferredSize(new Dimension(game.getCols() * cellSize, game.getRows() * cellSize));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Cell[][] board = game.getBoardMatrix();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                drawCell(graphics, board[row][col], row, col);
            }
        }

        drawGrid(graphics);
    }

    private void drawCell(Graphics graphics, Cell cell, int row, int col) {
        int x = col * cellSize;
        int y = row * cellSize;

        switch (cell) {
            case EMPTY -> graphics.setColor(new Color(30, 30, 30));
            case SNAKE_HEAD -> graphics.setColor(new Color(80, 255, 110));
            case SNAKE_BODY -> graphics.setColor(new Color(30, 150, 60));
            case SNAKE_TAIL -> graphics.setColor(new Color(20, 95, 45));
            case APPLE -> graphics.setColor(new Color(220, 40, 40));
        }

        graphics.fillRect(x, y, cellSize, cellSize);
    }

    private void drawGrid(Graphics graphics) {
        graphics.setColor(new Color(55, 55, 55));

        for (int row = 0; row <= game.getRows(); row++) {
            int y = row * cellSize;
            graphics.drawLine(0, y, game.getCols() * cellSize, y);
        }

        for (int col = 0; col <= game.getCols(); col++) {
            int x = col * cellSize;
            graphics.drawLine(x, 0, x, game.getRows() * cellSize);
        }
    }
}
