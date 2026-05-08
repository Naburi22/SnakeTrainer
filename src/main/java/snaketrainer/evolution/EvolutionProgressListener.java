package snaketrainer.evolution;

public interface EvolutionProgressListener {
    void onGenerationStarted(int generation, int totalGenerations);
}