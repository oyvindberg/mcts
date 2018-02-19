package tictactoe;

import mcts.State;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TicTacToe implements State<TicTacToe> {

    private final Player[][] board;
    private final Set<Move> moves;

    private Player player;
    private boolean victory;

    public TicTacToe(Player starting) {
        this(starting, 3);
    }

    public TicTacToe(Player starting, int dimension) {
        board = new Player[dimension][dimension];
        moves = IntStream.range(0, dimension)
                .mapToObj(row -> IntStream.range(0, dimension).mapToObj(column -> new Move(row, column)))
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
        player = starting;
    }

    private TicTacToe(TicTacToe original) {
        board = new Player[original.board.length][original.board.length];
        IntStream.range(0, board.length).forEach(row -> System.arraycopy(original.board[row], 0, board[row], 0, board[row].length));
        moves = new HashSet<>(original.moves);
        player = original.player;
        victory = original.victory;
    }

    public boolean isComplete() {
        return victory || moves.isEmpty();
    }

    public Optional<Player> getVictor() {
        return victory ? Optional.of(player.opponent()) : Optional.empty();
    }

    @Override
    public Stream<Supplier<TicTacToe>> moves() {
        return isComplete() ? Stream.empty() : moves.stream().map(move -> () -> {
            TicTacToe copy = new TicTacToe(this);
            move.accept(copy);
            return copy;
        });
    }

    @Override
    public double score(TicTacToe result) {
        if (victory) {
            return result.player == player ? 1 : -1;
        } else {
            return 0;
        }
    }

    @Override
    public TicTacToe randomPlay() {
        TicTacToe copy = new TicTacToe(this);
        while (!copy.isComplete()) {
            copy.moves.stream()
                    .skip(ThreadLocalRandom.current().nextInt(copy.moves.size()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new)
                    .accept(copy);
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(board).forEach(row -> {
            sb.append('|');
            Arrays.stream(row).forEach(field -> sb.append(field == null ? ' ' : field.symbol).append('|'));
            sb.append('\n');
        });
        return sb.append(board.length * board.length - moves.size()).append(" moves with next turn for '").append(player.symbol).append('\'').toString();
    }

    public enum Player {

        CIRCLE('o'), CROSS('x');

        final char symbol;

        Player(char symbol) {
            this.symbol = symbol;
        }

        Player opponent() {
            switch (this) {
                case CROSS:
                    return CIRCLE;
                case CIRCLE:
                    return CROSS;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private static class Move implements Consumer<TicTacToe> {

        private final int row, column;

        private Move(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public void accept(TicTacToe state) {
            state.board[row][column] = state.player;
            state.victory = Arrays.stream(state.board[row]).allMatch(element -> element == state.player)
                    || IntStream.range(0, state.board.length).allMatch(row -> state.board[row][column] == state.player)
                    || row == column
                    && IntStream.range(0, state.board.length).allMatch(index -> state.board[index][index] == state.player)
                    || row + column == state.board.length - 1
                    && IntStream.range(0, state.board.length).allMatch(index -> state.board[index][state.board.length - index - 1] == state.player);
            state.player = state.player.opponent();
            state.moves.remove(this);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Move && row == ((Move) other).row && column == ((Move) other).column;
        }

        @Override
        public int hashCode() {
            return 31 * row + column;
        }
    }
}
