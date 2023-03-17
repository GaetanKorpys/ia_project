package awele.bot.test;

import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxBot extends DemoBot {
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 9;

    /** Heuristique choisie */
    private static final HEURISTICS HEURISTIC = HEURISTICS.BEST;

    /**
     * @throws InvalidBotException
     */
    public NegamaxBot() throws InvalidBotException {
        this.setBotName("NegaMax & " + HEURISTIC + " & " + MAX_DEPTH);
        this.addAuthor("Gaetan Korpys");
        this.addAuthor("Theo Rousseau");
    }

    /**
     * Fonction d'initalisation du bot
     * Cette fonction est appelée avant chaque affrontement
     */
    @Override
    public void initialize() { }

    /**
     * Pas d'apprentissage
     */
    @Override
    public void learn() { }

    /**
     * Sélection du coup selon l'algorithme MinMax
     */
    @Override
    public double[] getDecision(Board board) {
        WrongNegamaxNode.initialize(board, NegamaxBot.MAX_DEPTH, HEURISTIC);
        return new WrongNegamaxNode(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -Double.MAX_VALUE, Double.MAX_VALUE).getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}
