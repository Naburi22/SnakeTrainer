package snaketrainer.evolution.selection;

import java.util.List;
import java.util.Random;

import snaketrainer.evolution.Individual;

public class TournamentSelection implements SelectionStrategy {
    private final int tournamentSize;
    private final Random random;

    public TournamentSelection(int tournamentSize, Random random) {
        this.tournamentSize = tournamentSize;
        this.random = random;
    }

    @Override
    public Individual select(List<Individual> orderedIndividuals) {
        Individual best = null;

        for (int i = 0; i < tournamentSize; i++) {
            Individual candidate = orderedIndividuals.get(random.nextInt(orderedIndividuals.size()));

            if (best == null || candidate.compareTo(best) < 0) {
                best = candidate;
            }
        }

        return best;
    }
}