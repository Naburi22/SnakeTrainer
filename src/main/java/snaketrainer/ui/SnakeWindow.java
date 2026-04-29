package snaketrainer.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.SnakeAgent;
import snaketrainer.agent.WeightVector;
import snaketrainer.agent.WeightedAgent;
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
    private static final int MAX_STEPS_WITHOUT_APPLE = 200;
    private static final int INPUT_COLUMNS = 8;

    private final SnakeGame visualGame;
    private final SnakeBoardPanel boardPanel;

    private final JLabel scoreLabel;
    private final JLabel stepsLabel;
    private final JLabel generationsLabel;
    private final JLabel agentsLabel;
    private final JLabel bestScoreLabel;
    private final JLabel bestAgentLabel;
    private final JLabel statusLabel;
    private final JLabel bestStepsLabel;
    private final JTextArea weightsArea;

    private final JTextField generationsField;
    private final JTextField agentsField;
    private final JButton runButton;

    private Timer visualTimer;
    private SnakeAgent currentAgent;
    private int visualStepsWithoutApple;
    private int lastVisualScore;

    private final JButton manualWeightsButton;
    private final JButton backButton;
    private final JButton runManualAgentButton;

    private final JPanel rightPanelCards;
    private final JPanel normalPanel;
    private final JPanel manualWeightsPanel;

    private final Map<FeatureName, JTextField> weightFields;

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
        bestStepsLabel = new JLabel("Pasos del mejor agente: -");

        weightsArea = new JTextArea(9, 24);
        weightsArea.setEditable(false);
        weightsArea.setText("");

        generationsField = new JTextField("10", INPUT_COLUMNS);
        agentsField = new JTextField("20", INPUT_COLUMNS);
        generationsField.setMaximumSize(generationsField.getPreferredSize());
        agentsField.setMaximumSize(agentsField.getPreferredSize());
        runButton = new JButton("Ejecutar");
        manualWeightsButton = new JButton("Probar pesos manuales");
        backButton = new JButton("Volver");
        runManualAgentButton = new JButton("Ejecutar agente manual");

        rightPanelCards = new JPanel(new CardLayout());
        weightFields = new EnumMap<>(FeatureName.class);

        normalPanel = createRightPanel();
        manualWeightsPanel = createManualWeightsPanel();

        configureWindow();
    }

    private void configureWindow() {
        setTitle("Snake Trainer - Computación Evolutiva");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        add(boardPanel, BorderLayout.WEST);
        rightPanelCards.add(normalPanel, "normal");
        rightPanelCards.add(manualWeightsPanel, "manual");

        add(rightPanelCards, BorderLayout.CENTER);

        runButton.addActionListener(event -> runTraining());
        manualWeightsButton.addActionListener(event -> showManualWeightsPanel());
        backButton.addActionListener(event -> showNormalPanel());
        runManualAgentButton.addActionListener(event -> runManualAgent());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createRightPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(1, 1));
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
        infoPanel.add(bestStepsLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(bestAgentLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        JScrollPane weightsScrollPane = new JScrollPane(weightsArea);
        weightsScrollPane.setBorder(BorderFactory.createTitledBorder("Vector de prioridades"));
        infoPanel.add(weightsScrollPane);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Parámetros"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Nº de iteraciones/generaciones:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(generationsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Nº de agentes por generación:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(agentsField, gbc);

        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(4, 4, 4, 4);

        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonsPanel.add(runButton, btnGbc);

        btnGbc.gridx = 1;
        buttonsPanel.add(manualWeightsButton, btnGbc);

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void runTraining() {
        int generations;
        int agentsPerGeneration;

        try {
            generations = Integer.parseInt(generationsField.getText().trim());
            agentsPerGeneration = Integer.parseInt(agentsField.getText().trim());

            if (generations <= 0 || agentsPerGeneration <= 0) {
                throw new IllegalArgumentException("Los valores deben ser enteros mayores que 0.");
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Los campos del modo evolutivo deben ser números enteros.",
                    "Parámetros inválidos",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
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
        bestStepsLabel.setText("Pasos del mejor agente: calculando...");
        weightsArea.setText("calculando...");

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
                    bestStepsLabel.setText("Pasos del mejor agente: " + result.getBestSteps());
                    weightsArea.setText(result.getBestWeights().toMultilineString());
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
        visualStepsWithoutApple = 0;
        lastVisualScore = visualGame.getScore();

        updateLabels();
        boardPanel.repaint();

        visualTimer = new Timer(VISUAL_DELAY_MS, event -> {
            if (visualGame.isGameOver()) {
                visualTimer.stop();
                statusLabel.setText("Estado: partida terminada por muerte");
                return;
            }

            if (visualStepsWithoutApple >= MAX_STEPS_WITHOUT_APPLE) {
                visualTimer.stop();
                statusLabel.setText("Estado: partida terminada por ineficiencia");
                return;
            }

            Cell[][] board = visualGame.getBoardMatrix();
            Direction decision = currentAgent.decideMove(board, visualGame.getDirection(), visualGame.getScore());

            visualGame.step(decision);

            if (visualGame.getScore() > lastVisualScore) {
                lastVisualScore = visualGame.getScore();
                visualStepsWithoutApple = 0;
            } else {
                visualStepsWithoutApple++;
            }

            updateLabels();
            boardPanel.repaint();
        });

        visualTimer.start();
    }

    private void updateLabels() {
        scoreLabel.setText("Puntuación actual: " + visualGame.getScore());
        stepsLabel.setText("Pasos: " + visualGame.getSteps());
    }

    private JPanel createManualWeightsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Pesos personalizados"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        for (FeatureName featureName : FeatureName.values()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            fieldsPanel.add(new JLabel(featureName.getDisplayName() + ":"), gbc);

            JTextField field = new JTextField("0.0", 8);
            weightFields.put(featureName, field);

            gbc.gridx = 1;
            gbc.gridy = row;
            fieldsPanel.add(field, gbc);

            row++;
        }

        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonsPanel.add(runManualAgentButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonsPanel.add(backButton, gbc);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void showManualWeightsPanel() {
        CardLayout layout = (CardLayout) rightPanelCards.getLayout();
        layout.show(rightPanelCards, "manual");
    }

    private void showNormalPanel() {
        CardLayout layout = (CardLayout) rightPanelCards.getLayout();
        layout.show(rightPanelCards, "normal");
    }

    private void runManualAgent() {
        double[] values = new double[FeatureName.size()];

        try {
            for (FeatureName featureName : FeatureName.values()) {
                JTextField field = weightFields.get(featureName);
                double value = Double.parseDouble(field.getText().trim());

                if (value < WeightVector.MIN_WEIGHT || value > WeightVector.MAX_WEIGHT) {
                    throw new IllegalArgumentException(
                            featureName.getDisplayName() + " debe estar entre -1 y 1."
                    );
                }

                values[featureName.ordinal()] = value;
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Todos los pesos deben ser números reales.",
                    "Pesos inválidos",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Pesos inválidos",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        WeightVector weights = new WeightVector(values);
        currentAgent = new WeightedAgent(weights);

        bestScoreLabel.setText("Mejor puntuación entrenamiento: -");
        bestStepsLabel.setText("Pasos del mejor agente: -");
        bestAgentLabel.setText("Mejor agente: agente manual");
        weightsArea.setText(weights.toMultilineString());
        statusLabel.setText("Estado: mostrando agente manual");

        showNormalPanel();
        startVisualGame();
    }
}
