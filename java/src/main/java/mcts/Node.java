package mcts;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class Node<S extends State<S>> implements Simulation<S> {

    private final S state;

    private final Node<S> parent;
    private List<Node<S>> children;

    private double score;
    private int visitCount;

    Node(S state) {
        this(state, null);
    }

    private Node(S state, Node<S> parent) {
        this.state = state;
        this.parent = parent;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public int getVisitCount() {
        return visitCount;
    }

    @Override
    public int getParentVisitCount() {
        return parent == null ? 0 : parent.visitCount;
    }

    @Override
    public S getState() {
        return state;
    }

    Node<S> select(Comparator<? super Simulation<? super S>> selector) {
        Node<S> node = this;
        while (node.children != null && node.children.size() > 0) {
            node = node.children.stream().max(selector).orElseThrow(IllegalStateException::new);
        }
        return node;
    }

    Node<S> expand() {
        if (children == null) {
            children = state.moves().map(lazyState -> new Node<>(lazyState.get(), this)).collect(Collectors.toList());
        }
        return children.stream().skip(children.isEmpty() ? 0 : ThreadLocalRandom.current().nextInt(children.size())).findFirst().orElse(this);
    }

    State<S> simulate() {
        return state.randomPlay();
    }

    void propagate(State<S> simulation) {
        Node<S> node = this;
        do {
            node.visitCount += 1;
            node.score += simulation.score(node.state);
            node = node.parent;
        } while (node != null);
    }

    Optional<S> findBestMove(Comparator<? super Simulation<? super S>> selector) {
        return Optional.ofNullable(children).flatMap(children -> children.stream().max(selector).map(node -> node.state));
    }
}
