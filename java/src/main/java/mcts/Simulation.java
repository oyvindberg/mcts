package mcts;

public interface Simulation<S> {

    double getScore();

    int getVisitCount();

    int getParentVisitCount();

    @SuppressWarnings("unused")
    S getState();
}
