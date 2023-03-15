package awele.bot.demo.minmax;

import awele.bot.CompetitorBot;
import awele.bot.DemoBot;
import awele.core.Board;
import awele.core.InvalidBotException;

/**
 * @author Alexandre Blansché
 * Bot qui prend ses décisions selon le MinMax
 */
public class MinMaxBot2 extends DemoBot
{
    /** Profondeur maximale */
    private static final int MAX_DEPTH = 10;
	
    /**
     * @throws InvalidBotException
     */
    public MinMaxBot2() throws InvalidBotException
    {
        this.setBotName ("MinMaxID");
        this.addAuthor ("Alexandre Blansché");
    }

    /**
     * Rien à faire
     */
    @Override
    public void initialize ()
    {
    }

    /**
     * Pas d'apprentissage
     */
    @Override
    public void learn ()
    {
    }

    /**
     * Sélection du coup selon l'algorithme MinMax
     */
    @Override
    public double [] getDecision (Board board)
    {
        MinMaxNode.initialize2 (board, MinMaxBot2.MAX_DEPTH);
        //return new MaxNode (board, 0, 0).getDecision ();
        MinMaxNode minMaxNode = MinMaxNode.iterativeDeepeningNegamax(board, 70, MinMaxBot2.MAX_DEPTH);
        return minMaxNode.getDecision();
    }

    /**
     * Rien à faire
     */
    @Override
    public void finish ()
    {
    }
}
