package awele.bot.test;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

import java.util.Date;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxIDBot extends DemoBot {
    /**
     * Profondeur maximale
     */
    private static final int MAX_DEPTH = 8;

    private static final int TIME_LIMIT = 100;

    /**
     * @throws InvalidBotException
     */
    public NegamaxIDBot() throws InvalidBotException {
        this.setBotName("NegaMax V3 Profondeur = " + MAX_DEPTH);
        this.addAuthor("Negamax");
    }

    /**
     * Fonction d'initalisation du bot
     * Cette fonction est appelée avant chaque affrontement
     */
    @Override
    public void initialize() {}

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
        return NegamaxNode.iterativeDeepeningNegamax(board, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), NegamaxIDBot.TIME_LIMIT,  NegamaxIDBot.MAX_DEPTH).getDecision();

    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}
