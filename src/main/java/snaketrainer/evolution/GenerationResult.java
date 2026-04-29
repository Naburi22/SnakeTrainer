package snaketrainer.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerationResult {
    private final int generationNumber;
    private final List<Individual> orderedIndividuals;

    public GenerationResult(int generationNumber, List<Individual> orderedIndividuals) {
        this.generationNumber = generationNumber;
        this.orderedIndividuals = new ArrayList<>(orderedIndividuals);
        Collections.sort(this.orderedIndividuals);
    }

    public int getGenerationNumber() {
        return generationNumber;
    }

    public List<Individual> getOrderedIndividuals() {
        return Collections.unmodifiableList(orderedIndividuals);
    }

    public Individual getBestIndividual() {
        return orderedIndividuals.get(0);
    }
}