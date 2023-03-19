package awele.bot.oldNegamax;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import javax.swing.*;
import java.util.Date;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxBot extends CompetitorBot {
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 10;

    /** Temps d'exécutuion limite */
    private static final int TIME_LIMIT = 100;

    /** Heuristique choisie */
    private static final HEURISTICS HEURISTIC = HEURISTICS.BEST;

    /**
     * @throws InvalidBotException
     */
    public NegamaxBot() throws InvalidBotException {
        this.setBotName ("OLD Negamax & " + HEURISTIC + " & " + MAX_DEPTH);
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
        NegamaxNode.initialize(NegamaxBot.MAX_DEPTH, HEURISTIC);
        return new NegamaxNode(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -Double.MAX_VALUE, Double.MAX_VALUE).getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}
