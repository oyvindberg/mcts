package mcts;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface State<S extends State<S>> {

    Stream<? extends Supplier<S>> moves();

    double score(S state);

    default State<S> randomPlay() {
        State<S> state = this;
        Optional<? extends Supplier<S>> next;
        while ((next = state.moves().findAny()).isPresent()) {
            state = next.map(Supplier::get).orElseThrow(IllegalStateException::new);
        }
        return state;
    }
}
