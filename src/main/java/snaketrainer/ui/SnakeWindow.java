package snaketrainer.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import snaketrainer.agent.FeatureGenome;
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
    private static final int DEFAULT_VISUAL_DELAY_MS = 90;
    private static final int MIN_SPEED_LEVEL = 1;
    private static final int MAX_SPEED_LEVEL = 10;
    private static final int DEFAULT_SPEED_LEVEL = 5;
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
    private final JProgressBar trainingProgressBar;
    private final JSlider speedSlider;

    private LoadingAnimation loadingAnimation;

    private final JTextField generationsField;
    private final JTextField agentsField;
    private final JButton runButton;

    private Timer visualTimer, trainingAnimationTimer;
    private SnakeAgent currentAgent;
    private int lastVisualScore;

    private final JButton manualWeightsButton;
    private final JButton backButton;
    private final JButton runManualAgentButton;
    private final JButton skipVisualGameButton;
    private final JButton copyCurrentAgentButton;

    private final JPanel rightPanelCards;
    private final JPanel normalPanel;
    private final JPanel manualWeightsPanel;

    private final Map<FeatureName, JTextField> weightFields;
    private final Map<FeatureName, JCheckBox> featureEnabledFields;

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

        weightsArea = new JTextArea(9, 42);
        weightsArea.setEditable(false);
        weightsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        weightsArea.setText("");

        trainingProgressBar = new JProgressBar();
        trainingProgressBar.setStringPainted(true);
        trainingProgressBar.setVisible(false);

        generationsField = new JTextField("10", INPUT_COLUMNS);
        agentsField = new JTextField("20", INPUT_COLUMNS);
        generationsField.setMaximumSize(generationsField.getPreferredSize());
        agentsField.setMaximumSize(agentsField.getPreferredSize());
        runButton = new JButton("Ejecutar");
        manualWeightsButton = new JButton("Probar pesos manuales");
        skipVisualGameButton = new JButton("Saltar partida");
        copyCurrentAgentButton = new JButton("Copiar agente actual");
        backButton = new JButton("Volver");
        runManualAgentButton = new JButton("Ejecutar agente manual");

        speedSlider = new JSlider(
            MIN_SPEED_LEVEL,
            MAX_SPEED_LEVEL,
            DEFAULT_SPEED_LEVEL
        );

        speedSlider.setMajorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(event -> updateVisualSpeed());

        rightPanelCards = new JPanel(new CardLayout());
        weightFields = new EnumMap<>(FeatureName.class);
        featureEnabledFields = new EnumMap<>(FeatureName.class);

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
        skipVisualGameButton.addActionListener(event -> skipCurrentVisualGame());
        copyCurrentAgentButton.addActionListener(event -> copyCurrentAgentToManualFields());

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createRightPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(1, 1));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // =====================================================
        // 1. INFORMACIÓN DE LA PARTIDA
        // =====================================================
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Información de la partida"));

        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.insets = new Insets(4, 6, 4, 6);
        infoGbc.anchor = GridBagConstraints.WEST;
        infoGbc.fill = GridBagConstraints.HORIZONTAL;
        infoGbc.weightx = 1.0;

        addInfoLabel(infoPanel, infoGbc, scoreLabel, 0, 0);
        addInfoLabel(infoPanel, infoGbc, stepsLabel, 1, 0);

        addInfoLabel(infoPanel, infoGbc, generationsLabel, 0, 1);
        addInfoLabel(infoPanel, infoGbc, agentsLabel, 1, 1);

        addInfoLabel(infoPanel, infoGbc, bestScoreLabel, 0, 2);
        addInfoLabel(infoPanel, infoGbc, bestStepsLabel, 1, 2);

        addInfoLabel(infoPanel, infoGbc, bestAgentLabel, 0, 3);
        addInfoLabel(infoPanel, infoGbc, statusLabel, 1, 3);

        infoGbc.gridx = 0;
        infoGbc.gridy = 4;
        infoGbc.gridwidth = 2;
        infoGbc.fill = GridBagConstraints.HORIZONTAL;
        infoPanel.add(trainingProgressBar, infoGbc);

        infoGbc.gridy = 5;
        JScrollPane weightsScrollPane = new JScrollPane(weightsArea);
        weightsScrollPane.setBorder(
                BorderFactory.createTitledBorder("Vector de prioridades / features activas")
        );
        infoPanel.add(weightsScrollPane, infoGbc);

        infoGbc.gridwidth = 1;

        // =====================================================
        // 2. CONFIGURACIÓN VISUAL
        // =====================================================
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.Y_AXIS));
        speedPanel.setBorder(BorderFactory.createTitledBorder("Velocidad de partida"));
        speedPanel.add(speedSlider);

        JPanel visualConfigPanel = new JPanel();
        visualConfigPanel.setLayout(new BoxLayout(visualConfigPanel, BoxLayout.Y_AXIS));
        visualConfigPanel.setBorder(BorderFactory.createTitledBorder("Configuración visual"));

        visualConfigPanel.add(speedPanel);

        // =====================================================
        // 3. PARÁMETROS EVOLUTIVOS
        // =====================================================
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Parámetros"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        addLabeledField(inputPanel, gbc, "Nº de iteraciones/generaciones:", generationsField, 0, 0);
        addLabeledField(inputPanel, gbc, "Nº de agentes por generación:", agentsField, 2, 0);

        // =====================================================
        // 4. BOTONES FUERA DE LA CAJA DE PARÁMETROS
        // =====================================================
        JPanel buttonsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(4, 4, 4, 4);

        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonsPanel.add(runButton, btnGbc);

        btnGbc.gridx = 1;
        btnGbc.gridy = 0;
        buttonsPanel.add(manualWeightsButton, btnGbc);

        btnGbc.gridx = 2;
        btnGbc.gridy = 0;
        buttonsPanel.add(skipVisualGameButton, btnGbc);

        btnGbc.gridx = 3;
        btnGbc.gridy = 0;
        buttonsPanel.add(copyCurrentAgentButton, btnGbc);

        // =====================================================
        // COMPOSICIÓN FINAL
        // =====================================================
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(visualConfigPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(buttonsPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private void addInfoLabel(
        JPanel panel,
        GridBagConstraints gbc,
        JLabel label,
        int column,
        int row
    ) {
        gbc.gridx = column;
        gbc.gridy = row;
        panel.add(label, gbc);
    }

    private void addLabeledField(
        JPanel panel,
        GridBagConstraints gbc,
        String labelText,
        JComponent field,
        int labelColumn,
        int row
    ) {
        gbc.gridx = labelColumn;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = labelColumn + 1;
        gbc.weightx = 0;
        panel.add(field, gbc);
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
        trainingProgressBar.setMinimum(0);
        trainingProgressBar.setMaximum(generations);
        trainingProgressBar.setValue(0);
        trainingProgressBar.setString("0 / " + generations);
        trainingProgressBar.setVisible(true);
        startTrainingAnimation();
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
                return trainer.train(generations, agentsPerGeneration, (generation, totalGenerations) -> {
                    SwingUtilities.invokeLater(() -> {
                        trainingProgressBar.setMaximum(totalGenerations);
                        trainingProgressBar.setValue(generation);
                        trainingProgressBar.setString(generation + " / " + totalGenerations);
                    });
                });
            }

            @Override
            protected void done() {
                try {
                    TrainingResult result = get();

                    currentAgent = result.getBestAgent();

                    bestScoreLabel.setText("Mejor puntuación entrenamiento: " + result.getBestScore());
                    bestStepsLabel.setText("Pasos del mejor agente: " + result.getBestSteps());
                    weightsArea.setText(result.getBestWeights().toMultilineString(result.getBestGenome()));
                    bestAgentLabel.setText("Mejor agente: " + currentAgent.getName());
                    statusLabel.setText("Estado: mostrando mejor agente");
                    stopTrainingAnimation();
                    trainingProgressBar.setVisible(false);
                    startVisualGame();
                } catch (Exception exception) {
                    stopTrainingAnimation();
                    trainingProgressBar.setVisible(false);
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
        lastVisualScore = visualGame.getScore();

        updateLabels();
        boardPanel.repaint();

        visualTimer = new Timer(getCurrentVisualDelay(), event -> {
            if (visualGame.isGameOver()) {
                visualTimer.stop();
                statusLabel.setText("Estado: partida terminada por muerte");
                return;
            }

            Cell[][] board = visualGame.getBoardMatrix();
            Direction decision = currentAgent.decideMove(board, visualGame.getDirection(), visualGame.getScore());

            visualGame.step(decision);

            if (visualGame.getScore() > lastVisualScore) {
                lastVisualScore = visualGame.getScore();
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
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Pesos personalizados y features activas"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        FeatureName[] features = FeatureName.values();
        int split = (features.length + 1) / 2;

        for (int i = 0; i < split; i++) {
            addManualFeatureRow(fieldsPanel, gbc, features[i],
                i, 0, i < FeatureGenome.MIN_ACTIVE_FEATURES);

            int rightIndex = i + split;
            if (rightIndex < features.length) {
                addManualFeatureRow(fieldsPanel, gbc, features[rightIndex],
                    i, 3, rightIndex < FeatureGenome.MIN_ACTIVE_FEATURES);
            }
        }

        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints btnGbc = new GridBagConstraints();
        btnGbc.insets = new Insets(4, 4, 4, 4);

        btnGbc.gridx = 0;
        btnGbc.gridy = 0;
        buttonsPanel.add(runManualAgentButton, btnGbc);

        btnGbc.gridx = 1;
        buttonsPanel.add(backButton, btnGbc);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void addManualFeatureRow(
            JPanel fieldsPanel,
            GridBagConstraints gbc,
            FeatureName featureName,
            int row,
            int startCol,
            boolean enabledByDefault
    ) {
        gbc.gridx = startCol;
        gbc.gridy = row;
        fieldsPanel.add(new JLabel(featureName.getDisplayName() + ":"), gbc);

        JTextField field = new JTextField("0.0", INPUT_COLUMNS);
        field.setMaximumSize(field.getPreferredSize());
        weightFields.put(featureName, field);

        gbc.gridx = startCol + 1;
        gbc.gridy = row;
        fieldsPanel.add(field, gbc);

        JCheckBox enabledBox = new JCheckBox("ON", enabledByDefault);
        featureEnabledFields.put(featureName, enabledBox);

        gbc.gridx = startCol + 2;
        gbc.gridy = row;
        fieldsPanel.add(enabledBox, gbc);
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
        boolean[] enabled = new boolean[FeatureName.size()];

        try {
            for (FeatureName featureName : FeatureName.values()) {
                JTextField field = weightFields.get(featureName);
                JCheckBox enabledBox = featureEnabledFields.get(featureName);
                String text = field.getText().trim().replace(",", ".");
                double value = Double.parseDouble(text);

                if (value < WeightVector.MIN_WEIGHT || value > WeightVector.MAX_WEIGHT) {
                    throw new IllegalArgumentException(
                            featureName.getDisplayName() + " debe estar entre -1 y 1."
                    );
                }

                values[featureName.ordinal()] = value;
                enabled[featureName.ordinal()] = enabledBox.isSelected();
            }

            int activeCount = countEnabled(enabled);
            if (activeCount < FeatureGenome.MIN_ACTIVE_FEATURES || activeCount > FeatureGenome.MAX_ACTIVE_FEATURES) {
                throw new IllegalArgumentException(
                        "El agente manual debe tener entre "
                                + FeatureGenome.MIN_ACTIVE_FEATURES
                                + " y "
                                + FeatureGenome.MAX_ACTIVE_FEATURES
                                + " features activas."
                );
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
        FeatureGenome genome = new FeatureGenome(enabled);
        currentAgent = new WeightedAgent(weights, genome);

        bestScoreLabel.setText("Mejor puntuación entrenamiento: -");
        bestStepsLabel.setText("Pasos del mejor agente: -");
        bestAgentLabel.setText("Mejor agente: agente manual");
        weightsArea.setText(weights.toMultilineString(genome));
        statusLabel.setText("Estado: mostrando agente manual");

        showNormalPanel();
        startVisualGame();
    }

    private int countEnabled(boolean[] enabled) {
        int count = 0;

        for (boolean value : enabled) {
            if (value) {
                count++;
            }
        }

        return count;
    }

    private void startTrainingAnimation() {
        if (trainingAnimationTimer != null) {
            trainingAnimationTimer.stop();
        }

        loadingAnimation = new LoadingAnimation(ROWS, COLS);

        trainingAnimationTimer = new Timer(DEFAULT_VISUAL_DELAY_MS, event -> {
            loadingAnimation.nextFrame();
            boardPanel.showOverrideBoard(loadingAnimation.getBoardMatrix());
        });

        trainingAnimationTimer.start();
    }

    private void stopTrainingAnimation() {
        if (trainingAnimationTimer != null) {
            trainingAnimationTimer.stop();
            trainingAnimationTimer = null;
        }

        loadingAnimation = null;
        boardPanel.clearOverrideBoard();
    }

    private int getCurrentVisualDelay() {
        int speed = speedSlider.getValue();

        if (speed <= 4) {
            // 1 -> 300 ms, 4 -> 90 ms
            return 300 - ((speed - 1) * (300 - 90) / (4 - 1));
        }

        // 4 -> 90 ms, 10 -> 20 ms
        return 90 - ((speed - 4) * (90 - 20) / (10 - 4));
    }

    private void updateVisualSpeed() {
        int delay = getCurrentVisualDelay();

        if (visualTimer != null) {
            visualTimer.setDelay(delay);
            visualTimer.setInitialDelay(delay);
        }

        if (trainingAnimationTimer != null) {
            trainingAnimationTimer.setDelay(delay);
            trainingAnimationTimer.setInitialDelay(delay);
        }
    }

    private void skipCurrentVisualGame() {
        if (currentAgent == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay ningún agente ejecutándose actualmente.",
                    "Sin agente",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (visualTimer != null) {
            visualTimer.stop();
        }

        final int maxStepsWithoutApple = ROWS * COLS * 2;

        int stepsWithoutApple = 0;
        int previousScore = visualGame.getScore();

        while (!visualGame.isGameOver() && stepsWithoutApple < maxStepsWithoutApple) {
            Cell[][] board = visualGame.getBoardMatrix();
            Direction decision = currentAgent.decideMove(
                    board,
                    visualGame.getDirection(),
                    visualGame.getScore()
            );

            visualGame.step(decision);

            if (visualGame.getScore() > previousScore) {
                previousScore = visualGame.getScore();
                stepsWithoutApple = 0;
            } else {
                stepsWithoutApple++;
            }
        }

        updateLabels();
        boardPanel.repaint();

        if (visualGame.isGameOver()) {
            statusLabel.setText("Estado: partida saltada hasta muerte");
        } else {
            statusLabel.setText("Estado: partida saltada hasta límite sin comer");
        }
    }

    private void copyCurrentAgentToManualFields() {
        if (!(currentAgent instanceof WeightedAgent weightedAgent)) {
            JOptionPane.showMessageDialog(
                    this,
                    "El agente actual no tiene vector de pesos editable.",
                    "Agente no compatible",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        WeightVector weights = weightedAgent.getWeights();

        for (FeatureName featureName : FeatureName.values()) {
            JTextField field = weightFields.get(featureName);

            if (field != null) {
                field.setText(String.format(java.util.Locale.US, "%.4f", weights.get(featureName)));
            }
        }

        if (featureEnabledFields != null) {
            for (FeatureName featureName : FeatureName.values()) {
                JCheckBox checkbox = featureEnabledFields.get(featureName);

                if (checkbox != null) {
                    checkbox.setSelected(weightedAgent.getGenome().isEnabled(featureName));
                }
            }
        }

        showManualWeightsPanel();
    }
}
