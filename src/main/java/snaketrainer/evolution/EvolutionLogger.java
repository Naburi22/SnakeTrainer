package snaketrainer.evolution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.WeightVector;

public class EvolutionLogger {
    private final String filePath;

    public EvolutionLogger(String filePath) {
        this.filePath = filePath;
    }

    public void clear() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("");
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo vaciar el archivo de log evolutivo.", exception);
        }
    }

    public void logGeneration(int generationNumber, List<Individual> individuals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("# Log de evolución");
            writer.newLine();
            writer.write("## Generación " + generationNumber);
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < individuals.size(); i++) {
                Individual individual = individuals.get(i);

                writer.write("### Agente " + (i + 1));
                writer.newLine();

                writer.write("- Puntuación: " + individual.getApples());
                writer.newLine();

                writer.write("- Pasos: " + individual.getSteps());
                writer.newLine();

                writer.write("- Causa de fin: " + individual.getEndCause().getDisplayName());
                writer.newLine();
                writer.newLine();

                writer.write("**Vector de pesos:**");
                writer.newLine();

                writeWeightsTable(writer, individual.getAgent().getWeights());

                writer.newLine();
                writer.write("---");
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el archivo de log evolutivo.", exception);
        }
    }

    private void writeWeightsTable(BufferedWriter writer, WeightVector weights) throws IOException {
        writer.write("| Feature | Valor |");
        writer.newLine();
        writer.write("|--------|------|");
        writer.newLine();

        for (FeatureName featureName : FeatureName.values()) {
            writer.write("| " + featureName.getDisplayName() + " | " +
                    String.format("%.6f", weights.get(featureName)) + " |");
            writer.newLine();
        }
    }

    public void logBestIndividual(Individual best, int generations) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("# Mejor agente global");
            writer.newLine();
            writer.newLine();

            writer.write("- Generaciones totales: " + generations);
            writer.newLine();

            writer.write("- Puntuación: " + best.getApples());
            writer.newLine();

            writer.write("- Pasos: " + best.getSteps());
            writer.newLine();

            writer.write("- Causa de fin: " + best.getEndCause().getDisplayName());
            writer.newLine();
            writer.newLine();

            writer.write("**Vector de pesos:**");
            writer.newLine();

            writeWeightsTable(writer, best.getAgent().getWeights());
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el mejor agente en el log.", exception);
        }
    }
}