package awele.bot.oldNegamax;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.bot.demo.minmax.MinMaxIDBot;
import awele.bot.demo.minmax.MinMaxNode;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxIDBot extends DemoBot {

    /** Profondeur maximale */
    private static final int MAX_DEPTH = 9;

    /** Temps d'exécutuion limite */
    private static final int TIME_LIMIT = 100;

    /** Heuristique choisie */
    private static final HEURISTICS HEURISTIC = HEURISTICS.BEST;

    /**
     * @throws InvalidBotException
     */
    public NegamaxIDBot() throws InvalidBotException {
        this.setBotName ("OLD NegamaxID & " + HEURISTIC + " & " + MAX_DEPTH);
        this.addAuthor ("Gaetan Korpys");
        this.addAuthor ("Theo Rousseau");
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
        NegamaxNode.initialize(NegamaxIDBot.MAX_DEPTH, HEURISTIC);
        return new NegamaxNode(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -Double.MAX_VALUE, Double.MAX_VALUE).getDecision();
        //NegamaxNode negamaxBot = NegamaxNode.iterativeDeepeningNegamax(board, NegamaxIDBot.TIME_LIMIT, NegamaxIDBot.MAX_DEPTH);

    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}
