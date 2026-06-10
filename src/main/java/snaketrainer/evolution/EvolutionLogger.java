package snaketrainer.evolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.FeatureName;
import snaketrainer.agent.WeightVector;

public class EvolutionLogger {
    private final String filePath;

    public EvolutionLogger(String filePath) {
        this.filePath = filePath;
        ensureDirectoryExists();
    }

    public void clear() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write("# Log de evolución");
            writer.newLine();
            writer.newLine();
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo vaciar el archivo de log evolutivo.", exception);
        }
    }

    public void logGeneration(int generationNumber, List<Individual> individuals) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("## Generación " + generationNumber);
            writer.newLine();
            writer.newLine();

            for (int i = 0; i < individuals.size(); i++) {
                Individual individual = individuals.get(i);

                writer.write("### Agente " + (i + 1));
                writer.newLine();
                writer.newLine();

                writer.write("- Puntuación: " + individual.getApples());
                writer.newLine();

                writer.write("- Pasos: " + individual.getSteps());
                writer.newLine();

                writer.write("- Causa de fin: " + individual.getEndCause().getDisplayName());
                writer.newLine();

                writer.write("- Features activas: " + individual.getAgent().getGenome().countEnabled()
                        + " / " + FeatureName.size());
                writer.newLine();
                writer.newLine();

                writer.write("**Vector de pesos y genoma de features:**");
                writer.newLine();
                writer.newLine();

                writeWeightsTable(writer, individual.getAgent().getWeights(), individual.getAgent().getGenome());

                writer.newLine();
                writer.write("---");
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el archivo de log evolutivo.", exception);
        }
    }



    public void logLearningState(LearningState state) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("**Función de aprendizaje:**");
            writer.newLine();
            writer.newLine();

            writer.write("- Ventana de estancamiento: " + state.getStagnationWindow() + " generaciones");
            writer.newLine();

            writer.write("- Generaciones sin mejora: " + state.getGenerationsWithoutImprovement());
            writer.newLine();

            writer.write("- Diversidad media de genomas: " + String.format("%.4f", state.getGenomeDiversity()));
            writer.newLine();

            writer.write("- mutationRate actual: " + String.format("%.4f", state.getMutationRate()));
            writer.newLine();

            writer.write("- weightMutationPercentage actual: " + String.format("%.4f", state.getWeightMutationPercentage()));
            writer.newLine();

            writer.write("- featureMutationRate actual: " + String.format("%.4f", state.getFeatureMutationRate()));
            writer.newLine();

            writer.write("- Decisión: " + state.getDecision());
            writer.newLine();
            writer.newLine();
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el estado de la función de aprendizaje.", exception);
        }
    }

    private void writeWeightsTable(BufferedWriter writer, WeightVector weights, FeatureGenome genome) throws IOException {
        writer.write("| Feature | Activa | Valor |");
        writer.newLine();
        writer.write("| ------- | ------ | ----- |");
        writer.newLine();

        for (FeatureName featureName : FeatureName.values()) {
            writer.write("| " + featureName.getDisplayName()
                    + " | " + (genome.isEnabled(featureName) ? "Sí" : "No")
                    + " | " + String.format("%.6f", weights.get(featureName)) + " |");
            writer.newLine();
        }
    }

    public void logBestIndividual(Individual best, int bestGeneration, int generations) {
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

            writer.write("- Generación en la que apareció: " + bestGeneration);
            writer.newLine();

            writer.write("- Causa de fin: " + best.getEndCause().getDisplayName());
            writer.newLine();

            writer.write("- Features activas: " + best.getAgent().getGenome().countEnabled()
                    + " / " + FeatureName.size());
            writer.newLine();
            writer.newLine();

            writer.write("**Vector de pesos y genoma de features:**");
            writer.newLine();
            writer.newLine();

            writeWeightsTable(writer, best.getAgent().getWeights(), best.getAgent().getGenome());
        } catch (IOException exception) {
            throw new RuntimeException("No se pudo escribir el mejor agente en el log.", exception);
        }
    }

    private void ensureDirectoryExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new RuntimeException("No se pudo crear el directorio de logs: " + parent.getAbsolutePath());
        }
    }
}
