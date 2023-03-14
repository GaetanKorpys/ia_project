package awele.bot.NegamaxID;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class NegamaxBotID extends DemoBot {
    /**
     * Profondeur maximale
     */
    private static final int MAX_DEPTH = 8;

    /**
     * @throws InvalidBotException
     */
    public NegamaxBotID() throws InvalidBotException {
        this.setBotName("NegaMax ID ");
        this.addAuthor("Quentin BEAUPUY & Vivien KORPYS");
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
        //System.out.println("New Tour : \n");
        NegamaxNodeID.initialize (board, NegamaxBotID.MAX_DEPTH);
        return NegamaxNodeID.iterativeDeepeningNegamax(board, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), 100).getDecision();
        //return new NegamaxNodeID(board, 0, board.getCurrentPlayer(), Board.otherPlayer(board.getCurrentPlayer()), -9999, 9999).getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish() { }
}
