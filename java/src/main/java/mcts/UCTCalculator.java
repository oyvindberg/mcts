package mcts;

import java.util.function.ToDoubleFunction;

public class UCTCalculator implements ToDoubleFunction<Simulation<?>> {

    private final double correction;

    public UCTCalculator() {
        this(Math.sqrt(2));
    }

    public UCTCalculator(double correction) {
        this.correction = correction;
    }

    @Override
    public double applyAsDouble(Simulation<?> simulation) {
        double score = simulation.getScore();
        int visitCount = simulation.getVisitCount(), parentVisitCount = simulation.getParentVisitCount();
        return visitCount == 0 || parentVisitCount == 0
                ? Integer.MAX_VALUE
                : (score / visitCount) + correction * Math.sqrt(Math.log(parentVisitCount) / visitCount);
    }
}
