package tictactoe;

import mcts.MonteCarloTreeSearch;

import java.util.concurrent.TimeUnit;

public class TicTacToeGame {

    public static void main(String[] args) {
        MonteCarloTreeSearch<TicTacToe> mcts = new MonteCarloTreeSearch<>();

        TicTacToe ticTacToe = new TicTacToe(TicTacToe.Player.CIRCLE);
        while (!ticTacToe.isComplete()) {
            ticTacToe = mcts.findNextMove(ticTacToe, 500, TimeUnit.MILLISECONDS).orElseThrow(() -> new IllegalStateException("Could not find a best move"));
            System.out.printf("%s%n%n", ticTacToe);
        }

        ticTacToe.getVictor().ifPresent(player -> {
            throw new AssertionError("Game was won by: " + player);
        });

        System.out.println("Completed game with a draw!");
    }
}
