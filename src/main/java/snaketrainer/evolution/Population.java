package snaketrainer.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import snaketrainer.agent.FeatureGenome;
import snaketrainer.agent.WeightedAgent;
import snaketrainer.agent.WeightVector;

public class Population {
    private final List<WeightedAgent> agents;

    public Population(List<WeightedAgent> agents) {
        this.agents = new ArrayList<>(agents);
    }

    public static Population random(int size, Random random) {
        List<WeightedAgent> agents = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            agents.add(new WeightedAgent(
                    WeightVector.random(random),
                    FeatureGenome.random(random),
                    random
            ));
        }

        return new Population(agents);
    }

    public List<WeightedAgent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    public int size() {
        return agents.size();
    }
}
