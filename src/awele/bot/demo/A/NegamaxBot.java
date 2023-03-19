package awele.bot.demo.A;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxBot extends CompetitorBot {
    /**
     * Profondeur maximale
     */
    private static final int MAX_DEPTH = 10;

    /**
     * @throws InvalidBotException
     */
    public NegamaxBot() throws InvalidBotException {
        this.setBotName("NegaMax V3 Profondeur = " + MAX_DEPTH);
        this.addAuthor("Quentin BEAUPUY & Vivien KORPYS");
    }

    /**
     * Fonction d'initalisation du bot
     * Cette fonction est appelée avant chaque affrontement
     */
    @Override
    public void initialize() {

    }

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
        NegamaxNode.initialize(NegamaxBot.MAX_DEPTH, board.getCurrentPlayer());
        return new NegamaxNode(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -9999, 9999).getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}