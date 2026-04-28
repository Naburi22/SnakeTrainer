package snaketrainer.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import snaketrainer.agent.SnakeAgent;
import snaketrainer.game.SnakeGame;
import snaketrainer.model.Cell;
import snaketrainer.model.Direction;
import snaketrainer.trainer.SnakeTrainer;
import snaketrainer.trainer.TrainingResult;

public class SnakeWindow extends JFrame {
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int CELL_SIZE = 28;
    private static final int VISUAL_DELAY_MS = 90;

    private final SnakeGame visualGame;
    private final SnakeBoardPanel boardPanel;

    private final JLabel scoreLabel;
    private final JLabel stepsLabel;
    private final JLabel generationsLabel;
    private final JLabel agentsLabel;
    private final JLabel bestScoreLabel;
    private final JLabel bestAgentLabel;
    private final JLabel statusLabel;

    private final JTextField generationsField;
    private final JTextField agentsField;
    private final JButton runButton;

    private Timer visualTimer;
    private SnakeAgent currentAgent;

    public SnakeWindow() {
        visualGame = new SnakeGame(ROWS, COLS);
        boardPanel = new SnakeBoardPanel(visualGame, CELL_SIZE);

        scoreLabel = new JLabel("Puntuación actual: 0");
        stepsLabel = new JLabel("Pasos: 0");
        generationsLabel = new JLabel("Generaciones: -");
        agentsLabel = new JLabel("Agentes/generación: -");
        bestScoreLabel = new JLabel("Mejor puntuación entrenamiento: -");
        bestAgentLabel = new JLabel("Mejor agente: -");
        statusLabel = new JLabel("Estado: esperando");

        generationsField = new JTextField("10");
        agentsField = new JTextField("20");
        runButton = new JButton("Ejecutar");

        configureWindow();
    }

    private void configureWindow() {
        setTitle("Snake Trainer - Computación Evolutiva");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        add(boardPanel, BorderLayout.WEST);
        add(createRightPanel(), BorderLayout.CENTER);

        runButton.addActionListener(event -> runTraining());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createRightPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información de la partida"));

        infoPanel.add(scoreLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(stepsLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(generationsLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(agentsLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(bestScoreLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(bestAgentLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(statusLabel);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Parámetros"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Nº de iteraciones/generaciones:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(generationsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Nº de agentes por generación:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(agentsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(runButton, gbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void runTraining() {
        int generations;
        int agentsPerGeneration;

        try {
            generations = Integer.parseInt(generationsField.getText().trim());
            agentsPerGeneration = Integer.parseInt(agentsField.getText().trim());

            if (generations <= 0 || agentsPerGeneration <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Introduce números enteros positivos.",
                    "Parámetros inválidos",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (visualTimer != null) {
            visualTimer.stop();
        }

        runButton.setEnabled(false);
        statusLabel.setText("Estado: entrenando...");
        generationsLabel.setText("Generaciones: " + generations);
        agentsLabel.setText("Agentes/generación: " + agentsPerGeneration);
        bestScoreLabel.setText("Mejor puntuación entrenamiento: calculando...");
        bestAgentLabel.setText("Mejor agente: calculando...");

        SwingWorker<TrainingResult, Void> worker = new SwingWorker<>() {
            @Override
            protected TrainingResult doInBackground() {
                SnakeTrainer trainer = new SnakeTrainer();
                return trainer.train(generations, agentsPerGeneration);
            }

            @Override
            protected void done() {
                try {
                    TrainingResult result = get();

                    currentAgent = result.getBestAgent();

                    bestScoreLabel.setText("Mejor puntuación entrenamiento: " + result.getBestScore());
                    bestAgentLabel.setText("Mejor agente: " + currentAgent.getName());
                    statusLabel.setText("Estado: mostrando mejor agente");

                    startVisualGame();
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(
                            SnakeWindow.this,
                            "Error durante el entrenamiento: " + exception.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    statusLabel.setText("Estado: error");
                } finally {
                    runButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void startVisualGame() {
        if (visualTimer != null) {
            visualTimer.stop();
        }

        visualGame.reset();
        updateLabels();
        boardPanel.repaint();

        visualTimer = new Timer(VISUAL_DELAY_MS, event -> {
            if (visualGame.isGameOver()) {
                visualTimer.stop();
                statusLabel.setText("Estado: partida terminada");
                return;
            }

            Cell[][] board = visualGame.getBoardMatrix();
            Direction decision = currentAgent.decideMove(board, visualGame.getDirection(), visualGame.getScore());

            visualGame.step(decision);

            updateLabels();
            boardPanel.repaint();
        });

        visualTimer.start();
    }

    private void updateLabels() {
        scoreLabel.setText("Puntuación actual: " + visualGame.getScore());
        stepsLabel.setText("Pasos: " + visualGame.getSteps());
    }
}
