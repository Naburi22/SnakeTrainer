package snaketrainer.evolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import snaketrainer.trainer.TrainingResult;

public class MassEvolutionLogger {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String filePath;

    private TrainingResult bestResult;
    private int bestExecutionNumber;

    public MassEvolutionLogger(String filePath) {
        this.filePath = filePath;
        ensureDirectoryExists();
    }

    public void startBatch(int executions, int generations, int agentsPerGeneration) {
        bestResult = null;
        bestExecutionNumber = -1;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("# Log de ejecuciones evolutivas en masa");
            writer.newLine();
            writer.newLine();

            writer.write("## Ejecución en masa - " + LocalDateTime.now().format(DATE_FORMATTER));
            writer.newLine();
            writer.newLine();

            writer.write("**Ajustes de la ejecución:**");
            writer.newLine();
            writer.newLine();

            writer.write("- Número de ejecuciones: " + executions);
            writer.newLine();

            writer.write("- Generaciones por ejecución: " + generations);
            writer.newLine();

            writer.write("- Agentes por generación: " + agentsPerGeneration);
            writer.newLine();
            writer.newLine();

            writer.write("| Ejecución | Puntuación | Nº de Pasos | Generación en la que apareció el mejor agente |");
            writer.newLine();

            writer.write("| --------- | ---------- | ----------- | -------------------------------------------- |");
            writer.newLine();
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo iniciar el log de ejecuciones en masa.", exception);
        }
    }

    public void logExecutionResult(int executionNumber, TrainingResult result) {
        updateBestResult(executionNumber, result);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("| " + executionNumber
                    + " | " + result.getBestScore()
                    + " | " + result.getBestSteps()
                    + " | " + result.getBestGeneration()
                    + " |");
            writer.newLine();
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el resultado de la ejecución en masa.", exception);
        }
    }

    public void finishBatch() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            if (bestResult != null) {
                writer.newLine();
                writer.write("**Mejor resultado del lote:**");
                writer.newLine();
                writer.newLine();

                writer.write("- Ejecución: " + bestExecutionNumber);
                writer.newLine();

                writer.write("- Puntuación: " + bestResult.getBestScore());
                writer.newLine();

                writer.write("- Pasos: " + bestResult.getBestSteps());
                writer.newLine();

                writer.write("- Generación en la que apareció: " + bestResult.getBestGeneration());
                writer.newLine();
            }

            writer.newLine();
            writer.write("---");
            writer.newLine();
            writer.newLine();
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo finalizar el log de ejecuciones en masa.", exception);
        }
    }

    private void updateBestResult(int executionNumber, TrainingResult result) {
        if (bestResult == null || isBetter(result, bestResult)) {
            bestResult = result;
            bestExecutionNumber = executionNumber;
        }
    }

    private boolean isBetter(TrainingResult candidate, TrainingResult currentBest) {
        if (candidate.getBestScore() != currentBest.getBestScore()) {
            return candidate.getBestScore() > currentBest.getBestScore();
        }

        return candidate.getBestSteps() < currentBest.getBestSteps();
    }

    private void ensureDirectoryExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("No se pudo crear el directorio de logs: " + parent.getAbsolutePath());
        }
    }
}