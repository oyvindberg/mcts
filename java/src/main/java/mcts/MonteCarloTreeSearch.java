package mcts;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class MonteCarloTreeSearch<S extends State<S>> {

    private final Comparator<? super Simulation<? super S>> selector;

    public MonteCarloTreeSearch() {
        this(Comparator.comparingDouble(new UCTCalculator()));
    }

    public MonteCarloTreeSearch(Comparator<? super Simulation<? super S>> selector) {
        this.selector = selector;
    }

    public Optional<S> findNextMove(S state, long time, TimeUnit timeUnit) {
        long expiration = System.currentTimeMillis() + timeUnit.toMillis(time);
        Node<S> root = new Node<>(state);
        while (!Thread.interrupted() && System.currentTimeMillis() < expiration) {
            Node<S> node = root.select(selector).expand();
            node.propagate(node.simulate());
        }
        return root.findBestMove(selector);
    }
}
