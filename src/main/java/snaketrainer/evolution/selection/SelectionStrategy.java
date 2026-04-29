package snaketrainer.evolution.selection;

import java.util.List;

import snaketrainer.evolution.Individual;

public interface SelectionStrategy {
    Individual select(List<Individual> orderedIndividuals);
}